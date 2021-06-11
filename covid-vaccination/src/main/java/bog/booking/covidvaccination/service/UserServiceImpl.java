package bog.booking.covidvaccination.service;

import bog.booking.covidvaccination.model.User;
import bog.booking.covidvaccination.model.UserDto;
import bog.booking.covidvaccination.repository.UserRepository;
import bog.booking.covidvaccination.repository.VaccineRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private final int FIRST_DOSE = 1;
    private final int SECOND_DOSE = 2;
    private final int DIFFERENCE = 21;
    private final int LEGAL_AGE = 18;
    private final long DAYS_OF_YEAR = 365L;
    private final int INCREASE = 0;
    private final int DECREASE = 1;

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository, VaccineRepository vaccineRepository) {
        this.userRepository = userRepository;
        this.vaccineRepository = vaccineRepository;
    }

    private final VaccineRepository vaccineRepository;


    @Override
    public List<Optional<UserDto>> getAllUsers() {
        List<Optional<UserDto>> users = new ArrayList<>();
        userRepository.findAll().forEach(user -> users.add(UserDto.toDto(user)));
        if(users.isEmpty()) log.warn("get users : no users available");
        return users;
    }


    @Override
    public List<Optional<UserDto>> getUsersByName(String firstName, String lastName) {
        List<Optional<UserDto>> users = new ArrayList<>();
        userRepository.findUserByFirstNameAndLastName(firstName,lastName).forEach(user -> users.add(UserDto.toDto(user)));
        if(users.isEmpty()) log.warn("get user: non-existing user: {} {}",firstName,lastName);
        return users;
    }


    @Override
    public List<Optional<UserDto>> getRegisteredByVaccine(String vaccine) {
        if(vaccineRepository.findVaccineByName(vaccine).isEmpty()) log.warn("get user: invalid vaccine name: {}",vaccine);
        List<Optional<UserDto>> users = new ArrayList<>();
        userRepository.findUserByVaccine(vaccine).forEach(user -> users.add(UserDto.toDto(user)));
        return users;
    }


    @Override
    public Optional<UserDto> getUserBySerialNumber(String serialNumber) {
        Optional<User> user = userRepository.findUserBySerialNumber(serialNumber);
        if(user.isEmpty()) log.warn("get user: invalid serial number");
        return user.map(UserDto::toDto).orElse(Optional.empty());
    }


    @Override
    public List<Optional<UserDto>> getRegisteredByDoseAndVaccine(String vaccine, int dose) {
        List<Optional<UserDto>> users = new ArrayList<>();
        userRepository.findUserByDoseAndVaccine(dose,vaccine).forEach(user -> users.add(UserDto.toDto(user)));
        if(users.isEmpty()) log.warn("get user: invalid vaccine or dose: {} {}",vaccine,dose);
        return users;
    }


    @Override
    public Optional<UserDto> RegistrationFirstDose(UserDto userDto) {
        User user = UserDto.toEntity(userDto);
        if(isRegistrationValid(user)) {
            try {
                userRepository.save(user);
            }catch (Exception e) {
                log.error("exception {}",e.getMessage());
            }
            updateVaccineQuantity(user.getVaccine(), FIRST_DOSE,DECREASE);
            return UserDto.toDto(userRepository.findUserBySerialNumber(user.getSerialNumber()).get());
        }
        return Optional.empty();
    }


    private void updateVaccineQuantity(String vaccine, int dose, int increaseOrDecrease) {
        if(vaccineRepository.findVaccineByName(vaccine).isPresent()) {
            long secondDose = 0;
            long firstDose = 0;
            if(dose == FIRST_DOSE) {
                secondDose = vaccineRepository.findDoseSecondByName(vaccine);
                if(increaseOrDecrease == DECREASE) {
                    firstDose = vaccineRepository.findDoseFirstByName(vaccine) - 1;
                } else {
                    firstDose = vaccineRepository.findDoseFirstByName(vaccine) + 1;
                }
            }else if (dose == SECOND_DOSE){
                if(increaseOrDecrease == DECREASE) {
                    secondDose = vaccineRepository.findDoseSecondByName(vaccine) - 1;
                } else {
                    secondDose = vaccineRepository.findDoseSecondByName(vaccine) + 1;
                }
                firstDose = vaccineRepository.findDoseFirstByName(vaccine);
            }
            vaccineRepository.updateVaccine(vaccine, firstDose, secondDose);
        }
    }

    /* Check if new user is valid for registration
        @param user - user info
     */
    private boolean isRegistrationValid(User user) {
        if(!isUserPresent(user.getSerialNumber())) {
            if (user.getDose() == FIRST_DOSE) {
                if (isAdult(user.getBirthDate(), user.getVaccinationDate())) {
                    if (isValidDate(user.getVaccinationDate())) {
                        if (vaccineRepository.findVaccineByName(user.getVaccine()).isPresent()) {
                            if (vaccineRepository.findVaccineByName(user.getVaccine()).get().getDose_first() > 0) {
                                return true;
                            }
                            log.error("no available vaccines: {}", user.getVaccine());
                            return false;
                        }
                        log.error("invalid vaccine name: {}", user.getVaccine());
                        return false;
                    }
                    log.error("invalid date: {}", user.getVaccinationDate());
                    return false;
                }
                log.error("invalid age: {}", user.getBirthDate());
                return false;
            }
            log.error("couldn't register without first dose");
            return false;
        }
        log.warn("user is registered already");
        return false;
    }

    /* Check if it's valid date for registration on first dose
        @param date date of registration
     */
    private boolean isValidDate(Date date) {return System.currentTimeMillis() < date.getTime();}

    /* Check if user already has registered
        @param serialNumber unique identifier of user
     */
    private boolean isUserPresent(String serialNumber) {return userRepository.findUserBySerialNumber(serialNumber).isPresent();}

    /* Check if users age is valid for vaccination
        @param birthDate date of birth
        @param vaccinationDate  date of registration on vaccination
     */
    private boolean isAdult(Date birthDate, Date vaccinationDate) {
        long age = vaccinationDate.getTime() - birthDate.getTime();
        if(TimeUnit.MILLISECONDS.toDays(age) % DAYS_OF_YEAR > DAYS_OF_YEAR/2) {
            age = TimeUnit.MILLISECONDS.toDays(age)/DAYS_OF_YEAR - 1;
        } else {
            age = TimeUnit.MILLISECONDS.toDays(age) / DAYS_OF_YEAR;
        }
        return age > LEGAL_AGE;
    }


    @Override
    public Optional<UserDto> RegistrationSecondDose(String serialNumber, Date date) {
        Optional<User> user = userRepository.findUserBySerialNumber(serialNumber);
        if(user.isPresent()) {
            if(user.get().getDose() == 1) {
                if (vaccineRepository.findVaccineByName(user.get().getVaccine()).get().getDose_second() > 0) {
                    if (hasValidTimePassed(date, user.get().getVaccinationDate()) ) {
                        try {
                            Date firstDose = user.get().getVaccinationDate();
                            userRepository.updateDose(serialNumber, date, SECOND_DOSE,firstDose);
                        } catch (Exception e) {
                            log.error("exception {}",e.getMessage());
                            return Optional.empty();
                        }
                        updateVaccineQuantity(user.get().getVaccine(), SECOND_DOSE,DECREASE);
                        return UserDto.toDto(userRepository.findUserBySerialNumber(user.get().getSerialNumber()).get());
                    }
                    log.warn("invalid date");
                    return Optional.empty();
                }
                log.error("no available vaccines: {}",user.get().getVaccine());
                return Optional.empty();
            }
            log.warn("already registered for second dose");
            return Optional.empty();
        }
        log.error("non-existing user");
        return Optional.empty();
    }

    /* Check if valid time has passed after getting first dose of vaccine
        @param firstDose  date of getting first vaccination
        @param secondDose date of getting second vaccination
     */
    private boolean hasValidTimePassed(Date firstDose, Date secondDose) {
        return TimeUnit.MILLISECONDS.toDays(firstDose.getTime() - secondDose.getTime()) > DIFFERENCE;
    }


    @Override
    public Optional<UserDto> cancelRegistration(String serialNumber) {
        Optional<User> user = userRepository.findUserBySerialNumber(serialNumber);
        if(user.isPresent()) {
            if(user.get().getDose() == FIRST_DOSE) {
                try {
                    userRepository.delete(user.get());
                }catch (Exception e) {
                    log.error("exception {}",e.getMessage());
                    return Optional.empty();
                }
            } else {
                Date firstDose = user.get().getFirstDoseDate();
                userRepository.updateDose(serialNumber,firstDose,FIRST_DOSE,firstDose);
            }
            updateVaccineQuantity(user.get().getVaccine(),user.get().getDose(),INCREASE);
            return UserDto.toDto(user.get());
        }
        log.error("couldn't delete : invalid serial number");
        return Optional.empty();
    }
}

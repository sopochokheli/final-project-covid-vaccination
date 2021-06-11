package bog.booking.covidvaccination.service;

import bog.booking.covidvaccination.model.UserDto;

import java.util.Date;
import java.util.List;
import java.util.Optional;


public interface UserService {

    List<Optional<UserDto>> getAllUsers();

    List<Optional<UserDto>> getUsersByName(String firstName, String lastName);

    List<Optional<UserDto>> getRegisteredByVaccine(String vaccine);

    Optional<UserDto> getUserBySerialNumber(String serialNumber);

    List<Optional<UserDto>> getRegisteredByDoseAndVaccine(String vaccine, int dose);

    Optional<UserDto> RegistrationFirstDose(UserDto user);

    Optional<UserDto> RegistrationSecondDose(String SerialNumber, Date date);

    Optional<UserDto> cancelRegistration(String serialNumber);

}

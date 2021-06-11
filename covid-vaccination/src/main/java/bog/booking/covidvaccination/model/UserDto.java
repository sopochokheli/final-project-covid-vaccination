package bog.booking.covidvaccination.model;


import lombok.Builder;
import lombok.Data;

import java.util.Date;
import java.util.Optional;

@Data
@Builder
public class UserDto {

    private String firstName;

    private String lastName;

    private Date birthDate;

    private String vaccine;

    private String serialNumber;

    private int dose;

    private Date vaccinationDate;

    public static Optional<UserDto>  toDto(User user) {
        return Optional.ofNullable(UserDto.builder().firstName(user.getFirstName())
                .lastName(user.getLastName())
                .birthDate(user.getBirthDate())
                .vaccine(user.getVaccine())
                .dose(user.getDose())
                .vaccinationDate(user.getVaccinationDate())
                .serialNumber(user.getSerialNumber())
                .build());
    }

    public static User toEntity(UserDto userDto) {
        User user = new User();
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setDose(userDto.getDose());
        user.setBirthDate(userDto.getBirthDate());
        user.setVaccinationDate(userDto.getVaccinationDate());
        user.setSerialNumber(userDto.getSerialNumber());
        user.setVaccine(userDto.getVaccine());
        return user;
    }

}

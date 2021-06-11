package bog.booking.covidvaccination.controller;

import bog.booking.covidvaccination.model.UserDto;
import bog.booking.covidvaccination.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/registration")
@Slf4j
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }


    @GetMapping()
    public ResponseEntity<List<Optional<UserDto>>> getAllAppointments() {
        List<Optional<UserDto>> users = userService.getAllUsers();
        if(users.isEmpty()) return new ResponseEntity("no user found",HttpStatus.NOT_FOUND);
        return new ResponseEntity(users,HttpStatus.OK);
    }

    @GetMapping("/{vaccine}")
    public ResponseEntity<List<Optional<UserDto>>> getAppointmentsByVaccine(@PathVariable String vaccine) {
        List<Optional<UserDto>> users = userService.getRegisteredByVaccine(vaccine);
        if(users.isEmpty()) return new ResponseEntity("no user found",HttpStatus.NOT_FOUND);
        return new ResponseEntity(users,HttpStatus.OK);
    }

    @GetMapping("/user/{serialNumber}")
    public ResponseEntity<UserDto> getUser(@PathVariable String serialNumber) {
        Optional<UserDto> user = userService.getUserBySerialNumber(serialNumber);
        if(user.isPresent())
            return new ResponseEntity(user, HttpStatus.OK);

        return new ResponseEntity("no user found",HttpStatus.NOT_FOUND);
    }

    @GetMapping("/user")
    public ResponseEntity<List<UserDto>> getUserByName(String firstName, String lastName) {
        List<Optional<UserDto>> users = userService.getUsersByName(firstName,lastName);
        if(users.isEmpty())
            return new ResponseEntity("no user found",HttpStatus.NOT_FOUND);

        return new ResponseEntity(users,HttpStatus.OK);
    }

    @GetMapping("/{vaccine}/{dose}")
    public ResponseEntity<List<UserDto>> getUsersByDoseAndVaccine(@PathVariable String vaccine,
                                                                  @PathVariable int dose) {
        List<Optional<UserDto>> users = userService.getRegisteredByDoseAndVaccine(vaccine,dose);
        if(users.isEmpty())
            return new ResponseEntity("no user found",HttpStatus.NOT_FOUND);

        return new ResponseEntity(users,HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<UserDto> register(@RequestBody UserDto user) {
        Optional<UserDto> userDto = userService.RegistrationFirstDose(user);
       if(userDto.isPresent())
           return new ResponseEntity(userDto.get(),HttpStatus.CREATED);
       return new ResponseEntity("failed to register on first dose",HttpStatus.BAD_REQUEST);
    }

    @PutMapping
    public ResponseEntity<UserDto> register(String serialNumber, String date) {
        SimpleDateFormat formatter =new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date toDate = formatter.parse(date);
            Optional<UserDto> userDto = userService.RegistrationSecondDose(serialNumber,toDate);
            if(userDto.isPresent())
                return new ResponseEntity(userDto.get(),HttpStatus.ACCEPTED);
        } catch (ParseException e) {
            log.error("failed to parse date : {}", e.getMessage());
        }
        return new ResponseEntity("failed to register on second dose",HttpStatus.BAD_REQUEST);
    }

    @DeleteMapping("/{serialNumber}")
    public ResponseEntity<UserDto> cancelRegistration(@PathVariable String serialNumber) {
        Optional<UserDto> userDto = userService.cancelRegistration(serialNumber);
        if(userDto.isPresent())
            return new ResponseEntity(userDto.get(),HttpStatus.ACCEPTED);
        return new ResponseEntity("failed to cancel registration",HttpStatus.BAD_REQUEST);
    }
}

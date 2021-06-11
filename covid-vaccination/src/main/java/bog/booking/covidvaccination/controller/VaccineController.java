package bog.booking.covidvaccination.controller;

import bog.booking.covidvaccination.model.Vaccine;
import bog.booking.covidvaccination.model.VaccineDto;
import bog.booking.covidvaccination.service.VaccineServiceImpl;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/vaccines")
public class VaccineController {

    @Qualifier("vaccine")
    private final VaccineServiceImpl vaccineService;

    public VaccineController(VaccineServiceImpl vaccineService) {
        this.vaccineService = vaccineService;
    }

    @GetMapping
    public ResponseEntity<List<VaccineDto>> getAllVaccines() {
        List<Optional<VaccineDto>> vaccines = vaccineService.getAllVaccines();
        if(vaccines.isEmpty())
            return new ResponseEntity("no vaccine found",HttpStatus.NOT_FOUND);
        return new ResponseEntity(vaccineService.getAllVaccines(), HttpStatus.OK);
    }

    @GetMapping("/{name}")
    public ResponseEntity<Vaccine> getVaccineByName(@PathVariable("name") String name) {
        Optional<VaccineDto> vaccine = vaccineService.getVaccineByName(name);
        if(vaccine.isPresent())
        return new ResponseEntity(vaccine,HttpStatus.OK);

        return new ResponseEntity("no vaccine found",HttpStatus.NOT_FOUND);
    }


    @PostMapping("/add")
    public ResponseEntity<String> addVaccine(@RequestBody VaccineDto vaccineDto) {
        return new ResponseEntity<>(vaccineService.saveVaccine(vaccineDto),HttpStatus.CREATED);
    }

    @PatchMapping("/{name}")
    public String updateVaccine(@PathVariable String name, long quantity) {
        return vaccineService.updateVaccine(name,quantity);
    }

    @DeleteMapping("/{name}")
    public String  deleteVaccine(@PathVariable String name) {
        return vaccineService.deleteVaccine(name);
    }

    @GetMapping("/{vaccine}/guideline")
    public ResponseEntity<String> getVaccineGuideline(@PathVariable String vaccine) {
        String guideline = vaccineService.getVaccineGuideline(vaccine);
        if(guideline != null)
            return new ResponseEntity<>(guideline,HttpStatus.OK);
        return new ResponseEntity<>(null,HttpStatus.BAD_REQUEST);
    }
}

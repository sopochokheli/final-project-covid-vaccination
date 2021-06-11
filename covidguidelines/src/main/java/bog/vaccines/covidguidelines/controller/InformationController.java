package bog.vaccines.covidguidelines.controller;

import bog.vaccines.covidguidelines.service.CovidGuidelinesService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/covid")
public class InformationController {

    final
    CovidGuidelinesService covidGuidelinesService;

    public InformationController(CovidGuidelinesService covidGuidelinesService) {
        this.covidGuidelinesService = covidGuidelinesService;
    }

    @GetMapping("/{vaccine}/guideline")
    public ResponseEntity<String> getCovidGuidelines(@PathVariable String vaccine) {
        String response = covidGuidelinesService.getGuideline(vaccine);
        if(response != null)
            return new ResponseEntity<>(response, HttpStatus.OK);
        return new ResponseEntity<>(null,HttpStatus.NOT_FOUND);
    }
}


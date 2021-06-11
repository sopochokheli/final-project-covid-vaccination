package bog.booking.covidvaccination.service;

import bog.booking.covidvaccination.model.Vaccine;
import bog.booking.covidvaccination.model.VaccineDto;
import bog.booking.covidvaccination.repository.VaccineRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class VaccineServiceImpl implements VaccineService {

    private final String ODD_NUM = "please enter odd number for quantity";

    @Value("${vaccines.guidelines.url}")
    private String covidGuidelineURl;

    private final RestTemplate restTemplate;

    private final
    VaccineRepository vaccineRepository;

    public VaccineServiceImpl(VaccineRepository vaccineRepository, RestTemplate restTemplate) {
        this.vaccineRepository = vaccineRepository;
        this.restTemplate = restTemplate;
    }


    @Override
    public List<Optional<VaccineDto>> getAllVaccines() {
        List<Optional<VaccineDto>> vaccinesDto = new ArrayList<>();
        vaccineRepository.findAll().forEach(vaccine -> vaccinesDto.add(VaccineDto.toDto(vaccine)));
        return vaccinesDto;
    }


    @Override
    public Optional<VaccineDto> getVaccineByName(String name) {
        Optional<Vaccine> vaccine = vaccineRepository.findVaccineByName(name);
        if(vaccine.isEmpty()) log.warn("failed to get vaccine, invalid name: {}",name);
        return vaccine.map(VaccineDto::toDto).orElse(Optional.empty());
    }

    @Override
    public String saveVaccine(VaccineDto vaccineDto) {
        if(vaccineDto.getQuantity()%2 == 0) {
            Vaccine vaccine = VaccineDto.toEntity(vaccineDto);
            try {
                if (vaccineRepository.findVaccineByName(vaccineDto.getName()).isEmpty())
                    vaccineRepository.save(VaccineDto.toEntity(vaccineDto));
                else return String.format("%s already exists", vaccineDto.getName());
            } catch (Exception e) {
                log.error("couldn't save vaccine: {}",e.getMessage());

                return String.format("%s vaccine saving failed, please try again", vaccine.getName());
            }
            return String.format("%s vaccine saved successfully", vaccine.getName());
        }
        return ODD_NUM;
    }

    @Override
    public String updateVaccine(String name, long quantity) {
        VaccineDto vaccineDto = VaccineDto.builder().quantity(quantity).name(name).build();
        if(vaccineDto.getQuantity()%2 == 0) {
            if (vaccineRepository.findVaccineByName(vaccineDto.getName()).isPresent()) {
                try {
                    vaccineRepository.updateVaccine(vaccineDto.getName(),
                            vaccineDto.getQuantity() / 2 + vaccineRepository.findDoseFirstByName(vaccineDto.getName()),
                            vaccineDto.getQuantity() / 2 + vaccineRepository.findDoseSecondByName(vaccineDto.getName()));
                } catch (Exception e) {
                    log.error("couldn't update vaccine: {}",e.getMessage());
                }
                return String.format("%d vaccine added successfully to %s",vaccineDto.getQuantity(),vaccineDto.getName());
            } else {
                log.warn("invalid vaccine name {}",vaccineDto.getName());
                return String.format("vaccine %s doesn't exist ",vaccineDto.getName());
            }
        }
        return ODD_NUM;
    }

    @Override
    public String deleteVaccine(String name) {
        Optional<Vaccine> vaccine = vaccineRepository.findVaccineByName(name);
        if(vaccine.isPresent()) {
            try {
                vaccineRepository.delete(vaccine.get());
                return String.format("%s vaccine deleted successfully", name);
            }catch (Exception e) {
                log.error("couldn't delete vaccine {}",e.getMessage());
            }
        }
        log.warn("couldn't delete non-existing vaccine {}",name);
        return String.format("non-existing vaccine %s",name);
    }

    @Override
    public String getVaccineGuideline(String vaccine) {
        String url = String.format("%s/%s/guideline",covidGuidelineURl,vaccine);
        try {
        ResponseEntity<String> guideline = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );
           return guideline.getBody();
        } catch (Exception e) {
            log.error("invalid vaccine: {}",vaccine);
        }
        return null;
    }


}

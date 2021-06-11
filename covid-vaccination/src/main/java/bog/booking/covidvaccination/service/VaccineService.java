package bog.booking.covidvaccination.service;

import bog.booking.covidvaccination.model.VaccineDto;

import java.util.List;
import java.util.Optional;

public interface VaccineService {

    List<Optional<VaccineDto>> getAllVaccines();

    Optional<VaccineDto> getVaccineByName(String name);

    String saveVaccine(VaccineDto vaccineDto);

    String updateVaccine(String name, long quantity);

    String deleteVaccine(String name);

    String getVaccineGuideline(String vaccine);

}

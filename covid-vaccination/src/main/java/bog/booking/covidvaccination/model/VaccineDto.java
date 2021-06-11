package bog.booking.covidvaccination.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Optional;

@Data
@Builder
@AllArgsConstructor
public class VaccineDto {

    private String name;

    private long quantity;

    public static Optional<VaccineDto> toDto(Vaccine vaccine) {
        return Optional.ofNullable(VaccineDto.builder()
                .name(vaccine.getName())
                .quantity(vaccine.getDose_first() + vaccine.getDose_second())
                .build());
    }

    public static Vaccine toEntity(VaccineDto vaccineDto) {
        Vaccine vaccine = new Vaccine();
        vaccine.setName(vaccineDto.getName());
        vaccine.setDose_first(vaccineDto.getQuantity()/2);
        vaccine.setDose_second(vaccineDto.getQuantity()/2);
        return vaccine;
    }

}

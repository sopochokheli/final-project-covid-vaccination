package bog.booking.covidvaccination.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table
@Data
public class Vaccine {

    @Id
    @Column
    private int id;

    @Column
    private String name;

    @Column
    private long dose_first;

    @Column
    private long dose_second;
}

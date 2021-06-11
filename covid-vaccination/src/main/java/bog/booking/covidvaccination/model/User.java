package bog.booking.covidvaccination.model;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;


@Entity
@Table
@Data
public class User {
    @Id
    @Column
    private int id;

    @Column
    private String firstName;

    @Column
    private String lastName;

    @Column
    private String serialNumber;

    @Column
    @Temporal(TemporalType.DATE)
    private Date birthDate;

    @Column
    private String vaccine;

    @Column
    private int dose;

    @Column
    @Temporal(TemporalType.DATE)
    private Date vaccinationDate;

    @Column
    @Temporal(TemporalType.DATE)
    private Date firstDoseDate;
}

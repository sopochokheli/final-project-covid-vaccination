package bog.booking.covidvaccination.repository;

import bog.booking.covidvaccination.model.User;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User,Integer> {

    List<User> findUserByVaccine(String vaccine);

    List<User> findUserByFirstNameAndLastName(String firstName, String lastName);

    Optional<User> findUserBySerialNumber(String  serialNumber);

    List<User> findUserByDoseAndVaccine(int dose, String vaccine);

    @Transactional
    @Modifying
    @Query("update User t set t.dose = :dose, t.vaccinationDate = :date, t.firstDoseDate = :firstDate where t.serialNumber = :serialNumber")
    void updateDose(String serialNumber, Date date, int dose,Date firstDate);

}

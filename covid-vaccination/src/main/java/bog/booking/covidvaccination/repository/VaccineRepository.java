package bog.booking.covidvaccination.repository;

import bog.booking.covidvaccination.model.Vaccine;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface VaccineRepository extends CrudRepository<Vaccine,Integer> {

    Optional<Vaccine> findVaccineByName(String name);

    @Transactional
    @Modifying
    @Query("update Vaccine t set t.dose_first = :dose_first, t.dose_second = :dose_second where t.name = :name")
    void updateVaccine(String name, long dose_first, long dose_second);

    @Query("select t.dose_first  from Vaccine t where t.name = :name")
    long findDoseFirstByName(String name);

    @Query("select t.dose_second from Vaccine t where t.name = :name")
    long findDoseSecondByName(String name);

}

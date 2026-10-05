package kigali.clinc.clinicmanagement.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import kigali.clinc.clinicmanagement.domain.Office;

@Repository
public interface OfficeRepository extends JpaRepository<Office, Long> {

    Office findByOfficeNumber(int officeNumber);

    Optional<Office> findByNameAndOfficeNumber(String name, int officeNumber);

    Boolean existsByOfficeNumber(int officeNumber);

    // C3 JPQL: busiest office (name, number, appointment count)
    @Query("SELECT o.name, o.officeNumber, COUNT(a) "
            + "FROM Appointment a JOIN a.doctor d JOIN d.office o "
            + "GROUP BY o.id, o.name, o.officeNumber "
            + "ORDER BY COUNT(a) DESC")
    List<Object[]> findBusiestOffice();
}

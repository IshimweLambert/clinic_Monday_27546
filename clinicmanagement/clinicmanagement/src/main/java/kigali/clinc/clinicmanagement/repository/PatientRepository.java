package kigali.clinc.clinicmanagement.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import kigali.clinc.clinicmanagement.domain.Patient;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {

    List<Patient> findByLastNameIgnoreCaseOrderByFirstNameAsc(String lastName);

    Optional<Patient> findBySsNumber(String ssNumber);

    default List<Patient> findPatientsByLastNameIgnoringCaseOrderByFirstNameAsc(String lastName) {
        return findByLastNameIgnoreCaseOrderByFirstNameAsc(lastName);
    }

    @Query("SELECT DISTINCT a.patient FROM Appointment a WHERE a.doctor.id = :doctorId")
    List<Patient> findPatientsOfDoctor(@Param("doctorId") Long doctorId);

    default List<Patient> findPatientsAssignedToDoctor(Long doctorId) {
        return findPatientsOfDoctor(doctorId);
    }

    @Query("SELECT a.patient FROM Appointment a "
            + "GROUP BY a.patient "
            + "HAVING COUNT(a) >= :minimumVisits "
            + "ORDER BY COUNT(a) DESC")
    List<Patient> findFrequentPatients(@Param("minimumVisits") long minimumVisits);

    default List<Patient> findPatientsWithAtLeastVisits(long minimumVisits) {
        return findFrequentPatients(minimumVisits);
    }
}

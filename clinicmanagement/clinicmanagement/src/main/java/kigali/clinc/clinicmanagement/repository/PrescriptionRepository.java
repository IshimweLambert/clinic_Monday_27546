package kigali.clinc.clinicmanagement.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import kigali.clinc.clinicmanagement.domain.Prescription;

@Repository
public interface PrescriptionRepository extends JpaRepository<Prescription, UUID> {

    List<Prescription> findByAppointment_IdOrderByIssuedDateDesc(Long appointmentId);

    @Query("SELECT p FROM Prescription p JOIN p.appointment a JOIN a.patient pat "
            + "WHERE pat.ssNumber = :ssNumber ORDER BY p.issuedDate DESC")
    List<Prescription> findByPatientSsNumberOrderByIssuedDateDesc(@Param("ssNumber") String ssNumber);

    @Query("SELECT p.medicineName, COUNT(p) FROM Prescription p GROUP BY p.medicineName ORDER BY COUNT(p) DESC")
    List<Object[]> findTopMedicines();

    boolean existsByAppointment_Id(Long appointmentId);
}
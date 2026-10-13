package kigali.clinc.clinicmanagement.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import kigali.clinc.clinicmanagement.domain.AppointmentStatus;
import kigali.clinc.clinicmanagement.domain.Doctor;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    @Query("SELECT d FROM Doctor d JOIN d.specializations s "
            + "WHERE LOWER(s.name) = LOWER(:name)")
    List<Doctor> findBySpecializationName(@Param("name") String name);

    default List<Doctor> findDoctorsWithSpecializationName(String name) {
        return findBySpecializationName(name);
    }

    @Query("SELECT d FROM Doctor d WHERE d.office IS NULL ORDER BY d.lastName ASC")
    List<Doctor> findDoctorsWithoutOffice();

    default List<Doctor> findDoctorsMissingOffice() {
        return findDoctorsWithoutOffice();
    }

    @Query("SELECT o FROM Office o WHERE NOT EXISTS (SELECT d FROM Doctor d WHERE d.office = o) ORDER BY o.officeNumber ASC")
    List<kigali.clinc.clinicmanagement.domain.Office> findFreeOffices();

    boolean existsByOffice_IdAndIdNot(Long officeId, Long doctorId);

    @Query("SELECT d FROM Doctor d WHERE LOWER(d.firstName) LIKE LOWER(CONCAT('%', :name, '%')) "
            + "OR LOWER(d.lastName) LIKE LOWER(CONCAT('%', :name, '%')) ORDER BY d.lastName ASC, d.firstName ASC")
    List<Doctor> searchByName(@Param("name") String name);

    @Query("SELECT d.firstName, d.lastName, COUNT(a) FROM Appointment a JOIN a.doctor d "
            + "WHERE a.status = :status AND a.appointmentDate BETWEEN :start AND :end "
            + "GROUP BY d.id, d.firstName, d.lastName HAVING COUNT(a) > 0 ORDER BY COUNT(a) DESC")
    List<Object[]> findDoctorWorkload(@Param("status") AppointmentStatus status,
            @Param("start") LocalDate start,
            @Param("end") LocalDate end);
}

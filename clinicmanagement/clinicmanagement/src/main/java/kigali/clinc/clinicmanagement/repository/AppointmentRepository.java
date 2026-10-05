package kigali.clinc.clinicmanagement.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import kigali.clinc.clinicmanagement.PendingAppointmentInfo;
import kigali.clinc.clinicmanagement.domain.Appointment;
import kigali.clinc.clinicmanagement.domain.AppointmentStatus;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    // ===== Previous exercise helpers (keep working) =====
    List<Appointment> findByDoctor_IdAndStatusOrderByAppointmentDateAsc(
            Long doctorId,
            AppointmentStatus status);

    @Query("SELECT new kigali.clinc.clinicmanagement.PendingAppointmentInfo("
            + "CONCAT(p.firstName, ' ', p.lastName), "
            + "CONCAT(d.firstName, ' ', d.lastName), "
            + "a.appointmentDate) "
            + "FROM Appointment a JOIN a.patient p JOIN a.doctor d "
            + "WHERE a.status = :status AND a.appointmentDate < :today")
    List<PendingAppointmentInfo> findOverduePendingAppointments(
            @Param("status") AppointmentStatus status,
            @Param("today") LocalDate today);

    @Query("SELECT d.id, d.firstName, d.lastName, COUNT(a) "
            + "FROM Appointment a JOIN a.doctor d "
            + "GROUP BY d.id, d.firstName, d.lastName "
            + "ORDER BY COUNT(a) DESC")
    List<Object[]> countAppointmentsPerDoctor();

    @Query("SELECT a FROM Appointment a JOIN a.doctor d JOIN d.specializations s "
            + "WHERE s.id = :specializationId")
    List<Appointment> findAppointmentsBySpecializationId(@Param("specializationId") Long specializationId);

    Page<Appointment> findByDoctor_IdOrderByAppointmentDateAsc(Long doctorId, Pageable pageable);

    // ===== Quiz Part A DERIVED =====

    // A2
    List<Appointment> findByStatusOrderByAppointmentDateAsc(AppointmentStatus status);

    // A3
    List<Appointment> findByAppointmentDateBetweenOrderByAppointmentDateAsc(
            LocalDate start,
            LocalDate end);

    // A4
    boolean existsByDoctor_IdAndAppointmentDateAndStatusNot(
            Long doctorId,
            LocalDate appointmentDate,
            AppointmentStatus status);

    // ===== Quiz Part C =====

    // C1
    @Query("SELECT a.status, COUNT(a) FROM Appointment a GROUP BY a.status")
    List<Object[]> countAppointmentsByStatus();

    // C4
    @Modifying(clearAutomatically = true)
    @Query("UPDATE Appointment a SET a.status = kigali.clinc.clinicmanagement.domain.AppointmentStatus.CANCELLED "
            + "WHERE a.doctor.id = :doctorId AND a.appointmentDate = :date "
            + "AND a.status <> kigali.clinc.clinicmanagement.domain.AppointmentStatus.COMPLETED")
    int cancelAppointmentsForDoctorOnDate(
            @Param("doctorId") Long doctorId,
            @Param("date") LocalDate date);

    // Bonus DELETE
    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM Appointment a WHERE a.status = :status AND a.appointmentDate < :date")
    int deleteByStatusAndAppointmentDateBefore(
            @Param("status") AppointmentStatus status,
            @Param("date") LocalDate date);
}

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

    List<Appointment> findByDoctor_IdAndStatusOrderByAppointmentDateAsc(
            Long doctorId,
            AppointmentStatus status);

    List<Appointment> findByDoctor_IdAndAppointmentDateAndStatusNotOrderByAppointmentDateAsc(
            Long doctorId,
            LocalDate appointmentDate,
            AppointmentStatus status);

    List<Appointment> findByPatient_SsNumberAndAppointmentDateGreaterThanEqualAndStatusInOrderByAppointmentDateAsc(
            String ssNumber,
            LocalDate date,
            List<AppointmentStatus> statuses);

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

    List<Appointment> findByStatusOrderByAppointmentDateAsc(AppointmentStatus status);

    List<Appointment> findByAppointmentDateBetweenOrderByAppointmentDateAsc(
            LocalDate start,
            LocalDate end);

    boolean existsByDoctor_IdAndAppointmentDateAndStatusNot(
            Long doctorId,
            LocalDate appointmentDate,
            AppointmentStatus status);

    @Query("SELECT a.status, COUNT(a) FROM Appointment a GROUP BY a.status")
    List<Object[]> countAppointmentsByStatus();

    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.status = :status")
    Long countByStatus(@Param("status") AppointmentStatus status);

    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.appointmentDate = :date")
    Long countByAppointmentDate(@Param("date") LocalDate date);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE Appointment a SET a.status = kigali.clinc.clinicmanagement.domain.AppointmentStatus.CANCELLED "
            + "WHERE a.doctor.id = :doctorId AND a.appointmentDate = :date "
            + "AND a.status <> kigali.clinc.clinicmanagement.domain.AppointmentStatus.COMPLETED")
    int cancelAppointmentsForDoctorOnDate(
            @Param("doctorId") Long doctorId,
            @Param("date") LocalDate date);

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM Appointment a WHERE a.status = :status AND a.appointmentDate < :date")
    int deleteByStatusAndAppointmentDateBefore(
            @Param("status") AppointmentStatus status,
            @Param("date") LocalDate date);
}

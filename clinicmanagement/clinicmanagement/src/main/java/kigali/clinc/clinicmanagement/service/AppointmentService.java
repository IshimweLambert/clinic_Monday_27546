package kigali.clinc.clinicmanagement.service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kigali.clinc.clinicmanagement.domain.Appointment;
import kigali.clinc.clinicmanagement.domain.AppointmentStatus;
import kigali.clinc.clinicmanagement.domain.Doctor;
import kigali.clinc.clinicmanagement.domain.Patient;
import kigali.clinc.clinicmanagement.repository.AppointmentRepository;
import kigali.clinc.clinicmanagement.repository.DoctorRepository;
import kigali.clinc.clinicmanagement.repository.PatientRepository;

@Service
public class AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepo;

    @Autowired
    private PatientRepository patientRepo;

    @Autowired
    private DoctorRepository doctorRepo;

    public String saveAppointment(Appointment appointment) {
        if (appointment.getPatient() == null || appointment.getPatient().getId() == null) {
            return "Patient is required";
        }
        if (appointment.getDoctor() == null || appointment.getDoctor().getId() == null) {
            return "Doctor is required";
        }
        if (appointment.getAppointmentDate() == null || appointment.getAppointmentDate().isBefore(LocalDate.now())) {
            return "Appointment date cannot be in the past";
        }
        Optional<Patient> patient = patientRepo.findById(appointment.getPatient().getId());
        if (patient.isEmpty()) {
            return "Patient not found";
        }
        Optional<Doctor> doctor = doctorRepo.findById(appointment.getDoctor().getId());
        if (doctor.isEmpty()) {
            return "Doctor not found";
        }

        appointment.setStatus(AppointmentStatus.SCHEDULED);
        boolean booked = appointmentRepo.existsByDoctor_IdAndAppointmentDateAndStatusNot(
                doctor.get().getId(),
                appointment.getAppointmentDate(),
                AppointmentStatus.CANCELLED);
        if (booked) {
            return "Doctor is already booked on that date";
        }

        appointment.setPatient(patient.get());
        appointment.setDoctor(doctor.get());
        appointmentRepo.save(appointment);
        return "Appointment is saved Successfully";
    }

    public List<Appointment> findAll() {
        return appointmentRepo.findAll();
    }

    public Optional<Appointment> findById(Long id) {
        return appointmentRepo.findById(id);
    }

    public String updateAppointment(Long id, Appointment appointment) {
        Optional<Appointment> existing = appointmentRepo.findById(id);
        if (existing.isEmpty()) {
            return "Appointment not found";
        }
        Appointment dbAppointment = existing.get();
        dbAppointment.setAppointmentDate(appointment.getAppointmentDate());
        dbAppointment.setReason(appointment.getReason());
        dbAppointment.setStatus(appointment.getStatus());

        if (appointment.getPatient() != null && appointment.getPatient().getId() != null) {
            Optional<Patient> patient = patientRepo.findById(appointment.getPatient().getId());
            if (patient.isEmpty()) {
                return "Patient not found";
            }
            dbAppointment.setPatient(patient.get());
        }
        if (appointment.getDoctor() != null && appointment.getDoctor().getId() != null) {
            Optional<Doctor> doctor = doctorRepo.findById(appointment.getDoctor().getId());
            if (doctor.isEmpty()) {
                return "Doctor not found";
            }
            dbAppointment.setDoctor(doctor.get());
        }

        appointmentRepo.save(dbAppointment);
        return "Appointment is updated Successfully";
    }

    public String deleteAppointment(Long id) {
        if (!appointmentRepo.existsById(id)) {
            return "Appointment not found";
        }
        appointmentRepo.deleteById(id);
        return "Appointment is deleted Successfully";
    }

    public List<Appointment> findByStatus(AppointmentStatus status) {
        return appointmentRepo.findByStatusOrderByAppointmentDateAsc(status);
    }

    public List<Appointment> findAppointmentsForStatus(AppointmentStatus status) {
        return findByStatus(status);
    }

    public List<Appointment> findBetween(LocalDate start, LocalDate end) {
        return appointmentRepo.findByAppointmentDateBetweenOrderByAppointmentDateAsc(start, end);
    }

    public List<Appointment> findAppointmentsInDateRange(LocalDate start, LocalDate end) {
        return findBetween(start, end);
    }

    public List<Object[]> statsByStatus() {
        return appointmentRepo.countAppointmentsByStatus();
    }

    public List<Object[]> summarizeStatusCounts() {
        return statsByStatus();
    }

    @Transactional
    public String cancelDay(Long doctorId, LocalDate date) {
        int updated = appointmentRepo.cancelAppointmentsForDoctorOnDate(doctorId, date);
        return updated + " appointments cancelled";
    }

    @Transactional
    public String cancelAppointmentsForDoctorOnDate(Long doctorId, LocalDate date) {
        return cancelDay(doctorId, date);
    }

    public Page<Appointment> findPage(Pageable pageable) {
        return appointmentRepo.findAll(pageable);
    }

    public Page<Appointment> findAppointmentPage(Pageable pageable) {
        return findPage(pageable);
    }

    @Transactional
    public String deleteCancelledBefore(LocalDate date) {
        int deleted = appointmentRepo.deleteByStatusAndAppointmentDateBefore(
                AppointmentStatus.CANCELLED, date);
        return deleted + " appointments deleted";
    }

    @Transactional
    public String removeCancelledAppointmentsBefore(LocalDate date) {
        return deleteCancelledBefore(date);
    }

    @Transactional
    public String updateAppointmentStatus(Long id, AppointmentStatus newStatus) {
        Optional<Appointment> appointmentOpt = appointmentRepo.findById(id);
        if (appointmentOpt.isEmpty()) {
            return "The appointment with that id does not exist";
        }
        Appointment appointment = appointmentOpt.get();
        AppointmentStatus current = appointment.getStatus();
        if (current == AppointmentStatus.COMPLETED || current == AppointmentStatus.CANCELLED) {
            return "Cannot change status from " + current + " to " + newStatus;
        }
        if (current == AppointmentStatus.SCHEDULED && (newStatus != AppointmentStatus.CONFIRMED && newStatus != AppointmentStatus.CANCELLED)) {
            return "Cannot change status from " + current + " to " + newStatus;
        }
        if (current == AppointmentStatus.CONFIRMED && (newStatus != AppointmentStatus.COMPLETED && newStatus != AppointmentStatus.CANCELLED)) {
            return "Cannot change status from " + current + " to " + newStatus;
        }
        appointment.setStatus(newStatus);
        appointmentRepo.save(appointment);
        return "Appointment status updated successfully";
    }

    @Transactional
    public String rescheduleAppointment(Long id, LocalDate newDate) {
        Optional<Appointment> appointmentOpt = appointmentRepo.findById(id);
        if (appointmentOpt.isEmpty()) {
            return "The appointment with that id does not exist";
        }
        Appointment appointment = appointmentOpt.get();
        if (appointment.getStatus() == AppointmentStatus.COMPLETED || appointment.getStatus() == AppointmentStatus.CANCELLED) {
            return "A " + appointment.getStatus() + " appointment cannot be rescheduled";
        }
        if (newDate.isBefore(LocalDate.now())) {
            return "Appointment date cannot be in the past";
        }
        boolean booked = appointmentRepo.existsByDoctor_IdAndAppointmentDateAndStatusNot(
                appointment.getDoctor().getId(),
                newDate,
                AppointmentStatus.CANCELLED);
        if (booked) {
            return "Doctor is already booked on that date";
        }
        appointment.setAppointmentDate(newDate);
        appointment.setStatus(AppointmentStatus.SCHEDULED);
        appointmentRepo.save(appointment);
        return "Appointment rescheduled successfully";
    }

    public List<Appointment> findAgenda(Long doctorId, LocalDate date) {
        return appointmentRepo.findByDoctor_IdAndAppointmentDateAndStatusNotOrderByAppointmentDateAsc(
                doctorId,
                date,
                AppointmentStatus.CANCELLED);
    }

    public List<Appointment> findUpcomingForPatient(String ssNumber) {
        return appointmentRepo.findByPatient_SsNumberAndAppointmentDateGreaterThanEqualAndStatusInOrderByAppointmentDateAsc(
                ssNumber,
                LocalDate.now(),
                Arrays.asList(AppointmentStatus.SCHEDULED, AppointmentStatus.CONFIRMED));
    }
}

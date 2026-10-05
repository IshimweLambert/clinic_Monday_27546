package kigali.clinc.clinicmanagement.service;

import java.time.LocalDate;
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
        Optional<Patient> patient = patientRepo.findById(appointment.getPatient().getId());
        if (patient.isEmpty()) {
            return "Patient not found";
        }
        Optional<Doctor> doctor = doctorRepo.findById(appointment.getDoctor().getId());
        if (doctor.isEmpty()) {
            return "Doctor not found";
        }

        // A4: no double booking (ignore CANCELLED)
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

    // A2
    public List<Appointment> findByStatus(AppointmentStatus status) {
        return appointmentRepo.findByStatusOrderByAppointmentDateAsc(status);
    }

    // A3
    public List<Appointment> findBetween(LocalDate start, LocalDate end) {
        return appointmentRepo.findByAppointmentDateBetweenOrderByAppointmentDateAsc(start, end);
    }

    // C1
    public List<Object[]> statsByStatus() {
        return appointmentRepo.countAppointmentsByStatus();
    }

    // C4
    @Transactional
    public String cancelDay(Long doctorId, LocalDate date) {
        int updated = appointmentRepo.cancelAppointmentsForDoctorOnDate(doctorId, date);
        return updated + " appointments cancelled";
    }

    // Bonus page
    public Page<Appointment> findPage(Pageable pageable) {
        return appointmentRepo.findAll(pageable);
    }

    // Bonus delete
    @Transactional
    public String deleteCancelledBefore(LocalDate date) {
        int deleted = appointmentRepo.deleteByStatusAndAppointmentDateBefore(
                AppointmentStatus.CANCELLED, date);
        return deleted + " appointments deleted";
    }
}

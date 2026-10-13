package kigali.clinc.clinicmanagement.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kigali.clinc.clinicmanagement.domain.Appointment;
import kigali.clinc.clinicmanagement.domain.AppointmentStatus;
import kigali.clinc.clinicmanagement.repository.AppointmentRepository;
import kigali.clinc.clinicmanagement.repository.SpecializationRepository;

@RestController
@RequestMapping("/api/query")
public class QueryController {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private SpecializationRepository specializationRepository;

    @GetMapping("/doctor/{doctorId}/pending")
    public ResponseEntity<?> getPendingAppointmentsForDoctor(@PathVariable Long doctorId) {
        return ResponseEntity.ok(
                appointmentRepository.findByDoctor_IdAndStatusOrderByAppointmentDateAsc(
                        doctorId, AppointmentStatus.PENDING));
    }

    @GetMapping("/pending/{doctorId}")
    public ResponseEntity<?> pendingByDoctor(@PathVariable Long doctorId) {
        return getPendingAppointmentsForDoctor(doctorId);
    }

    @GetMapping("/pending-overdue")
    public ResponseEntity<?> findDelayedPendingAppointments() {
        return ResponseEntity.ok(
                appointmentRepository.findOverduePendingAppointments(
                        AppointmentStatus.PENDING, LocalDate.now()));
    }

    @GetMapping("/overdue-pending")
    public ResponseEntity<?> overduePending() {
        return findDelayedPendingAppointments();
    }

    @GetMapping("/appointments-per-doctor")
    public ResponseEntity<?> appointmentsByDoctorSummary() {
        List<Object[]> rows = appointmentRepository.countAppointmentsPerDoctor();
        List<Map<String, Object>> result = new ArrayList<>();
        for (Object[] row : rows) {
            Map<String, Object> item = new HashMap<>();
            item.put("doctorId", row[0]);
            item.put("firstName", row[1]);
            item.put("lastName", row[2]);
            item.put("appointmentCount", row[3]);
            result.add(item);
        }
        return ResponseEntity.ok(result);
    }

    @GetMapping("/doctor-specialization/{specializationId}")
    public ResponseEntity<?> findAppointmentsForSpecialization(@PathVariable Long specializationId) {
        return ResponseEntity.ok(
                appointmentRepository.findAppointmentsBySpecializationId(specializationId));
    }

    @GetMapping("/by-specialization/{specializationId}")
    public ResponseEntity<?> bySpecialization(@PathVariable Long specializationId) {
        return findAppointmentsForSpecialization(specializationId);
    }

    @GetMapping("/doctor/{doctorId}/appointments")
    public ResponseEntity<?> doctorAppointmentsPage(@PathVariable Long doctorId) {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("appointmentDate").ascending());
        Page<Appointment> page = appointmentRepository.findByDoctor_IdOrderByAppointmentDateAsc(doctorId, pageable);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/doctors-per-specialization")
    public ResponseEntity<?> doctorsPerSpecialization() {
        List<Object[]> rows = specializationRepository.countDoctorsPerSpecialization();
        List<Map<String, Object>> result = new ArrayList<>();
        for (Object[] row : rows) {
            Map<String, Object> item = new HashMap<>();
            item.put("specializationId", row[0]);
            item.put("name", row[1]);
            item.put("doctorCount", row[2]);
            result.add(item);
        }
        return ResponseEntity.ok(result);
    }
}

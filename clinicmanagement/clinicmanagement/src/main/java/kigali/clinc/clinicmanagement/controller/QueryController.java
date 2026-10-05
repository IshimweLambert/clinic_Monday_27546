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

    // 1. Derived: PENDING appointments of a doctor, ordered by date
    @GetMapping("/pending/{doctorId}")
    public ResponseEntity<?> pendingByDoctor(@PathVariable Long doctorId) {
        return ResponseEntity.ok(
                appointmentRepository.findByDoctor_IdAndStatusOrderByAppointmentDateAsc(
                        doctorId, AppointmentStatus.PENDING));
    }

    // 2. JPQL: PENDING appointments whose date has passed
    @GetMapping("/pending-overdue")
    public ResponseEntity<?> overduePending() {
        return ResponseEntity.ok(
                appointmentRepository.findOverduePendingAppointments(
                        AppointmentStatus.PENDING, LocalDate.now()));
    }

    // 3. Aggregate: appointments per doctor, busiest first
    @GetMapping("/appointments-per-doctor")
    public ResponseEntity<?> appointmentsPerDoctor() {
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

    // 4. JOIN: appointments of doctors with a given specialization
    @GetMapping("/by-specialization/{specializationId}")
    public ResponseEntity<?> bySpecialization(@PathVariable Long specializationId) {
        return ResponseEntity.ok(
                appointmentRepository.findAppointmentsBySpecializationId(specializationId));
    }

    // 5. Pagination: page 1, 10 per page, sorted by date (Spring page 0 = first page)
    @GetMapping("/doctor/{doctorId}/appointments")
    public ResponseEntity<?> doctorAppointmentsPage(@PathVariable Long doctorId) {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("appointmentDate").ascending());
        Page<Appointment> page = appointmentRepository.findByDoctor_IdOrderByAppointmentDateAsc(doctorId, pageable);
        return ResponseEntity.ok(page);
    }

    // Specialization aggregate: 3 columns (id, name, doctorCount)
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

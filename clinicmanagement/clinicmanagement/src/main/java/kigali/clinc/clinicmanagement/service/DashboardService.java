package kigali.clinc.clinicmanagement.service;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kigali.clinc.clinicmanagement.domain.AppointmentStatus;
import kigali.clinc.clinicmanagement.repository.AppointmentRepository;
import kigali.clinc.clinicmanagement.repository.DoctorRepository;
import kigali.clinc.clinicmanagement.repository.PatientRepository;
import kigali.clinc.clinicmanagement.repository.PrescriptionRepository;

@Service
public class DashboardService {

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private PrescriptionRepository prescriptionRepository;

    public Map<String, Long> getSummary() {
        Map<String, Long> summary = new LinkedHashMap<>();
        summary.put("patients", patientRepository.count());
        summary.put("doctors", doctorRepository.count());
        summary.put("freeOffices", Long.valueOf(doctorRepository.findFreeOffices().size()));
        summary.put("appointmentsToday", appointmentRepository.countByAppointmentDate(LocalDate.now()));
        summary.put("scheduled", appointmentRepository.countByStatus(AppointmentStatus.SCHEDULED));
        summary.put("prescriptions", prescriptionRepository.count());
        return summary;
    }
}

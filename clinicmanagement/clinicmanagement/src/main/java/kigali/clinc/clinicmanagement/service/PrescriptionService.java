package kigali.clinc.clinicmanagement.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kigali.clinc.clinicmanagement.domain.Appointment;
import kigali.clinc.clinicmanagement.domain.AppointmentStatus;
import kigali.clinc.clinicmanagement.domain.Prescription;
import kigali.clinc.clinicmanagement.repository.AppointmentRepository;
import kigali.clinc.clinicmanagement.repository.PrescriptionRepository;

@Service
public class PrescriptionService {

    @Autowired
    private PrescriptionRepository prescriptionRepo;

    @Autowired
    private AppointmentRepository appointmentRepo;

    @Transactional
    public String savePrescription(Long appointmentId, Prescription prescription) {
        Optional<Appointment> appointmentOpt = appointmentRepo.findById(appointmentId);
        if (appointmentOpt.isEmpty()) {
            return "The appointment with that id does not exist";
        }
        Appointment appointment = appointmentOpt.get();
        if (appointment.getStatus() != AppointmentStatus.COMPLETED) {
            return "Prescriptions are only for COMPLETED appointments";
        }
        prescription.setAppointment(appointment);
        prescription.setIssuedDate(LocalDate.now());
        prescriptionRepo.save(prescription);
        return "Prescription saved successfully";
    }

    public Optional<Appointment> findAppointment(Long appointmentId) {
        return appointmentRepo.findById(appointmentId);
    }

    public List<Prescription> findByAppointmentId(Long appointmentId) {
        return prescriptionRepo.findByAppointment_IdOrderByIssuedDateDesc(appointmentId);
    }

    public List<Prescription> findByPatientSsNumber(String ssNumber) {
        return prescriptionRepo.findByPatientSsNumberOrderByIssuedDateDesc(ssNumber);
    }

    @Transactional
    public String updatePrescription(UUID id, Prescription updated) {
        Optional<Prescription> existing = prescriptionRepo.findById(id);
        if (existing.isEmpty()) {
            return "The prescription with that id does not exist";
        }
        Prescription prescription = existing.get();
        prescription.setMedicineName(updated.getMedicineName());
        prescription.setDosage(updated.getDosage());
        prescription.setDurationDays(updated.getDurationDays());
        prescriptionRepo.save(prescription);
        return "Prescription updated successfully";
    }

    @Transactional
    public String deletePrescription(UUID id) {
        Optional<Prescription> existing = prescriptionRepo.findById(id);
        if (existing.isEmpty()) {
            return "The prescription with that id does not exist";
        }
        Prescription prescription = existing.get();
        if (prescriptionRepo.existsByAppointment_Id(prescription.getAppointment().getId())) {
            long count = prescriptionRepo.findByAppointment_IdOrderByIssuedDateDesc(prescription.getAppointment().getId()).size();
            if (count > 1 || (count == 1 && prescriptionRepo.findById(id).isPresent())) {
                return "That appointment has prescriptions, delete them first";
            }
        }
        prescriptionRepo.deleteById(id);
        return "Prescription deleted successfully";
    }

    public List<Object[]> findTopMedicines() {
        return prescriptionRepo.findTopMedicines();
    }
}
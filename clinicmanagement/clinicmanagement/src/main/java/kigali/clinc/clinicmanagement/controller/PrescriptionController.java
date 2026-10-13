package kigali.clinc.clinicmanagement.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import kigali.clinc.clinicmanagement.domain.Prescription;
import kigali.clinc.clinicmanagement.service.PrescriptionService;

@RestController
@RequestMapping(value = "/api/prescriptions")
public class PrescriptionController {

    @Autowired
    private PrescriptionService prescriptionService;

    @PostMapping(value = "/save", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> savePrescription(@RequestParam Long appointmentId,
                                             @RequestBody Prescription prescription) {
        String returnedMessage = prescriptionService.savePrescription(appointmentId, prescription);
        if (returnedMessage.equalsIgnoreCase("Prescription saved successfully")) {
            return new ResponseEntity<>(returnedMessage, HttpStatus.CREATED);
        } else if (returnedMessage.equalsIgnoreCase("The appointment with that id does not exist")) {
            return new ResponseEntity<>(returnedMessage, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(returnedMessage, HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/appointment/{appointmentId}")
    public ResponseEntity<?> getByAppointment(@PathVariable Long appointmentId) {
        Optional<?> appointment = prescriptionService.findAppointment(appointmentId);
        if (appointment.isEmpty()) {
            return new ResponseEntity<>("The appointment with that id does not exist", HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(prescriptionService.findByAppointmentId(appointmentId));
    }

    @GetMapping("/patient/{ssNumber}")
    public ResponseEntity<?> getByPatient(@PathVariable String ssNumber) {
        return ResponseEntity.ok(prescriptionService.findByPatientSsNumber(ssNumber));
    }

    @PutMapping(value = "/update/{id}", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updatePrescription(@PathVariable UUID id, @RequestBody Prescription prescription) {
        String returnedMessage = prescriptionService.updatePrescription(id, prescription);
        if (returnedMessage.equalsIgnoreCase("Prescription updated successfully")) {
            return new ResponseEntity<>(returnedMessage, HttpStatus.OK);
        }
        return new ResponseEntity<>(returnedMessage, HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deletePrescription(@PathVariable UUID id) {
        String returnedMessage = prescriptionService.deletePrescription(id);
        if (returnedMessage.equalsIgnoreCase("Prescription deleted successfully")) {
            return new ResponseEntity<>(returnedMessage, HttpStatus.OK);
        }
        return new ResponseEntity<>(returnedMessage, HttpStatus.NOT_FOUND);
    }

    @GetMapping("/stats/top-medicines")
    public ResponseEntity<?> topMedicines() {
        return ResponseEntity.ok(prescriptionService.findTopMedicines());
    }
}

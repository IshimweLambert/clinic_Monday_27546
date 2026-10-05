package kigali.clinc.clinicmanagement.controller;

import java.util.Optional;

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

import kigali.clinc.clinicmanagement.domain.Doctor;
import kigali.clinc.clinicmanagement.service.DoctorService;

@RestController
@RequestMapping(value = "/api/doctors")
public class DoctorController {

    @Autowired
    private DoctorService doctorService;

    @PostMapping(value = "/save", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> saveDoctor(@RequestBody Doctor doctor) {
        String returnedMessage = doctorService.saveDoctor(doctor);
        if (returnedMessage.equalsIgnoreCase("Doctor is saved Successfully")) {
            return new ResponseEntity<>(returnedMessage, HttpStatus.CREATED);
        }
        return new ResponseEntity<>(returnedMessage, HttpStatus.CONFLICT);
    }

    @GetMapping(value = "/all")
    public ResponseEntity<?> getAllDoctors() {
        return new ResponseEntity<>(doctorService.findAll(), HttpStatus.OK);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<?> getDoctorById(@PathVariable Long id) {
        Optional<Doctor> doctor = doctorService.findById(id);
        if (doctor.isEmpty()) {
            return new ResponseEntity<>("Doctor not found", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(doctor.get(), HttpStatus.OK);
    }

    @PutMapping(value = "/update/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateDoctor(@PathVariable Long id, @RequestBody Doctor doctor) {
        String returnedMessage = doctorService.updateDoctor(id, doctor);
        if (returnedMessage.equalsIgnoreCase("Doctor is updated Successfully")) {
            return new ResponseEntity<>(returnedMessage, HttpStatus.OK);
        } else if (returnedMessage.equalsIgnoreCase("Doctor not found")) {
            return new ResponseEntity<>(returnedMessage, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(returnedMessage, HttpStatus.CONFLICT);
    }

    @DeleteMapping(value = "/delete/{id}")
    public ResponseEntity<?> deleteDoctor(@PathVariable Long id) {
        String returnedMessage = doctorService.deleteDoctor(id);
        if (returnedMessage.equalsIgnoreCase("Doctor is deleted Successfully")) {
            return new ResponseEntity<>(returnedMessage, HttpStatus.OK);
        }
        return new ResponseEntity<>(returnedMessage, HttpStatus.NOT_FOUND);
    }

    @PostMapping(value = "/{doctorId}/specialization/{specializationId}")
    public ResponseEntity<?> addSpecialization(@PathVariable Long doctorId,
                                               @PathVariable Long specializationId) {
        String returnedMessage = doctorService.addSpecialization(doctorId, specializationId);
        if (returnedMessage.equalsIgnoreCase("Specialization added to doctor Successfully")) {
            return new ResponseEntity<>(returnedMessage, HttpStatus.OK);
        }
        return new ResponseEntity<>(returnedMessage, HttpStatus.NOT_FOUND);
    }

    @DeleteMapping(value = "/{doctorId}/specialization/{specializationId}")
    public ResponseEntity<?> removeSpecialization(@PathVariable Long doctorId,
                                                  @PathVariable Long specializationId) {
        String returnedMessage = doctorService.removeSpecialization(doctorId, specializationId);
        if (returnedMessage.equalsIgnoreCase("Specialization removed from doctor Successfully")) {
            return new ResponseEntity<>(returnedMessage, HttpStatus.OK);
        }
        return new ResponseEntity<>(returnedMessage, HttpStatus.NOT_FOUND);
    }

    // B1
    @GetMapping("/by-specialization")
    public ResponseEntity<?> bySpecialization(@RequestParam String name) {
        return ResponseEntity.ok(doctorService.findBySpecializationName(name));
    }

    // B2
    @GetMapping("/without-office")
    public ResponseEntity<?> withoutOffice() {
        return ResponseEntity.ok(doctorService.findDoctorsWithoutOffice());
    }
}

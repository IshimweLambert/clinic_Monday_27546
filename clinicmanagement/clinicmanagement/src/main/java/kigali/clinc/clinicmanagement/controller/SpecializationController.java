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
import org.springframework.web.bind.annotation.RestController;

import kigali.clinc.clinicmanagement.domain.Specialization;
import kigali.clinc.clinicmanagement.service.SpecializationService;

@RestController
@RequestMapping(value = "/api/specializations")
public class SpecializationController {

    @Autowired
    private SpecializationService specializationService;

    @PostMapping(value = "/save", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> saveSpecialization(@RequestBody Specialization specialization) {
        String returnedMessage = specializationService.saveSpecialization(specialization);
        if (returnedMessage.equalsIgnoreCase("Specialization is saved Successfully")) {
            return new ResponseEntity<>(returnedMessage, HttpStatus.CREATED);
        }
        return new ResponseEntity<>(returnedMessage, HttpStatus.CONFLICT);
    }

    @GetMapping(value = "/all")
    public ResponseEntity<?> getAllSpecializations() {
        return new ResponseEntity<>(specializationService.findAll(), HttpStatus.OK);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<?> getSpecializationById(@PathVariable Long id) {
        Optional<Specialization> specialization = specializationService.findById(id);
        if (specialization.isEmpty()) {
            return new ResponseEntity<>("Specialization not found", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(specialization.get(), HttpStatus.OK);
    }

    @PutMapping(value = "/update/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateSpecialization(@PathVariable Long id, @RequestBody Specialization specialization) {
        String returnedMessage = specializationService.updateSpecialization(id, specialization);
        if (returnedMessage.equalsIgnoreCase("Specialization is updated Successfully")) {
            return new ResponseEntity<>(returnedMessage, HttpStatus.OK);
        } else if (returnedMessage.equalsIgnoreCase("Specialization not found")) {
            return new ResponseEntity<>(returnedMessage, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(returnedMessage, HttpStatus.CONFLICT);
    }

    @DeleteMapping(value = "/delete/{id}")
    public ResponseEntity<?> deleteSpecialization(@PathVariable Long id) {
        String returnedMessage = specializationService.deleteSpecialization(id);
        if (returnedMessage.equalsIgnoreCase("Specialization is deleted Successfully")) {
            return new ResponseEntity<>(returnedMessage, HttpStatus.OK);
        }
        return new ResponseEntity<>(returnedMessage, HttpStatus.NOT_FOUND);
    }

    // B3
    @GetMapping("/unused")
    public ResponseEntity<?> unused() {
        return ResponseEntity.ok(specializationService.findUnused());
    }
}

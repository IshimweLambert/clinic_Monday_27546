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

import kigali.clinc.clinicmanagement.domain.Office;
import kigali.clinc.clinicmanagement.service.OfficeService;

@RestController
@RequestMapping(value = "/api/offices")
public class OfficeController {

    @Autowired
    private OfficeService officeService;

    @PostMapping(value = "/save", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> saveOffice(@RequestBody Office office) {
        String returnedMessage = officeService.saveOffice(office);
        if (returnedMessage.equalsIgnoreCase("Office is saved Successfully")) {
            return new ResponseEntity<>(returnedMessage, HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(returnedMessage, HttpStatus.CONFLICT);
        }
    }

    @GetMapping(value = "/all")
    public ResponseEntity<?> getAllOffices() {
        return new ResponseEntity<>(officeService.findAll(), HttpStatus.OK);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<?> getOfficeById(@PathVariable Long id) {
        Optional<Office> office = officeService.findById(id);
        if (office.isEmpty()) {
            return new ResponseEntity<>("Office not found", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(office.get(), HttpStatus.OK);
    }

    @PutMapping(value = "/update/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateOffice(@PathVariable Long id, @RequestBody Office office) {
        String returnedMessage = officeService.updateOffice(id, office);
        if (returnedMessage.equalsIgnoreCase("Office is updated Successfully")) {
            return new ResponseEntity<>(returnedMessage, HttpStatus.OK);
        } else if (returnedMessage.equalsIgnoreCase("Office not found")) {
            return new ResponseEntity<>(returnedMessage, HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(returnedMessage, HttpStatus.CONFLICT);
        }
    }

    @DeleteMapping(value = "/delete/{id}")
    public ResponseEntity<?> deleteOffice(@PathVariable Long id) {
        String returnedMessage = officeService.deleteOffice(id);
        if (returnedMessage.equalsIgnoreCase("Office is deleted Successfully")) {
            return new ResponseEntity<>(returnedMessage, HttpStatus.OK);
        }
        return new ResponseEntity<>(returnedMessage, HttpStatus.NOT_FOUND);
    }

    @GetMapping("/most-used")
    public ResponseEntity<?> getMostUsedOffice() {
        return ResponseEntity.ok(officeService.findBusiestOffice());
    }

    @GetMapping("/busiest")
    public ResponseEntity<?> busiest() {
        return getMostUsedOffice();
    }
}

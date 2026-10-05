package kigali.clinc.clinicmanagement.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kigali.clinc.clinicmanagement.domain.Office;
import kigali.clinc.clinicmanagement.repository.OfficeRepository;

@Service
public class OfficeService {

    @Autowired
    private OfficeRepository officeRepo;

    public String saveOffice(Office office) {
        if (officeRepo.findByOfficeNumber(office.getOfficeNumber()) != null) {
            return "Office with that number " + office.getOfficeNumber() + " already exists";
        }
        officeRepo.save(office);
        return "Office is saved Successfully";
    }

    public List<Office> findAll() {
        return officeRepo.findAll();
    }

    public Optional<Office> findById(Long id) {
        return officeRepo.findById(id);
    }

    public String updateOffice(Long id, Office office) {
        Optional<Office> existing = officeRepo.findById(id);
        if (existing.isEmpty()) {
            return "Office not found";
        }
        Office dbOffice = existing.get();
        Office sameNumber = officeRepo.findByOfficeNumber(office.getOfficeNumber());
        if (sameNumber != null && !sameNumber.getId().equals(id)) {
            return "Office with that number " + office.getOfficeNumber() + " already exists";
        }
        dbOffice.setName(office.getName());
        dbOffice.setOfficeNumber(office.getOfficeNumber());
        officeRepo.save(dbOffice);
        return "Office is updated Successfully";
    }

    public String deleteOffice(Long id) {
        if (!officeRepo.existsById(id)) {
            return "Office not found";
        }
        officeRepo.deleteById(id);
        return "Office is deleted Successfully";
    }

    // C3
    public Object findBusiestOffice() {
        List<Object[]> rows = officeRepo.findBusiestOffice();
        if (rows.isEmpty()) {
            return "No appointments yet";
        }
        return rows.get(0);
    }
}

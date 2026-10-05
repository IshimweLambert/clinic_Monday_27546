package kigali.clinc.clinicmanagement.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kigali.clinc.clinicmanagement.domain.Specialization;
import kigali.clinc.clinicmanagement.repository.SpecializationRepository;

@Service
public class SpecializationService {

    @Autowired
    private SpecializationRepository specializationRepo;

    public String saveSpecialization(Specialization specialization) {
        if (specializationRepo.findByName(specialization.getName()) != null) {
            return "Specialization with that name already exists";
        }
        specializationRepo.save(specialization);
        return "Specialization is saved Successfully";
    }

    public List<Specialization> findAll() {
        return specializationRepo.findAll();
    }

    public Optional<Specialization> findById(Long id) {
        return specializationRepo.findById(id);
    }

    public String updateSpecialization(Long id, Specialization specialization) {
        Optional<Specialization> existing = specializationRepo.findById(id);
        if (existing.isEmpty()) {
            return "Specialization not found";
        }
        Specialization sameName = specializationRepo.findByName(specialization.getName());
        if (sameName != null && !sameName.getId().equals(id)) {
            return "Specialization with that name already exists";
        }
        Specialization dbSpec = existing.get();
        dbSpec.setName(specialization.getName());
        specializationRepo.save(dbSpec);
        return "Specialization is updated Successfully";
    }

    public String deleteSpecialization(Long id) {
        if (!specializationRepo.existsById(id)) {
            return "Specialization not found";
        }
        specializationRepo.deleteById(id);
        return "Specialization is deleted Successfully";
    }

    // B3
    public List<Specialization> findUnused() {
        return specializationRepo.findUnusedSpecializations();
    }
}

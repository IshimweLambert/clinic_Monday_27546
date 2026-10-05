package kigali.clinc.clinicmanagement.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kigali.clinc.clinicmanagement.domain.Doctor;
import kigali.clinc.clinicmanagement.domain.Office;
import kigali.clinc.clinicmanagement.domain.Specialization;
import kigali.clinc.clinicmanagement.repository.DoctorRepository;
import kigali.clinc.clinicmanagement.repository.OfficeRepository;
import kigali.clinc.clinicmanagement.repository.SpecializationRepository;

@Service
public class DoctorService {

    @Autowired
    private DoctorRepository doctorRepo;

    @Autowired
    private OfficeRepository officeRepo;

    @Autowired
    private SpecializationRepository specializationRepo;

    public String saveDoctor(Doctor doctor) {
        if (doctor.getOffice() != null && doctor.getOffice().getId() != null) {
            Optional<Office> office = officeRepo.findById(doctor.getOffice().getId());
            if (office.isEmpty()) {
                return "Office not found";
            }
            List<Doctor> existing = doctorRepo.findAll();
            for (Doctor d : existing) {
                if (d.getOffice() != null && d.getOffice().getId().equals(office.get().getId())) {
                    return "That office already has a doctor";
                }
            }
            doctor.setOffice(office.get());
        }
        doctorRepo.save(doctor);
        return "Doctor is saved Successfully";
    }

    public List<Doctor> findAll() {
        return doctorRepo.findAll();
    }

    public Optional<Doctor> findById(Long id) {
        return doctorRepo.findById(id);
    }

    public String updateDoctor(Long id, Doctor doctor) {
        Optional<Doctor> existing = doctorRepo.findById(id);
        if (existing.isEmpty()) {
            return "Doctor not found";
        }
        Doctor dbDoctor = existing.get();
        dbDoctor.setFirstName(doctor.getFirstName());
        dbDoctor.setLastName(doctor.getLastName());
        dbDoctor.setDateOfBirth(doctor.getDateOfBirth());
        if (doctor.getOffice() != null && doctor.getOffice().getId() != null) {
            Optional<Office> office = officeRepo.findById(doctor.getOffice().getId());
            if (office.isEmpty()) {
                return "Office not found";
            }
            dbDoctor.setOffice(office.get());
        }
        doctorRepo.save(dbDoctor);
        return "Doctor is updated Successfully";
    }

    @Transactional
    public String deleteDoctor(Long id) {
        Optional<Doctor> existing = doctorRepo.findById(id);
        if (existing.isEmpty()) {
            return "Doctor not found";
        }
        Doctor doctor = existing.get();
        Office office = doctor.getOffice();
        if (office != null) {
            doctor.setOffice(null);
            office.setDoctor(null);
            officeRepo.save(office);
            doctorRepo.save(doctor);
        }
        doctorRepo.delete(doctor);
        return "Doctor is deleted Successfully";
    }

    @Transactional
    public String addSpecialization(Long doctorId, Long specializationId) {
        Optional<Doctor> doctorOpt = doctorRepo.findById(doctorId);
        Optional<Specialization> specOpt = specializationRepo.findById(specializationId);
        if (doctorOpt.isEmpty()) {
            return "Doctor not found";
        }
        if (specOpt.isEmpty()) {
            return "Specialization not found";
        }
        Doctor doctor = doctorOpt.get();
        doctor.addSpecialization(specOpt.get());
        doctorRepo.save(doctor);
        return "Specialization added to doctor Successfully";
    }

    @Transactional
    public String removeSpecialization(Long doctorId, Long specializationId) {
        Optional<Doctor> doctorOpt = doctorRepo.findById(doctorId);
        Optional<Specialization> specOpt = specializationRepo.findById(specializationId);
        if (doctorOpt.isEmpty()) {
            return "Doctor not found";
        }
        if (specOpt.isEmpty()) {
            return "Specialization not found";
        }
        Doctor doctor = doctorOpt.get();
        doctor.removeSpecialization(specOpt.get());
        doctorRepo.save(doctor);
        return "Specialization removed from doctor Successfully";
    }

    // B1
    public List<Doctor> findBySpecializationName(String name) {
        return doctorRepo.findBySpecializationName(name);
    }

    // B2
    public List<Doctor> findDoctorsWithoutOffice() {
        return doctorRepo.findDoctorsWithoutOffice();
    }
}

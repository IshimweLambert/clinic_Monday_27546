package kigali.clinc.clinicmanagement.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kigali.clinc.clinicmanagement.domain.Appointment;
import kigali.clinc.clinicmanagement.domain.AppointmentStatus;
import kigali.clinc.clinicmanagement.domain.Doctor;
import kigali.clinc.clinicmanagement.domain.Office;
import kigali.clinc.clinicmanagement.domain.Specialization;
import kigali.clinc.clinicmanagement.repository.AppointmentRepository;
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

    @Autowired
    private AppointmentRepository appointmentRepo;

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
        if (doctorOpt.isEmpty()) {
            return "The doctor with that id does not exist";
        }
        Optional<Specialization> specOpt = specializationRepo.findById(specializationId);
        if (specOpt.isEmpty()) {
            return "That specialization does not exist";
        }
        Doctor doctor = doctorOpt.get();
        if (doctor.getSpecializations().contains(specOpt.get())) {
            return "The doctor already has that specialization";
        }
        doctor.addSpecialization(specOpt.get());
        doctorRepo.save(doctor);
        return "Specialization added to the doctor";
    }

    @Transactional
    public String removeSpecialization(Long doctorId, Long specializationId) {
        Optional<Doctor> doctorOpt = doctorRepo.findById(doctorId);
        if (doctorOpt.isEmpty()) {
            return "The doctor with that id does not exist";
        }
        Optional<Specialization> specOpt = specializationRepo.findById(specializationId);
        if (specOpt.isEmpty()) {
            return "That specialization does not exist";
        }
        Doctor doctor = doctorOpt.get();
        if (!doctor.getSpecializations().contains(specOpt.get())) {
            return "The doctor does not have that specialization";
        }
        doctor.removeSpecialization(specOpt.get());
        doctorRepo.save(doctor);
        return "Specialization removed from the doctor";
    }

    @Transactional
    public String changeDoctorOffice(Long doctorId, int officeNumber) {
        Optional<Doctor> doctorOpt = doctorRepo.findById(doctorId);
        if (doctorOpt.isEmpty()) {
            return "The doctor with that id does not exist";
        }
        Office office = officeRepo.findByOfficeNumber(officeNumber);
        if (office == null) {
            return "The office with that office number does not exist";
        }
        Doctor doctor = doctorOpt.get();
        if (doctor.getOffice() != null && doctor.getOffice().getId().equals(office.getId())) {
            return "Office changed successfully";
        }
        if (doctorRepo.existsByOffice_IdAndIdNot(office.getId(), doctorId)) {
            return "That office is taken by other doctor";
        }
        if (doctor.getOffice() != null) {
            doctor.getOffice().setDoctor(null);
            officeRepo.save(doctor.getOffice());
        }
        doctor.setOffice(office);
        doctorRepo.save(doctor);
        return "Office changed successfully";
    }

    public List<Doctor> findDoctorsMatchingSpecializationName(String name) {
        return doctorRepo.findBySpecializationName(name);
    }

    public List<Doctor> findDoctorsMissingOffice() {
        return doctorRepo.findDoctorsWithoutOffice();
    }

    public List<Doctor> findBySpecializationName(String name) {
        return findDoctorsMatchingSpecializationName(name);
    }

    public List<Doctor> findDoctorsWithoutOffice() {
        return findDoctorsMissingOffice();
    }

    public List<Office> findFreeOffices() {
        return officeRepo.findFreeOffices();
    }

    public List<Doctor> searchDoctorsByName(String name) {
        return doctorRepo.searchByName(name);
    }

    public List<Appointment> findAgenda(Long doctorId, LocalDate date) {
        Optional<Doctor> doctorOpt = doctorRepo.findById(doctorId);
        if (doctorOpt.isEmpty()) {
            return null;
        }
        return appointmentRepo.findByDoctor_IdAndAppointmentDateAndStatusNotOrderByAppointmentDateAsc(
                doctorId, date, AppointmentStatus.CANCELLED);
    }

    public List<Object[]> findDoctorWorkload(LocalDate start, LocalDate end) {
        return doctorRepo.findDoctorWorkload(AppointmentStatus.COMPLETED, start, end);
    }
}

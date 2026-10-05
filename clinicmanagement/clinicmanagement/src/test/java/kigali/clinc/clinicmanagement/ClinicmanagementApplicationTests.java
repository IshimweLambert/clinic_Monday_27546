package kigali.clinc.clinicmanagement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import kigali.clinc.clinicmanagement.domain.Appointment;
import kigali.clinc.clinicmanagement.domain.AppointmentStatus;
import kigali.clinc.clinicmanagement.domain.Doctor;
import kigali.clinc.clinicmanagement.domain.Patient;
import kigali.clinc.clinicmanagement.repository.AppointmentRepository;
import kigali.clinc.clinicmanagement.repository.DoctorRepository;
import kigali.clinc.clinicmanagement.repository.OfficeRepository;
import kigali.clinc.clinicmanagement.repository.PatientRepository;
import kigali.clinc.clinicmanagement.repository.SpecializationRepository;

@SpringBootTest
@Transactional
class ClinicmanagementApplicationTests {

	@Autowired
	private AppointmentRepository appointmentRepository;

	@Autowired
	private PatientRepository patientRepository;

	@Autowired
	private DoctorRepository doctorRepository;

	@Autowired
	private SpecializationRepository specializationRepository;

	@Autowired
	private OfficeRepository officeRepository;

	@Test
	void contextLoads() {
	}

	@Test
	void derivedQueriesReturnExpectedRows() {
		List<Patient> uwasePatients = patientRepository.findByLastNameIgnoreCaseOrderByFirstNameAsc("uWaSe");
		assertEquals(2, uwasePatients.size());
		assertEquals(List.of("Aline", "Claudine"),
				uwasePatients.stream().map(patient -> patient.getFirstName()).toList());

		List<Appointment> scheduled = appointmentRepository
				.findByStatusOrderByAppointmentDateAsc(AppointmentStatus.SCHEDULED);
		assertEquals(3, scheduled.size());
		assertTrue(scheduled.get(0).getAppointmentDate().isBefore(scheduled.get(1).getAppointmentDate()));

		List<Appointment> octoberAppointments = appointmentRepository
				.findByAppointmentDateBetweenOrderByAppointmentDateAsc(
						LocalDate.of(2026, 10, 1), LocalDate.of(2026, 10, 31));
		assertEquals(7, octoberAppointments.size());
		assertTrue(appointmentRepository.existsByDoctor_IdAndAppointmentDateAndStatusNot(
				findDoctor("Mugisha").getId(), LocalDate.of(2026, 10, 20), AppointmentStatus.CANCELLED));
		assertFalse(appointmentRepository.existsByDoctor_IdAndAppointmentDateAndStatusNot(
				findDoctor("Mugisha").getId(), LocalDate.of(2026, 12, 1), AppointmentStatus.CANCELLED));
	}

	@Test
	void jpqlJoinQueriesReturnExpectedRows() {
		assertEquals(List.of("Mugisha"),
				doctorRepository.findBySpecializationName("cArDiOlOgY").stream()
						.map(doctor -> doctor.getLastName()).toList());
		assertEquals(List.of("Habimana"),
				doctorRepository.findDoctorsWithoutOffice().stream().map(doctor -> doctor.getLastName()).toList());
		assertEquals(List.of("Neurology"),
				specializationRepository.findUnusedSpecializations().stream()
						.map(specialization -> specialization.getName()).toList());

		List<Patient> alicePatients = patientRepository.findPatientsOfDoctor(findDoctor("Mugisha").getId());
		assertEquals(2, alicePatients.size());
		assertEquals(2, alicePatients.stream().map(patient -> patient.getId()).distinct().count());
	}

	@Test
	void aggregateAndBulkQueriesReturnExpectedResults() {
		List<Object[]> statusCounts = appointmentRepository.countAppointmentsByStatus();
		assertEquals(4, statusCounts.size());
		assertEquals(10L, statusCounts.stream().mapToLong(row -> ((Number) row[1]).longValue()).sum());

		List<Patient> frequentPatients = patientRepository.findFrequentPatients(3);
		assertEquals(List.of("Uwase"),
				frequentPatients.stream().map(patient -> patient.getLastName()).distinct().toList());

		Object[] busiestOffice = officeRepository.findBusiestOffice().get(0);
		assertEquals("Room 101", busiestOffice[0]);
		assertEquals(101, ((Number) busiestOffice[1]).intValue());
		assertEquals(5L, ((Number) busiestOffice[2]).longValue());

		int cancelled = appointmentRepository.cancelAppointmentsForDoctorOnDate(
				findDoctor("Mugisha").getId(), LocalDate.of(2026, 10, 20));
		assertEquals(2, cancelled);
	}

	@Test
	void bonusPaginationAndCleanupUseExpectedQuerySemantics() {
		Page<Appointment> page = appointmentRepository.findAll(
				PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "appointmentDate")));
		assertEquals(5, page.getContent().size());
		assertEquals(10, page.getTotalElements());
		assertEquals(2, page.getTotalPages());

		int deleted = appointmentRepository.deleteByStatusAndAppointmentDateBefore(
				AppointmentStatus.CANCELLED, LocalDate.of(2026, 10, 15));
		assertEquals(2, deleted);
	}

	private Doctor findDoctor(String lastName) {
		return doctorRepository.findAll().stream()
				.filter(doctor -> doctor.getLastName().equals(lastName))
				.findFirst()
				.orElseThrow();
	}
}

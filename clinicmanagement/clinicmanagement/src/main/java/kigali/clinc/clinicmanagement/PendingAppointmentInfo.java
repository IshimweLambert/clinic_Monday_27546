package kigali.clinc.clinicmanagement;

import java.time.LocalDate;

/** JPQL constructor result for overdue PENDING appointments (slide query 2). */
public class PendingAppointmentInfo {

    private String patientName;
    private String doctorName;
    private LocalDate date;

    public PendingAppointmentInfo(String patientName, String doctorName, LocalDate date) {
        this.patientName = patientName;
        this.doctorName = doctorName;
        this.date = date;
    }

    public String getPatientName() {
        return patientName;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public LocalDate getDate() {
        return date;
    }
}

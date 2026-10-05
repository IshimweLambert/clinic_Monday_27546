package kigali.clinc.clinicmanagement.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity 
@Table (name="office")
public class Office {
    
    @Id 
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;


    @Column (name="name")
    private String name;

    @Column (name="office_number", unique = true)
    private int officeNumber;

    @JsonIgnoreProperties({"office", "specializations", "appointments"})
    @OneToOne (mappedBy = "office")
    private Doctor doctor;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getOfficeNumber() {
        return officeNumber;
    }

    public void setOfficeNumber(int officeNumber) {
        this.officeNumber = officeNumber;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }


}

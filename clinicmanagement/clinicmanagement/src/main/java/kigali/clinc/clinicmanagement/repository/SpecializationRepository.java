package kigali.clinc.clinicmanagement.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import kigali.clinc.clinicmanagement.domain.Specialization;

@Repository
public interface SpecializationRepository extends JpaRepository<Specialization, Long> {

    Specialization findByName(String name);

    @Query("SELECT s FROM Specialization s WHERE s.doctors IS EMPTY")
    List<Specialization> findUnusedSpecializations();

    @Query("SELECT s.id, s.name, COUNT(d) "
            + "FROM Specialization s LEFT JOIN s.doctors d "
            + "GROUP BY s.id, s.name "
            + "ORDER BY COUNT(d) DESC")
    List<Object[]> countDoctorsPerSpecialization();

    default List<Specialization> findUnassignedSpecializations() {
        return findUnusedSpecializations();
    }
}

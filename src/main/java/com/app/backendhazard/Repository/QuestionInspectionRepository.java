package com.app.backendhazard.Repository;

import com.app.backendhazard.Models.AreaKerja;
import com.app.backendhazard.Models.InspectionQuestion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestionInspectionRepository extends JpaRepository<InspectionQuestion, Long> {
    List<InspectionQuestion> findByAreaKerja_Id(Long areaKerjaId);
}

package com.app.backendhazard.Repository;

import com.app.backendhazard.Models.InspectionAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnswerInspectionRepository extends JpaRepository<InspectionAnswer, Long> {
}

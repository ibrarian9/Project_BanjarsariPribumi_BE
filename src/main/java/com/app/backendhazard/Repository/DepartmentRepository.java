package com.app.backendhazard.Repository;

import com.app.backendhazard.Models.Department;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepartmentRepository extends JpaRepository<Department, Long> {
}

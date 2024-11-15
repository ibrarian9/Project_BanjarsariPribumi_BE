package com.app.backendhazard.Repository;

import com.app.backendhazard.Models.Company;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyRepository extends JpaRepository<Company, Long> {
}

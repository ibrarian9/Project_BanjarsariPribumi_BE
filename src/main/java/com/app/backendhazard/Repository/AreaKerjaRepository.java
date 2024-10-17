package com.app.backendhazard.Repository;

import com.app.backendhazard.Models.AreaKerja;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AreaKerjaRepository extends JpaRepository<AreaKerja, Long> {
}

package com.app.backendhazard.Repository;

import com.app.backendhazard.Models.Nik;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NikRepository extends JpaRepository<Nik, Long> {
    Optional<Nik> findNikByDataNik(String nik);
}

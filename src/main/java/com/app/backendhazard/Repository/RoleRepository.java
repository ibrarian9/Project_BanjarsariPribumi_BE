package com.app.backendhazard.Repository;

import com.app.backendhazard.Models.Roles;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Roles, Long> {
}

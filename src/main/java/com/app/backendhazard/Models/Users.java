package com.app.backendhazard.Models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"username"}),
        @UniqueConstraint(columnNames = {"email"}),
        @UniqueConstraint(columnNames = {"nik"})
})
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "username", length = 50, nullable = false)
    private String username;
    @Column(name = "nik", length = 50, nullable = false)
    private String nik;
    @Column(name = "email", length = 50, nullable = false)
    private String email;
    @JsonIgnore
    @Column(name = "password", length = 120, nullable = false)
    private String password;
    @ManyToOne
    @JoinColumn(name = "status_karyawan_id", referencedColumnName = "id")
    private StatusKaryawan statusKaryawan;
    @ManyToOne
    @JoinColumn(name = "role_id", referencedColumnName = "id")
    private Roles role;

    public Users(String username, String nik, String email, String password, StatusKaryawan statusKaryawan, Roles role) {
        this.username = username;
        this.nik = nik;
        this.email = email;
        this.password = password;
        this.statusKaryawan = statusKaryawan;
        this.role = role;
    }
}

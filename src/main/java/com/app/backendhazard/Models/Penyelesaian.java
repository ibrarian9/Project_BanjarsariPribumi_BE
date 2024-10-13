package com.app.backendhazard.Models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "penyelesaian")
public class Penyelesaian {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "nama_penyelesaian")
    private String namaPenyelesaian;
    @ManyToOne
    @JoinColumn(name = "department_id", referencedColumnName = "id")
    private Department department;
    @Lob
    @JsonIgnore
    @Column(name = "gambar", columnDefinition = "TEXT")
    private String gambar;
    @Transient
    @JsonProperty("linkGambar")
    public String getLinkGambar() {
        return "http://192.168.1.14:8080/api/resolutionImage/" + id;
    }
    @Transient
    public String imagePath(){
        if (gambar == null) return null;
        return "upload/resolution/" + id + "/" + gambar;
    }
}

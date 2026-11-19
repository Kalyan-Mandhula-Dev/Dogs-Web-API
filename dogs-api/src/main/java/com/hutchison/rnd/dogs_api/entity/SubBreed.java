package com.hutchison.rnd.dogs_api.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name="sub_breeds")
@Getter
@Setter
public class SubBreed {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="name")
    private String name;

    @Version
    @Column(name="version")
    private Integer version;

    @ManyToOne(fetch= FetchType.LAZY)
    @JoinColumn(name = "breed_id")
    private Breed parentBreed;

    @Column(name="created_at")
    private LocalDateTime createdAt;

    @Column(name="modified_at")
    private LocalDateTime modifiedAt;

    @PrePersist
    public void onCreate(){
        LocalDateTime currentTime = LocalDateTime.now();
        createdAt = currentTime;
        modifiedAt =  currentTime;
    }

    @PreUpdate
    public void onUpdate(){
        modifiedAt =  LocalDateTime.now();
    }
}
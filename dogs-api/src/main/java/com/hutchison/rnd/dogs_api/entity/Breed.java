package com.hutchison.rnd.dogs_api.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="breeds")
@Getter
@Setter
public class Breed {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="name")
    private String name;

    @Version
    @Column(name="version")
    private Integer version;

    @Column(name="created_at")
    private Instant createdAt;

    @Column(name="modified_at")
    private Instant modifiedAt;

    @OneToMany(mappedBy="parentBreed")
    @OnDelete(action= OnDeleteAction.CASCADE)
    private List<SubBreed> subBreeds = new ArrayList<>();

    @PrePersist
    public void onCreate(){
        Instant currentTime = Instant.now();
        createdAt = currentTime;
        modifiedAt =  currentTime;
    }

    @PreUpdate
    public void onUpdate(){
        modifiedAt =  Instant.now();
    }
}

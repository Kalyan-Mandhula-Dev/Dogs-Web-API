package com.hutchison.rnd.dogs_api.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.OptimisticLock;

import java.time.LocalDateTime;
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
    private LocalDateTime createdAt;

    @Column(name="modified_at")
    private LocalDateTime modifiedAt;

    @OneToMany(mappedBy="parentBreed")
    @OnDelete(action= OnDeleteAction.CASCADE)
    private List<SubBreed> subBreeds = new ArrayList<>();

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

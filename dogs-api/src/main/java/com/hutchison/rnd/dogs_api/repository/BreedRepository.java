package com.hutchison.rnd.dogs_api.repository;

import com.hutchison.rnd.dogs_api.entity.Breed;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BreedRepository extends JpaRepository<Breed, Long> {

    @Query("SELECT DISTINCT breed from Breed breed LEFT JOIN FETCH breed.subBreeds")
    public List<Breed> findAllWithSubBreeds();

    @Query("SELECT DISTINCT breed from Breed breed LEFT JOIN FETCH breed.subBreeds WHERE breed.id=:id")
    public Optional<Breed> findByIdWithSubBreeds(@Param("id") Long id);

    public boolean existsByName(String name);

    public List<Breed> findTop10ByIdGreaterThanOrderByIdAsc(Long lastId);

    @Query("SELECT DISTINCT breed from Breed breed LEFT JOIN FETCH breed.subBreeds where breed.id IN :ids")
    public List<Breed> findAllWithSubBreedsByIds(@Param("ids") List<Long> ids);
}

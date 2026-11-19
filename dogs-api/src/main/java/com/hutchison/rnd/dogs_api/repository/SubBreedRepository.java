package com.hutchison.rnd.dogs_api.repository;

import com.hutchison.rnd.dogs_api.entity.SubBreed;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubBreedRepository extends JpaRepository<SubBreed, Long> {

    public List<SubBreed> findTop10ByParentBreed_IdAndIdGreaterThanOrderByIdAsc(Long breedId, Long lastId);
}

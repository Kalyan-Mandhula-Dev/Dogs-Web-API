package com.hutchison.rnd.dogs_api.service;

import com.hutchison.rnd.dogs_api.dto.request.CreateSubBreedRequest;
import com.hutchison.rnd.dogs_api.dto.request.UpdateSubBreedRequest;
import com.hutchison.rnd.dogs_api.dto.response.SubBreedResponse;
import com.hutchison.rnd.dogs_api.entity.Breed;
import com.hutchison.rnd.dogs_api.entity.SubBreed;
import com.hutchison.rnd.dogs_api.exception.*;
import com.hutchison.rnd.dogs_api.repository.BreedRepository;
import com.hutchison.rnd.dogs_api.repository.SubBreedRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class SubBreedService {

    private final SubBreedRepository subBreedRepository;
    private final BreedRepository breedRepository;

    @Transactional(readOnly = true)
    public List<SubBreedResponse> getAllSubBreeds(Long breedId) {
        Breed breed = findBreedById(breedId);
        return breed.getSubBreeds().stream().map(this::mapToResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<SubBreedResponse> getSubBreeds(Long breedId, Long lastId) {
        List<SubBreed> subBreeds = subBreedRepository.findTop10ByParentBreed_IdAndIdGreaterThanOrderByIdAsc(breedId, lastId);
        return subBreeds.stream().map(this::mapToResponse).toList();
    }

    private SubBreed findSubBreedById(Long id) {
        return subBreedRepository.findById(id).orElseThrow(SubBreedNotFoundException::new);
    }

    private Breed findBreedById(Long breedId) {
        return breedRepository.findById(breedId).orElseThrow(BreedNotFoundException::new);
    }

    private SubBreed findSubBreedForBreed(Long breedId, Long id) {
        SubBreed subBreed = findSubBreedById(id);
        if (!breedId.equals(subBreed.getParentBreed().getId())) {
            throw new SubBreedNotFoundUnderBreedException();
        }
        return subBreed;
    }

    @Transactional(readOnly = true)
    public SubBreedResponse getSubBreedById(Long breedId, Long id) {
        SubBreed subBreed = findSubBreedForBreed(breedId, id);
        return mapToResponse(subBreed);
    }

    @Transactional
    public SubBreedResponse createSubBreed(Long breedId, CreateSubBreedRequest createSubBreedRequest) {

        Breed breed = findBreedById(breedId);

        SubBreed subBreed = new SubBreed();
        subBreed.setName(createSubBreedRequest.getName());
        subBreed.setParentBreed(breed);
        subBreedRepository.saveAndFlush(subBreed);
        return mapToResponse(subBreed);
    }

    @Transactional
    public List<SubBreed> createSubBreeds(Long breedId, List<String> subBreedsNames) {

        Breed breed = findBreedById(breedId);

        long distinctSubBreedsCount = subBreedsNames.stream().map(String::toLowerCase).distinct().count();

        if (distinctSubBreedsCount != subBreedsNames.size()) {
            throw new DuplicateSubBreedsFoundException();
        }

        List<SubBreed> subBreedsToSave = new ArrayList<>();
        for (String name : subBreedsNames) {
            SubBreed subBreed = new SubBreed();
            subBreed.setName(name);
            subBreed.setParentBreed(breed);
            subBreedsToSave.add(subBreed);
        }

        return subBreedRepository.saveAllAndFlush(subBreedsToSave);
    }

    @Transactional
    public SubBreedResponse updateSubBreed(Long breedId, Long id, UpdateSubBreedRequest updateSubBreedRequest) {

        SubBreed subBreed = findSubBreedForBreed(breedId, id);

        if (!Objects.equals(subBreed.getVersion(), updateSubBreedRequest.getVersion())) {
            throw new SubBreedVersionConflictException();
        }

        subBreed.setName(updateSubBreedRequest.getName());
        subBreedRepository.saveAndFlush(subBreed);
        return mapToResponse(subBreed);
    }

    @Transactional
    public void deleteSubBreed(Long breedId, Long id, Integer version) {
        SubBreed subBreed = findSubBreedForBreed(breedId, id);

        if (!Objects.equals(subBreed.getVersion(), version)) {
            throw new SubBreedVersionConflictException();
        }

        subBreedRepository.delete(subBreed);
    }

    private SubBreedResponse mapToResponse(SubBreed subBreed) {
        SubBreedResponse subBreedResponse = new SubBreedResponse();
        subBreedResponse.setId(subBreed.getId());
        subBreedResponse.setName(subBreed.getName());
        subBreedResponse.setVersion(subBreed.getVersion());
        subBreedResponse.setParentBreedId(subBreed.getParentBreed().getId());
        subBreedResponse.setParentBreedName(subBreed.getParentBreed().getName());
        subBreedResponse.setCreatedAt(subBreed.getCreatedAt());
        subBreedResponse.setModifiedAt(subBreed.getModifiedAt());
        return subBreedResponse;
    }
}

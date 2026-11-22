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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
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
        findBreedById(breedId);
        List<SubBreed> subBreeds = subBreedRepository.findTop10ByParentBreed_IdAndIdGreaterThanOrderByIdAsc(breedId, lastId);
        log.debug("Fetched {} sub-breeds for breed id={} after lastId={}", subBreeds.size(), breedId, lastId);
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
        log.info("Created sub-breed id={} name={} for breed id={}", subBreed.getId(), subBreed.getName(), breedId);
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

        List<SubBreed> saved = subBreedRepository.saveAllAndFlush(subBreedsToSave);
        log.info("Created {} sub-breeds for breed id={}", saved.size(), breedId);
        return saved;
    }

    @Transactional
    public SubBreedResponse updateSubBreed(Long breedId, Long id, UpdateSubBreedRequest updateSubBreedRequest) {

        SubBreed subBreed = findSubBreedForBreed(breedId, id);

        if (!Objects.equals(subBreed.getVersion(), updateSubBreedRequest.getVersion())) {
            throw new SubBreedVersionConflictException();
        }

        subBreed.setName(updateSubBreedRequest.getName());
        subBreedRepository.saveAndFlush(subBreed);
        log.info("Updated sub-breed id={} name={} for breed id={}", subBreed.getId(), subBreed.getName(), breedId);
        return mapToResponse(subBreed);
    }

    @Transactional
    public void deleteSubBreed(Long breedId, Long id, Integer version) {
        SubBreed subBreed = findSubBreedForBreed(breedId, id);

        if (!Objects.equals(subBreed.getVersion(), version)) {
            throw new SubBreedVersionConflictException();
        }

        subBreedRepository.delete(subBreed);
        log.info("Deleted sub-breed id={} name={} from breed id={}", subBreed.getId(), subBreed.getName(), breedId);
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

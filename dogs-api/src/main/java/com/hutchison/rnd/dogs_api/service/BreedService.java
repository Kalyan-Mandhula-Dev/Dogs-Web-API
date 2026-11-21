package com.hutchison.rnd.dogs_api.service;

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.json.JsonMapper;
import com.hutchison.rnd.dogs_api.dto.request.CreateBreedRequest;
import com.hutchison.rnd.dogs_api.dto.request.UpdateBreedRequest;
import com.hutchison.rnd.dogs_api.dto.response.BreedResponse;
import com.hutchison.rnd.dogs_api.entity.Breed;
import com.hutchison.rnd.dogs_api.entity.SubBreed;
import com.hutchison.rnd.dogs_api.exception.BreedNotFoundException;
import com.hutchison.rnd.dogs_api.exception.BreedVersionConflictException;
import com.hutchison.rnd.dogs_api.exception.InvalidImportFileException;
import com.hutchison.rnd.dogs_api.repository.BreedRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class BreedService {

    private final BreedRepository breedRepository;
    private final SubBreedService subBreedService;
    private final JsonMapper objMapper;

    public List<BreedResponse> getAllBreeds() {
        List<Breed> breeds = breedRepository.findAllWithSubBreeds();
        return breeds.stream().map(this::mapToResponse).toList();
    }

    public BreedResponse getBreedById(Long id) {
        Breed breed = breedRepository.findByIdWithSubBreeds(id).orElseThrow(BreedNotFoundException::new);
        return mapToResponse(breed);
    }

    @Transactional(readOnly = true)
    public List<BreedResponse> getBreeds(Long lastId) {
        List<Breed> breeds = breedRepository.findTop10ByIdGreaterThanOrderByIdAsc(lastId);
        List<Long> breedIds = breeds.stream().map(Breed::getId).toList();
        breedRepository.findAllWithSubBreedsByIds(breedIds);
        return breeds.stream().map(this::mapToResponse).toList();
    }

    @Transactional
    public BreedResponse createBreed(CreateBreedRequest createBreedRequest) {
        Breed breed = new Breed();
        breed.setName(createBreedRequest.getName());
        breedRepository.saveAndFlush(breed);

        List<SubBreed> subBreeds = new ArrayList<>();
        if (createBreedRequest.getSubBreeds() != null && !createBreedRequest.getSubBreeds().isEmpty()) {
            subBreeds = subBreedService.createSubBreeds(breed.getId(), createBreedRequest.getSubBreeds());
        }

        BreedResponse breedResponse = mapToResponse(breed);
        breedResponse.setSubBreeds(subBreeds.stream().map(SubBreed::getName).toList());
        return breedResponse;
    }

    @Transactional
    public BreedResponse updateBreed(Long id, UpdateBreedRequest updateBreedRequest) {
        Breed breed = breedRepository.findByIdWithSubBreeds(id).orElseThrow(BreedNotFoundException::new);

        if (!Objects.equals(breed.getVersion(), updateBreedRequest.getVersion())) {
            throw new BreedVersionConflictException();
        }

        breed.setName(updateBreedRequest.getName());
        breedRepository.saveAndFlush(breed);
        return mapToResponse(breed);
    }

    @Transactional
    public void deleteBreed(Long id, Integer version) {
        Breed breed = breedRepository.findById(id).orElseThrow(BreedNotFoundException::new);

        if (!Objects.equals(breed.getVersion(), version)) {
            throw new BreedVersionConflictException();
        }

        breedRepository.delete(breed);
    }

    @Transactional
    public List<BreedResponse> importBreeds(MultipartFile file) {
        try {
            Map<String, List<String>> breeds = objMapper.readValue(
                    file.getInputStream(),
                    new TypeReference<Map<String, List<String>>>() {
                    }
            );

            return breeds.entrySet().stream().map((entry) -> {

                String name = entry.getKey();
                List<String> subBreeds = entry.getValue();

                if (name == null || name.isBlank()) {
                    throw new InvalidImportFileException("Breed name cannot be blank");
                }

                if (name.length() > 100) {
                    throw new InvalidImportFileException("Breed name cannot exceed 100 characters");
                }

                if (subBreeds != null) {

                    for (String sub : subBreeds) {
                        if (sub == null || sub.isBlank()) {
                            throw new InvalidImportFileException("Sub-breed names cannot be blank");
                        }

                        if (sub.length() > 100) {
                            throw new InvalidImportFileException("Sub-breed name cannot exceed 100 characters");
                        }
                    }
                }

                CreateBreedRequest request = new CreateBreedRequest();
                request.setName(name);
                request.setSubBreeds(subBreeds);

                return createBreed(request);
            }).toList();
        } catch (IOException ex) {
            throw new InvalidImportFileException("Invalid JSON file");
        }
    }


    private BreedResponse mapToResponse(Breed breed) {
        BreedResponse breedResponse = new BreedResponse();
        breedResponse.setId(breed.getId());
        breedResponse.setName(breed.getName());
        breedResponse.setVersion(breed.getVersion());
        breedResponse.setCreatedAt(breed.getCreatedAt());
        breedResponse.setModifiedAt(breed.getModifiedAt());
        if (breed.getSubBreeds() != null) {
            breedResponse.setSubBreeds(breed.getSubBreeds().stream().map(SubBreed::getName).toList());
        }
        return breedResponse;
    }
}


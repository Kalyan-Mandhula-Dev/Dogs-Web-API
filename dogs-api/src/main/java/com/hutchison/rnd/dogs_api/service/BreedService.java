package com.hutchison.rnd.dogs_api.service;

import tools.jackson.core.JacksonException;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.json.JsonMapper;
import com.hutchison.rnd.dogs_api.dto.request.CreateBreedRequest;
import com.hutchison.rnd.dogs_api.dto.request.UpdateBreedRequest;
import com.hutchison.rnd.dogs_api.dto.response.BreedResponse;
import com.hutchison.rnd.dogs_api.dto.response.ImportBreedsResponse;
import com.hutchison.rnd.dogs_api.entity.Breed;
import com.hutchison.rnd.dogs_api.entity.SubBreed;
import com.hutchison.rnd.dogs_api.exception.BreedNotFoundException;
import com.hutchison.rnd.dogs_api.exception.BreedVersionConflictException;
import com.hutchison.rnd.dogs_api.exception.InvalidImportFileException;
import com.hutchison.rnd.dogs_api.repository.BreedRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
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
        log.debug("Fetched {} breeds after lastId={}", breeds.size(), lastId);
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

        log.info("Created breed id={} name={}", breed.getId(), breed.getName());

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
        log.info("Updated breed id={} name={}", breed.getId(), breed.getName());
        return mapToResponse(breed);
    }

    @Transactional
    public void deleteBreed(Long id, Integer version) {
        Breed breed = breedRepository.findById(id).orElseThrow(BreedNotFoundException::new);

        if (!Objects.equals(breed.getVersion(), version)) {
            throw new BreedVersionConflictException();
        }

        breedRepository.delete(breed);
        log.info("Deleted breed id={} name={}", breed.getId(), breed.getName());
    }

    @Transactional
    public ImportBreedsResponse importBreeds(MultipartFile file) {
        List<CreateBreedRequest> requests = validateImportFile(readImportFile(file));

        int imported = 0;
        List<String> skippedBreeds = new ArrayList<>();

        for (CreateBreedRequest request : requests) {
            if (breedRepository.existsByNameIgnoreCase(request.getName())) {
                skippedBreeds.add(request.getName());
            } else {
                createBreed(request);
                imported++;
            }
        }

        log.info("Imported {} breeds, skipped {} existing breeds",
                imported, skippedBreeds.size());
        return new ImportBreedsResponse(imported, skippedBreeds.size(), skippedBreeds);
    }

    private Map<String, List<String>> readImportFile(MultipartFile file) {
        try {
            return objMapper.readValue(
                    file.getInputStream(),
                    new TypeReference<Map<String, List<String>>>() {
                    }
            );
        } catch (IOException | JacksonException ex) {
            throw new InvalidImportFileException("Invalid JSON file");
        }
    }

    private List<CreateBreedRequest> validateImportFile(Map<String, List<String>> breeds) {
        if (breeds == null || breeds.isEmpty()) {
            throw new InvalidImportFileException("File contains no breeds");
        }

        List<CreateBreedRequest> requests = new ArrayList<>();
        Set<String> seenNames = new HashSet<>();

        for (Map.Entry<String, List<String>> entry : breeds.entrySet()) {
            String name = entry.getKey() == null ? null : entry.getKey().trim();

            if (name == null || name.isBlank()) {
                throw new InvalidImportFileException("Breed name cannot be blank");
            }
            if (name.length() > 100) {
                throw new InvalidImportFileException("Breed name cannot exceed 100 characters: " + name);
            }
            if (!seenNames.add(name.toLowerCase())) {
                throw new InvalidImportFileException("Duplicate breed in file: " + name);
            }

            List<String> subBreeds = new ArrayList<>();
            Set<String> seenSubBreeds = new HashSet<>();

            if (entry.getValue() != null) {
                for (String sub : entry.getValue()) {
                    sub = sub == null ? null : sub.trim();

                    if (sub == null || sub.isBlank()) {
                        throw new InvalidImportFileException("Sub-breed names cannot be blank in breed: " + name);
                    }
                    if (sub.length() > 100) {
                        throw new InvalidImportFileException("Sub-breed name cannot exceed 100 characters in breed: " + name);
                    }
                    if (!seenSubBreeds.add(sub.toLowerCase())) {
                        throw new InvalidImportFileException("Duplicate sub-breed '" + sub + "' in breed: " + name);
                    }
                    subBreeds.add(sub);
                }
            }

            CreateBreedRequest request = new CreateBreedRequest();
            request.setName(name);
            request.setSubBreeds(subBreeds);
            requests.add(request);
        }

        return requests;
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


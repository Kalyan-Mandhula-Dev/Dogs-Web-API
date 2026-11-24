package com.hutchison.rnd.dogs_api.controller;

import com.hutchison.rnd.dogs_api.dto.request.CreateBreedRequest;
import com.hutchison.rnd.dogs_api.dto.request.UpdateBreedRequest;
import com.hutchison.rnd.dogs_api.dto.response.BreedResponse;
import com.hutchison.rnd.dogs_api.dto.response.ImportBreedsResponse;
import com.hutchison.rnd.dogs_api.service.BreedService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/breeds")
@RequiredArgsConstructor
public class BreedController {

    private final BreedService breedService;

    @GetMapping("/{id}")
    public ResponseEntity<BreedResponse> getBreedById(@PathVariable("id") Long id){
        BreedResponse breed = breedService.getBreedById(id);
        return ResponseEntity.ok().body(breed);
    }

    @GetMapping
    public List<BreedResponse> getBreeds(@RequestParam(defaultValue = "0") Long lastId) {
        return breedService.getBreeds(lastId);
    }

    @PostMapping
    public ResponseEntity<BreedResponse> createBreed(@Valid @RequestBody CreateBreedRequest createBreedRequest){
        BreedResponse createdBreed = breedService.createBreed(createBreedRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdBreed);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BreedResponse> updateBreed(@PathVariable("id") Long id, @Valid @RequestBody UpdateBreedRequest updateBreedRequest){
        BreedResponse updatedBreed = breedService.updateBreed(id, updateBreedRequest);
        return ResponseEntity.status(HttpStatus.OK).body(updatedBreed);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBreed(@PathVariable("id") Long id, @RequestParam("version") Integer version){
        breedService.deleteBreed(id, version);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ImportBreedsResponse> importBreeds(@RequestPart("file") MultipartFile file){
        ImportBreedsResponse summary = breedService.importBreeds(file);
        return ResponseEntity.ok(summary);
    }
}

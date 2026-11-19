package com.hutchison.rnd.dogs_api.controller;

import com.hutchison.rnd.dogs_api.dto.request.CreateBreedRequest;
import com.hutchison.rnd.dogs_api.dto.request.CreateSubBreedRequest;
import com.hutchison.rnd.dogs_api.dto.request.UpdateBreedRequest;
import com.hutchison.rnd.dogs_api.dto.request.UpdateSubBreedRequest;
import com.hutchison.rnd.dogs_api.dto.response.BreedResponse;
import com.hutchison.rnd.dogs_api.dto.response.SubBreedResponse;
import com.hutchison.rnd.dogs_api.service.SubBreedService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/breeds/{breedId}/subbreeds")
@RequiredArgsConstructor
public class SubBreedController {

    private final SubBreedService subBreedService;

//    @GetMapping
//    public ResponseEntity<List<SubBreedResponse>> getAllSubBreeds(@PathVariable("breedId") Long breedId){
//        List<SubBreedResponse> subBreeds = subBreedService.getAllSubBreeds(breedId);
//        return ResponseEntity.ok().body(subBreeds);
//    }

    @GetMapping
    public ResponseEntity<List<SubBreedResponse>> getSubBreeds(@PathVariable("breedId") Long breedId, @RequestParam("lastId") Long lastId){
        List<SubBreedResponse> subBreeds = subBreedService.getSubBreeds(breedId, lastId);
        return ResponseEntity.ok().body(subBreeds);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SubBreedResponse> getSubBreedById(@PathVariable("breedId") Long breedId, @PathVariable("id") Long id){
        SubBreedResponse subBreed = subBreedService.getSubBreedById(breedId, id);
        return ResponseEntity.ok().body(subBreed);
    }

    @PostMapping
    public ResponseEntity<SubBreedResponse> createSubBreed(@PathVariable("breedId") Long breedId, @Valid @RequestBody CreateSubBreedRequest createSubBreedRequest){
        SubBreedResponse createdSubBreed = subBreedService.createSubBreed(breedId, createSubBreedRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdSubBreed);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SubBreedResponse> updateSubBreed(@PathVariable("breedId") Long breedId, @PathVariable("id") Long id, @Valid @RequestBody UpdateSubBreedRequest updateSubBreedRequest){
        SubBreedResponse updatedSubBreed = subBreedService.updateSubBreed(breedId, id, updateSubBreedRequest);
        return ResponseEntity.status(HttpStatus.OK).body(updatedSubBreed);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSubBreed(@PathVariable("breedId") Long breedId, @PathVariable("id") Long id){
        subBreedService.deleteSubBreed(breedId, id);
        return ResponseEntity.noContent().build();
    }
}

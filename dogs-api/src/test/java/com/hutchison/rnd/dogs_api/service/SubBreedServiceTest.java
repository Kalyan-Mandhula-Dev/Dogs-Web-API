package com.hutchison.rnd.dogs_api.service;

import com.hutchison.rnd.dogs_api.dto.request.CreateSubBreedRequest;
import com.hutchison.rnd.dogs_api.dto.request.UpdateSubBreedRequest;
import com.hutchison.rnd.dogs_api.dto.response.SubBreedResponse;
import com.hutchison.rnd.dogs_api.entity.Breed;
import com.hutchison.rnd.dogs_api.entity.SubBreed;
import com.hutchison.rnd.dogs_api.exception.*;
import com.hutchison.rnd.dogs_api.repository.BreedRepository;
import com.hutchison.rnd.dogs_api.repository.SubBreedRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SubBreedServiceTest {

    @Mock
    private SubBreedRepository subBreedRepository;

    @Mock
    private BreedRepository breedRepository;

    @InjectMocks
    private SubBreedService subBreedService;


    @Test
    void createSubBreed_shouldThrowExcepitonWhenBreedNotFound(){
        CreateSubBreedRequest createSubBreedRequest = new CreateSubBreedRequest();
        createSubBreedRequest.setName("american");
        when(breedRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(BreedNotFoundException.class , () -> subBreedService.createSubBreed(1L, createSubBreedRequest));
        verifyNoInteractions(subBreedRepository);
    }

    @Test
    void createSubBreed_shouldCreateSubBreedSuccessfully(){

        CreateSubBreedRequest createSubBreedRequest = new CreateSubBreedRequest();
        createSubBreedRequest.setName("American");

        Breed breed = new Breed();
        breed.setName("Corgi");
        breed.setId(1L);

        when(breedRepository.findById(1L)).thenReturn(Optional.of(breed));
        SubBreedResponse result = subBreedService.createSubBreed(1L, createSubBreedRequest);

        assertAll("subBreed",
                () -> assertNotNull(result),
                () -> assertEquals("American", result.getName()),
                () -> assertEquals("Corgi", result.getParentBreedName())
        );

        verify(subBreedRepository, times(1)).saveAndFlush(any(SubBreed.class));
    }

    @Test
    void createSubBreeds_shouldThrowExceptionOnDuplicateSubBreedNames(){
        List<String> subBreeds = List.of("American", "Australian", "american");

        Breed breed = new Breed();
        breed.setName("Corgi");
        breed.setId(1L);

        when(breedRepository.findById(1L)).thenReturn(Optional.of(breed));

        assertThrows(DuplicateSubBreedsFoundException.class, () -> subBreedService.createSubBreeds(1L, subBreeds));
        verifyNoInteractions(subBreedRepository);
    }

    @Test
    void createSubBreeds_shouldCreateSubBreedsOnValidNames(){
        List<String> subBreeds = List.of("American", "Australian", "European");

        Breed breed = new Breed();
        breed.setName("Corgi");
        breed.setId(1L);

        when(breedRepository.findById(1L)).thenReturn(Optional.of(breed));
        when(subBreedRepository.saveAllAndFlush(anyList())).thenAnswer(invocation -> {
            return invocation.getArgument(0);
        });

        List<SubBreed> savedSubBreeds = subBreedService.createSubBreeds(1L, subBreeds);

        assertEquals(3, savedSubBreeds.size());

        assertAll(
                () -> assertEquals("American", savedSubBreeds.get(0).getName()),
                () -> assertEquals("Australian", savedSubBreeds.get(1).getName()),
                () -> assertEquals("European", savedSubBreeds.get(2).getName())
        );

        verify(subBreedRepository, times(1)).saveAllAndFlush(anyList());
    }


    @Test
    void updateSubBreed_shouldThrowExceptionWhenSubBreedNotFound(){
        when(subBreedRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(SubBreedNotFoundException.class, ()-> subBreedService.updateSubBreed(1L, 1L, new UpdateSubBreedRequest()));
        verify(subBreedRepository, never()).saveAndFlush(any(SubBreed.class));
    }

    @Test
    void updateSubBreed_shouldThrowExceptionWhenSubBreedParentIdDoesNotMatch(){
        Breed breed = new Breed();
        breed.setName("Corgi");
        breed.setId(2L);

        SubBreed subBreed = new SubBreed();
        subBreed.setName("American");
        subBreed.setId(1L);
        subBreed.setParentBreed(breed);

        when(subBreedRepository.findById(1L)).thenReturn(Optional.of(subBreed));
        assertThrows(SubBreedNotFoundUnderBreedException.class, ()-> subBreedService.updateSubBreed(3L, 1L, new UpdateSubBreedRequest()));
        verify(subBreedRepository, never()).saveAndFlush(any(SubBreed.class));
    }

    @Test
    void updateSubBreed_shouldThrowExceptionWhenSubBreedVersionsMissMatch(){
        Breed breed = new Breed();
        breed.setName("Corgi");
        breed.setId(2L);

        SubBreed subBreed = new SubBreed();
        subBreed.setName("American");
        subBreed.setId(1L);
        subBreed.setVersion(1);
        subBreed.setParentBreed(breed);

        UpdateSubBreedRequest updateSubBreedRequest = new UpdateSubBreedRequest();
        updateSubBreedRequest.setName("European");
        updateSubBreedRequest.setVersion(0);

        when(subBreedRepository.findById(1L)).thenReturn(Optional.of(subBreed));
        assertThrows(SubBreedVersionConflictException.class, ()-> subBreedService.updateSubBreed(2L, 1L, updateSubBreedRequest));
        verify(subBreedRepository, never()).saveAndFlush(any(SubBreed.class));
    }

    @Test
    void updateSubBreed_shouldUpdateBreedSuccessfully(){
        Breed breed = new Breed();
        breed.setName("Corgi");
        breed.setId(2L);

        SubBreed subBreed = new SubBreed();
        subBreed.setName("American");
        subBreed.setId(1L);
        subBreed.setVersion(1);
        subBreed.setParentBreed(breed);

        UpdateSubBreedRequest updateSubBreedRequest = new UpdateSubBreedRequest();
        updateSubBreedRequest.setName("European");
        updateSubBreedRequest.setVersion(1);

        when(subBreedRepository.findById(1L)).thenReturn(Optional.of(subBreed));
        when(subBreedRepository.saveAndFlush(subBreed)).thenAnswer(invocation -> {
            SubBreed subbreed = invocation.getArgument(0);
            subbreed.setName(updateSubBreedRequest.getName());
            return subbreed;
        });

        SubBreedResponse result = subBreedService.updateSubBreed(2L, 1L, updateSubBreedRequest);
        assertEquals("European", result.getName());
        verify(subBreedRepository, times(1)).saveAndFlush(any(SubBreed.class));
    }

    @Test
    void deleteSubBreed_shouldDeleteSubBreedSuccessfully() {

        Breed breed = new Breed();
        breed.setId(1L);
        breed.setName("Corgi");

        SubBreed subBreed = new SubBreed();
        subBreed.setId(2L);
        subBreed.setName("American");
        subBreed.setParentBreed(breed);
        subBreed.setVersion(0);

        when(subBreedRepository.findById(2L)).thenReturn(Optional.of(subBreed));
        subBreedService.deleteSubBreed(1L, 2L, 0);
        verify(subBreedRepository, times(1)).delete(subBreed);
    }

    @Test
    void deleteSubBreed_shouldThrowExceptionWhenSubBreedNotFound() {

        when(subBreedRepository.findById(2L)).thenReturn(Optional.empty());
        assertThrows(SubBreedNotFoundException.class, () -> subBreedService.deleteSubBreed(1L, 2L, 0));
        verify(subBreedRepository, never()).delete(any(SubBreed.class));
    }

}

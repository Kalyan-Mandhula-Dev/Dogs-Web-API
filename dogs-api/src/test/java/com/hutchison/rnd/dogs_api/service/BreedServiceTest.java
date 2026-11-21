package com.hutchison.rnd.dogs_api.service;

import com.hutchison.rnd.dogs_api.dto.request.CreateBreedRequest;
import com.hutchison.rnd.dogs_api.dto.request.UpdateBreedRequest;
import com.hutchison.rnd.dogs_api.dto.response.BreedResponse;
import com.hutchison.rnd.dogs_api.entity.Breed;
import com.hutchison.rnd.dogs_api.entity.SubBreed;
import com.hutchison.rnd.dogs_api.exception.BreedNotFoundException;
import com.hutchison.rnd.dogs_api.exception.BreedVersionConflictException;
import com.hutchison.rnd.dogs_api.repository.BreedRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BreedServiceTest {

    @Mock
    private BreedRepository breedRepository;

    @Mock
    private SubBreedService subBreedService;

    @InjectMocks
    private BreedService breedService;

    @Test
    void createBreed_shouldCreateBreedWithoutSubBreeds(){

        CreateBreedRequest createBreedRequest = new CreateBreedRequest();
        createBreedRequest.setName("Corgi");
        createBreedRequest.setSubBreeds(List.of());

        when(breedRepository.saveAndFlush(any(Breed.class))).thenAnswer(invocation -> {
            Breed breed = invocation.getArgument(0);
            breed.setVersion(1);
            return breed;
        });

        BreedResponse createdBreed = breedService.createBreed(createBreedRequest);
        assertAll("breed",
                ()-> assertNotNull(createdBreed),
                ()-> assertEquals("Corgi", createdBreed.getName()),
                ()-> assertEquals(1, createdBreed.getVersion())
        );
        verifyNoInteractions(subBreedService);
    }

    @Test
    void createBreed_shouldCreateBreedWithSubBreeds(){

        CreateBreedRequest createBreedRequest = new CreateBreedRequest();
        createBreedRequest.setName("Corgi");
        createBreedRequest.setSubBreeds(List.of("Corgi-1", "Corgi-2"));

        when(breedRepository.saveAndFlush(any(Breed.class))).thenAnswer(invocation -> {
            Breed breed = invocation.getArgument(0);
            breed.setId(1L);
            breed.setVersion(1);
            return breed;
        });

        when(subBreedService.createSubBreeds(1L, createBreedRequest.getSubBreeds())).thenAnswer(invocation -> {
            List<String> subBreedsNames = invocation.getArgument(1);
            SubBreed subBreed1 = new SubBreed();
            subBreed1.setName(subBreedsNames.get(0));

            SubBreed subBreed2 = new SubBreed();
            subBreed2.setName(subBreedsNames.get(1));

            return List.of(subBreed1, subBreed2);
        });

        BreedResponse createdBreed = breedService.createBreed(createBreedRequest);
        verify(subBreedService, times(1)).createSubBreeds(1L, createBreedRequest.getSubBreeds());
        assertAll("breed",
                ()-> assertNotNull(createdBreed),
                ()-> assertEquals("Corgi", createdBreed.getName()),
                ()-> assertEquals(1, createdBreed.getVersion()),
                ()-> assertEquals(2, createdBreed.getSubBreeds().size())
        );
    }


    @Test
    void updateBreed_shouldThrowExceptionWhenBreedNotFound(){
        when(breedRepository.findByIdWithSubBreeds(1L)).thenReturn(Optional.empty());
        assertThrows(BreedNotFoundException.class, () -> breedService.updateBreed(1L, new UpdateBreedRequest()));
    }

    @Test
    void updateBreed_shouldThrowExceptionWhenBreedVersionMissMatch(){

        Breed breed = new Breed();
        breed.setId(1L);
        breed.setName("Corgi");
        breed.setVersion(1);

        UpdateBreedRequest updateBreedRequest = new UpdateBreedRequest();
        updateBreedRequest.setName("Labrador");
        updateBreedRequest.setVersion(0);

        when(breedRepository.findByIdWithSubBreeds(1L)).thenReturn(Optional.of(breed));
        assertThrows(BreedVersionConflictException.class, ()-> breedService.updateBreed(1L, updateBreedRequest));
        verify(breedRepository, never()).saveAndFlush(any(Breed.class));
    }

    @Test
    void updateBreed_shouldUpdateBreedSuccessfully(){

        Breed breed = new Breed();
        breed.setId(1L);
        breed.setName("Corgi");
        breed.setVersion(1);

        UpdateBreedRequest updateBreedRequest = new UpdateBreedRequest();
        updateBreedRequest.setName("Labrador");
        updateBreedRequest.setVersion(1);

        when(breedRepository.findByIdWithSubBreeds(1L)).thenReturn(Optional.of(breed));
        when(breedRepository.saveAndFlush(any(Breed.class))).thenAnswer(invocation -> {
            Breed updatedBreed = invocation.getArgument(0);
            updatedBreed.setVersion(2);
            return updatedBreed;
        });

        BreedResponse result = breedService.updateBreed(1L, updateBreedRequest);
        assertAll("breed",
                ()-> assertNotNull(result),
                ()-> assertEquals("Labrador", result.getName()),
                ()-> assertEquals(2, result.getVersion())
        );
    }

    @Test
    void deleteBreed_shouldThrowExceptionWhenBreedNotFound() {
        when(breedRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(BreedNotFoundException.class, () -> breedService.deleteBreed(1L, 0));
        verify(breedRepository, never()).delete(any(Breed.class));
    }

    @Test
    void deleteBreed_shouldDeleteBreedSuccessfully() {
        Breed breed = new Breed();
        breed.setId(1L);
        breed.setVersion(0);

        when(breedRepository.findById(1L)).thenReturn(Optional.of(breed));
        breedService.deleteBreed(1L, 0);
        verify(breedRepository, times(1)).delete(breed);
    }
}

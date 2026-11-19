package com.hutchison.rnd.dogs_api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CreateBreedRequest {

    @NotBlank
    @Size(max = 100)
    private String name;
    private List<@NotBlank String> subBreeds;
}

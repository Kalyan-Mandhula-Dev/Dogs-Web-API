package com.hutchison.rnd.dogs_api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateSubBreedRequest {
    @NotBlank
    @Size(max = 100)
    private String name;
}

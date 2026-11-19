package com.hutchison.rnd.dogs_api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateBreedRequest {

    @NotBlank
    @Size(max = 100)
    private String name;

    @NotNull
    private Integer version;
}

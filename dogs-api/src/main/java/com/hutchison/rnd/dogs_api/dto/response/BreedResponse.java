package com.hutchison.rnd.dogs_api.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class BreedResponse {
    private Long id;
    private String name;
    private Integer version;
    private List<String> subBreeds;
    private Instant createdAt;
    private Instant modifiedAt;
}

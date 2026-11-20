package com.hutchison.rnd.dogs_api.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;


@Getter
@Setter
@NoArgsConstructor
public class SubBreedResponse {
    private Long id;
    private String name;
    private Integer version;
    private Long parentBreedId;
    private String parentBreedName;
    private Instant createdAt;
    private Instant modifiedAt;
}

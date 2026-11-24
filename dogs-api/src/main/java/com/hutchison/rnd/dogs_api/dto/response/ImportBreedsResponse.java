package com.hutchison.rnd.dogs_api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ImportBreedsResponse {
    private int imported;
    private int skipped;
    private List<String> skippedBreeds;
}

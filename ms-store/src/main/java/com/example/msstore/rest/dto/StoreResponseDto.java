package com.example.msstore.rest.dto;

import com.example.msstore.domain.entities.Store;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class StoreResponseDto implements Serializable {

    private String name;
    private String bio;
    private String totalRating;

    public StoreResponseDto(Store store){
        name = store.getName();
        bio = store.getBio();
        totalRating = String.format("%.1f",store.getAverageRating());
    }
}

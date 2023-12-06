package com.example.msstore.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ReviewRequestDto implements Serializable {

    @NotNull(message = "insert a rating to establishment")
    private Integer rating;
    @NotBlank(message = "insert a comment to establishment")
    private String comment;

}

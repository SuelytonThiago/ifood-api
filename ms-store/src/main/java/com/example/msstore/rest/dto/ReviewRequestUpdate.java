package com.example.msstore.rest.dto;

import com.example.msstore.rest.dto.validation.NoSpaces;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ReviewRequestUpdate implements Serializable {


    private Integer rating;
    @NoSpaces(message = "this comment cannot be only spaces")
    private String comment;
}

package com.example.msstore.rest.dto;

import com.example.msstore.domain.entities.Categories;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDto implements Serializable {

    @NotBlank(message = "The categoryName cannot be empty or null")
    private String name;

    public CategoryDto(Categories category){
        name = category.getName();
    }
}

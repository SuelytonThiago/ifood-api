package com.example.msstore.domain.entities;

import com.example.msstore.rest.dto.ReviewRequestDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;


@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
public class Review implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long reviewerId;
    @Min(1)
    @Max(5)
    private Integer rating;
    private String comment;
    private LocalDate date;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "reviewRestaurant_id")
    private Store reviewStore;


    public Review(ReviewRequestDto dto, Long userId,Store store){
        reviewerId = userId;
        rating = dto.getRating();
        date = LocalDate.now();
        comment = dto.getComment();
        reviewStore = store;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Review review)) return false;
        return getId().equals(review.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}

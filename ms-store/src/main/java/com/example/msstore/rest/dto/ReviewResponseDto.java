package com.example.msstore.rest.dto;

import com.example.msstore.domain.entities.Review;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


@NoArgsConstructor
@AllArgsConstructor
@Data
public class ReviewResponseDto implements Serializable {
    private Integer rating;
    private String comment;
    private String date;

    public ReviewResponseDto(Review review){
        rating = review.getRating();
        comment = review.getComment();
        date = review.getDate().toString();
    }

}

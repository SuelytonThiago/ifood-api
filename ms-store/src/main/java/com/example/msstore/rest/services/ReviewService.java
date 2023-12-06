package com.example.msstore.rest.services;

import com.example.msstore.domain.entities.Review;
import com.example.msstore.domain.repositories.ReviewRepository;
import com.example.msstore.rest.services.exceptions.CustomException;
import com.example.msstore.rest.services.exceptions.ObjectNotFoundException;
import com.example.msstore.rest.dto.ReviewRequestDto;
import com.example.msstore.rest.dto.ReviewRequestUpdate;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;



@Service
@AllArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;

    private final StoreService storeService;

    private final TokenService tokenService;

    public void createNewReview(Long idRestaurant, HttpServletRequest request, ReviewRequestDto reviewDto){
        try {
            var id = tokenService.getClaimId(request);
            var store = storeService.findById(idRestaurant);
            var review = reviewRepository.save(new Review(reviewDto, id, store));
            store.getReviews().add(review);
        }catch (ConstraintViolationException e){
            throw new CustomException(
                    "This rating must be greater than or equal to 1 and less than or equal to 5");
        }
    }

    public Review findByID(Long id){
        return reviewRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("The review is not found"));
    }

    @Transactional
    public void deleteById(Long idReview){
        var review = findByID(idReview);
        var store = storeService.findById(review.getReviewStore().getId());
        store.getReviews().remove(review);
        storeService.save(store);
        reviewRepository.delete(review);
    }

    @Transactional
    public void updateReviewData(Long idReview, ReviewRequestUpdate request){
        try {
            var review = findByID(idReview);
            updateData(review, request);
            reviewRepository.save(review);
        }
        catch (ConstraintViolationException e){
            throw new CustomException(
                    "This rating must be greater than or equal to 1 and less than or equal to 5");
        }
    }

    private void updateData(Review review, ReviewRequestUpdate dto) {
        if(dto.getComment() != null){
            review.setComment(dto.getComment());
        }
        if(dto.getRating() != null){
            review.setRating(dto.getRating());
        }
    }
}

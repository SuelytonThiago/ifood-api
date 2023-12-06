package com.example.msstore.rest.services;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.msstore.domain.entities.Review;
import com.example.msstore.domain.entities.Store;
import com.example.msstore.domain.enums.TypeCode;
import com.example.msstore.domain.repositories.ReviewRepository;
import com.example.msstore.rest.dto.ReviewRequestDto;
import com.example.msstore.rest.dto.ReviewRequestUpdate;
import com.example.msstore.rest.services.exceptions.CustomException;
import com.example.msstore.rest.services.exceptions.ObjectNotFoundException;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Date;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
public class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private StoreService storeService;

    @Mock
    private TokenService  tokenService;

    @InjectMocks
    private ReviewService reviewService;

    private Review review;
    private Store store;
    private String access_token;
    private ReviewRequestDto reviewRequestDto;
    private ReviewRequestUpdate reviewRequestUpdate;

    @BeforeEach
    public void beforeEach(){
        store = new Store(1L,"pizzaria","vendemos pizza",1L, TypeCode.RESTAURANT);
        review = new Review(1L,1L,5,"muito bom", LocalDate.now(), store);
        access_token = JWT.create().withClaim("id",1L)
                .withExpiresAt(new Date(System.currentTimeMillis() + 100000))
                .sign(Algorithm.HMAC512("secret"));
        reviewRequestDto= new ReviewRequestDto(4,"very good");
        reviewRequestUpdate = new ReviewRequestUpdate(2,"good");
    }


    @Test
    @DisplayName("test Given StoreId, HttpServletRequest and ReviewRequest Object When CreateNewReview Will Save Review Object")
    public void testCreateNewReview(){
        HttpServletRequest request = mock(HttpServletRequest.class);
        given(tokenService.getClaimId(any(HttpServletRequest.class))).willReturn(1L);
        given(storeService.findById(anyLong())).willReturn(store);

        reviewService.createNewReview(store.getId(),request,reviewRequestDto);

        verify(tokenService).getClaimId(any(HttpServletRequest.class));
        verify(storeService).findById(anyLong());
        verify(reviewRepository).save(any(Review.class));
        verifyNoMoreInteractions(tokenService);
        verifyNoMoreInteractions(storeService);
        verifyNoMoreInteractions(reviewRepository);
    }

    @Test
    @DisplayName("test Given StoreId, HttpServletRequest and ReviewRequest Object With Ratting Less Than 1 When CreateNewReview Will Save Review Object")
    public void testCreateNewReviewWithRattingLessThan1(){
        HttpServletRequest request = mock(HttpServletRequest.class);
        given(tokenService.getClaimId(any(HttpServletRequest.class))).willReturn(1L);
        given(storeService.findById(anyLong())).willReturn(store);
        given(reviewRepository.save(any(Review.class))).willThrow(ConstraintViolationException.class);
        reviewRequestDto.setRating(0);

        assertThatExceptionOfType(CustomException.class)
                .isThrownBy(() -> reviewService.createNewReview(store.getId(),request,reviewRequestDto))
                .withMessage( "This rating must be greater than or equal to 1 and less than or equal to 5");
        verify(reviewRepository).save(any(Review.class));
        verify(tokenService).getClaimId(any(HttpServletRequest.class));
        verify(storeService).findById(anyLong());
        verifyNoMoreInteractions(reviewRepository);
        verifyNoMoreInteractions(tokenService);
        verifyNoMoreInteractions(storeService);
    }

    @Test
    @DisplayName("test Given StoreId, HttpServletRequest and ReviewRequest Object With Ratting Greater Than 5 When CreateNewReview Will Save Review Object")
    public void testCreateNewReviewWithRattingGreaterThan5(){
        HttpServletRequest request = mock(HttpServletRequest.class);
        given(tokenService.getClaimId(any(HttpServletRequest.class))).willReturn(1L);
        given(storeService.findById(anyLong())).willReturn(store);
        given(reviewRepository.save(any(Review.class))).willThrow(ConstraintViolationException.class);
        reviewRequestDto.setRating(6);

        assertThatExceptionOfType(CustomException.class)
                .isThrownBy(() -> reviewService.createNewReview(store.getId(),request,reviewRequestDto))
                .withMessage( "This rating must be greater than or equal to 1 and less than or equal to 5");

        verify(reviewRepository).save(any(Review.class));
        verify(tokenService).getClaimId(any(HttpServletRequest.class));
        verify(storeService).findById(anyLong());
        verifyNoMoreInteractions(reviewRepository);
        verifyNoMoreInteractions(tokenService);
        verifyNoMoreInteractions(storeService);
    }

    @Test
    @DisplayName("test given reviewId when findById then return review object")
    public void testFindById(){
        given(reviewRepository.findById(anyLong())).willReturn(Optional.of(review));

        var outputReview = reviewService.findByID(review.getId());

        assertThat(outputReview).usingRecursiveComparison().isEqualTo(review);
        verify(reviewRepository).findById(anyLong());
        verifyNoMoreInteractions(reviewRepository);
    }

    @Test
    @DisplayName("test given WithInvalidReviewId when findById then return review object")
    public void testFindByIdWithInvalidReviewId(){
        given(reviewRepository.findById(anyLong())).willReturn(Optional.empty());

        assertThatExceptionOfType(ObjectNotFoundException.class)
                .isThrownBy(() -> reviewService.findByID(review.getId()))
                .withMessage("The review is not found");
        verify(reviewRepository).findById(anyLong());
        verifyNoMoreInteractions(reviewRepository);
    }

    @Test
    @DisplayName("test given reviewId when deleteById Will delete Review Object")
    public void testDeleteById(){
        given(reviewRepository.findById(anyLong())).willReturn(Optional.of(review));
        given(storeService.findById(anyLong())).willReturn(store);
        reviewService.deleteById(review.getId());

        verify(reviewRepository).findById(anyLong());
        verify(storeService).findById(anyLong());
        verify(reviewRepository).delete(any(Review.class));
        verifyNoMoreInteractions(reviewRepository);
    }

    @Test
    @DisplayName("test given With Invalid ReviewId when deleteById Then Throw ObjectNotFoundException")
    public void testDeleteByIdWithInvalidReviewId(){
        given(reviewRepository.findById(anyLong())).willReturn(Optional.empty());


        assertThatExceptionOfType(ObjectNotFoundException.class)
                .isThrownBy(() -> reviewService.deleteById(review.getId()))
                .withMessage("The review is not found");
        verify(reviewRepository).findById(anyLong());
        verifyNoMoreInteractions(reviewRepository);
    }

    @Test
    @DisplayName("test Given ReviewId and ReviewRequest Object When UpdateReviewData Will Update Review Data")
    public void testUpdateReviewData(){
        given(reviewRepository.findById(anyLong())).willReturn(Optional.of(review));

        reviewService.updateReviewData(review.getId(),reviewRequestUpdate);

        verify(reviewRepository).findById(anyLong());
        verify(reviewRepository).save(any(Review.class));
        verifyNoMoreInteractions(reviewRepository);

    }

    @Test
    @DisplayName("test Given ReviewId and ReviewRequest Object With Ratting Less Than 1 When UpdateReviewData Then Throw CustomException")
    public void testUpdateReviewDataWithRattingLessThan1(){
        given(reviewRepository.findById(anyLong())).willReturn(Optional.of(review));
        given(reviewRepository.save(any(Review.class))).willThrow(ConstraintViolationException.class);
        reviewRequestUpdate.setRating(0);
        assertThatExceptionOfType(CustomException.class)
                .isThrownBy(() -> reviewService.updateReviewData(review.getId(),reviewRequestUpdate))
                .withMessage("This rating must be greater than or equal to 1 and less than or equal to 5");
        verify(reviewRepository).findById(anyLong());
        verify(reviewRepository).save(any(Review.class));
        verifyNoMoreInteractions(reviewRepository);
    }

    @Test
    @DisplayName("test Given ReviewId and ReviewRequest Object With Ratting Greater Than 1 When UpdateReviewData Then Throw CustomException")
    public void testUpdateReviewDataWithRattingGreaterThan1(){
        given(reviewRepository.findById(anyLong())).willReturn(Optional.of(review));
        given(reviewRepository.save(any(Review.class))).willThrow(ConstraintViolationException.class);
        reviewRequestUpdate.setRating(6);
        assertThatExceptionOfType(CustomException.class)
                .isThrownBy(() -> reviewService.updateReviewData(review.getId(),reviewRequestUpdate))
                .withMessage("This rating must be greater than or equal to 1 and less than or equal to 5");
        verify(reviewRepository).findById(anyLong());
        verify(reviewRepository).save(any(Review.class));
        verifyNoMoreInteractions(reviewRepository);
    }

    @Test
    @DisplayName("test Given With Invalid ReviewId and ReviewRequest Object When UpdateReviewData Will Update Review Data")
    public void testUpdateReviewDataWithInvalidReviewId(){
        given(reviewRepository.findById(anyLong())).willReturn(Optional.empty());

        assertThatExceptionOfType(ObjectNotFoundException.class)
                .isThrownBy(() -> reviewService.updateReviewData(review.getId(),reviewRequestUpdate))
                .withMessage("The review is not found");
        verify(reviewRepository).findById(anyLong());
        verifyNoMoreInteractions(reviewRepository);
    }

}

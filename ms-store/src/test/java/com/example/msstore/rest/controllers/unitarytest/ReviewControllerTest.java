package com.example.msstore.rest.controllers.unitarytest;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.msstore.domain.entities.Review;
import com.example.msstore.domain.entities.Store;
import com.example.msstore.domain.enums.TypeCode;
import com.example.msstore.rest.controllers.ReviewController;
import com.example.msstore.rest.dto.ReviewRequestDto;
import com.example.msstore.rest.dto.ReviewRequestUpdate;
import com.example.msstore.rest.services.ReviewService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.util.Date;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class ReviewControllerTest {

    @Mock
    private ReviewService reviewService;

    @InjectMocks
    private ReviewController reviewController;

    private ObjectMapper objectMapper;
    private MockMvc mockMvc;
    private Review review;
    private Store store;
    private String access_token;
    private ReviewRequestDto reviewRequestDto;
    private ReviewRequestUpdate reviewRequestUpdate;

    @BeforeEach
    public void beforeEach(){
        objectMapper = new ObjectMapper();
        mockMvc= MockMvcBuilders.standaloneSetup(reviewController)
                .alwaysDo(print()).build();

        store = new Store(1L,"pizzaria","vendemos pizza",1L, TypeCode.RESTAURANT);
        review = new Review(1L,1L,5,"muito bom", LocalDate.now(), store);
        access_token = JWT.create().withClaim("id",1L)
                .withExpiresAt(new Date(System.currentTimeMillis() + 100000))
                .sign(Algorithm.HMAC512("secret"));
        reviewRequestDto= new ReviewRequestDto(4,"very good");
        reviewRequestUpdate = new ReviewRequestUpdate(2,"good");
    }

    @Test
    @DisplayName("test given reviewRequest object when create then return http status isCreated")
    @WithMockUser(username = "user",roles = "USER")
    public void testCreate() throws Exception {
        mockMvc.perform(post("/api/reviews/create/{id}",store.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reviewRequestDto)))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("test given reviewRequest object with null rating when create then return HttpStatus isBadRequest")
    @WithMockUser(username = "user",roles = "USER")
    public void testCreateWithNullRating() throws Exception {
        reviewRequestDto.setRating(null);
        mockMvc.perform(post("/api/reviews/create/{id}",store.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reviewRequestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("test given reviewRequest object with null comment when create then return HttpStatus isBadRequest")
    @WithMockUser(username = "user",roles = "USER")
    public void testCreateWithNullComment() throws Exception {
        reviewRequestDto.setComment(null);
        mockMvc.perform(post("/api/reviews/create/{id}",store.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reviewRequestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "user",roles = "USER")
    @DisplayName("test given reviewRequest object with Empty comment when create then return HttpStatus isBadRequest")
    public void testCreateWithEmptyComment() throws Exception {
        reviewRequestDto.setComment("");
        mockMvc.perform(post("/api/reviews/create/{id}",store.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reviewRequestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("test given review id when delete will delete review from database")
    @WithMockUser(username = "user",roles = "USER")
    public void testDelete() throws Exception {
        mockMvc.perform(delete("/api/reviews/delete/{id}",review.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reviewRequestDto)))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("test given review request object and review id when update will update review data")
    @WithMockUser(username = "user",roles = "USER")
    public void testUpdate() throws Exception {
        mockMvc.perform(patch("/api/reviews/update/{id}",review.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reviewRequestUpdate)))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "user",roles = "USER")
    @DisplayName("test given review request object with comment contains only spaces and review id when update then return HttpStatus isBadRequest")
    public void testUpdateWithCommentContainOnlySpaces() throws Exception {
        reviewRequestUpdate.setComment("  ");
        mockMvc.perform(patch("/api/reviews/update/{id}",review.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reviewRequestUpdate)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

}

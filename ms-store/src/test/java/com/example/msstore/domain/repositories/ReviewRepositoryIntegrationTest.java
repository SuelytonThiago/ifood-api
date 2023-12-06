package com.example.msstore.domain.repositories;

import com.example.msstore.config.ContainerBase;
import com.example.msstore.domain.entities.Review;
import com.example.msstore.domain.entities.Store;
import com.example.msstore.grpc.client.UserServiceGrpcClient;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import static org.assertj.core.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestPropertySource("classpath:application-test.properties")
public class ReviewRepositoryIntegrationTest extends ContainerBase {

    @Autowired
    private ReviewRepository reviewRepository;

    @MockBean
    private UserServiceGrpcClient grpcClient;

    @Autowired
    private StoreRepository storeRepository;

    private static Store store;
    private static Review review;

    @Test
    @Order(1)
    @DisplayName("integration test given review object when save then return review object")
    public void testSave(){
        store = storeRepository.findByName("habbibs").get();
        var savedReview = reviewRepository.save(new Review(null,2L,5,"muito bom", LocalDate.now(), store));
        assertThat(savedReview.getId()).isNotNull();
        assertThat(savedReview.getComment()).isEqualTo("muito bom");
        assertThat(savedReview.getReviewerId()).isEqualTo(2L);
        assertThat(savedReview.getReviewStore()).isEqualTo(store);
        assertThat(savedReview.getDate()).isEqualTo(LocalDate.now());
        assertThat(savedReview.getRating()).isEqualTo(5);

        review = savedReview;
        System.out.println(review.getId());
    }

    @Test
    @Order(2)
    @DisplayName("integration test given review id when findById then return review object")
    public void testFindById(){
        var outputReview = reviewRepository.findById(review.getId()).get();

        assertThat(outputReview).usingRecursiveComparison()
                .comparingOnlyFields("reviewerId","rating","comment","date")
                .isEqualTo(review);
    }

    @Test
    @Order(3)
    @DisplayName("integration test when findAll then return reviewList")
    public void testFindAll(){
        var list = reviewRepository.findAll();

        assertThat(list.size()).isEqualTo(2);
        assertThat(list).extracting("reviewerId","rating","comment","date")
                .contains(
                        tuple(1L,5,"muito bom", LocalDate.now()),
                        tuple(2L,5,"muito bom", LocalDate.now()));
    }


    @Test
    @Order(4)
    @Transactional
    @DisplayName("integration test given review object when delete will delete review from database")
    public void testDelete(){
        reviewRepository.delete(review);
        assertThat(reviewRepository.findById(review.getId()).isEmpty()).isTrue();
    }
}

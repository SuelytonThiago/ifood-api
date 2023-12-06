package com.example.msstore.rest.controllers.unitarytest;

import com.example.msstore.domain.entities.Categories;
import com.example.msstore.domain.entities.Food;
import com.example.msstore.domain.entities.Review;
import com.example.msstore.domain.entities.Store;
import com.example.msstore.domain.enums.TypeCode;
import com.example.msstore.rest.controllers.StoreController;
import com.example.msstore.rest.dto.*;
import com.example.msstore.rest.services.StoreService;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.time.LocalDate;
import java.util.Collections;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
public class StoreControllerTest {

    @Mock
    private StoreService storeService;

    @InjectMocks
    private StoreController storeController;

    private ObjectMapper objectMapper;
    private MockMvc mockMvc;
    private Food food;
    private Categories category;
    private Review reviewP,reviewS,reviewR;
    private Store restaurant,pharmacy,supermarket;
    private StoreRequestDto storeRequestDto;
    private StoreRequestUpdate storeRequestUpdate;

    @BeforeEach
    public void beforeEach(){
        objectMapper = new ObjectMapper();
        mockMvc= MockMvcBuilders.standaloneSetup(storeController)
                .alwaysDo(print()).build();

        restaurant = new Store(1L,"pizzaria","vendemos pizza",2L, TypeCode.RESTAURANT);
        pharmacy = new Store(2L,"farmacia","bio far",3L, TypeCode.PHARMACY);
        supermarket = new Store(3L,"mercado","bio mer",4L, TypeCode.SUPERMARKET);

        category = new Categories(1L,"pizza");
        food = new Food(1L,"pizza",30.00,10,category,restaurant);

        reviewR = new Review(1L,1L,5,"muito bom", LocalDate.now(), restaurant);
        reviewS = new Review(1L,1L,5,"muito bom", LocalDate.now(), supermarket);
        reviewP = new Review(1L,1L,5,"muito bom", LocalDate.now(), pharmacy);

        restaurant.getReviews().add(reviewR);
        pharmacy.getReviews().add(reviewP);
        supermarket.getReviews().add(reviewS);

        restaurant.getMenu().add(food);

        storeRequestDto = new StoreRequestDto(
                "habbibs",
                "bio",
                3,
                "jose",
                "jose@example.com",
                "99940028922",
                "97980163001",
                "senha123");
        storeRequestUpdate = new StoreRequestUpdate("habbibs","new bio");
    }

    @Test
    @WithMockUser(username = "admin",roles = "ADMIN")
    @DisplayName("test given toreRequest object when createNewStore then return HttpStatus isCreated")
    public void testCreateNewStore() throws Exception {
        mockMvc.perform(post("/api/store/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(storeRequestDto)))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(username = "admin",roles = "ADMIN")
    @DisplayName("test given storeRequest object  With Empty StoreName when createNewStore then return HttpStatus isBadRequest")
    public void testCreateNewStoreWithEmptyStoreName() throws Exception {
        storeRequestDto.setStoreName("");
        mockMvc.perform(post("/api/store/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(storeRequestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "admin",roles = "ADMIN")
    @DisplayName("test given storeRequest object  With Null StoreName when createNewStore then return HttpStatus isBadRequest")
    public void testCreateNewStoreWithNullStoreName() throws Exception {
        storeRequestDto.setStoreName(null);
        mockMvc.perform(post("/api/store/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(storeRequestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "admin",roles = "ADMIN")
    @DisplayName("test given storeRequest object  With Empty bio when createNewStore then return HttpStatus isBadRequest")
    public void testCreateNewStoreWithEmptyBio() throws Exception {
        storeRequestDto.setBio("");
        mockMvc.perform(post("/api/store/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(storeRequestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "admin",roles = "ADMIN")
    @DisplayName("test given storeRequest object  With Null bio when createNewStore then return HttpStatus isBadRequest")
    public void testCreateNewStoreWithNullBio() throws Exception {
        storeRequestDto.setBio(null);
        mockMvc.perform(post("/api/store/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(storeRequestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "admin",roles = "ADMIN")
    @DisplayName("test given storeRequest object With Empty OwnerName when createNewStore then return HttpStatus isBadRequest")
    public void testCreateNewStoreWithEmptyOwnerName() throws Exception {
        storeRequestDto.setOwnerName("");
        mockMvc.perform(post("/api/store/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(storeRequestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "admin",roles = "ADMIN")
    @DisplayName("test given storeRequest object With Null OwnerName when createNewStore then return HttpStatus isBadRequest")
    public void testCreateNewStoreWithNullOwnerName() throws Exception {
        storeRequestDto.setOwnerName(null);
        mockMvc.perform(post("/api/store/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(storeRequestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "admin",roles = "ADMIN")
    @DisplayName("test given storeRequest object with Empty Email when createNewStore then return HttpStatus isBadRequest")
    public void testCreateNewStoreWithEmptyEmail() throws Exception {
        storeRequestDto.setEmail("");
        mockMvc.perform(post("/api/store/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(storeRequestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "admin",roles = "ADMIN")
    @DisplayName("test given storeRequest object with Null Email  when createNewStore then return HttpStatus isBadRequest")
    public void testCreateNewStoreWithNullEmail() throws Exception {
        storeRequestDto.setEmail(null);
        mockMvc.perform(post("/api/store/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(storeRequestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "admin",roles = "ADMIN")
    @DisplayName("test given storeRequest object with Null Email  when createNewStore then return HttpStatus isBadRequest")
    public void testCreateNewStoreWithInvalidEmail() throws Exception {
        storeRequestDto.setEmail("user");
        mockMvc.perform(post("/api/store/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(storeRequestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "admin",roles = "ADMIN")
    @DisplayName("test given storeRequest object  with Null contactNumber when createNewStore then return HttpStatus isBadRequest")
    public void testCreateNewStoreWithNullContactNumber() throws Exception {
        storeRequestDto.setContactNumber(null);
        mockMvc.perform(post("/api/store/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(storeRequestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "admin",roles = "ADMIN")
    @DisplayName("test given storeRequest object Contact Number Contains Letters when createNewStore then return HttpStatus isBadRequest")
    public void testCreateNewStoreWithContactNumberContainsLetters() throws Exception {
        storeRequestDto.setContactNumber("9994002opiu");
        mockMvc.perform(post("/api/store/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(storeRequestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "admin",roles = "ADMIN")
    @DisplayName("test given storeRequest object With ContactNumber Contains Less Than 11 Digits when createNewStore then return HttpStatus isBadRequest")
    public void testCreateNewStoreWithContactNumberContainsLessThan11Digits() throws Exception {
        storeRequestDto.setContactNumber("9994002");
        mockMvc.perform(post("/api/store/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(storeRequestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "admin",roles = "ADMIN")
    @DisplayName("test given storeRequest object With ContactNumber Contains More Than 11 Digits when createNewStore then return HttpStatus isBadRequest")
    public void testCreateNewStoreWithContactNumberContainsMoreThan11Digits() throws Exception {
        storeRequestDto.setContactNumber("999400289222");
        mockMvc.perform(post("/api/store/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(storeRequestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "admin",roles = "ADMIN")
    @DisplayName("test given storeRequest object With Null cpf when createNewStore then return HttpStatus isBadRequest")
    public void testCreateNewStoreWithNullCpf() throws Exception {
        storeRequestDto.setCpf(null);
        mockMvc.perform(post("/api/store/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(storeRequestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "admin",roles = "ADMIN")
    @DisplayName("test given storeRequest object With invalid cpf when createNewStore then return HttpStatus isBadRequest")
    public void testCreateNewStoreWithInvalidCpf() throws Exception {
        storeRequestDto.setCpf("asdadsad");
        mockMvc.perform(post("/api/store/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(storeRequestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "admin",roles = "ADMIN")
    @DisplayName("test given storeRequest object With Null Password when createNewStore then return HttpStatus isBadRequest")
    public void testCreateNewStoreWithNullPassword() throws Exception {
        storeRequestDto.setPassword(null);
        mockMvc.perform(post("/api/store/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(storeRequestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "admin",roles = "ADMIN")
    @DisplayName("test given storeRequest object With Password Contains Less Than 8 Digits when createNewStore then return HttpStatus isBadRequest")
    public void testCreateNewStoreWithPasswordContainsLessThan8Digits() throws Exception {
        storeRequestDto.setPassword("3x4mpl3");
        mockMvc.perform(post("/api/store/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(storeRequestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "admin",roles = "ADMIN")
    @DisplayName("test given storeRequest object With Password Contains Only Letters when createNewStore then return HttpStatus isBadRequest")
    public void testCreateNewStoreWithPasswordContainsOnlyLetters() throws Exception {
        storeRequestDto.setPassword("exampleee");
        mockMvc.perform(post("/api/store/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(storeRequestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "admin",roles = "ADMIN")
    @DisplayName("test given storeRequest object With Password Contains Only Numbers when createNewStore then return HttpStatus isBadRequest")
    public void testCreateNewStoreWithPasswordContainsOnlyNumbers() throws Exception {
        storeRequestDto.setPassword("12345678");
        mockMvc.perform(post("/api/store/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(storeRequestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }


    @Test
    @DisplayName("test when findAllByPharmacy then return pharmacy list")
    public void testFindAllByPharmacy() throws Exception {
        var list = Collections.singletonList(new StoreResponseDto(pharmacy));
        given(storeService.findAllPharmacy()).willReturn(list);

        mockMvc.perform(get("/api/store/pharmacy"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()",is(list.size())));
    }

    @Test
    @DisplayName("test when findAllBySupermarket then return supermarket list")
    public void testFindAllBySupermarket() throws Exception {
        var list = Collections.singletonList(new StoreResponseDto(supermarket));
        given(storeService.findAllSupermarket()).willReturn(list);

        mockMvc.perform(get("/api/store/supermarket"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()",is(list.size())));
    }

    @Test
    @DisplayName("test when findAllByRestaurant then return restaurant list")
    public void testFindAllByRestaurant() throws Exception {
        var list = Collections.singletonList(new StoreResponseDto(restaurant));
        given(storeService.findAllRestaurant()).willReturn(list);

        mockMvc.perform(get("/api/store/restaurant"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()",is(list.size())));
    }

    @Test
    @DisplayName("test given store name when findByName then return Store response object")
    public void testFindByName() throws Exception {
        var storeDto = new StoreResponseDto(restaurant);
        given(storeService.findByName(anyString())).willReturn(restaurant);

        mockMvc.perform(get("/api/store/search")
                        .param("storeName",restaurant.getName()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name",is(storeDto.getName())))
                .andExpect(jsonPath("$.bio",is(storeDto.getBio())))
                .andExpect(jsonPath("$.totalRating",is(storeDto.getTotalRating())));
    }

    @Test
    @DisplayName("test given store id when getReviews then return reviews store list")
    public void testGetReviews() throws Exception {
        var list = Collections.singletonList(new ReviewResponseDto(reviewR));

        given(storeService.getReviews(anyLong())).willReturn(list);

        mockMvc.perform(get("/api/store/reviews/{id}",restaurant.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()",is(list.size())));
    }

    @Test
    @DisplayName("test given store id when getStoreMenu then return Store menu list")
    public void testGetStoreMenu() throws Exception {
        var list = Collections.singletonList(new FoodDto(food));

        given(storeService.showStoreMenu(anyLong())).willReturn(list);

        mockMvc.perform(get("/api/store/menu/{id}",restaurant.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()",is(list.size())));
    }

    @Test
    @DisplayName("test give storeId when delete will delete store from database")
    @WithMockUser(username = "admin",roles = "ADMIN")
    public void testDelete() throws Exception {
        mockMvc.perform(delete("/api/store/delete/{id}",restaurant.getId()))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("test given storeRequest object and storeId when update will update store data ")
    @WithMockUser(username = "owner",roles = "OWNER")
    public void testUpdate() throws Exception {
        mockMvc.perform(patch("/api/store/update/{id}",restaurant.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(storeRequestUpdate)))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("test given storeObject with Name contain only spaces when update then Return HttpStatus isBadRequest")
    public void testUpdateWithNameContainOnlySpaces() throws Exception {
        storeRequestUpdate.setName("   ");
        mockMvc.perform(patch("/api/store/update/{id}",restaurant.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(storeRequestUpdate)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("test given storeObject with bio contain only spaces when update then Return HttpStatus isBadRequest")
    public void testUpdateWithBioContainOnlySpaces() throws Exception {
        storeRequestUpdate.setBio("   ");
        mockMvc.perform(patch("/api/store/update/{id}",restaurant.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(storeRequestUpdate)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }




}

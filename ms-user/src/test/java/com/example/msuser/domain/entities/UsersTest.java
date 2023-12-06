package com.example.msuser.domain.entities;
import com.example.msuser.rest.dto.UserRequestDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

public class UsersTest {

    private UserRequestDto userRequestDto;

    @BeforeEach
    void setUp(){
        userRequestDto = new UserRequestDto();
        userRequestDto.setName("ana");
        userRequestDto.setEmail("ana@example.com");
        userRequestDto.setContactNumber("40028922");
        userRequestDto.setPassword("senha123");
        userRequestDto.setCpf("88688901007");
    }

    @Test
    public void testGivenUserRequestDto_whenOf_thenReturnUserObject(){
        var user = Users.of(userRequestDto);

        assertThat(user).usingRecursiveComparison()
                .comparingOnlyFields("name","email","contactNumber","password","cpf")
                .isEqualTo(userRequestDto);
    }
}

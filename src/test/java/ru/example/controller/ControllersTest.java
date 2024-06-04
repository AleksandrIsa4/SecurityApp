package ru.example.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.example.dto.CreateUserRequest;
import ru.example.dto.SignInRequest;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ControllersTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    CreateUserRequest createUserRequest;

    SignInRequest signInRequest, signInRequestFailName, signInRequestFailPassword;

    @BeforeEach
    void init() {
        createUserRequest = new CreateUserRequest("Jon123", "jondoe@gmail.com", "my_1secret1_password");
        signInRequest = new SignInRequest("Jon123", "my_1secret1_password");
        signInRequestFailName = new SignInRequest("Jon123Fail", "my_1secret1_password");
        signInRequestFailPassword = new SignInRequest("Jon123", "my_1secret1_passwordFail");
    }

    @Test
    @SneakyThrows
    @Sql("/data-test.sql")
    void signUpTest() {
        mockMvc.perform(MockMvcRequestBuilders.post("/auth/sign-up")
                        .content(objectMapper.writeValueAsString(createUserRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.token").exists())
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @SneakyThrows
    void getWithoutTokenPrivateFail() {
        mockMvc.perform(MockMvcRequestBuilders.get("/private")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andDo(print());
    }

    @Test
    @SneakyThrows
    @WithMockUser(username = "Jon123", authorities = "ROLE_MODERATOR")
    void getWithTokenPrivate() {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                        .get("/private"))
                .andExpect(status().is2xxSuccessful())
                .andReturn();
        String text = result.getResponse().getContentAsString();
        Assertions.assertEquals(text, "Hello, authorized user!");
    }

    @Test
    @SneakyThrows
    @Sql("/data-test.sql")
    void signInTest() {
        mockMvc.perform(MockMvcRequestBuilders.post("/auth/sign-up")
                        .content(objectMapper.writeValueAsString(createUserRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.token").exists())
                .andExpect(status().isOk())
                .andDo(print());
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/auth/sign-in")
                        .content(objectMapper.writeValueAsString(signInRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andDo(print())
                .andReturn();
        JsonNode jsonNode = objectMapper.readTree(result.getResponse().getContentAsString());
        String token = jsonNode.get("token").asText();
        MvcResult result2 = mockMvc.perform(MockMvcRequestBuilders
                        .get("/private")
                        .header("authorization", "Bearer " + token))
                .andExpect(status().is2xxSuccessful())
                .andReturn();
        String text = result2.getResponse().getContentAsString();
        Assertions.assertEquals(text, "Hello, authorized user!");
    }

    @Test
    @SneakyThrows
    void signInTestFailName() {
        mockMvc.perform(MockMvcRequestBuilders.post("/auth/sign-in")
                        .content(objectMapper.writeValueAsString(signInRequestFailName))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andDo(print());
    }

    @Test
    @SneakyThrows
    void signInTestFailPassword() {
        mockMvc.perform(MockMvcRequestBuilders.post("/auth/sign-in")
                        .content(objectMapper.writeValueAsString(signInRequestFailPassword))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andDo(print());
    }

    @Test
    @SneakyThrows
    @WithMockUser(username = "Jon123", authorities = "ROLE_SUPER_ADMIN")
    void getWithTokenAdmin() {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                        .get("/admin"))
                .andExpect(status().is2xxSuccessful())
                .andReturn();
        String text = result.getResponse().getContentAsString();
        Assertions.assertEquals(text, "Hello, admin!");
    }

    @Test
    @SneakyThrows
    @WithMockUser(username = "Jon123", authorities = "ROLE_MODERATOR")
    void getWithTokenUserToAdmin() {
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/admin"))
                .andExpect(status().is4xxClientError())
                .andDo(print());
    }

    @Test
    @SneakyThrows
    @WithMockUser(username = "Jon123", authorities = "ROLE_SUPER_ADMIN")
    void getWithTokenPublic() {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                        .get("/public"))
                .andExpect(status().is2xxSuccessful())
                .andReturn();
        String text = result.getResponse().getContentAsString();
        Assertions.assertEquals(text, "Hello!");
    }
}
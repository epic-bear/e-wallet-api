package com.app.ewalletapi.api;

import com.app.ewalletapi.api.request.UserRequest;
import com.app.ewalletapi.api.response.UserResponse;
import com.app.ewalletapi.factory.UserFactory;
import com.app.ewalletapi.model.User;
import com.app.ewalletapi.model.UserStatus;
import com.app.ewalletapi.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("application-test")
@AutoConfigureMockMvc
class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserFactory userFactory;
    @Autowired
    private UserRepository userRepository;

    @Test
    void createUser() throws Exception {
        UserRequest userRequest = new UserRequest();
        userRequest.setName("John Doe");
        userRequest.setPassword("password");

        MvcResult result = mockMvc.perform(post("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(status().isOk())
                .andReturn();

        UserResponse userResponse = objectMapper.readValue(result.getResponse().getContentAsString(), UserResponse.class);

        assertEquals("John Doe", userResponse.getName());
        assertEquals(UserStatus.ACTIVE, userResponse.getUserStatus());
        assertNotNull(userResponse.getWalletsIds());
        assertEquals(0, userResponse.getWalletsIds().size());
    }

    @Test
    void getUser() throws Exception {
        User user = userFactory.createTestUser();

        MvcResult getResult = mockMvc.perform(get("/user/{userId}", user.getUserId()))
                .andExpect(status().isOk())
                .andReturn();

        UserResponse getUserResponse = objectMapper.readValue(getResult.getResponse().getContentAsString(), UserResponse.class);

        assertEquals(user.getUserId(), getUserResponse.getUserId());
        assertEquals("John Doe", getUserResponse.getName());
        assertEquals(UserStatus.ACTIVE, getUserResponse.getUserStatus());
        assertNotNull(getUserResponse.getWalletsIds());
        assertEquals(0, getUserResponse.getWalletsIds().size());
    }

    @Test
    void updateUser() throws Exception {
        User user = userFactory.createTestUser();
        UserRequest updatedUserRequest = new UserRequest();
        updatedUserRequest.setUserId(user.getUserId());
        updatedUserRequest.setName("John Smith");
        updatedUserRequest.setPassword("newPassword");

        MvcResult result = mockMvc.perform(put("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedUserRequest)))
                .andExpect(status().isOk())
                .andReturn();

        UserResponse userResponse = objectMapper.readValue(result.getResponse().getContentAsString(), UserResponse.class);

        User updatedUser = userRepository.findById(user.getUserId()).orElse(null);

        assertNotNull(updatedUser);
        assertEquals(updatedUser.getUserId(), userResponse.getUserId());
        assertEquals("John Smith", userResponse.getName());
        assertEquals("newPassword", updatedUser.getPassword());
        assertEquals(UserStatus.ACTIVE, userResponse.getUserStatus());
        assertNotNull(userResponse.getWalletsIds());
        assertEquals(0, userResponse.getWalletsIds().size());

    }

    @Test
    void resetStatus() throws Exception {
        User user = userFactory.createTestUser();
        UserRequest userRequest = new UserRequest();
        userRequest.setUserId(user.getUserId());
        userRequest.setName("John Doe");
        userRequest.setPassword("password");

        MvcResult result = mockMvc.perform(put("/user/reset")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(status().isOk())
                .andReturn();

        User updatedUser = userRepository.findById(user.getUserId()).orElse(null);
        assertEquals("User unblocked successfully", result.getResponse().getContentAsString());
        assertNotNull(updatedUser);
        assertEquals(UserStatus.SUSPICIOUS, updatedUser.getUserStatus());
    }

    @Test
    void deleteUser() throws Exception {
        User user = userFactory.createTestUser();

        MvcResult result = mockMvc.perform(delete("/user/{userId}", user.getUserId()))
                .andExpect(status().isOk())
                .andReturn();

        assertEquals("User deleted successfully", result.getResponse().getContentAsString());
        assertFalse(userRepository.existsById(user.getUserId()));
    }
}

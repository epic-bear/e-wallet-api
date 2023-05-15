package com.app.ewalletapi.api;

import com.app.ewalletapi.api.request.WalletRequest;
import com.app.ewalletapi.api.response.WalletResponse;
import com.app.ewalletapi.factory.UserFactory;
import com.app.ewalletapi.factory.WalletFactory;
import com.app.ewalletapi.model.User;
import com.app.ewalletapi.model.Wallet;
import com.app.ewalletapi.repository.UserRepository;
import com.app.ewalletapi.repository.WalletRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("application-test")
@AutoConfigureMockMvc
public class WalletControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserFactory userFactory;
    @Autowired
    private WalletFactory walletFactory;
    @Autowired
    private WalletRepository walletRepository;

    @Test
    void createWallet() throws Exception {
        long userId = userFactory.createTestUser().getUserId();
        WalletRequest walletRequest = new WalletRequest();
        walletRequest.setUserId(userId);

        MvcResult result = mockMvc.perform(post("/wallet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(walletRequest)))
                .andExpect(status().isOk())
                .andReturn();

        WalletResponse walletResponse = objectMapper.readValue(result.getResponse().getContentAsString(), WalletResponse.class);

        assertEquals(userId, walletResponse.getUserId());
        assertNotNull(walletResponse.getTransactionIds());
        assertEquals(0, walletResponse.getTransactionIds().size());
    }

    @Test
    void getAllUserWallets() throws Exception {
        User testUser = userFactory.createTestUser();
        Wallet wallet = walletFactory.createTestWallet(testUser);
        WalletRequest walletRequest = new WalletRequest();
        walletRequest.setUserId(testUser.getUserId());
        walletRequest.setUserName(testUser.getName());
        walletRequest.setUserPassword(testUser.getPassword());

        MvcResult result = mockMvc.perform(get("/wallet/all")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(walletRequest)))
                .andExpect(status().isOk())
                .andReturn();

        List<WalletResponse> walletResponses = objectMapper.readValue(result.getResponse()
                .getContentAsString(), new TypeReference<List<WalletResponse>>() {
        });

        assertEquals(1, walletResponses.size());
        WalletResponse walletResponse = walletResponses.get(0);
        assertEquals(wallet.getWalletId(), walletResponse.getWalletId());
        assertEquals(testUser.getUserId(), walletResponse.getUserId());
        assertEquals(0, walletResponse.getBalance());
        assertNotNull(walletResponse.getTransactionIds());
        assertEquals(0, walletResponse.getTransactionIds().size());
    }

    @Test
    void getWalletById() throws Exception {
        User testUser = userFactory.createTestUser();
        Wallet wallet = walletFactory.createTestWallet(testUser);
        WalletRequest walletRequest = new WalletRequest();
        walletRequest.setUserId(testUser.getUserId());
        walletRequest.setUserName(testUser.getName());
        walletRequest.setUserPassword(testUser.getPassword());

        MvcResult result = mockMvc.perform(get("/wallet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(walletRequest)))
                .andExpect(status().isOk())
                .andReturn();

        WalletResponse walletResponse = objectMapper.readValue(result.getResponse().getContentAsString(), WalletResponse.class);

        assertEquals(wallet.getWalletId(), walletResponse.getWalletId());
        assertEquals(testUser.getUserId(), walletResponse.getUserId());
        assertEquals(0, walletResponse.getBalance());
        assertNotNull(walletResponse.getTransactionIds());
        assertEquals(0, walletResponse.getTransactionIds().size());
    }

    @Test
    void deleteWallet() throws Exception {
        User testUser = userFactory.createTestUser();
        Wallet wallet = walletFactory.createTestWallet(testUser);
        WalletRequest walletRequest = new WalletRequest();
        walletRequest.setWalletId(wallet.getWalletId());

        mockMvc.perform(delete("/wallet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(walletRequest)))
                .andExpect(status().isOk())
                .andExpect(content().string("Wallet deleted successfully"));

        assertFalse(walletRepository.existsById(wallet.getWalletId()));
    }
}

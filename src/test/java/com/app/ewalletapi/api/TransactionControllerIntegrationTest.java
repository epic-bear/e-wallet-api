package com.app.ewalletapi.api;

import com.app.ewalletapi.api.request.TransactionRequest;
import com.app.ewalletapi.api.response.TransactionResponse;
import com.app.ewalletapi.factory.UserFactory;
import com.app.ewalletapi.factory.WalletFactory;
import com.app.ewalletapi.model.TransactionStatus;
import com.app.ewalletapi.model.TransactionType;
import com.app.ewalletapi.model.User;
import com.app.ewalletapi.model.Wallet;
import com.app.ewalletapi.repository.WalletRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("application-test")
@AutoConfigureMockMvc
public class TransactionControllerIntegrationTest {

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
    void depositFunds() throws Exception {
        User user = userFactory.createTestUser();
        Wallet wallet = walletFactory.createTestWallet(user);
        TransactionRequest transactionRequest = new TransactionRequest();
        transactionRequest.setWalletId(wallet.getWalletId());
        transactionRequest.setUserName(user.getName());
        transactionRequest.setUserPassword(user.getPassword());
        transactionRequest.setAmount(100.0);

        MvcResult result = mockMvc.perform(post("/transaction/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transactionRequest)))
                .andExpect(status().isOk())
                .andReturn();

        TransactionResponse transactionResponse = objectMapper.readValue(result.getResponse().getContentAsString(), TransactionResponse.class);

        assertEquals(wallet.getWalletId(), transactionResponse.getWalletId());
        assertEquals(transactionRequest.getAmount(), transactionResponse.getAmount());
        assertNotNull(transactionResponse.getDate());
        assertEquals(TransactionType.DEPOSIT, transactionResponse.getTransactionType());
        assertNotNull(transactionResponse.getStatus());
    }

    @Test
    void withdrawFunds() throws Exception {
        User user = userFactory.createTestUser();
        Wallet wallet = walletFactory.createTestWallet(user);
        wallet.setBalance(100);
        walletRepository.save(wallet);
        TransactionRequest transactionRequest = new TransactionRequest();
        transactionRequest.setWalletId(wallet.getWalletId());
        transactionRequest.setUserName(user.getName());
        transactionRequest.setUserPassword(user.getPassword());
        transactionRequest.setAmount(50);

        MvcResult result = mockMvc.perform(post("/transaction/withdraw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transactionRequest)))
                .andExpect(status().isOk())
                .andReturn();

        TransactionResponse transactionResponse = objectMapper.readValue(result.getResponse().getContentAsString(), TransactionResponse.class);

        assertEquals(wallet.getWalletId(), transactionResponse.getWalletId());
        assertEquals(50, transactionResponse.getAmount());
        assertNotNull(transactionResponse.getDate());
        assertEquals(TransactionType.WITHDRAW, transactionResponse.getTransactionType());
        assertEquals(TransactionStatus.SUCCESSFUL, transactionResponse.getStatus());
        Wallet updatedWallet = walletRepository.findById(wallet.getWalletId()).orElse(null);
        assertNotNull(updatedWallet);
        assertEquals(50, updatedWallet.getBalance());
    }
}

package com.app.ewalletapi.api.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionRequest {

    @NotNull
    private long walletId;
    @NotNull
    private long userId;
    @NotBlank
    private String userName;
    @NotBlank
    private String userPassword;
    @Positive
    private double amount;
}

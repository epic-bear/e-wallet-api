package com.app.ewalletapi.api.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@AllArgsConstructor
@Getter
@Setter
public class WalletResponse {

    private long walletId;
    private long userId;
    private double balance;
    private Set<Long> transactionIds;
}

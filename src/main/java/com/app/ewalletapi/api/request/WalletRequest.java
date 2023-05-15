package com.app.ewalletapi.api.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WalletRequest {

    private long userId;
    private long walletId;
    private String userName;
    private String userPassword;
}

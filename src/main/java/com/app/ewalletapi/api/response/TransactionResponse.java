package com.app.ewalletapi.api.response;

import com.app.ewalletapi.model.TransactionStatus;
import com.app.ewalletapi.model.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@AllArgsConstructor
@Getter
@Setter
public class TransactionResponse {

    private long transactionId;
    private long walletId;
    private double amount;
    private Date date;
    private TransactionType transactionType;
    private TransactionStatus status;

}

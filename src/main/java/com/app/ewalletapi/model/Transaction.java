package com.app.ewalletapi.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.util.Date;

@AllArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "transactions")
public class Transaction {

    public static final double SINGLE_TRANSACTION_LIMIT = 2000;
    public static final double SUSPICIOUS_TRANSACTION_THRESHOLD = 10000;
    @Id
    @GeneratedValue
    private long transactionId;
    @Positive
    private double amount;
    @NonNull
    private Date date;
    @NonNull
    private TransactionType transactionType;
    @NonNull
    private TransactionStatus status;
    @ManyToOne
    @JoinColumn(name = "wallet_id")
    private Wallet wallet;

    public Transaction() {

    }
}

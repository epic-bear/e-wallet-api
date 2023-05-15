package com.app.ewalletapi.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "wallets")
public class Wallet {

    @Id
    @GeneratedValue
    private long walletId;
    @Min(0)
    private double balance;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    @OneToMany(mappedBy = "wallet", cascade = CascadeType.ALL)
    private Set<Transaction> transactions = new HashSet<>();

    public Wallet() {

    }
}

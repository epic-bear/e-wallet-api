package com.app.ewalletapi.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "users")
public class User {

    public static final double DAILY_WITHDRAW_LIMIT = 5000;
    public static final byte SUSPICIOUS_USER_THRESHOLD = 5;
    public static final byte USER_BLOCK_THRESHOLD = 10;
    @Id
    @GeneratedValue
    private long userId;
    @NotBlank
    private String name;
    @NonNull
    private UserStatus userStatus;
    @NotBlank
    private String password;
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private Set<Wallet> wallets = new HashSet<>();

    public User() {

    }
}

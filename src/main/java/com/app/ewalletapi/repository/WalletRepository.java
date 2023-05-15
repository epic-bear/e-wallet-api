package com.app.ewalletapi.repository;

import com.app.ewalletapi.model.User;
import com.app.ewalletapi.model.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WalletRepository extends JpaRepository<Wallet, Long> {
    Optional<List<Wallet>> findAllWalletsByUser(User user);
}

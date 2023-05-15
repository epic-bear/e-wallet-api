package com.app.ewalletapi.factory;

import com.app.ewalletapi.model.User;
import com.app.ewalletapi.model.Wallet;
import com.app.ewalletapi.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class WalletFactory {

    private final WalletRepository walletRepository;

    @Autowired
    public WalletFactory(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }

    public Wallet createTestWallet(User user) {
        Wallet wallet = new Wallet();
        wallet.setUser(user);
        wallet.setBalance(0);
        walletRepository.save(wallet);
        return wallet;
    }
}

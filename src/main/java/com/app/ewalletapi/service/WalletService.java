package com.app.ewalletapi.service;

import com.app.ewalletapi.api.request.WalletRequest;
import com.app.ewalletapi.model.*;
import com.app.ewalletapi.repository.WalletRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Service
@Transactional
public class WalletService {

    private final WalletRepository walletRepository;
    private final UserService userService;
    private final MessageSource messageSource;

    @Autowired
    public WalletService(WalletRepository walletRepository, UserService userService, MessageSource messageSource) {
        this.walletRepository = walletRepository;
        this.userService = userService;
        this.messageSource = messageSource;
    }

    public Wallet createWallet(WalletRequest walletRequest) throws Exception {
        try {
            Wallet wallet = new Wallet();
            wallet.setUser(userService.getUserById(walletRequest.getUserId()));
            wallet.setBalance(0);
            return walletRepository.save(wallet);
        } catch (Exception e) {
            throw new Exception("Failed to create wallet: " + e.getMessage());
        }
    }

    public List<Wallet> getAllUserWallets(WalletRequest walletRequest) throws Exception {
        User user = userService.getUserById(walletRequest.getUserId());
        userService.checkUserCredentials(user, walletRequest.getUserName(), walletRequest.getUserPassword());
        return walletRepository.findAllWalletsByUser(user).get();
    }

    public Wallet getWallet(WalletRequest walletRequest) throws Exception {
        Wallet wallet = walletRepository.findById(walletRequest.getWalletId()).orElseThrow(() ->
                new Exception(messageSource.getMessage("WalletNotFound", new Object[]{walletRequest.getWalletId()}, Locale.getDefault())));
        userService.checkUserCredentials(wallet.getUser(), walletRequest.getUserName(), walletRequest.getUserPassword());
        return wallet;
    }

    public TransactionStatus withdrawFunds(Wallet wallet, double amount) {
        if (!isWithdrawAvailable(wallet, amount)) {
            return TransactionStatus.REJECTED;
        }
        return updateWallet(wallet, amount, TransactionType.WITHDRAW);
    }

    public TransactionStatus depositFunds(Wallet wallet, double amount) {
        return updateWallet(wallet, amount, TransactionType.DEPOSIT);
    }

    public TransactionStatus updateWallet(Wallet wallet, double amount, TransactionType transactionType) {
        int numberOfTransactions = userService.countTransactionsWithinHour(wallet.getUser());
        if (numberOfTransactions > User.USER_BLOCK_THRESHOLD) {
            userService.changeUserStatus(wallet.getUser(), UserStatus.BLOCKED);
            return TransactionStatus.REJECTED;
        }
        if (numberOfTransactions > User.SUSPICIOUS_USER_THRESHOLD) {
            userService.changeUserStatus(wallet.getUser(), UserStatus.SUSPICIOUS);
        }
        if (transactionType == TransactionType.DEPOSIT) {
            wallet.setBalance(wallet.getBalance() + amount);
        } else if (transactionType == TransactionType.WITHDRAW) {
            wallet.setBalance(wallet.getBalance() - amount);
        }
        walletRepository.save(wallet);
        return TransactionStatus.SUCCESSFUL;
    }

    public void deleteWallet(WalletRequest walletRequest) throws Exception {
        Wallet wallet = walletRepository.findById(walletRequest.getWalletId()).orElseThrow(() ->
                new Exception(messageSource.getMessage("WalletNotFound", new Object[]{walletRequest.getWalletId()}, Locale.getDefault())));
        walletRepository.delete(wallet);
    }

    private boolean isWithdrawAvailable(Wallet wallet, double amount) {
        return isAmountAvailable(wallet, amount) && !isDailyWithdrawLimitExceeded(wallet, amount);
    }

    private boolean isAmountAvailable(Wallet wallet, double amount) {
        return wallet.getBalance() >= amount;
    }

    private boolean isDailyWithdrawLimitExceeded(Wallet wallet, double amount) {
        LocalDate today = LocalDate.now();
        Set<Transaction> transactions = wallet.getTransactions();
        double dailyTotal = transactions.stream()
                .filter(transaction -> transaction.getTransactionType() == TransactionType.WITHDRAW)
                .filter(transaction -> transaction.getStatus() == TransactionStatus.SUCCESSFUL)
                .filter(transaction -> transaction.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().equals(today))
                .mapToDouble(Transaction::getAmount)
                .sum();
        return dailyTotal + amount > User.DAILY_WITHDRAW_LIMIT;
    }
}

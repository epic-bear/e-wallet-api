package com.app.ewalletapi.service;

import com.app.ewalletapi.api.request.TransactionRequest;
import com.app.ewalletapi.api.request.WalletRequest;
import com.app.ewalletapi.model.Transaction;
import com.app.ewalletapi.model.TransactionStatus;
import com.app.ewalletapi.model.TransactionType;
import com.app.ewalletapi.model.Wallet;
import com.app.ewalletapi.repository.TransactionRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@Transactional
public class TransactionService {

    private final WalletService walletService;
    private final UserService userService;
    private final TransactionRepository transactionRepository;

    @Autowired
    public TransactionService(WalletService walletService, UserService userService, TransactionRepository transactionRepository) {
        this.walletService = walletService;
        this.userService = userService;
        this.transactionRepository = transactionRepository;
    }

    public Transaction processTransaction(TransactionRequest transactionRequest, TransactionType transactionType) throws Exception {
        Wallet wallet = walletService.getWallet(new WalletRequest(transactionRequest.getUserId(),
                transactionRequest.getWalletId(),
                transactionRequest.getUserName(),
                transactionRequest.getUserPassword()));
        userService.checkUserAccess(wallet.getUser());
        Transaction transaction = new Transaction();
        transaction.setAmount(transactionRequest.getAmount());
        transaction.setTransactionType(transactionType);
        transaction.setWallet(wallet);
        TransactionStatus transactionStatus = checkAmount(transaction.getAmount());
        if (transactionStatus == TransactionStatus.NEW) {
            if (transactionType == TransactionType.DEPOSIT) {
                transaction.setStatus(walletService.depositFunds(wallet, transaction.getAmount()));
            } else {
                transaction.setStatus(walletService.withdrawFunds(wallet, transaction.getAmount()));
            }
        } else {
            transaction.setStatus(transactionStatus);
        }
        transaction.setDate(new Date());
        transactionRepository.save(transaction);
        return transaction;
    }

    private TransactionStatus checkAmount(double amount) {
        if (amount > Transaction.SUSPICIOUS_TRANSACTION_THRESHOLD) {
            return TransactionStatus.SUSPICIOUS;
        } else if (amount > Transaction.SINGLE_TRANSACTION_LIMIT) {
            return TransactionStatus.REJECTED;
        } else {
            return TransactionStatus.NEW;
        }
    }
}
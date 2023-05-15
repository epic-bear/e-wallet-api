package com.app.ewalletapi.api;

import com.app.ewalletapi.api.request.WalletRequest;
import com.app.ewalletapi.api.response.WalletResponse;
import com.app.ewalletapi.model.Transaction;
import com.app.ewalletapi.model.Wallet;
import com.app.ewalletapi.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/wallet")
public class WalletController {

    private final WalletService walletService;

    @Autowired
    public WalletController(final WalletService walletService) {
        this.walletService = walletService;
    }

    @PostMapping
    public ResponseEntity<WalletResponse> createWallet(@RequestBody WalletRequest walletRequest) throws Exception {
        Wallet wallet = walletService.createWallet(walletRequest);
        WalletResponse response = new WalletResponse(wallet.getWalletId(), wallet.getUser().getUserId(),
                wallet.getBalance(), wallet.getTransactions().stream().map(Transaction::getTransactionId).collect(Collectors.toSet()));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/all")
    public List<WalletResponse> getAllUserWallets(@RequestBody WalletRequest walletRequest) throws Exception {
        List<Wallet> wallets = walletService.getAllUserWallets(walletRequest);
        return wallets.stream()
                .map(wallet -> new WalletResponse(wallet.getWalletId(), wallet.getUser().getUserId(),
                        wallet.getBalance(), wallet.getTransactions().stream().map(Transaction::getTransactionId).collect(Collectors.toSet())))
                .collect(Collectors.toList());
    }

    @GetMapping
    public ResponseEntity<WalletResponse> getWalletById(@RequestBody WalletRequest walletRequest) throws Exception {
        Wallet wallet = walletService.getWallet(walletRequest);
        WalletResponse response = new WalletResponse(wallet.getWalletId(), wallet.getUser().getUserId(),
                wallet.getBalance(), wallet.getTransactions().stream().map(Transaction::getTransactionId).collect(Collectors.toSet()));
        return ResponseEntity.ok(response);
    }

    @DeleteMapping
    public ResponseEntity<String> deleteWallet(@RequestBody WalletRequest walletRequest) {
        try {
            walletService.deleteWallet(walletRequest);
            return ResponseEntity.ok("Wallet deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Wallet not found");
        }
    }
}
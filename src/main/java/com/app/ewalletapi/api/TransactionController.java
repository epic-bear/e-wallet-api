package com.app.ewalletapi.api;

import com.app.ewalletapi.api.request.TransactionRequest;
import com.app.ewalletapi.api.response.TransactionResponse;
import com.app.ewalletapi.model.Transaction;
import com.app.ewalletapi.model.TransactionType;
import com.app.ewalletapi.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/transaction")
public class TransactionController {

    private final TransactionService transactionService;

    @Autowired
    public TransactionController(final TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/deposit")
    public ResponseEntity<TransactionResponse> depositFunds(@RequestBody TransactionRequest transactionRequest) throws Exception {
        Transaction transaction = transactionService.processTransaction(transactionRequest, TransactionType.DEPOSIT);
        TransactionResponse response = new TransactionResponse(transaction.getTransactionId(),
                transaction.getWallet().getWalletId(),
                transaction.getAmount(),
                transaction.getDate(),
                transaction.getTransactionType(),
                transaction.getStatus()
        );
        return ResponseEntity.ok(response);
    }

    @PostMapping("/withdraw")
    public ResponseEntity<TransactionResponse> withdrawFunds(@RequestBody TransactionRequest transactionRequest) throws Exception {
        Transaction transaction = transactionService.processTransaction(transactionRequest, TransactionType.WITHDRAW);
        TransactionResponse response = new TransactionResponse(transaction.getTransactionId(),
                transaction.getWallet().getWalletId(),
                transaction.getAmount(),
                transaction.getDate(),
                transaction.getTransactionType(),
                transaction.getStatus()
        );
        return ResponseEntity.ok(response);
    }
}

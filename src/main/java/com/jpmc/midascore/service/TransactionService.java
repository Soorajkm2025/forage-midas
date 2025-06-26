package com.jpmc.midascore.service;

import com.jpmc.midascore.external.Incentive;
import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.persistence.TransactionRecord;
import com.jpmc.midascore.persistence.TransactionRecordRepository;
import com.jpmc.midascore.persistence.UserRecord;
import com.jpmc.midascore.persistence.UserRecordRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Service
public class TransactionService {

    private final TransactionRecordRepository transactionRecordRepository;
    private final UserRecordRepository userRecordRepository;
    private final RestTemplate restTemplate;

    public TransactionService(TransactionRecordRepository transactionRecordRepository,
                              UserRecordRepository userRecordRepository,
                              RestTemplate restTemplate) {
        this.transactionRecordRepository = transactionRecordRepository;
        this.userRecordRepository = userRecordRepository;
        this.restTemplate = restTemplate;
    }

    // ✅ Optional: Show all users at startup (useful for debugging if Wilbur is loaded)
    @PostConstruct
    public void logAllUsers() {
        System.out.println("📋 Existing users in DB:");
        userRecordRepository.findAll().forEach(user ->
                System.out.println(" - " + user.getName() + " | Balance: " + user.getBalance())
        );
    }

    public void processTransaction(Transaction tx) {
        Optional<UserRecord> senderOpt = userRecordRepository.findById(tx.getSenderId());
        Optional<UserRecord> recipientOpt = userRecordRepository.findById(tx.getRecipientId());

        if (senderOpt.isEmpty() || recipientOpt.isEmpty()) {
            return; // Either sender or recipient is invalid
        }

        UserRecord sender = senderOpt.get();
        UserRecord recipient = recipientOpt.get();

        if (sender.getBalance() < tx.getAmount()) {
            return; // Not enough balance
        }

        // ✅ Call incentive API
        Incentive incentive = restTemplate.postForObject("http://localhost:8080/incentive", tx, Incentive.class);
        float incentiveAmount = (incentive != null && incentive.getAmount() != null) ? incentive.getAmount() : 0f;

        // ✅ Update balances
        sender.setBalance(sender.getBalance() - tx.getAmount());
        recipient.setBalance(recipient.getBalance() + tx.getAmount() + incentiveAmount);

        userRecordRepository.save(sender);
        userRecordRepository.save(recipient);

        // ✅ Save transaction record
        TransactionRecord transactionRecord = new TransactionRecord(sender, recipient, tx.getAmount(), incentiveAmount);
        transactionRecordRepository.save(transactionRecord);

        // ✅ Log if Wilbur is involved
        String senderName = sender.getName().trim();
        String recipientName = recipient.getName().trim();

        if ("wilbur".equalsIgnoreCase(senderName) || "wilbur".equalsIgnoreCase(recipientName)) {
            float wilburBalance = "wilbur".equalsIgnoreCase(senderName) ? sender.getBalance() : recipient.getBalance();
            System.out.println("💰 WILBUR BALANCE: " + wilburBalance);
        }
    }
}

package com.jpmc.midascore.controller;

import com.jpmc.midascore.persistence.UserRecord;
import com.jpmc.midascore.persistence.UserRecordRepository;
import com.jpmc.midascore.foundation.Balance;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
public class BalanceController {

    private final UserRecordRepository userRecordRepository;

    public BalanceController(UserRecordRepository userRecordRepository) {
        this.userRecordRepository = userRecordRepository;
    }

    @GetMapping("/balance")
    public Balance getBalance(@RequestParam("userId") Long userId) {
        Optional<UserRecord> userOpt = userRecordRepository.findById(userId);
        float balance = userOpt.map(UserRecord::getBalance).orElse(0f);
        return new Balance(balance);
    }
}

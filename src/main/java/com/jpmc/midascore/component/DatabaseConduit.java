package com.jpmc.midascore.component;

import com.jpmc.midascore.persistence.UserRecordRepository;
import com.jpmc.midascore.persistence.UserRecord;
import org.springframework.stereotype.Component;

@Component
public class DatabaseConduit {
    private final UserRecordRepository userRepository;

    public DatabaseConduit(UserRecordRepository userRecordRepository) {
        this.userRepository = userRecordRepository;
    }

    public void save(UserRecord userRecord) {
        userRepository.save(userRecord);
    }

}

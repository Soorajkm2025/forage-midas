package com.jpmc.midascore.persistence;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class UserRecord {

    @Id
    @jakarta.persistence.GeneratedValue
    private Long id;


    private String name;

    private Float balance;

    // Default constructor (required by JPA)
    public UserRecord() {
    }

    // Full constructor (id, name, balance)
    public UserRecord(Long id, String name, Float balance) {
        this.id = id;
        this.name = name;
        this.balance = balance;
    }

    // New constructor (name, balance) — for convenience during test/user creation
    public UserRecord(String name, Float balance) {
        this.name = name;
        this.balance = balance;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Float getBalance() {
        return balance;
    }

    public void setBalance(Float balance) {
        this.balance = balance;
    }
}

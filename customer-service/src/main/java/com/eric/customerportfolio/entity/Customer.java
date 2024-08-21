package com.eric.customerportfolio.entity;

import org.springframework.data.annotation.Id;

public class Customer {

    @Id
    private Integer id;
    private final String name;
    private final Integer balance;

    public Customer(Integer id, String name, Integer balance) {
        this.id = id;
        this.name = name;
        this.balance = balance;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Integer getBalance() {
        return balance;
    }

}

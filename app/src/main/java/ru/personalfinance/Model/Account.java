package ru.personalfinance.Model;

public class Account {

    private int amount;
    private String type;
    private String name;
    private String id;

    public Account(int amount, String type, String name, String id) {
        this.amount = amount;
        this.type = type;
        this.name = name;
        this.id = id;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Account() {

    }

    public Account(String name) {
        this.name = name;
    }

}

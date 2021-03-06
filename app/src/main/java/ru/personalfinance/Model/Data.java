package ru.personalfinance.Model;

public class Data {

    private int amount;
    private String type;
    private String change;
    private String note;
    private String id;
    private long date;
    private String account;

    public Data(int amount, String type, String change, String note, String id, long date, String account) {
        this.amount = amount;
        this.type = type;
        this.change = change;
        this.note = note;
        this.id = id;
        this.date = date;
        this.account = account;
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

    public String getChange() {
        return change;
    }

    public void setChange(String change) {
        this.change = change;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public Data() {

    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }
}

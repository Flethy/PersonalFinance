package ru.personalfinance.Model;

public class Data {

    private int amount;
    private String type;
    private String change;
    private String note;
    private String id;

    public Data(int amount, String type, String change, String note, String id, String date) {
        this.amount = amount;
        this.type = type;
        this.change = change;
        this.note = note;
        this.id = id;
        this.date = date;
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    private String date;

    public Data() {

    }

}

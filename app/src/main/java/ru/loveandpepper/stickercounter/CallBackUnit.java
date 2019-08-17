package ru.loveandpepper.stickercounter;

public class CallBackUnit {
    private String phoneNumber;
    private String name;
    private String comment;

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getName() {
        return name;
    }

    public String getComment() {
        return comment;
    }

    public CallBackUnit(String phoneNumber, String name, String comment) {
        this.phoneNumber = phoneNumber;
        this.name = name;
        this.comment = comment;
    }

    @Override
    public String toString() {
        return phoneNumber +
                " | Имя: " + name  +
                " | " + comment;
    }
}

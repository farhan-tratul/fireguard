package edu.ewubd.fireguard;

public class EmergencyContact {
    private String name;
    private String phoneNumber;

    public EmergencyContact(String name, String phoneNumber) {
        this.name = name;
        this.phoneNumber = phoneNumber;
    }

    public String getName() {
        return name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }
}

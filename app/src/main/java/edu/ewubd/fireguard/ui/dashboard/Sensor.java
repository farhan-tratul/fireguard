package edu.ewubd.fireguard.ui.dashboard;

public class Sensor {

    public String humidity;

    public String   gas_status;
    public String temperature;

    public Sensor(String humidity, String gas_status, String temperature) {
       this.humidity=humidity;
       this.gas_status=gas_status;
       this.temperature=temperature;
    }
}

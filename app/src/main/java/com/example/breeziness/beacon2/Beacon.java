package com.example.breeziness.beacon2;

class Beacon {
    private int major;
    private int minor;
    private String uuid;
    private int rssi;
    private double distance;
    private int txPower;

    public Beacon(String uuid, int major, int minor, int rssi, double distance, int txPower) {
        this.uuid = uuid;
        this.major = major;
        this.minor = minor;
        this.rssi = rssi;
        this.distance = distance;
        this.txPower = txPower;
    }

    public String getTxPower() {
        return "" + txPower;
    }

    public String getDistance() {
        return "" + distance;
    }

    public String getRssi() {
        return "" + rssi;
    }

    public String getMajor() {
        return "" + major;
    }

    public String getMinor() {
        return "" + minor;
    }

    public String getUuid() {
        return uuid;
    }

    @Override
    public String toString() {
        return " uuid:" + uuid + " major:" + major + " minor:" + minor;
    }

}

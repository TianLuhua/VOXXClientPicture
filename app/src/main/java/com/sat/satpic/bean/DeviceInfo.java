package com.sat.satpic.bean;

public class DeviceInfo {

    private String ipAddress;
    private String Name;

    public DeviceInfo(String ipAddress, String name) {
        super();
        this.ipAddress = ipAddress;
        Name = name;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    @Override
    public String toString() {
        return "DeviceInfo{" +
                "ipAddress='" + ipAddress + '\'' +
                ", Name='" + Name + '\'' +
                '}';
    }
}

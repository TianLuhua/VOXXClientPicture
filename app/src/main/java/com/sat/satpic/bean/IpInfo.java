package com.sat.satpic.bean;

public class IpInfo {

    private String ipAddress;
    private int num;


    public IpInfo(String ipAddress, int num) {
        this.ipAddress = ipAddress;
        this.num = num;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    @Override
    public String toString() {
        return "IpInfo{" +
                "ipAddress='" + ipAddress + '\'' +
                ", num=" + num +
                '}';
    }
}

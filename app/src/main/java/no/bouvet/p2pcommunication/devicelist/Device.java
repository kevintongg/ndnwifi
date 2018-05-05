package no.bouvet.p2pcommunication.devicelist;

import android.util.Log;

/**
 * Created by micha on 2/25/2018.
 */

public class Device {

  private String deviceName;
  private String macAddress;
  private String ip;

  public Device(String deviceName, String macAddress) {
    this.deviceName = deviceName;
    this.macAddress = macAddress;
    Log.d("------ Device ------ :", String.format("%s | %s", deviceName, macAddress));

  }

  public Device(String deviceName, String macAddress, String ip) {
    this.deviceName = deviceName;
    this.macAddress = macAddress;
    this.ip = ip;
  }

  public String getDeviceName() {
    return deviceName;
  }

  public String getMacAddress() {
    return macAddress;
  }

  public String getIp() {
    return ip;
  }

  public void setDeviceName(String deviceName) {
    this.deviceName = deviceName;
  }

  public void setMacAddress(String macAddress) {
    this.macAddress = macAddress;
  }

  public void setIp(String ip) {
    this.ip = ip;
  }
}

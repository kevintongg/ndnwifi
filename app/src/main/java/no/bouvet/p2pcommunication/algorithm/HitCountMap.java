package no.bouvet.p2pcommunication.algorithm;

import android.support.annotation.NonNull;
import android.util.Log;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.Set;

/**
 * Created by Syntax Mike on 4/27/2018.
 */

public class HitCountMap {

  private final Map<String, HitCounter> INTEREST_MAP;

  HitCountMap() {
    INTEREST_MAP = new HashMap<>();
  }

  // data was returned successfully for interest from ip
  public void addHit(String ip) {
    if (INTEREST_MAP.containsKey(ip)) {
      INTEREST_MAP.get(ip).addHit(ip);
    } else {
      HitCounter counter = new HitCounter();
      counter.addHit(ip);
      INTEREST_MAP.put(ip, counter);
    }
  }

  // returns a priority queue of IpInterfaces
  public PriorityQueue<IpInterface> ipByCount(String interest, Set<String> ips) {
    String TAG = "HitCountMap";
    Log.d(TAG, "number of interfaces: " + ips.size());
    Map<String, Integer> count = new HashMap<>(ips.size());
    for (String ip : ips) {
      if (!count.containsKey(ip)) {
        count.put(ip, 0);
      }
    }
    PriorityQueue<IpInterface> pq = new PriorityQueue<>();
    if (INTEREST_MAP.containsKey(interest)) {
      HitCounter htc = INTEREST_MAP.get(interest);
      Map<String, Integer> listOfMaps = htc.INTERFACE_MAP;

      for (Map.Entry<String, Integer> entry : listOfMaps.entrySet()) {
        int max = Math.max(count.get(entry.getKey()), entry.getValue());
        count.put(entry.getKey(), max);
      }
    }

    for (Object o : count.entrySet()) {
      Entry pair = (Entry) o;
      IpInterface inter = new IpInterface((String) pair.getKey(), (int) pair.getValue());
      pq.add(inter);
      Log.d(TAG, "adding interface: " + inter.getIpAddress() + " with count: " + inter.getCount());
    }
    return pq;
  }

  // return the ip with the highest hit count for interest
  public String getBestIP() {

    double maxHits = 0;
    String maxIP = "";

    for (Map.Entry<String, HitCounter> entry : INTEREST_MAP.entrySet()) {
      HitCounter value = entry.getValue();

      if (value.getMaxHits() > maxHits) {
        maxHits = value.getMaxHits();
        maxIP = value.maxHitIP;
      }
    }

    return maxIP;
  }

  public boolean hasBestIP(String interest) {
    return INTEREST_MAP.containsKey(interest);
  }

  static class HitCounter {

    private final Map<String, Integer> INTERFACE_MAP;
    private Integer maxHits;
    private String maxHitIP;

    HitCounter() {
      INTERFACE_MAP = new HashMap<>();
      maxHits = null;
      maxHitIP = null;
    }

    public String getMaxHitIP() {
      // returns the ip with the highest number of hits
      return maxHitIP;
    }

    Integer getMaxHits() {
      return maxHits;
    }

    // adds 1 hit to this ip
    void addHit(String ip) {
      Integer count = INTERFACE_MAP.containsKey(ip) ? INTERFACE_MAP.get(ip) + 1 : 1;
      INTERFACE_MAP.put(ip, count);
      if (maxHits == null || count > maxHits) {
        maxHits = count;
        maxHitIP = ip;
      }
    }
  }

  static class IpInterface implements Comparable<IpInterface> {

    private String ipAddress;
    private int count;

    IpInterface(String Ip, int counter) {
      this.ipAddress = Ip;
      this.count = counter;
    }

    int getCount() {
      return count;
    }

    public void setCount(int count) {
      this.count = count;
    }

    String getIpAddress() {
      return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
      this.ipAddress = ipAddress;
    }

    @Override
    public int compareTo(@NonNull IpInterface other) {
      return other.count - this.count;
    }
  }
}


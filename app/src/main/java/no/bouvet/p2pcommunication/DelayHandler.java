package no.bouvet.p2pcommunication;

import android.os.Handler;

/**
 * Created by micha on 2/2/2018.
 *
 * Borrowed from https://stackoverflow.com/questions/15874117/how-to-set-delay-in-android
 */

public class DelayHandler {

  public static void delay(int secs, final DelayCallback delayCallback) {
    Handler handler = new Handler();
    handler.postDelayed(delayCallback::afterDelay, secs * 1000);
  }

  public interface DelayCallback {
    void afterDelay();
  }
}

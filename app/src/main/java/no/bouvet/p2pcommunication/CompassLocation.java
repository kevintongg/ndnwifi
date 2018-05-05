package no.bouvet.p2pcommunication;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import java.util.Objects;

/**
 * Created by micha on 11/10/2017.
 */

public class CompassLocation implements SensorEventListener {

  private static final String TAG = "CompassActivity";
  private SensorManager mSensorManager;
  private SensorEvent mSensorEvent;
  private Sensor mSensor;
  private Sensor gSensor;
  private float[] mGravity = new float[3];
  private float[] mGeomagnetic = new float[3];
  private float correctAzimuth = 0;

  public CompassLocation(Context context) {
    mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
    gSensor = Objects.requireNonNull(mSensorManager).getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
  }

  public void onStart() {
    mSensorManager.registerListener(this, gSensor, SensorManager.SENSOR_DELAY_GAME);
    mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_GAME);
  }

  @Override
  public void onAccuracyChanged(Sensor sensor, int accuracy) {

  }

  @Override
  public void onSensorChanged(SensorEvent event) {

    final float alpha = 0.97f;

    synchronized (this) {
      if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
        mGravity[0] = (alpha * mGravity[0]) + ((1 - alpha) * event.values[0]);
        mGravity[1] = (alpha * mGravity[1]) + ((1 - alpha) * event.values[1]);
        mGravity[2] = (alpha * mGravity[2]) + ((1 - alpha) * event.values[2]);
      }
      if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
        mGeomagnetic[0] = (alpha * mGeomagnetic[0]) + ((1 - alpha) * event.values[0]);
        mGeomagnetic[1] = (alpha * mGeomagnetic[1]) + ((1 - alpha) * event.values[1]);
        mGeomagnetic[2] = (alpha * mGeomagnetic[2]) + ((1 - alpha) * event.values[2]);
      }

      float R[] = new float[9];
      float I[] = new float[9];
      boolean success = SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic);

      if (success) {
        float orientation[] = new float[3];
        SensorManager.getOrientation(R, orientation);
        float azimuth = (float) Math.toDegrees(orientation[0]);
        azimuth = (azimuth + 360) % 360;
        //Log.d(TAG, "azimuth (deg): " + azimuth);
      }
    }
  }

  public float getCorrectAzimuth() {
    return correctAzimuth;
  }

  public void setCorrectAzimuth(float correctAzimuth) {
    this.correctAzimuth = correctAzimuth;
  }
}

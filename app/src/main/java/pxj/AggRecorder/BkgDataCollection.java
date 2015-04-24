package pxj.AggRecorder;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

/**
 * Created by Rice on 4/12/2015.
 */
public class BkgDataCollection extends IntentService implements SensorEventListener{

    public static final String LOG_TAG = "BkgDataCollection";
    private SensorManager sm = null;
    private Sensor accSensor = null;
//    private SensorEventListener lsn = null;

    private LocationManager lm = null;
    private Location lc = null;

    private float[] accData = new float[3];
    private float[] locData = new float[3];

    public BkgDataCollection(){
        super("BkgDataCollection");
    }
    @Override
   public void onCreate(){
//        sm = (SensorManager) getSystemService(SENSOR_SERVICE);
//        accSensor = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
//        lsn = new SensorEventListener(){
//            @Override
//            public void onSensorChanged(SensorEvent event) {
////                new FetchSensorData().execute(event);
//            }
//
//            @Override
//            public void onAccuracyChanged(Sensor sensor, int accuracy) {
//
//            }
//        };
//        sm.registerListener(lsn, accSensor, SensorManager.SENSOR_DELAY_FASTEST);

    }

    @Override
    protected void onHandleIntent(Intent workIntent){

//        Get sensors
        sm = (SensorManager) getSystemService(SENSOR_SERVICE);
        accSensor = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
//        lsn = new SensorEventListener(){
//            @Override
//            public void onSensorChanged(SensorEvent event) {
////                new FetchSensorData().execute(event);
//            }
//
//            @Override
//            public void onAccuracyChanged(Sensor sensor, int accuracy) {
//
//            }
//        };

        sm.registerListener(this, accSensor, SensorManager.SENSOR_DELAY_FASTEST);





    }

    @Override
            public void onSensorChanged(SensorEvent event) {
//                new FetchSensorData().execute(event);
        Log.d(LOG_TAG,"Acc updating");
        accData[0] = event.values[0];
        accData[1] = event.values[1];
        accData[2] = event.values[2];
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }

}

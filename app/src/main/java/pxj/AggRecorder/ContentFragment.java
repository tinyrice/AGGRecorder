package pxj.AggRecorder;

/**
 * Created by Xiewen on 2015/4/8.
 */

import android.app.Fragment;
//import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
//import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.app.Activity;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import static android.location.LocationManager.NETWORK_PROVIDER;
//import java.util.SimpleTimeZone;

public class ContentFragment extends Fragment {

    public ContentFragment() {

    }


    private ArrayAdapter<String> mDevAdaptor;

    private SensorManager sm = null;
    private Sensor accSensor = null;
    private SensorEventListener lsn = null;
    private float gravity[] = new  float[3];
    private float linear_acceleration[] = new float[3];

    private LocationManager lm = null;
//    private Location lct = null;
    private LocationListener gpslistener = null;
    double alt, lat, lng;

    File sdCard = Environment.getExternalStorageDirectory();
    File dirTosave = new File (sdCard.getAbsolutePath()+"/AggData");

    FileOutputStream outputStream = null;
    private Activity mActivity;


    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        gpslistener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if (location != null) {
                    Log.i("LocationUpdate", "Location changed : Lat: "
                            + location.getLatitude() + " Lng: "
                            + location.getLongitude());
                }
//                makeUseOfNewLocation(location);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        lm = (LocationManager)mActivity.getSystemService(mActivity.LOCATION_SERVICE);
        lm.requestLocationUpdates(NETWORK_PROVIDER, 0, 0, gpslistener);
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500, 0, gpslistener);


//        Toast.makeText(mActivity, "is writable"+ isExternalStorageWritable(), Toast.LENGTH_SHORT).show();
        SimpleDateFormat dateforfile = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String currentFilename = dateforfile.format(new Date());
        Log.i("dir",dirTosave.toString());
        Log.i("CurrentFilename",currentFilename);
        if(!dirTosave.mkdirs()){
            Log.e("dir", "Directory not created");
        }
        File currentFile = new File(dirTosave,currentFilename+".txt");

        try {
//            outputStream = mActivity.openFileOutput(currentFilename, Context.MODE_PRIVATE);
            outputStream = new FileOutputStream(currentFile);
            outputStream.write("Time,LinearAcc_x,LinearAcc_y,LinearAcc_z,GPS_Alt,GPS_Lat,GPS_Long\n".getBytes());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;


    }

    @Override
    public void onResume() {
        super.onResume();
//        SensorManager sm = (SensorManager)this.getActivity().getSystemService(mActivity.SENSOR_SERVICE);
        sm = (SensorManager) mActivity.getSystemService(mActivity.SENSOR_SERVICE);
        accSensor = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        lsn = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {

//                new FetchSensorData().execute(event);
                final float alpha = 0.8f;
                String[] output_data_as_String = new String [6];

                gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
                gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
                gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];

                linear_acceleration[0] = event.values[0] - gravity[0];
                linear_acceleration[1] = event.values[1] - gravity[1];
                linear_acceleration[2] = event.values[2] - gravity[2];

                Location lct = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                lct = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//                System.out.println("Location??"+ lct +"\n");
                Log.d("Location",lct.toString());


                if (lct != null){
                    alt = lct.getAltitude();
                    lat = lct.getLatitude();
                    lng = lct.getLongitude();
                }
                else
                {
                    alt = 0.0;
                    lat = 0.0;
                    lng = 0.0;
                }



                output_data_as_String[0] = Float.toString(linear_acceleration[0]);
                output_data_as_String[1] = Float.toString(linear_acceleration[1]);
                output_data_as_String[2] = Float.toString(linear_acceleration[2]);
                output_data_as_String[3] = Double.toString(alt);
                output_data_as_String[4] = Double.toString(lat);
                output_data_as_String[5] = Double.toString(lng);




                new FetchSensorData().execute(output_data_as_String);



//                String currentTime = new SimpleDateFormat("HHmmssss").format(new Date());
                String currentTime = System.currentTimeMillis()+"";


                try {
                    outputStream.write((currentTime + ",").getBytes());
                    outputStream.write((output_data_as_String[0] + ",").getBytes());
                    outputStream.write((output_data_as_String[1] + ",").getBytes());
                    outputStream.write((output_data_as_String[2] + ",").getBytes());
                    outputStream.write((output_data_as_String[3] + ",").getBytes());
                    outputStream.write((output_data_as_String[4] + ",").getBytes());
                    outputStream.write((output_data_as_String[5] + "\n").getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }


            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };

        sm.registerListener(lsn, accSensor, SensorManager.SENSOR_DELAY_GAME);

//Location find
        lm = (LocationManager) mActivity.getSystemService(mActivity.LOCATION_SERVICE);


    }

    @Override
    public void onPause() {

        super.onPause();
        sm.unregisterListener(lsn, accSensor);

    }

    @Override
    public void onStop() {
        super.onStop();
        sm.unregisterListener(lsn, accSensor);
        lm.removeUpdates(gpslistener);
        try {
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            Toast.makeText(mActivity, "Read Sensor data!", Toast.LENGTH_SHORT).show();


            // new FetchSensorData().execute(event);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_content, container, false);

        String[] dev_data = new String[]{
                "Accelerator x = -0.31",
                "Accelerator y = 0.05",
                "Accelerator z = 10.16",
                "Gyroscope x = 0.02",
                "Gyroscope y = 0.09",
                "Gyroscope z = -0.38",
                "GPS latitude = 29,40,2",
                "GPS longitude = -95,26,24",
                "GPS Altitude = -59.80"
        };
        List<String> fakedata = new ArrayList<String>(Arrays.asList(dev_data));
        mDevAdaptor = new ArrayAdapter<String>(
                getActivity(),
                R.layout.list_sensor,
                R.id.list_sensor,
                fakedata);

        View rootview = inflater.inflate(R.layout.fragment_main, container, false);

        ListView listview = (ListView) rootView.findViewById(R.id.list_item);
        listview.setAdapter(mDevAdaptor);

        return rootView;
    }

    public class FetchSensorData extends AsyncTask<String[], Void, String[]> {


        @Override
        protected String[] doInBackground(String[]... data_as_String) {

//            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
//            String currentDateandTime = sdf.format(new Date());



            return data_as_String[0];

        }


        @Override
        protected void onPostExecute(String[] sensorReadout) {


            if (sensorReadout != null) {



                sensorReadout[0] = "Accelerometer x = " + sensorReadout[0];
                sensorReadout[1] = "Accelerometer y = " + sensorReadout[1];
                sensorReadout[2] = "Accelerometer z = " + sensorReadout[2];
                sensorReadout[3] = "GPS Altitude = " + sensorReadout[3];
                sensorReadout[4] = "GPS Latitude = " + sensorReadout[4];
                sensorReadout[5] = "GPS Longitude = " + sensorReadout[5];
//                sensorReadout[6] = "Time = " + currentDateandTime;

                mDevAdaptor.clear();
                mDevAdaptor.addAll(sensorReadout);
                mDevAdaptor.notifyDataSetChanged();
            } else {
                Toast.makeText(getActivity(), "sensorReadout is null!", Toast.LENGTH_SHORT).show();
            }

//            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HH_mm_ss_SSS");
//            String currentDateandTime = sdf.format(new Date());
//            Log.i("Time test", currentDateandTime.toString());
//            try {
//                outputStream.write(currentDateandTime.getBytes());
//            } catch (IOException e) {
//                e.printStackTrace();
//            }

        }
    }



}

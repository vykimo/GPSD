package vykk.clientwifi;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import android.location.Location;

import com.google.gson.Gson;


public class MainActivity extends Activity {

    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION=1;
    MockLocationProvider mock;
    TextView textResponse;
    EditText editTextAddress, editTextPort;
    Button buttonConnect, buttonClear;
    Location location;
    Socket socket = null;

    private Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextAddress = (EditText) findViewById(R.id.address);
        editTextPort = (EditText) findViewById(R.id.port);
        buttonConnect = (Button) findViewById(R.id.connect);
        buttonClear = (Button) findViewById(R.id.clear);
        textResponse = (TextView) findViewById(R.id.response);

        buttonConnect.setOnClickListener(buttonConnectOnClickListener);

        buttonClear.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                textResponse.setText("");
            }
        });

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){
            //demander la permission
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }

        try {
            mock = new MockLocationProvider(LocationManager.GPS_PROVIDER, this);
        }
        catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    OnClickListener buttonConnectOnClickListener = new OnClickListener(){
        @Override
        public void onClick(View arg0) {
            MyClientTask myClientTask = new MyClientTask(
                    editTextAddress.getText().toString(),
                    Integer.parseInt(editTextPort.getText().toString()));
            myClientTask.execute();
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[],
                                           int[] grantResults) {
    }

    @Override
    protected void onDestroy() {
        mock.shutdown();
        super.onDestroy();
    }


    public class MyClientTask extends AsyncTask<Void, Void, Void> {

        String dstAddress;
        int dstPort;
        String response = "";

        MyClientTask(String addr, int port){
            dstAddress = addr;
            dstPort = port;
        }

        @Override
        protected Void doInBackground(Void... arg0) {

            try {
                while(true){
                    socket = new Socket(dstAddress, dstPort);
                    InputStream inputStream = socket.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    location= gson.fromJson(reader, Location.class);
                    if (location!=null ) {
                        response += "Latitude : " + String.valueOf(location.getLatitude()) + "\nLongitude : " + String.valueOf(location.getLongitude())+"\n";
                        mock.pushLocation(location);
                    }
                    else {
                        response += "Aucun signal Gps, veuillez réessayer à un autre endroit"+"\n";
                    }
                    textResponse.post(new Runnable() {
                        public void run() {
                            textResponse.setText(response);
                        }
                    });
                }
            } catch (UnknownHostException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                response = "UnknownHostException: " + e.toString();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                response = "IOException: " + e.toString();
            } catch (SecurityException e) {
                e.printStackTrace();
                response = "SecurityException: " + e.toString();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
                response = "IllegalArgumentException: " + e.toString();
            }
            finally {
                if(socket != null){
                    try {
                        socket.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            finish();
        }

    }

}
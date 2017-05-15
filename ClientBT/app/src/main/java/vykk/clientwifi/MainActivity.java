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

import java.io.InputStreamReader;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;


public class MainActivity extends Activity {

    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION=1;
    private static final String TAG = "MyActivity";
    MockLocationProvider mock;
    TextView textResponse;
    EditText editTextAddress, editTextPort;
    Button buttonConnect,buttonStop;
    Position position;
    Socket socket = null;
    Boolean stop=false;

    Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextAddress = (EditText) findViewById(R.id.address);
        editTextPort = (EditText) findViewById(R.id.port);
        buttonConnect = (Button) findViewById(R.id.connect);
        buttonStop = (Button) findViewById(R.id.stop);
        textResponse = (TextView) findViewById(R.id.response);

        buttonConnect.setOnClickListener(buttonConnectOnClickListener);

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
        buttonStop.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mock.shutdown();
                stop=true;
            }
        });
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
        super.onDestroy();
        mock.shutdown();
        if(socket != null){
            try {
                socket.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }


    public class MyClientTask extends AsyncTask<Void, Void, Void> {

        String dstAddress;
        int dstPort;
        int count = 0;
        String response;

        MyClientTask(String addr, int port){
            dstAddress = addr;
            dstPort = port;
        }

        @Override
        protected Void doInBackground(Void... arg0) {

            try {
                socket = new Socket(dstAddress, dstPort);
                while(!stop){
                    InputStream inputStream = socket.getInputStream();
                    JsonReader reader = new JsonReader(new InputStreamReader(inputStream));
                    position= gson.fromJson(reader, Position.class);
                    count++;
                    if (position!=null) {
                        response = "#" + count + " Latitude : " + String.valueOf(position.getLatitude()) + "\nLongitude : " + String.valueOf(position.getLongitude())+"\n";
                        mock.pushLocation(position);
                    }
                    else {
                        response = "#" + count + " Aucun signal Gps, veuillez réessayer à un autre endroit"+"\n";
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
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }

    }

}
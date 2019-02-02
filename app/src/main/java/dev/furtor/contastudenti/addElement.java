package dev.furtor.contastudenti;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;

import java.util.LinkedList;

public class addElement extends AppCompatActivity {
    SharedPreferences sharedPref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_element);



        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Button button = findViewById(R.id.buttonSubscribe);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(getBaseContext(), MainActivity.class);

                //Set the Data to pass
                EditText testosalvato = findViewById(R.id.testosalvato);

                try {
                String txtData = testosalvato.getText().toString();
                if (!checkTesto(txtData)){
                    throw new Exception();
                }
                i.putExtra("txtData", txtData);
                startActivity(i);

                } catch (Exception e) {
                    //formato di toast topic non valido
                    Toast.makeText(getBaseContext(),"Formato Topic non valido", Toast.LENGTH_LONG).show();

                }


            }


        });
    }

    private boolean checkTesto(String txtData) {
        if (txtData.split("/").length <= 2)
            return false;
        return true;
    }


}

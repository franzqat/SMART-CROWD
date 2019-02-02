package dev.furtor.contastudenti;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class addElement extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_element);


        Toolbar toolbar = findViewById(R.id.toolbar);
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
                //controlla la corretteza del testo inserito
                if (!checkTesto(txtData)){
                    //se non è corretto lancia l'eccezione
                    throw new Exception();
                }
                //lo lancia nell'intent e lo passa alla main activity
                i.putExtra("txtData", txtData);
                startActivity(i);

                } catch (Exception e) {
                    //formato di toast topic non valido
                    Toast.makeText(getBaseContext(),"Il formato del topic deve essere di tipo a/b/c/d con almeno un livello", Toast.LENGTH_LONG).show();

                }


            }


        });
    }

    /**
     * Controlla la correttezza del topic inserito
     * @param txtData
     * @return
     */
    private boolean checkTesto(String txtData) {
        if (txtData.split("/").length < 2)
            return false;
        return true;
    }


}

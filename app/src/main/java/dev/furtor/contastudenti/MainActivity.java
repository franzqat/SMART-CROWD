package dev.furtor.contastudenti;



import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import dev.furtor.contastudenti.helpers.MqttHelper;

public class MainActivity extends AppCompatActivity {
    MqttHelper mqttHelper;
    ProgressBar pb;
    int progressStatus=0;
    TextView dataReceived, aula3;
    Button topic1,topic2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pb = findViewById(R.id.aula3progress);
        aula3 = findViewById(R.id.aula3);



        dataReceived = findViewById(R.id.aulastudio);
        startMqtt();

        topic2 = findViewById(R.id.bottone2);
        topic1 = findViewById(R.id.topic1);
        topic1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mqttHelper.subscribeToTopic("unict/didattica/aulastudio"); //@string aula_studio_D1

            }
        });
        topic2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                  mqttHelper.subscribeToTopic("unict/didattica/aula3"); //@string aula_studio_D1




            }
        });



    }




    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 111:
            {
                if (grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    try {

                    }
                    catch(SecurityException e){
                        Toast.makeText(getApplicationContext(),"Impossibile",Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(),"Impossibile",Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

    private void startMqtt(){
        mqttHelper = new MqttHelper(getApplicationContext());
        mqttHelper.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean b, String s) {
                Log.w("Mqtt","connection complete!");
            }

            @Override
            public void connectionLost(Throwable throwable) {
                Log.w("Mqtt","connection lost!");
            }

            @Override
            public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
                Log.w("Debug",mqttMessage.toString() + " topic " + topic);
                String[] result= topic.split("/");
                dataReceived = findViewById(getResources().getIdentifier(result[result.length -1], "id", getPackageName()));
                dataReceived.setText(mqttMessage.toString());
                progressStatus = Integer.parseInt(mqttMessage.toString());
               // pb.setProgress(progressStatus);
            new Thread(new UIUpdater(dataReceived,pb,progressStatus)).start();
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

            }
        });
    }


}

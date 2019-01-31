package dev.furtor.contastudenti;



import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.LinkedHashMap;
import java.util.LinkedList;

import dev.furtor.contastudenti.helpers.MqttHelper;

public class MainActivity extends AppCompatActivity {
    MqttHelper mqttHelper;
    LinkedHashMap<String, ElementsStructure> map = new LinkedHashMap<>();

    ProgressBar pb;
    int progressStatus=0;
    TextView dataReceived, aula3;
    Button topic1,topic2;

    LinearLayout linearLayout;

    LinkedList<String> list = new LinkedList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        popolaLista();
        linearLayout = findViewById(R.id.linear_layout);



        addElement(list);

        startMqtt();
        pb = findViewById(R.id.aula3progress);
        aula3 = findViewById(R.id.aula3);
/*


        dataReceived = findViewById(R.id.aulastudio);


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


*/
    }

    private void popolaLista() {
        list.add("unict/didattica/aulastudio");
        list.add("unict/didattica/aula1");
        list.add("unict/didattica/aula2");
        list.add("unict/didattica/aula3");
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
               //HANDLE ROUTINE;
               ElementsStructure element = map.get(topic);
               element.getTextView().setText(mqttMessage.toString() + "/" + element.getMaxStudenti() );
               element.getProgressBar().setProgress(Integer.parseInt(mqttMessage.toString()));


            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

            }
        });
    }

    private void addElement(LinkedList list){
        //Adding a LinearLayout with HORIZONTAL orientation
        LinearLayout textLinearLayout = new LinearLayout(this);
        textLinearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.addView(textLinearLayout);
        Switch aSwitch;
        TextView t;
        ProgressBar p;
        for (final Object s : list) {
            String[] result= s.toString().split("/");

            aSwitch = addButton(linearLayout, result[result.length -1] + " di " + result[result.length -2]);
            t = addTextView(linearLayout, "/");
            p = addProgressBar(linearLayout);


            aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        mqttHelper.subscribeToTopic(s.toString());
                        map.get(s.toString()).getTextView().setVisibility(View.VISIBLE);
                        map.get(s.toString()).getProgressBar().setVisibility(View.VISIBLE);
                    } else {
                        mqttHelper.unsubscribeToTopic(s.toString());
                        map.get(s.toString()).getTextView().setVisibility(View.GONE);
                        map.get(s.toString()).getProgressBar().setVisibility(View.GONE);
                    }
                }
            });

            map.put(s.toString(), new ElementsStructure(aSwitch, t, p));

            t.setText("waiting");

            addLineSeperator();
            }

        }



    private TextView addTextView(LinearLayout textLinearLayout, String testo) {


        //linearLayout.addView(textLinearLayout);
        TextView textView = new TextView(this);

        textView.setText(testo);

      //  setTextViewAttributes(textView);
        textView.setTextColor(Color.BLACK);
        textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        textLinearLayout.addView(textView);
        textView.setVisibility(View.GONE);

        return textView;
    }
    private ProgressBar addProgressBar(LinearLayout textLinearLayout) {

        ProgressBar progressBar =new ProgressBar(getApplicationContext(), null, android.R.attr.progressBarStyleHorizontal);
        // Apply the layout parameters for progress bar
        progressBar.getProgressDrawable().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
        textLinearLayout.addView(progressBar);
        progressBar.setVisibility(View.GONE);
        return progressBar;
    }

    private Switch addButton(LinearLayout textLinearLayout, String testo){

        Switch aSwitch = new Switch(this);

        //aSwitch.setTextOff("Monitora "+ testo);
      // aSwitch.setTextOn("Stop " + testo);
        aSwitch.setText("Monitora " + testo);
        linearLayout.addView(aSwitch);

        return aSwitch;
    }

    private void addEditTexts() {

        LinearLayout editTextLayout = new LinearLayout(this);
        editTextLayout.setOrientation(LinearLayout.VERTICAL);

        linearLayout.addView(editTextLayout);

        for (int i = 1; i <= 3; i++) {
            EditText editText = new EditText(this);
            editText.setHint("EditText " + String.valueOf(i));
            setEditTextAttributes(editText);
            editTextLayout.addView(editText);
        }
        addLineSeperator();
    }

    private void setEditTextAttributes(EditText editText) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        params.setMargins(convertDpToPixel(16),
                convertDpToPixel(16),
                convertDpToPixel(16),
                0
        );

        editText.setLayoutParams(params);
    }


    private void setTextViewAttributes(TextView textView) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        params.setMargins(convertDpToPixel(16),
                convertDpToPixel(16),
                0, 0
        );

        textView.setTextColor(Color.BLACK);
        textView.setLayoutParams(params);
    }


    //This function to convert DPs to pixels
    private int convertDpToPixel(float dp) {
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return Math.round(px);
    }
    private void addLineSeperator() {
        LinearLayout lineLayout = new LinearLayout(this);
        lineLayout.setBackgroundColor(Color.BLACK);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                2);
        params.setMargins(0, convertDpToPixel(10), 0, convertDpToPixel(10));
        lineLayout.setLayoutParams(params);
        linearLayout.addView(lineLayout);
    }

}

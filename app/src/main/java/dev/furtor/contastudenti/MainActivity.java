package dev.furtor.contastudenti;



import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.LinkedList;

import dev.furtor.contastudenti.helpers.MqttHelper;

public class MainActivity extends AppCompatActivity {

    MqttHelper mqttHelper;
    LinkedHashMap<String, ElementsStructure> map = new LinkedHashMap<>();
    LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LinkedList<Topic> list;
        list = getTopicListPref(this, "topics");


        if(getIntent().getExtras() != null) {
            Intent i = getIntent();
            String txtData = i.getExtras().getString("txtData", "");
            // retrieve preference

            list.add(new Topic(txtData)); //default 100 studenti max
            setTopicListPref(this, "topics", list);
        }


        linearLayout = findViewById(R.id.linear_layout);
        if (list.isEmpty()){
            refreshtoDefault();
        }else {
            addElementsView(list);
        }


        startMqtt();
        Log.w("stato","on create");
    }
    private void refreshtoDefault() {
        linearLayout.removeAllViewsInLayout();
        setTopicListPref(this, "topics", popolaLista());
        addElementsView(getTopicListPref(getApplicationContext(), "topics"));
    }


    public static void setTopicListPref(Context context, String key, LinkedList<Topic> values) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject = new JSONObject();

        for ( Topic s : values) {
            try {
                jsonObject = new JSONObject();
                jsonObject.put("topic", s.getTopicName());
                jsonObject.put("maxStudenti", s.getMaxStudenti());
                jsonArray.put(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        if (!values.isEmpty()) {
          //  Log.w("debug", jsonArray.toString());
            editor.putString(key, jsonArray.toString());
        } else {
            editor.putString(key, null);
        }
        editor.commit();

    }
    private void resetListPref(Context context, String key) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(key);
        editor.commit();

    }

    public static LinkedList<Topic> getTopicListPref(Context context, String key) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String json = prefs.getString(key, null);
        LinkedList<Topic> topicLinkedList = new LinkedList<Topic>();
        if (json != null) {
            try {
                JSONArray jsonArray = new JSONArray(json);
                for (int i = 0; i < jsonArray.length(); i++) {
                    String topicName = jsonArray.getJSONObject(i).getString("topic");
                    int maxStudenti = Integer.parseInt(jsonArray.getJSONObject(i).getString("maxStudenti"));
                    topicLinkedList.add(new Topic(topicName,maxStudenti));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return topicLinkedList;
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }




    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.addelement:
            //    Toast.makeText(getBaseContext(),"Funzione add non ancora implementata", Toast.LENGTH_LONG).show();
              Intent i = new Intent(this, addElement.class);
                startActivity(i);
                return true;
            case R.id.restore:
                resetListPref(this, "topics");
                refreshtoDefault();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }




    private LinkedList<Topic> popolaLista() {
        LinkedList<Topic> list = new LinkedList<>();
        list.add(new Topic("unict/didattica/aulastudio", 200));
        list.add(new Topic("unict/didattica/aula1", 30));
        list.add(new Topic("unict/didattica/aula2"));
        list.add(new Topic("unict/didattica/aula3",40));
        return list;
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
                Log.w("Mqtt","Messaggio ricevuto: "+mqttMessage.toString() + " topic " + topic);
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

    private void addElementsView(LinkedList<Topic> list){
        //Adding a LinearLayout with VERTICAL orientation
        LinearLayout textLinearLayout = new LinearLayout(this);
        textLinearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.addView(textLinearLayout);

        Switch aSwitch;
        TextView textView;
        ProgressBar progressBar;
        int maxStudenti;

        for (final Topic topic : list) {
            maxStudenti = topic.getMaxStudenti();

            String[] result = topic.getTopicName().split("/");
            aSwitch = addSwitch(linearLayout, result[result.length - 1] + " di " + result[result.length - 2]);

            textView = addTextView(linearLayout, "/");
            progressBar = addProgressBar(linearLayout);

            aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        mqttHelper.subscribeToTopic(topic.getTopicName());

                        map.get(topic.getTopicName()).getTextView().setVisibility(View.VISIBLE);
                        map.get(topic.getTopicName()).getProgressBar().setVisibility(View.VISIBLE);
                    } else {
                        mqttHelper.unsubscribeToTopic(topic.getTopicName());

                        map.get(topic.getTopicName()).getTextView().setVisibility(View.GONE);
                        map.get(topic.getTopicName()).getProgressBar().setVisibility(View.GONE);
                    }
                }
            });

            map.put(topic.getTopicName(), new ElementsStructure(aSwitch, textView, progressBar, maxStudenti ));

            textView.setText("waiting");

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

    private Switch addSwitch(LinearLayout textLinearLayout, String testo){

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

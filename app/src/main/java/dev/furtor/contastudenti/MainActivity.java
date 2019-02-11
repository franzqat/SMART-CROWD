package dev.furtor.contastudenti;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
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

    private static final String TAG = "mqtt";
    MqttHelper mqttHelper;
    LinkedHashMap<String, ElementsStructure> map = new LinkedHashMap<>();
    LinearLayout linearLayout;

    public MainActivity(){
        Log.d(TAG, "Costruttore");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "On Create");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LinkedList<Topic> list;
        list = getTopicListPref(this);

        //Controllo se esiste un messaggio proveniente dall'attività addElement
        if(getIntent().getExtras() != null) {
            Intent i = getIntent();
            String txtData = i.getExtras().getString("txtData", null);
            if (txtData != null) {// retrieve preference e inserimento in lista
                list.add(new Topic(txtData));
            //salvataggio persistente della lista
            setTopicListPref(this, list);
        }
        }


        linearLayout = findViewById(R.id.linear_layout);
        //se la lista è vuota popola la lista ai valori di default
        if (list.isEmpty()){
            //refresh chiama elementsview al suo interno
            refreshtoDefault();
        }else {
            addElementsView(list);
        }
        //avvia mqtt
        startMqtt();
        Log.w("stato","on create");
    }

    /**
     * Rimuove tutte le view
     * Setta la lista ai valori di default in maniera persistente
     * Ricrea le view dei valori di default
     */
    private void refreshtoDefault() {
        linearLayout.removeAllViewsInLayout();
        setTopicListPref(this, popolaLista());
        addElementsView(getTopicListPref(getApplicationContext()));
    }


    /**
     * Salva il contenuto della lista di topic in maniera persistente legandolo alla chiave topics
     * @param context
     * @param values
     */
    private static void setTopicListPref(Context context, LinkedList<Topic> values) {
        Log.d("debug", "Set topic list pref");
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject;

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
            editor.putString("topics", jsonArray.toString());
        } else {
            editor.putString("topics", null);
        }
        editor.apply();

    }

    /**
     * Resetta la preferenza corrispondente alla chiave
     * @param context
     * @param key chiave da resettare
     */
    private void resetListPref(Context context, String key) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(key);
        editor.apply();

    }

    /**
     * Get del contenuto della lista di topic in maniera persistente legato alla chiave topics
     * @param context
     * @return
     */
    private static LinkedList<Topic> getTopicListPref(Context context) {
        Log.d("debug", "get topic list pref");
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String json = prefs.getString("topics", null);
        LinkedList<Topic> topicLinkedList = new LinkedList<>();
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
            //aggiunta di un nuovo topic, l'intent i porta all'activity addElement per la creazione di un nuovo topic
            case R.id.addelement:
              Intent i = new Intent(this, addElement.class);
              startActivity(i);
                return true;
            // cancella le preferenze e ripristina la lista dei topic ai valori di default
            case R.id.restore:
                resetListPref(this, "topics");
                refreshtoDefault();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    /**
     * Popolazione di default della lista di topics
     * @return
     */
    private LinkedList<Topic> popolaLista() {
        LinkedList<Topic> list = new LinkedList<>();
        list.add(new Topic("unict/didattica/aulastudio", 200));
        list.add(new Topic("unict/didattica/aula1", 30));
        list.add(new Topic("unict/didattica/aula2"));
        list.add(new Topic("unict/didattica/aula3",40));
        return list;
    }

    /**
     * Avvia il processo di MQTT
     */
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
            public void messageArrived(String topic, MqttMessage mqttMessage) {
                Log.w("Mqtt","Messaggio ricevuto: "+mqttMessage.toString() + " topic " + topic);
               //Handle message routine;
                //elementStructure contiene i componenti associati ad un topic
               ElementsStructure element = map.get(topic);
               //set del numero di studenti e del valore della progress bar associata al topic
                int currentNumber = Integer.parseInt(mqttMessage.toString()) - element.getAccessPoints();
               element.getTextView().setText(currentNumber + "/" + element.getMaxStudenti() );
               element.getProgressBar().setProgress(currentNumber);
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

            }
        });
    }

    /**
     * Aggiunge la vista di ogni elemento presente nella lista di topics
     * Conserva i componenti all'interno della mappa associando ciascuno di essi al topic a cui corrispondono
     * @param list
     */
    private void addElementsView(LinkedList<Topic> list){
        //Adding a LinearLayout with VERTICAL orientation
        LinearLayout textLinearLayout = new LinearLayout(this);
        textLinearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.addView(textLinearLayout);

        Switch aSwitch;
        TextView textView;
        ProgressBar progressBar;
        int maxStudenti, accessPoints;

        for (final Topic topic : list) {
            maxStudenti = topic.getMaxStudenti();
            accessPoints = topic.getAccessPoints();
            //aspetto un result del tipo a/b/c/d
            String[] result = topic.getTopicName().split("/");
            //si stampano gli ultimi due livelli del topic nello switch
            aSwitch = addSwitch(linearLayout, result[result.length - 1] + " di " + result[result.length - 2]);

            textView = addTextView(linearLayout, "/");
            progressBar = addProgressBar(linearLayout, maxStudenti);

            aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        //sottoscrizione mqtt a topic
                        mqttHelper.subscribeToTopic(topic.getTopicName());
                        //si rendono visibili la TextView e la Progress Bar
                        map.get(topic.getTopicName()).getTextView().setVisibility(View.VISIBLE);
                        map.get(topic.getTopicName()).getProgressBar().setVisibility(View.VISIBLE);
                    } else {
                        //annulla la sottoscrizione mqtt al topic
                        mqttHelper.unsubscribeToTopic(topic.getTopicName());
                        //visibilità dei componenti nascosta
                        map.get(topic.getTopicName()).getTextView().setVisibility(View.GONE);
                        map.get(topic.getTopicName()).getProgressBar().setVisibility(View.GONE);
                    }
                }
            });

            map.put(topic.getTopicName(), new ElementsStructure(aSwitch, textView, progressBar, maxStudenti, accessPoints ));
            //in attesa del primo messaggio viene settato il testo al valore waiting
            textView.setText("waiting");
            //aggiunge una linea di separazione
            addLineSeperator();
            }

        }


    /**
     * Aggiunge una TextView di default la cui visibilità è posta a GONE
     * @param textLinearLayout
     * @param testo
     * @return
     */
    private TextView addTextView(LinearLayout textLinearLayout, String testo) {

        TextView textView = new TextView(this);
        textView.setText(testo);
        textView.setTextColor(Color.BLACK);
        textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        textLinearLayout.addView(textView);
        textView.setVisibility(View.GONE);

        return textView;
    }

    /**
     * Aggiunge una progressBar di default la cui visibilità è posta a GONE
     * @param textLinearLayout
     * @return
     */
    private ProgressBar addProgressBar(LinearLayout textLinearLayout, int max) {

        ProgressBar progressBar =new ProgressBar(getApplicationContext(), null, android.R.attr.progressBarStyleHorizontal);
        progressBar.setMax(max);
        progressBar.getProgressDrawable().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
        textLinearLayout.addView(progressBar);
        progressBar.setVisibility(View.GONE);
        return progressBar;
    }

    private Switch addSwitch(LinearLayout textLinearLayout, String testo){

        Switch aSwitch = new Switch(this);
        aSwitch.setText("Monitora " + testo);
        linearLayout.addView(aSwitch);

        return aSwitch;
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


    @Override
    public void onStop(){
        Log.d(TAG, "On Stop");
        super.onStop();
    }

    @Override
    public void onResume(){
        Log.d(TAG, "On Resume");
        super.onResume();
    }

    @Override
    public void onPause(){
        Log.d(TAG, "On Pause");
        super.onPause();
    }

    @Override
    public void onDestroy(){
        Log.d(TAG, "On Destroy");
        mqttHelper.disconnect();
        super.onDestroy();
    }

}

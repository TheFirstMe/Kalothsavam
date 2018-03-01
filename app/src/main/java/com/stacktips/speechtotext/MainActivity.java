package com.stacktips.speechtotext;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.stacktips.speechtotext.utilities.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.net.URL;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity{

    private static final int REQ_CODE_SPEECH_INPUT = 100;
    //private TextView mVoiceInputTv;
    private TextView mresultsTable;
    private ImageButton mSpeakBtn;
    private ProgressBar mLoadingIndicator;
    private TextView mErrorMessageDisplay;
    private TextView mEventName;
    private TextView mResultList;
    private String eventQuery;
    private String haha;
    private static int var;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        var=0;

        //mVoiceInputTv = (TextView) findViewById(R.id.voiceInput);
        mSpeakBtn = (ImageButton) findViewById(R.id.btnSpeak);
        mEventName = (TextView) findViewById(R.id.eventName);
        mResultList = (TextView) findViewById(R.id.resultList);
        //mErrorMessageDisplay = (TextView) findViewById(R.id.tv_error_message_display);
        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);
        //Button click event listener : Once button is clicked, function to input voice is called.
        mSpeakBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mEventName.setText("");
                mResultList.setText("");
                startVoiceInput();
            }
        });
    }


    private void makeEventSearchQuery(String eventQuery){
        URL eventSearchUrl = NetworkUtils.buildUrl(eventQuery);
        //mUrlDisplayTextView.setText(eventSearchUrl.toString());
        new EventQueryTask().execute(eventSearchUrl);
    }

    private void showJsonDataView(){
        //mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        //mresultsTable.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage(){
        //mresultsTable.setVisibility(View.INVISIBLE);
        //mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }

    private void JSONparse(String data){
        try {
            JSONObject eventObject = new JSONObject(data);
            String text="";
            int status = eventObject.getInt("status");
            if(status==1) {
                JSONArray resultObject = eventObject.getJSONArray("result");
                String name = resultObject.getJSONObject(0).getString("name");
                for (int i = 0; i < resultObject.length(); i++) {
                    JSONObject result = resultObject.getJSONObject(i);
                    //Log.i("name",result.getString("name"));

                    String position = result.getString("position");
                    String grade = result.getString("grade");
                    String college = result.getString("college");
                    String points = result.getString("points");
                    text+="Position:  " + position + "\n";
                    text+="College:  " + college + "\n";
                    text+="Points:  " + points + "\n";
                    text+="Grade:  " + grade + "\n\n\n";
                }
                    mEventName.setText(name);
                    mResultList.setText(text);
            }
            else {
                Log.i("dd",mEventName.getText().toString());
                if(mEventName.getText().toString().equals("")) {
                    mEventName.setText(" ");
                    correctedEventName(haha);
                }
                String error = eventObject.getString("statusText");
                mResultList.setText("");
                    mEventName.setText(error + " for " + "\""+ haha + "\"");


                var-=1;
            }
            //String uniName = uniObject.getString("name");
        }
        catch (JSONException e){
            e.printStackTrace();
        }
    }

    private static int distance(String a, String b){
        a = a.toLowerCase();
        b = b.toLowerCase();
        int [] costs = new int [b.length() + 1];
        for (int j = 0; j < costs.length; j++)
            costs[j] = j;
        for (int i = 1; i <= a.length(); i++) {
            // j == 0; nw = lev(i - 1, j)
            costs[0] = i;
            int nw = i - 1;
            for (int j = 1; j <= b.length(); j++) {
                int cj = Math.min(1 + Math.min(costs[j], costs[j - 1]), a.charAt(i - 1) == b.charAt(j - 1) ? nw : nw + 1);
                nw = costs[j];
                costs[j] = cj;
            }
        }
        return costs[b.length()];
    }
    private int correctedEventName(String speechInput){
        var+=1;
        String data[] = {"Aksharaslokam","Bharathanatyam","Chenda","Chaakyarkoothu","Computer Designing","Collage","Duff muttukali",
                "Naatakam English","Naatakam Hindi","Naatakam Kannada","Naatakam Malayalam",
                "Etakka","Prabandharachana Arabic","Cartoon Rachana",
                "Prabandharachana English","Prabandharachana Hindi","Prabandharachana Kannada",
                "Prabandharachana Malayalam","Prabandharachana Sanskrit","Prabandharachana Urdu",
                "Pullankuzhal – Pourasthyam","Ghattam","Guitar – Paschchathyam",
                "Kaliman Prathima Nirmaanam","Keralanatanam","Ghazal - Female","Ghazal - Male",
                "Karnataka Sangeetham – Female","Karnataka Sangeetham – Male","Kathakali","Kathaprasangam",
                "Kaavyakeli","Lalitha Sangeetham – Female","Lalitha Sangeetham – Male","Maddhalam","Mime",
                "Kuchipudi",
                "Mimicry","Mohiniyattam","Mono Act","Mappilappaattu – Group","Mappilappaattu – Single","Mrudangam",
                "Maargamkali","Naatoti Nritham – Group","Naatanpattu","Naatoti Nritham – Single – Female",
                "Naatoti Nritham – Single – Male","Naagaswaram – Pourasthyam","Oppana","Painting – Ennachayam",
                "Painting – Jalachayam","Photography","Pookkalam","Kavithaalaapanam Arabic",
                "Kavithaalaapanam English","Kavithaalaapanam Hindi","Kavithaalaapanam Kannada",
                "Kavithaalaapanam Malayalam","Kavithaalaapanam Sanskrit","Kavithaalaapanam Urdu",
                "Kavitharachana Arabic","Kavitharachana English","Kavitharachana Hindi","Kavitharachana Kannada",
                "Kavitharachana Malayalam","Kavitharachana Sanskrit","Kavitharachana Urdu","Parichamuttukali",
                "Pashchaathya Sangeetham – Single","Poster Rachana","Poorakkali","Quiz","Rangoli",
                "Sanghagaanam – Pashchathyam","Sanghagaanam – Indian","Sanghanritham – Female","Sanghanritham – Male",
                "Skit – Malayalam","Cherukatharachana Arabic","Cherukatharachana English","Cherukatharachana Hindi",
                "Cherukatharachana Kannada", "Cherukatharachana Malayalam","Cherukatharachana Sanskrit","Cherukatharachana Urdu",
                "Thirakatharachana - Documentary English","Thirakatharachana - Documentary Hindi",
                "Thirakatharachana - Documentary Malayalam", "Thirakatharachana - Kathachithram English",
                "Thirakatharachana - Kathachithram Hindi","Thirakatharachana - Kathachithram Malayalam",
                "Tabala","Test Event","Thullal","Thiruvaathirakali – Female", "Theruvunaatakam Malayalam",
                "Veena – Pourasthyam","Violin – Pourasthyam","Yakshagaanam"};
        haha= speechInput;
        Log.i("msg1","hello");
        for(int i=0;i < data.length; i++){
            String b[] = data[i].split(" ");
            if(speechInput.equalsIgnoreCase(data[i])){
                Log.i("msg4","hellofn");
                makeEventSearchQuery(speechInput);
                return 0;
            }
            /*else if(b.length>1){
                if(speechInput.toLowerCase().contains(b[0].toLowerCase()))
                    makeEventSearchQuery(data[i]);
                return;
            }*/

        }

        int min = 100,min_idx=0;
        for (int i = 0; i < data.length; i++) {
            if(data[i].toLowerCase().charAt(0)!=speechInput.toLowerCase().charAt(0) && data[i].charAt(data[i].length()-1)!=speechInput.charAt(speechInput.length()-1) )
                continue;
            int d= distance(speechInput, data[i]);
            if(d<min){
                min=d;
                min_idx = i;
            }
        }
        haha = data[min_idx].trim();
        makeEventSearchQuery(data[min_idx].trim());
        Log.i("msg2",data[min_idx].trim());
        return 0;
    }

    public class EventQueryTask extends AsyncTask<URL, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(URL... params) {
            URL searchUrl = params[0];
            String eventResults = null;
            try {
                eventResults = NetworkUtils.getResponseFromHttpUrl(searchUrl);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return eventResults;
        }

        @Override
        protected void onPostExecute(String eventResults) {
            // COMPLETED (27) As soon as the loading is complete, hide the loading indicator
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            if (eventResults != null && !eventResults.equals("")) {
                // COMPLETED (17) Call showJsonDataView if we have valid, non-null results

                showJsonDataView();
                JSONparse(eventResults);
                //mresultsTable.setText(eventResults);
            } else {
                // COMPLETED (16) Call showErrorMessage if the result is null in onPostExecute
                showErrorMessage();
            }
        }
    }

    private void startVoiceInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en_US");
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Please tell me your event name.");
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                   // mVoiceInputTv.setText(result.get(0));
                    //mrec.setText(result.get(0));
                    Log.i("text",result.get(0));
                    //correctedEventName(result.get(0));
                    haha = result.get(0);
                    makeEventSearchQuery(result.get(0));
                }
                break;
            }

        }
    }
}

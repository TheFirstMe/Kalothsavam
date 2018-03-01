package com.stacktips.speechtotext;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;



import com.stacktips.speechtotext.utilities.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.net.URL;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final int REQ_CODE_SPEECH_INPUT = 100;
    //private TextView mVoiceInputTv;
    private TextView mresultsTable;
    private ImageButton mSpeakBtn;
    private ProgressBar mLoadingIndicator;
    private TextView mErrorMessageDisplay;
    private TextView mEventName;
    private TextView mResultList;
    private String eventQuery;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
                    String teams = result.getString("teams");
                    String points = result.getString("points");
                    text+="Position:  " + position + "\n";
                    text+="Teams:  " + teams + "\n";
                    text+="Points:  " + points + "\n";
                    text+="Grade:  " + grade + "\n\n\n";
                }
                    mEventName.setText(name);
                    mResultList.setText(text);
            }
            else {
                String error = eventObject.getString("statusText");
                mResultList.setText("");
                mEventName.setText(error + " for " + "\""+ eventQuery + "\"");
            }
            //String uniName = uniObject.getString("name");
            //String uniURL = uniObject.getString("url");
        }
        catch (JSONException e){
            e.printStackTrace();
        }
    }

    /*private static int distance(String a, String b){
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
    private String correctedEventName(String speechInput){
        for (int i = 0; i < data.length; i += 2)
            System.out.println("distance(" + data[i] + ", " + data[i+1] + ") = " + distance(data[i], data[i+1]));
        makeEventSearchQuery(speechInput);
    }*/

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
                    eventQuery = result.get(0);
                    //correctedEventName(eventQuery);
                    makeEventSearchQuery(result.get(0));
                }
                break;
            }

        }
    }
}

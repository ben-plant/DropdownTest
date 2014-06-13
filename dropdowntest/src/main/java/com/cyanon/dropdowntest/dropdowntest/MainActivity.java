package com.cyanon.dropdowntest.dropdowntest;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends Activity implements ffbFragmentFront.OnFragmentInteractionListener {

    protected JSONObject jsonObject = null;

    private ArrayList<String> mFeatures = new ArrayList<String>();
    private ArrayList<String> mFunctions = new ArrayList<String>();
    private ArrayList<String> mBenefits = new ArrayList<String>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        JSONFetch jsonFetch = new JSONFetch();
        jsonFetch.execute();
    }

    private void populateFragments() {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        for (int i = 0; i < mFeatures.size(); i++) {
            fragmentTransaction.setCustomAnimations(R.anim.fragment_slide, R.anim.fragment_slide);
            fragmentTransaction.add(R.id.container, ffbFragmentFront.newInstance(mFunctions, mFeatures.get(i), mBenefits));
        }
        fragmentTransaction.commit();
    }

    private void updateTextView() {
        //TextView textView = (TextView) findViewById(R.id.testTextView);
        if (jsonObject != null) {
            try {
                JSONObject questions = jsonObject.getJSONObject("features");
                for (int i = 0; i < questions.length(); i++)
                {
                    String foo = questions.getString("Q" + i);
                    mFeatures.add(i, foo);
                }
                JSONObject answers = jsonObject.getJSONObject("functions");
                for (int i = 0; i < answers.length(); i++)
                {
                    String foo = answers.getString("F" + i);
                    mFunctions.add(i, foo);
                }
                JSONObject benefits = jsonObject.getJSONObject("benefits");
                for (int i = 0; i < benefits.length(); i++)
                {
                    String foo = benefits.getString("B" + i);
                    mBenefits.add(i, foo);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    private class JSONFetch extends AsyncTask<Void, Void, JSONObject>
    {
        protected JSONObject doInBackground(Void... params) {
            JSONObject jsonResponse = null;

            try {
                URL jsonURL = new URL("http://212.56.88.108:47047/sony/test_json.php");
                HttpURLConnection connection = (HttpURLConnection)jsonURL.openConnection();
                connection.connect();
                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK)
                {
                    InputStream inputStream = connection.getInputStream();
                    Reader reader = new InputStreamReader(inputStream);
                    int contentLength = connection.getContentLength();
                    char[] charArray = new char[contentLength];
                    reader.read(charArray);
                    String responseData = new String(charArray);

                    jsonResponse = new JSONObject(responseData);
                }
                else
                {
                    Log.e("JSONFetch", "Fuckup!");
                }
            }
            catch (MalformedURLException e)
            {
                e.printStackTrace();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
            return jsonResponse;
        }

        protected void onPostExecute(JSONObject result)
        {
            jsonObject = result;
            updateTextView();
            populateFragments();
        }
    }
}


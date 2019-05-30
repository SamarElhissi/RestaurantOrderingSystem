package mondo.orderingsystem;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Naruto on 8/21/2016.
 */
public class Terms extends AppCompatActivity {


    Toolbar mToolbar; ProgressBar progressBar;
    TextView terms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.terms);
//
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        terms = (TextView) findViewById(R.id.terms);
        new getTerms().execute();
        /***************Navigation drawer****************/

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitleTextColor(Color.rgb(5,5,5));
        setSupportActionBar(mToolbar);
//
//        // enabling action bar app icon and behaving it as toggle button
       getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back);
        getSupportActionBar().setTitle("Terms");

//
   }

    private class getTerms extends AsyncTask<Void, Void, String> {

        private Exception exception;

        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            super.onPreExecute();
            this.exception = null;

        }

        protected String  doInBackground(Void... urls) {

            String API_URL = "http://order.mondocloudsolutions.com/Api/mondo_os/details";
            InputStream inStream = null;
            HttpURLConnection urlConnection = null;

            try {

                URL url = new URL(API_URL);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);
                urlConnection.connect();
                inStream = urlConnection.getInputStream();
                BufferedReader bReader = new BufferedReader(new InputStreamReader(inStream));

                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = bReader.readLine()) != null) {
                    stringBuilder.append(line).append("\n");
                }

                return stringBuilder.toString();
            }catch (Exception e) {

                this.exception = e;

            } finally {
                if (inStream != null) {
                    try {
                        // this will close the bReader as well
                        inStream.close();
                    } catch (IOException ignored) {
                    }
                }
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
            return null;
        }

        protected void onPostExecute(String  response) {
            if(response == null) {
                //    setErrorText("Connection Error!");

            }else {

                try{

                    JSONObject object = (JSONObject) new JSONTokener(response).nextValue();
                    JSONArray data =(JSONArray) object.getJSONArray("data");
                    JSONObject termsobj = (JSONObject)data.get(0);
                    String term = termsobj.optString("terms");
                    terms.setText(term);

                }catch(Exception e){
                    // layout.setBackgroundResource(0);
                }

            }
            progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {

        int id = item.getItemId();
        if(id==android.R.id.home)
        {
            finish();
           return true;
       }

        return super.onOptionsItemSelected(item);
    }
}

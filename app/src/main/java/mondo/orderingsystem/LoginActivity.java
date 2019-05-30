package mondo.orderingsystem;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Naruto on 7/11/2016.
 */
public class LoginActivity extends AppCompatActivity {
    private Button login;
    TextView email; TextView password;
    String emailText,passwordText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        login = (Button) findViewById(R.id.loginBtn);
        email = (TextView) findViewById(R.id.email);
        password = (TextView) findViewById(R.id.password);
        if(SaveSharedPreference.getEmail(LoginActivity.this).length() == 0)
        {
            // call Login Activity
        }
        else
        {
            Intent intent = new Intent(getApplicationContext(),MainActivity.class);
            startActivity(intent);
        }

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(email.getText().toString().equals(""))
                    email.setError("Enter email!");
                else if(password.getText().toString().equals(""))
                    password.setError("Enter password!");
                else{
                    emailText = email.getText().toString();
                    passwordText = password.getText().toString();
                    new Login().execute();

                }

            }
        });
    }

    private class Login extends AsyncTask<Void, Void, String> {

        private Exception exception;

        protected void onPreExecute() {
            super.onPreExecute();
            this.exception = null;

        }

        protected String  doInBackground(Void... urls) {

            String API_URL = "http://order.mondocloudsolutions.com/Api/mondo_os/login";
            JSONObject object = null;
            InputStream inStream = null;
            HttpURLConnection urlConnection = null;

            try {

                URL url = new URL(API_URL);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);

                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("email", emailText));
                params.add(new BasicNameValuePair("password", passwordText));

                OutputStream os = urlConnection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(getQuery(params));
                writer.flush();
                writer.close();
                os.close();

                urlConnection.connect();
                int status = urlConnection.getResponseCode();
                if(status != HttpURLConnection.HTTP_OK )
                    inStream = urlConnection.getErrorStream();
                else
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
                //   setErrorText("Connection Error!");

            }else {
                try{
                    JSONObject object = (JSONObject) new JSONTokener(response).nextValue();


                    if(object.optString("status").equals("false")){
                        Toast.makeText(getApplicationContext(),object.optString("message"),Toast.LENGTH_LONG).show();
                    }else{
                        JSONObject data = object.getJSONObject("data");
                        SaveSharedPreference.setEmail(getApplicationContext(),data.optString("employee_email"));
                        SaveSharedPreference.setPassword(getApplicationContext(),passwordText);
                        SaveSharedPreference.setEmployeeId(getApplicationContext(),data.optString("employee_id"));
                        SaveSharedPreference.setEmployeeFN(getApplicationContext(),data.optString("employee_first_name"));
                        SaveSharedPreference.setEmployeeLN(getApplicationContext(),data.optString("employee_last_name"));
                        SaveSharedPreference.setCompany_name(getApplicationContext(),data.optString("company_name"));
                        SaveSharedPreference.setCustomer_id(getApplicationContext(),data.optString("customer_id"));

                        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                        startActivity(intent);
                    }

                }catch (Exception ex){

                }


            }

        }
    }

    private String getQuery(List<NameValuePair> params) throws UnsupportedEncodingException
    {
        StringBuilder result = new StringBuilder();
        boolean first = true;

        for (NameValuePair pair : params)
        {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(pair.getName(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(pair.getValue(), "UTF-8"));
        }

        return result.toString();
    }
}

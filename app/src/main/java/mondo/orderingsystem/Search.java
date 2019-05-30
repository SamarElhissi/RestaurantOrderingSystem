package mondo.orderingsystem;


import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

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

public class Search extends AppCompatActivity {
    private Toolbar mToolbar;

    private SearchAdapter searchAdapter;
    private ListView listView;

    private ArrayList<String> namesList = new ArrayList<String>();
    private ArrayList<String> logoImages = new ArrayList<String>();
    private ArrayList<String> descList = new ArrayList<String>();
    private ArrayList<String> pricesList = new ArrayList<String>();
    private ArrayList<String> restaurantNames = new ArrayList<String>();
    private ArrayList<JSONObject> MenuItems = new ArrayList<JSONObject>();
    private ArrayList<JSONObject> dataList = new ArrayList<JSONObject>();
    private ProgressBar progressBar;
    OrderDataBase dataBase ;
    EditText search_input; ImageView search_btn;
    String searchText="";
    String restaurant_id =""; String restaurant_name="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search);
        dataBase = new OrderDataBase(this);
        /*******************************/

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        listView = (ListView) findViewById(R.id.listView);
        search_input = (EditText) findViewById(R.id.search_input);
        search_btn = (ImageView) findViewById(R.id.search_btn);
        progressBar.setVisibility(View.GONE);

        search_input.setImeActionLabel("Search", KeyEvent.KEYCODE_ENTER);
        search_input.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == KeyEvent.KEYCODE_ENTER) {
                    new getSearchResult().execute();
                }
                return handled;
            }
        });
        /*****************resuturant list**************/

        searchAdapter = new SearchAdapter(Search.this, namesList, logoImages, descList,pricesList, restaurantNames);
        searchAdapter.notifyDataSetChanged();
        listView.setAdapter(searchAdapter);
        /***************************************/
        Intent intent = getIntent();
        if(intent.getStringExtra("restaurant_id") != null){
          restaurant_id = intent.getStringExtra("restaurant_id");
          restaurant_name = intent.getStringExtra("restaurant_name");
        }


        // enabling action bar app icon and behaving it as toggle button
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitleTextColor(Color.rgb(5,5,5));
        setSupportActionBar(mToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back);
        getSupportActionBar().setTitle("Search "+restaurant_name);

        search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new getSearchResult().execute();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
                Intent intent = new Intent(view.getContext(), MenuAddons.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                try{

                    JSONObject jsonobject= (JSONObject) dataList.get(position);
                    JSONObject jsonobject2= (JSONObject) MenuItems.get(position);
                    JSONArray Addons = jsonobject.getJSONArray("Addons");

                intent.putExtra("main_menu_id", ""+jsonobject.optString("main_menu_id"));
                intent.putExtra("main_menu_name", ""+ jsonobject.optString("main_menu_name"));
                intent.putExtra("item_id", ""+jsonobject2.optString("item_id"));
                intent.putExtra("item_name", ""+jsonobject2.optString("item_name"));
                intent.putExtra("item_desc", ""+jsonobject2.optString("item_desc"));
                intent.putExtra("item_price", ""+jsonobject2.optString("item_price"));
                intent.putExtra("Addons", Addons.toString());
                intent.putExtra("cover", jsonobject.optString("restaurant_image"));
                intent.putExtra("logoimage", jsonobject.optString("restaurant_logo"));
                intent.putExtra("restaurant_name", jsonobject.optString("restaurant_name"));
                intent.putExtra("restaurantCurrency", jsonobject.optString("restaurant_currency_name"));
                intent.putExtra("restaurant_tax", jsonobject.optString("restaurant_tax"));

                JSONArray item_addons = jsonobject2.getJSONArray("item_addons");
                    intent.putExtra("item_addons", item_addons.toString());

                JSONArray item_size = jsonobject2.getJSONArray("item_size");
                    intent.putExtra("item_size", item_size.toString());
                    view.getContext().startActivity(intent);
                }catch (Exception e){
                 Toast.makeText(getApplicationContext(),"Error",Toast.LENGTH_LONG).show();
                }

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle action bar actions click
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /***
     * Called when invalidateOptionsMenu() is triggered
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        return super.onPrepareOptionsMenu(menu);
    }

    /*****************************************************************/
    private class getSearchResult extends AsyncTask<Void, Void, String> {

        private Exception exception;

        protected void onPreExecute() {
            super.onPreExecute();
            this.exception = null;
            progressBar.setVisibility(View.VISIBLE);
            searchText = search_input.getText().toString();
        }

        protected String  doInBackground(Void... urls) {

            String API_URL = "http://order.mondocloudsolutions.com/Api/mondo_os/search";
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
                params.add(new BasicNameValuePair("restaurant_id", restaurant_id));
                params.add(new BasicNameValuePair("customer_id", SaveSharedPreference.getCustomer_id(getApplicationContext())));
                params.add(new BasicNameValuePair("keyword", searchText));

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
                //    setErrorText("Connection Error!");

            }else {

                try{

                    JSONObject object = (JSONObject) new JSONTokener(response).nextValue();
                    JSONArray data =object.getJSONArray("data");
                    namesList.clear(); logoImages.clear(); descList.clear(); pricesList.clear(); restaurantNames.clear();
                    MenuItems.clear();dataList.clear();
                    for(int i=0;i<data.length();i++){

                        JSONObject jsonobject= (JSONObject) data.get(i);
                        JSONArray MenuItem = jsonobject.getJSONArray("Menu_Item");
                        for(int j = 0 ; j<MenuItem.length();j++){
                            JSONObject jsonobject2= (JSONObject) MenuItem.get(j);
                            namesList.add(jsonobject2.optString("item_name"));
                            logoImages.add(jsonobject2.optString("item_image"));
                            descList.add(jsonobject2.optString("item_desc") );
                            pricesList.add(jsonobject2.optString("item_price"));
                            restaurantNames.add(jsonobject.optString("restaurant_name"));
                            dataList.add(jsonobject);
                            MenuItems.add(jsonobject2);
                        }


                    }


                }catch(Exception e){
                    // layout.setBackgroundResource(0);
                }

            }

            searchAdapter.notifyDataSetChanged();
            progressBar.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
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

/*****************************************************************/
}

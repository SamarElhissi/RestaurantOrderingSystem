package mondo.orderingsystem;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

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
import java.util.HashMap;
import java.util.List;

public class LatestOrders extends AppCompatActivity {
    Toolbar mToolbar;
    ArrayList<String>Order_idList = new ArrayList<>();
    ArrayList<JSONObject>GroupsList = new ArrayList<>();
    ArrayList<String>Item_nameList = new ArrayList<>();
    ArrayList<String>Order_List = new ArrayList<>();
    ArrayList<Integer>QuantityList = new ArrayList<>();
    ArrayList<Double>PricesList = new ArrayList<>();
    ArrayList<String>InstructionsList = new ArrayList<>();
    ArrayList<String>Restaurant_nameList = new ArrayList<>();
    ArrayList<String>StatusList = new ArrayList<>();
    HashMap<String, List <Object>> listDataChild= new HashMap<>();
    ExpandableListView  listView;
    LatestOrdersAdapter latestOrdersAdapter;
    int employee_id; ProgressBar progressBar;
    Button loadMore; public int page=1;
     ViewGroup footer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.latest_orders);
        progressBar =(ProgressBar) findViewById(R.id.progressBar);

        listView = (ExpandableListView) findViewById(R.id.Orders_listView);
        LayoutInflater inflater = getLayoutInflater();
        footer = (ViewGroup)inflater.inflate(R.layout.latest_orders_footer, listView, false);

        loadMore =(Button) footer.findViewById(R.id.loadMore);

        latestOrdersAdapter = new LatestOrdersAdapter(LatestOrders.this, Order_List,Order_idList, PricesList,Restaurant_nameList,StatusList,listDataChild);
        employee_id = Integer.parseInt(SaveSharedPreference.getEmployeeId(getApplicationContext()));
        listView.setAdapter(latestOrdersAdapter);
        new getOrders().execute();

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitleTextColor(Color.rgb(5,5,5));
        setSupportActionBar(mToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back);
        getSupportActionBar().setTitle("Last orders");

        loadMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                page = page+1;
                new getOrders().execute();
            }
        });

        listView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v,
                                        int groupPosition, long id) {

                Intent i = new Intent(getApplicationContext(), LatestOrdersItem.class);
                i.putExtra("groups", GroupsList.get(groupPosition).toString());
                startActivity(i);
                return true; // This way the expander cannot be collapsed
            }
        });


    }


    @Override
    public void onResume(){
        super.onResume();
        page= 1;

        new getOrders().execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if(id==android.R.id.home)
        {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private class getOrders extends AsyncTask<Void, Void, String> {

        private Exception exception;

        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            super.onPreExecute();
            this.exception = null;

        }

        protected String  doInBackground(Void... urls) {

            String API_URL = "http://order.mondocloudsolutions.com/Api/mondo_os/get_orders_by_group/" + SaveSharedPreference.getEmployeeId(getApplicationContext())+"/"+page+"/10";
            JSONObject object = null;
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
                    if(page == 1){

                        Order_idList.clear(); PricesList.clear();Restaurant_nameList.clear();StatusList.clear();listDataChild.clear();
                        Order_List.clear();GroupsList.clear();
                    }

                    JSONObject object = (JSONObject) new JSONTokener(response).nextValue();
                    JSONArray data =(JSONArray) object.getJSONArray("data");
                    JSONArray data2 =(JSONArray) data.get(0);
                   // int groups = latestOrdersAdapter.getGroupCount();
                    for(int i=0;i<data2.length();i++){

                        JSONObject jsonobject= (JSONObject) data2.get(i);
                        GroupsList.add(jsonobject);
                        String order_id = jsonobject.optString("order_group_id");
                        String res_id = jsonobject.optString("restaurant_id");
                        Order_idList.add(jsonobject.optString("order_group_id"));
                        Order_List.add(order_id+""+res_id);
                        if(jsonobject.optString("price_with_fees").equals("null"))
                            PricesList.add(0.0);
                        else
                            PricesList.add(Double.parseDouble(jsonobject.optString("price_with_fees")));

                        Restaurant_nameList.add(jsonobject.optString("restaurant_name"));
                      //  StatusList.add(jsonobject.optString("order_status"));
                        JSONArray order_data = (JSONArray)jsonobject.getJSONArray("order_data");
                        List <Object> ll = new ArrayList<>();
                        for(int j=0;j<order_data.length();j++){
                          //  StatusList.clear();
                            JSONObject jsonobject2= (JSONObject) order_data.get(j);
                            ll.add(jsonobject2);
                            StatusList.add(i,jsonobject2.optString("order_status"));
                        }
                        Double[] x = new Double[2];
                        x[0]= Double.parseDouble(jsonobject.optString("fees"));
                        Double fees = (Double.parseDouble(jsonobject.optString("fees"))/100)* Double.parseDouble(jsonobject.optString("price_without_fees"));
                        x[1]=(Math.round(fees* 100d)/100d);
                        ll.add(x);
                        listDataChild.put(order_id+""+res_id, ll);
                    }

                    for(int i=1;i<data.length();i++){

                        JSONObject json= (JSONObject) data.get(i);
                        JSONObject jsonobject= (JSONObject) json.get(i+"");
                        GroupsList.add(jsonobject);
                        String order_id = jsonobject.optString("order_group_id");
                        String res_id = jsonobject.optString("restaurant_id");
                        Order_idList.add(jsonobject.optString("order_group_id"));
                        Order_List.add(order_id+""+res_id);
                        if(jsonobject.optString("price_with_fees").equals("null"))
                            PricesList.add(0.0);
                        else
                            PricesList.add(Double.parseDouble(jsonobject.optString("price_with_fees")));

                        Restaurant_nameList.add(jsonobject.optString("restaurant_name"));
                        //  StatusList.add(jsonobject.optString("order_status"));
                        JSONArray order_data = (JSONArray)jsonobject.getJSONArray("order_data");
                        List <Object> ll = new ArrayList<>();
                        for(int j=0;j<order_data.length();j++){
                           // StatusList.clear();
                            JSONObject jsonobject2= (JSONObject) order_data.get(j);
                            ll.add(jsonobject2);
                            StatusList.add(i,jsonobject2.optString("order_status"));
                        }
                        Double[] x = new Double[2];
                        x[0]= Double.parseDouble(jsonobject.optString("fees"));
                        Double fees = (Double.parseDouble(jsonobject.optString("fees"))/100)* Double.parseDouble(jsonobject.optString("price_without_fees"));
                        x[1]=(Math.round(fees* 100d)/100d);
                        ll.add(x);
                        listDataChild.put(order_id+""+res_id, ll);
                    }
                    latestOrdersAdapter.notifyDataSetChanged();
                    listView.setVisibility(View.VISIBLE);
                     if(latestOrdersAdapter.getGroupCount() <10 || data.length() ==0){
                    //  footer.setVisibility(View.GONE);
                         listView.removeFooterView(footer);
                      }else     listView.addFooterView(footer, null, false);

                    for(int i=0; i < latestOrdersAdapter.getGroupCount(); i++)
                        listView.expandGroup(i);



                }catch(Exception e){
                   Toast.makeText(getApplicationContext(),"Error", Toast.LENGTH_LONG).show();

                }

            }
            progressBar.setVisibility(View.GONE);
        }
    }
}

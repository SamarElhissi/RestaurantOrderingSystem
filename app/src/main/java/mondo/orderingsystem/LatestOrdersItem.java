package mondo.orderingsystem;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LatestOrdersItem extends AppCompatActivity {
    Toolbar mToolbar;
    ArrayList<String>Order_idList = new ArrayList<>();
    ArrayList<String>Menu_nameList = new ArrayList<>();
    ArrayList<String>Item_nameList = new ArrayList<>();
    ArrayList<String>Item_sizeList = new ArrayList<>();
    ArrayList<Integer>QuantityList = new ArrayList<>();
    ArrayList<Double>PricesList = new ArrayList<>();
    ArrayList<String>InstructionsList = new ArrayList<>();
    ArrayList<String>Restaurant_nameList = new ArrayList<>();
    ArrayList<String>StatusList = new ArrayList<>();
    HashMap<String, List<List<String>>> listDataChild= new HashMap<>();
    ExpandableListView  listView;
    LatestOrdersItemAdapter latestOrdersAdapter;
    int employee_id; ProgressBar progressBar;
    Button loadMore; public int page=1;
     ViewGroup footer;
    RelativeLayout checkout;
    String order_id,rest_name, status, rest_id, order_group_id;
    TextView price, Tax, subtotal, Tax_value, rest_name_t,order;
    ImageView tick;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.latest_orders_item);

        listView = (ExpandableListView) findViewById(R.id.Orders_listView);
        checkout = (RelativeLayout) findViewById(R.id.checkout);
        tick = (ImageView) findViewById(R.id.tick);
        latestOrdersAdapter = new LatestOrdersItemAdapter(LatestOrdersItem.this, Order_idList, Item_nameList, Item_sizeList,
                QuantityList, PricesList,Restaurant_nameList,InstructionsList,StatusList,listDataChild);
        employee_id = Integer.parseInt(SaveSharedPreference.getEmployeeId(getApplicationContext()));
        listView.setAdapter(latestOrdersAdapter);
        Intent intent = getIntent();
        String group = intent.getStringExtra("groups");
        price = (TextView) findViewById(R.id.price);
        Tax = (TextView) findViewById(R.id.HST_value);
        subtotal = (TextView) findViewById(R.id.subTotal);
        Tax_value = (TextView) findViewById(R.id.HST_percent);
        rest_name_t = (TextView) findViewById(R.id.rest_name);
        order = (TextView) findViewById(R.id.order);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitleTextColor(Color.rgb(5,5,5));
        setSupportActionBar(mToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back);

        checkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(Order_idList.size() ==0){
                    Toast.makeText(getApplicationContext(),"Cannot checkout 0 orders",Toast.LENGTH_LONG).show();
                }else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(LatestOrdersItem.this);
                    builder.setMessage("Are you sure you want to order this item again?")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                    new sendOrderJson().execute(order_group_id);
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
            }
        });
        try{

            JSONObject object = new JSONObject (group);
            JSONArray data =(JSONArray) object.getJSONArray("order_data");
            Double fees = Double.parseDouble(object.optString("fees"))/100;
            Double price_without_fees = Double.parseDouble(object.optString("price_without_fees"));
            Tax.setText((Math.round(fees *price_without_fees* 100d)/100d)+"");
            Tax_value.setText("("+(object.optString("fees"))+"%)");
            subtotal.setText(object.optString("price_without_fees"));
            price.setText(object.optString("price_with_fees"));

            for(int i=0;i<data.length();i++){

                JSONObject jsonobject= (JSONObject) data.get(i);
                order_id = (jsonobject.optString("order_id"));
                order_group_id = (jsonobject.optString("order_group_id"));
                rest_name = (jsonobject.optString("restaurant_name"));
                rest_id = (jsonobject.optString("restaurant_id"));
                status = (jsonobject.optString("order_status"));
                Order_idList.add((jsonobject.optString("order_id")));
                Item_nameList.add(jsonobject.optString("order_item_name"));
                InstructionsList.add(jsonobject.optString("note"));
                Item_sizeList.add(jsonobject.optString("size_name"));
                QuantityList.add(Integer.parseInt(jsonobject.optString("order_item_quantity")) );
                if(jsonobject.optString("order_total_price").equals("null"))
                    PricesList.add(0.0);
                else
                    PricesList.add(Double.parseDouble(jsonobject.optString("order_total_price")));

                Restaurant_nameList.add(jsonobject.optString("restaurant_name"));
                StatusList.add(jsonobject.optString("order_status"));
                JSONArray addons = (JSONArray)jsonobject.getJSONArray("addons");
                List<List<String>> addon = new ArrayList<>();
                for(int j=0;j<addons.length();j++){
                    List<String> oneItem = new ArrayList<>();
                    JSONObject jsonobject2= (JSONObject) addons.get(j);
                    oneItem.add(jsonobject2.optString("addons_name"));
                    oneItem.add(jsonobject2.optString("quantity"));
                    oneItem.add(jsonobject2.optString("addons_group_name"));
                    addon.add(oneItem);
                }

                listDataChild.put(order_id, addon);
            }
            latestOrdersAdapter.notifyDataSetChanged();
            for(int i=0; i < latestOrdersAdapter.getGroupCount(); i++)
                listView.expandGroup(i);


        }catch(Exception e){
            Toast.makeText(getApplicationContext(),"Error", Toast.LENGTH_LONG).show();

        }

        getSupportActionBar().setTitle("");
        order.setText("Order "+ order_group_id);
        rest_name_t.setText(""+rest_name);
        if (status.equals("0")){
           tick.setImageResource(R.drawable.pending);
        }
        else if (status.equals("1")){
            tick.setImageResource(R.drawable.tick);
        }else{
            tick.setImageResource(R.drawable.rejected);
        }

    }

//    @Override
//    public void onBackPressed(){
//        finish();
//   // super.onBackPressed();
//
//   }
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

    private class sendOrderJson extends AsyncTask<String, Void, String[]> {

        private Exception exception;

        protected void onPreExecute() {
            super.onPreExecute();
            this.exception = null;

        }

        protected String[]  doInBackground(String...order_id) {
            String order = order_id[0].toString();
            String API_URL ="";
            if(order_id.length > 1){
                String confirm = order_id[1].toString();
                API_URL = "http://order.mondocloudsolutions.com/Api/mondo_os/order_again_by_group/"+order+"/"+rest_id+"/"+confirm;
            }else{
                API_URL = "http://order.mondocloudsolutions.com/Api/mondo_os/order_again_by_group/"+order+"/"+rest_id;
            }


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
                String[] str =new String[2];
                str[0] = stringBuilder.toString();
                str[1] = order;
                return str;
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

        protected void onPostExecute(final String[]  response) {
            if(response[0] == null) {
                //   setErrorText("Connection Error!");

            }else {
                try{
                    JSONObject object = (JSONObject) new JSONTokener(response[0]).nextValue();
                    String status = object.optString("status");
                    String type = object.optString("type");
                    String message = object.optString("message");

                    if(status.equals("false")){
                        if(type.equals("confirm")){
                            AlertDialog.Builder builder = new AlertDialog.Builder(LatestOrdersItem.this);
                            builder.setMessage(message)
                                    .setCancelable(false)
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            String[] input = {response[1], "1"};
                                            new LatestOrdersItem.sendOrderJson().execute(input);
                                        }
                                    })
                                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                        }
                                    });
                            AlertDialog alert = builder.create();
                            alert.show();

                        }else{
                            Toast.makeText(getApplicationContext(),message, Toast.LENGTH_LONG).show();
                        }
                    }else{
                        Toast.makeText(getApplicationContext(),"Order has been sent successfully", Toast.LENGTH_LONG).show();
                        Intent i = new Intent(LatestOrdersItem.this, LatestOrders.class);
                        finish();
                     //   startActivity(i);

                    }
                }catch(Exception e){
                    Toast.makeText(getApplicationContext(),"Error", Toast.LENGTH_LONG).show();
                }
            }

        }
    }

}

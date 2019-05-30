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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
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

public class CartActivity extends AppCompatActivity {
    Toolbar mToolbar;
    OrderDataBase dataBase;
    ArrayList<Integer>Order_idList = new ArrayList<>();
    ArrayList<String>Menu_nameList = new ArrayList<>();
    ArrayList<Integer>Item_idList = new ArrayList<>();
    ArrayList<String>Item_nameList = new ArrayList<>();
    ArrayList<String>Item_sizeList = new ArrayList<>();
    ArrayList<Integer>Item_sizeIdList = new ArrayList<>();
    ArrayList<Integer>QuantityList = new ArrayList<>();
    ArrayList<Double>PricesList = new ArrayList<>();
    ArrayList<String>InstructionsList = new ArrayList<>();
    ArrayList<String>Restaurant_nameList = new ArrayList<>();
    HashMap<Integer, List<List<String>>> listDataChild= new HashMap<>();
    ExpandableListView  listView;
    ActionMode actionMode;
    CartAdapter cartAdapter;
    int employee_id;
    RelativeLayout checkout;
    double total_price = 0, tax =0 ;
    String json = "";ProgressBar progressBar;
    TextView price, Tax, subtotal, Tax_value;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cart_activity);

        dataBase = new OrderDataBase(this);
        listView = (ExpandableListView) findViewById(R.id.Orders_listView);
        checkout = (RelativeLayout) findViewById(R.id.checkout);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        cartAdapter = new CartAdapter(CartActivity.this, Order_idList, Menu_nameList, Item_nameList, Item_sizeList,
                QuantityList, PricesList,InstructionsList,Restaurant_nameList,listDataChild);
        employee_id = Integer.parseInt(SaveSharedPreference.getEmployeeId(getApplicationContext()));
        listView.setAdapter(cartAdapter);

        price = (TextView) findViewById(R.id.price);
        Tax = (TextView) findViewById(R.id.HST_value);
        subtotal = (TextView) findViewById(R.id.subTotal);
        Tax_value = (TextView) findViewById(R.id.HST_percent);

        getOrders();
        registerForContextMenu(listView);

        checkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(Order_idList.size() ==0){
                    Toast.makeText(getApplicationContext(),"Cannot checkout 0 orders",Toast.LENGTH_LONG).show();
                }else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(CartActivity.this);
                    builder.setMessage("Are you sure you want to checkout orders? ")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Order[] orders = new Order[Order_idList.size()];
                                    for (int i = 0; i < Order_idList.size(); i++) {
                                        int order_id = (int) Order_idList.get(i);
                                        Order order = getOrder(order_id);
                                        orders[i] = order;
                                    }
                                    Gson gson = new Gson();
                                    json = gson.toJson(orders);

                                    new sendOrderJson().execute();
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

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                // Start the CAB using the ActionMode.Callback defined above
                // check / not check a child

                int itemType = ExpandableListView.getPackedPositionType(id);

                if(itemType == ExpandableListView.PACKED_POSITION_TYPE_GROUP) {
                    int groupPosition = ExpandableListView.getPackedPositionGroup(id);
                    ArrayList<Object> positionList = new ArrayList<Object>();
                    positionList.add(groupPosition);
                    positionList.add(view);
                    positionList.add(Order_idList.get(groupPosition));
                    if(cartAdapter.getSelectedChildren().size() == 0){
                        actionMode = startActionMode(actionModeCallback);
                        mToolbar.setVisibility(View.GONE);

                    }
                    if (cartAdapter.isPositionAleadyAdded(positionList) == null) {
                        cartAdapter.selectGroup(positionList);
                        actionMode.setTitle(cartAdapter.getSelectedChildren().size()+" Selected");
                    } else {

                        cartAdapter.deSelectGroup(positionList);
                        actionMode.setTitle(cartAdapter.getSelectedChildren().size()+" Selected");
                        if(cartAdapter.getSelectedChildren().size() == 0) {
                            actionMode.finish();

                        }
                    }

                    // Start the CAB using the ActionMode.Callback
                    // defined above

                    return true;

                } else {
                    // null item; we don't consume the click
                    return false;
                }


            }
        });


        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitleTextColor(Color.rgb(5,5,5));
        setSupportActionBar(mToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back);
        getSupportActionBar().setTitle("Cart");

    }

    private ActionMode.Callback actionModeCallback = new ActionMode.Callback() {// Called when the action mode is created; startActionMode() was called

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            // Inflate a menu resource providing context menu items
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.menu_selection, menu);
            return true;
        }

        // Called each time the action mode is shown. Always called after onCreateActionMode, but
        // may be called multiple times if the mode is invalidated.
        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false; // Return false if nothing is done
        }


        // Called when the user selects a contextual menu item
        @Override
        public boolean onActionItemClicked(final ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.delete:
                    AlertDialog.Builder builder = new AlertDialog.Builder(CartActivity.this);
                    builder.setMessage("Are you sure you want to delete the item?")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    deleteCurrentItems();
                                    mode.finish(); // Action picked, so close the CAB
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();

                    return true;
                default:
                    return false;
            }
        }

        // Called when the user exits the action mode
        @Override
        public void onDestroyActionMode(final ActionMode mode) {
            cartAdapter.unSelectAllChildren();
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    mToolbar.setVisibility(View.VISIBLE);
                }
            }, 300);

//            int doneButtonId = Resources.getSystem().getIdentifier("back", "id", "android");
//            View layout =  findViewById(doneButtonId);
//           // ImageView doneview = (ImageView) layout.getChildAt(0);
//            layout.setOnClickListener(new View.OnClickListener() {
//
//                @Override
//                public void onClick(View v) {
//
//                }
//            });
        }
    };

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

    public void deleteCurrentItems(){
        List<ArrayList<Object>> selectedItems = cartAdapter.getSelectedChildren();
        for(int i = 0 ; i< selectedItems.size();i++){
         int groupPosition = (int)selectedItems.get(i).get(0);
         dataBase.deleteOrder(Order_idList.get(groupPosition));
        }
        getOrders();
    }

    public void getOrders(){
        total_price = 0; tax =0;
        Cursor orders = dataBase.get_All_Orders(employee_id);
        Order_idList.clear();Menu_nameList.clear();Item_nameList.clear();Item_sizeList.clear();QuantityList.clear();
        InstructionsList.clear();PricesList.clear();listDataChild.clear();Item_idList.clear();Restaurant_nameList.clear();
        for(orders.moveToFirst(); !orders.isAfterLast(); orders.moveToNext()) {
            int order_id= orders.getInt(orders.getColumnIndex("order_id"));
            Order_idList.add(order_id);
            Menu_nameList.add(orders.getString(orders.getColumnIndex("main_menu_name")));
            Item_nameList.add(orders.getString(orders.getColumnIndex("item_name")));
            Item_idList.add(orders.getInt(orders.getColumnIndex("item_id")));
            Item_sizeList.add(orders.getString(orders.getColumnIndex("item_size_name")));
            Item_sizeIdList.add(orders.getInt(orders.getColumnIndex("item_size")));
            QuantityList.add(orders.getInt(orders.getColumnIndex("quantity")));
            InstructionsList.add(orders.getString(orders.getColumnIndex("instructions")));
            PricesList.add(orders.getDouble(orders.getColumnIndex("item_price")));
            Restaurant_nameList.add(orders.getString(orders.getColumnIndex("restaurant_name")));
            total_price += orders.getDouble(orders.getColumnIndex("item_price"));
            tax =orders.getDouble(orders.getColumnIndex("TAX"));
            Cursor addons = dataBase.get_All_Addons(order_id);
            List<List<String>> addon = new ArrayList<>();
            for(addons.moveToFirst(); !addons.isAfterLast(); addons.moveToNext()) {
                List<String> oneItem = new ArrayList<>();
                oneItem.add(addons.getString(addons.getColumnIndex("addon_name")));
                oneItem.add(addons.getString(addons.getColumnIndex("addon_quantity")));
                oneItem.add(addons.getString(addons.getColumnIndex("addon_price")));
                oneItem.add(addons.getString(addons.getColumnIndex("addon_id")));
                oneItem.add(addons.getString(addons.getColumnIndex("addons_name")));
                addon.add(oneItem);
            }
            addons.close();

            listDataChild.put(order_id, addon);
        }

        orders.close();
        cartAdapter.notifyDataSetChanged();
        for(int i=0; i < cartAdapter.getGroupCount(); i++)
            listView.expandGroup(i);

        Tax.setText((Math.round(total_price *tax* 100d)/100d)+"");
        Tax_value.setText("("+(int)(tax*100)+"%)");
        subtotal.setText((Math.round(total_price * 100d)/100d)+"");
        double total_tax = (total_price*tax);
        double total = total_price+total_tax;
        price.setText((Math.round(total * 100d)/100d)+"");

    }

    public Order getOrder(int order_id){
        int index = Order_idList.indexOf(order_id);
        int item_id = Item_idList.get(index);
        int quantity = QuantityList.get(index);
        int size_id = Item_sizeIdList.get(index);
        String note = InstructionsList.get(index);

        List<List<String>> addons = listDataChild.get(order_id);
        Addon[] addon = new Addon[addons.size()];
        for(int i=0;i<addons.size();i++){
            List<String> oneAddon = addons.get(i);
            Addon ad= new Addon(Integer.parseInt(oneAddon.get(3)), Integer.parseInt(oneAddon.get(1)));
            addon[i] = ad;
        }

        Order order = new Order(addon ,employee_id,item_id,quantity,size_id,note);
       return order;
    }


    private class sendOrderJson extends AsyncTask<Void, Void, String> {

        private Exception exception;

        protected void onPreExecute() {
            super.onPreExecute();
            this.exception = null;
            progressBar.setVisibility(View.VISIBLE);
        }

        protected String  doInBackground(Void... urls) {

            String API_URL = "http://order.mondocloudsolutions.com/Api/mondo_os/new_order";
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
                params.add(new BasicNameValuePair("order_data", json));

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

                Toast.makeText(getApplicationContext(),"Order has been sent successfully",Toast.LENGTH_LONG).show();
                dataBase.deleteAllOrders(employee_id);
                finish();
                Intent i = new Intent(CartActivity.this,LatestOrders.class);
                startActivity(i);
            }
            progressBar.setVisibility(View.GONE);
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

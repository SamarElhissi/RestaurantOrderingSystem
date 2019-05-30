package mondo.orderingsystem;


import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.squareup.picasso.Picasso;

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

public class ResturantMenu extends AppCompatActivity {

    private MenuAdapter menuAdapter;
    private ExpandableListView listView;
    private TextView errorText;
    private ArrayList<String> idsList = new ArrayList<String>();
    private ArrayList<String> namesList = new ArrayList<String>();
    private ArrayList<String> coverImages = new ArrayList<String>();
    private ArrayList<JSONArray> Addons = new ArrayList<JSONArray>();
    private JSONArray menuItems;
    private HashMap<String, JSONArray> listDataChild= new HashMap<String, JSONArray>();;
    String restaurant_id="";
    private ProgressBar progressBar;
    OrderDataBase dataBase ;
    String logoimage = "";
    String restaurant_name = "";
    String restaurantCurrency = "", restaurant_tax="";
    Toolbar mToolbar;
    int employee_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu);
        dataBase = new OrderDataBase(this);
        /*******************************/
        employee_id = Integer.parseInt(SaveSharedPreference.getEmployeeId(getApplicationContext()));
        progressBar = (ProgressBar) findViewById(R.id.menu_progressBar);
        listView = (ExpandableListView) findViewById(R.id.menu);
        ImageView logo = (ImageView) findViewById(R.id.rest_logo);
        Intent intent = getIntent();

        logoimage = intent.getStringExtra("restaurant_logo");
        restaurant_id = intent.getStringExtra("restaurant_id");
        restaurant_name = intent.getStringExtra("restaurant_name");
        restaurantCurrency = intent.getStringExtra("restaurantCurrency");
        restaurant_tax = intent.getStringExtra("restaurant_tax");

        Picasso.with(getApplicationContext()).load("http://order.mondocloudsolutions.com/upload/images/"+ logoimage).resize(80,80).centerCrop().into(logo);
        /*****************resuturant list**************/

        menuAdapter = new MenuAdapter(ResturantMenu.this, idsList,namesList, coverImages,listDataChild,Addons);
        menuAdapter.notifyDataSetChanged();
        listView.setAdapter(menuAdapter);

        new getRestuarntMenu().execute();
       // TextView title = (TextView) findViewById(R.id.title);
      //  title.setText("POpeyws");
        mToolbar = (Toolbar) findViewById(R.id.menu_toolbar);
        mToolbar.setTitleTextColor(Color.rgb(5,5,5));
        setSupportActionBar(mToolbar);
//
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back);
        getSupportActionBar().setTitle(restaurant_name);

        listView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {

                JSONObject jsonobject = (JSONObject) parent.getExpandableListAdapter().getChild(groupPosition, childPosition);
                Intent intent = new Intent(v.getContext(), MenuAddons.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                intent.putExtra("main_menu_id", ""+idsList.get(groupPosition).toString());
                intent.putExtra("main_menu_name", ""+ namesList.get(groupPosition).toString());
                intent.putExtra("item_id", ""+jsonobject.optString("item_id"));
                intent.putExtra("item_name", ""+jsonobject.optString("item_name"));
                intent.putExtra("item_desc", ""+jsonobject.optString("item_desc"));
                intent.putExtra("item_price", ""+jsonobject.optString("item_price"));
                intent.putExtra("Addons", Addons.get(groupPosition).toString());

                intent.putExtra("cover", ""+jsonobject.optString("item_image"));
                intent.putExtra("logoimage", logoimage);
                intent.putExtra("restaurant_name", restaurant_name);
                intent.putExtra("restaurantCurrency", restaurantCurrency);
                intent.putExtra("restaurant_tax", restaurant_tax);

                try{
                    JSONArray item_addons = jsonobject.getJSONArray("item_addons");
                    intent.putExtra("item_addons", item_addons.toString());

                    JSONArray item_size = jsonobject.getJSONArray("item_size");
                    intent.putExtra("item_size", item_size.toString());

                }catch (Exception e){

                }
                v.getContext().startActivity(intent);
                return false;
            }
        });

    }


    @Override
    public void onResume(){
        super.onResume();
        ResturantMenu.this.invalidateOptionsMenu();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_items, menu);
        RelativeLayout badgeLayout = (RelativeLayout) menu.findItem(R.id.cart).getActionView();
        RelativeLayout search = (RelativeLayout) menu.findItem(R.id.search_item).getActionView();

        int count = dataBase.getOrderCount(employee_id);
        String msg = "" +count;
        MainActivity.setNotification(badgeLayout, msg, getApplicationContext());
        badgeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), CartActivity.class);
                startActivity(i);
            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), Search.class);
                i.putExtra("restaurant_id", restaurant_id);
                i.putExtra("restaurant_name", restaurant_name);
                startActivity(i);
            }
        });
        return true;
    }

    /***
     * Called when invalidateOptionsMenu() is triggered
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // if nav drawer is opened, hide the action items
        //   boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        //   menu.findItem(R.id.action_settings).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    /*****************************************************************/
    private class getRestuarntMenu extends AsyncTask<Void, Void, String> {

        private Exception exception;

        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);

            super.onPreExecute();
            this.exception = null;

        }

        protected String  doInBackground(Void... urls) {

            String API_URL = "http://order.mondocloudsolutions.com/Api/mondo_os/menu/"+restaurant_id;
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
             //   setErrorText("Connection Error!");

            }else {

                try{

                    JSONObject object = (JSONObject) new JSONTokener(response).nextValue();
                    JSONArray data = object.getJSONArray("data");
                    idsList.clear();  namesList.clear();  coverImages.clear();
                    Addons.clear();listDataChild.clear();
                    for(int i=0;i<data.length();i++){

                        JSONObject jsonobject= (JSONObject) data.get(i);
                        idsList.add(jsonobject.optString("main_menu_id"));
                        namesList.add(jsonobject.optString("main_menu_name"));
                        coverImages.add(jsonobject.optString("main_menu_image"));
                       // menuItems=jsonobject.getJSONArray("Menu_Item");
                        Addons.add(jsonobject.getJSONArray("Addons"));
                        listDataChild.put(idsList.get(i), jsonobject.getJSONArray("Menu_Item"));

                    }
                   // menuAdapter.notifyDataSetChanged();

                 //   listView.setVisibility(View.VISIBLE);

                    for(int i=0; i < menuAdapter.getGroupCount(); i++)
                        listView.expandGroup(i);
                }catch(Exception e){
                    Toast.makeText(getApplicationContext(),"Error", Toast.LENGTH_LONG).show();
                }

            }

            progressBar.setVisibility(View.GONE);
        }
    }

    void setErrorText(String msg){
        errorText.setText(msg);
        errorText.setVisibility(View.VISIBLE);
    }
/*****************************************************************/
}

package mondo.orderingsystem;


import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{
    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    // nav drawer title
    private CharSequence mDrawerTitle;
    // used to store app title
    private CharSequence mTitle;

    // slide menu items
    private String[] navMenuTitles;
    private TypedArray navMenuIcons;

    private ArrayList<NavDrawerItem> navDrawerItems;
    private NavDrawerListAdapter adapter;
    private SwipeRefreshLayout swipeView;
    private ResturantAdapter resturantAdapter;
    private ListView listView;
    private TextView errorText;
    private ArrayList<String> idsList = new ArrayList<String>();
    private ArrayList<String> namesList = new ArrayList<String>();
    private ArrayList<String> logoImages = new ArrayList<String>();
    private ArrayList<String> locationsList = new ArrayList<String>();
    private ArrayList<String> coverImages = new ArrayList<String>();
    private ArrayList<String> restaurantCurrency = new ArrayList<String>();
    private ArrayList<String> restaurant_tax = new ArrayList<String>();
    private ProgressBar progressBar;
    OrderDataBase dataBase ;int employee_id;
    public static String tax= "";
    private static final int MY_PERMISSIONS_REQUEST = 0;

    // Resgistration Id from GCM
    private static final String PREF_GCM_REG_ID = "PREF_GCM_REG_ID";
    private SharedPreferences prefs;
    // Your project number and web server url. Please change below.
    private static final String GCM_SENDER_ID = "96491869165";
    private static final String WEB_SERVER_URL = "http://order.mondocloudsolutions.com/Api/gcm/register_user";

    GoogleCloudMessaging gcm;
    private static final int ACTION_PLAY_SERVICES_DIALOG = 100;
    protected static final int MSG_REGISTER_WITH_GCM = 101;
    protected static final int MSG_REGISTER_WEB_SERVER = 102;
    protected static final int MSG_REGISTER_WEB_SERVER_SUCCESS = 103;
    protected static final int MSG_REGISTER_WEB_SERVER_FAILURE = 104;
    private String gcmRegId;
    Intent i;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dataBase = new OrderDataBase(this);
        /*******************************/
        employee_id = Integer.parseInt(SaveSharedPreference.getEmployeeId(getApplicationContext()));
        swipeView = (SwipeRefreshLayout) findViewById(R.id.swipe_view);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        listView = (ListView) findViewById(R.id.listView);

        /*************swipe view******************/

        swipeView.setOnRefreshListener(MainActivity.this);
        swipeView.setColorSchemeColors(Color.RED, Color.WHITE);
        swipeView.setDistanceToTriggerSync(20);// in dips
        swipeView.setSize(SwipeRefreshLayout.DEFAULT);// LARGE also can be used

        i = getIntent();
        String msg = i.getStringExtra("msg");
        String title = i.getStringExtra("title");

//        Log.i("", SaveSharedPreference.getReg_id(this));
        if(!(msg == null) &&!msg.equals("") && !msg.equals("null")  ){
            AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
            builder1.setTitle(title);
            builder1.setMessage(msg);
            builder1.setCancelable(true);
            builder1.setPositiveButton(android.R.string.ok,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });

            AlertDialog alert11 = builder1.create();
            alert11.show();
//
        }
       /*****************resuturant list**************/

        resturantAdapter = new ResturantAdapter(MainActivity.this, idsList,namesList, logoImages, coverImages,locationsList, restaurantCurrency, restaurant_tax);
        resturantAdapter.notifyDataSetChanged();
        listView.setAdapter(resturantAdapter);
        new getRestuarnts().execute();

        /**************Logout**************************/
        RelativeLayout logout = (RelativeLayout) findViewById(R.id.Logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SaveSharedPreference.setEmail(getApplicationContext(),"");
                SaveSharedPreference.setPassword(getApplicationContext(),"");
                SaveSharedPreference.setEmployeeId(getApplicationContext(),"");
                SaveSharedPreference.setEmployeeFN(getApplicationContext(),"");
                SaveSharedPreference.setEmployeeLN(getApplicationContext(),"");
                SaveSharedPreference.setCompany_name(getApplicationContext(),"");
                SaveSharedPreference.setCustomer_id(getApplicationContext(),"");
                SaveSharedPreference.setReg_id(getApplicationContext(),"");

                Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(intent);
            }
        });
       /***************Navigation drawer****************/

        mTitle = mDrawerTitle = getTitle();
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        // load slide menu items
        navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);
        // nav drawer icons from resources
        navMenuIcons = getResources().obtainTypedArray(R.array.nav_drawer_icons);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.list_slidermenu);

        navDrawerItems = new ArrayList<NavDrawerItem>();
        // adding nav drawer items to array
        // Home
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[0], navMenuIcons.getResourceId(0, -1)));
        // Find People
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[1], navMenuIcons.getResourceId(1, -1)));
        // Photos
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[2], navMenuIcons.getResourceId(2, -1)));
        // Communities, Will add a counter here
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[3], navMenuIcons.getResourceId(3, -1)));
        // Pages
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[4], navMenuIcons.getResourceId(4, -1)));
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[5], navMenuIcons.getResourceId(5, -1)));

        // Recycle the typed array
        navMenuIcons.recycle();
        mDrawerList.setOnItemClickListener(new SlideMenuClickListener());

        // setting the nav drawer list adapter
        adapter = new NavDrawerListAdapter(getApplicationContext(), navDrawerItems);
        mDrawerList.setAdapter(adapter);
        ActionBar b = getSupportActionBar();
        // enabling action bar app icon and behaving it as toggle button
       getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.menuicon);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.drawable.menu_icon, //nav menu toggle icon
                R.string.app_name, // nav drawer open - description for accessibility
                R.string.app_name // nav drawer close - description for accessibility
        ) {
            public void onDrawerClosed(View view) {
                getSupportActionBar().setTitle(mTitle);
                // calling onPrepareOptionsMenu() to show action bar icons
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                getSupportActionBar().setTitle(mDrawerTitle);
                // calling onPrepareOptionsMenu() to hide action bar icons
                invalidateOptionsMenu();
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

//        if (savedInstanceState == null) {
//            // on first time display view for first nav item
//            displayView(0);
//        }

        if (isGoogelPlayInstalled()) {
            gcm = GoogleCloudMessaging.getInstance(getApplicationContext());
            // Read saved registration id from shared preferences.
            gcmRegId = SaveSharedPreference.getReg_id(getApplicationContext());

            if (TextUtils.isEmpty(gcmRegId)) {
                GCMhandler.sendEmptyMessage(MSG_REGISTER_WITH_GCM);
            }
            else{
                //   regIdView.setText(gcmRegId);
                //   Toast.makeText(getApplicationContext(), "Already registered with GCM", Toast.LENGTH_SHORT).show();
            }
        }


    }

    private boolean isGoogelPlayInstalled() {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        ACTION_PLAY_SERVICES_DIALOG).show();
            } else {
                Toast.makeText(getApplicationContext(),
                        "Google Play Service is not installed",
                        Toast.LENGTH_SHORT).show();
                finish();
            }
            return false;
        }
        return true;

    }

    Handler handler = new Handler() {

        public void handleMessage(android.os.Message msg) {

            swipeView.postDelayed(new Runnable() {

                @Override
                public void run() {

             swipeView.setRefreshing(false);

                }
            }, 1000);


        };
    };

    @Override
    public void onRefresh() {

        swipeView.postDelayed(new Runnable() {

            @Override
            public void run() {
                swipeView.setRefreshing(true);
                handler.sendEmptyMessage(0);
            }
        }, 1000);
        namesList.clear();idsList.clear();logoImages.clear();locationsList.clear();coverImages.clear();
        restaurantCurrency.clear();restaurant_tax.clear();
        new getRestuarnts().execute();
    }

    /**
     * Slide menu item click listener
     * */
    private class SlideMenuClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            // display view for selected nav drawer item
            displayView(position);
        }
    }
    @Override
    public void onResume(){
        super.onResume();
        MainActivity.this.invalidateOptionsMenu();
    }
    TextView tv;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_items, menu);
        RelativeLayout badgeLayout = (RelativeLayout) menu.findItem(R.id.cart).getActionView();
        RelativeLayout search = (RelativeLayout) menu.findItem(R.id.search_item).getActionView();
        int count = dataBase.getOrderCount(employee_id);
        String msg = "" +count;
        setNotification(badgeLayout, msg, getApplicationContext());

//        MenuItem searchItem = menu.findItem(R.id.search);
//        SearchView searchView =
//                (SearchView) MenuItemCompat.getActionView(searchItem);
//        EditText searchEditText = (EditText) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
//        searchEditText.setTextColor(Color.BLACK);
//        searchEditText.setHintTextColor(Color.BLACK);

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
                startActivity(i);
            }
        });

        return true;
    }

     public static void setNotification(RelativeLayout badgeLayout, String message, final Context context){
         TextSwitcher textSwitcher = (TextSwitcher) badgeLayout.findViewById(R.id.textSwitcher);

         textSwitcher.setFactory(new ViewSwitcher.ViewFactory() {


             public View makeView() {
                 // TODO Auto-generated method stub
                 // create new textView and set the properties like clolr, size etc
                 TextView myText = new TextView(context);
                 myText.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL);
                 myText.setTextSize(11);
                 myText.setTextColor(Color.WHITE);
                 myText.setBackgroundResource(R.drawable.badge_circle);
                 myText.setPadding(2,2,2,2);
                 myText.setTypeface(null, Typeface.BOLD);

                // myText.setWidth(38);
                // myText.setHeight(38);
                 return myText;
             }
         });

         textSwitcher.setInAnimation(context, android.R.anim.fade_in);
         textSwitcher.setOutAnimation(context, android.R.anim.fade_out);
//
if(message.equals("0")) textSwitcher.setVisibility(View.GONE);
         else{
    textSwitcher.setText(message);
    textSwitcher.setVisibility(View.VISIBLE);
}

     }
    @Override
    public void onBackPressed(){
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // toggle nav drawer on selecting action bar app icon/title
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle action bar actions click
        switch (item.getItemId()) {
            case R.id.cart:
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);
            default:
                return super.onOptionsItemSelected(item);
        }
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

    /**
     * Diplaying fragment view for selected nav drawer list item
     * */
    private void displayView(int position) {
        // update the main content by replacing fragments
        Fragment fragment = null;
        switch (position) {
            case 0:

             //   fragment = new HomeFragment();
                break;
            case 1:
            //    fragment = new FindPeopleFragment();
                break;
            case 2:
                Intent i4 = new Intent(MainActivity.this, LatestOrders.class);
                startActivity(i4);
                break;
            case 3:
                Intent feed = new Intent(MainActivity.this, Feedback.class);
                startActivity(feed);
                //fragment = new Terms();

                break;
            case 4:
                Intent i = new Intent(MainActivity.this, Terms.class);
                startActivity(i);

              // fragment = new PagesFragment();
                break;
            case 5:
                Intent i2 = new Intent(MainActivity.this, About.class);
                startActivity(i2);

                // fragment = new PagesFragment();
                break;
            default:
                break;
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.frame_container, fragment).commit();

            // update selected item and title, then close the drawer
            mDrawerList.setItemChecked(position, true);
            mDrawerList.setSelection(position);
            setTitle(navMenuTitles[position]);
            mDrawerLayout.closeDrawer(mDrawerList);
        } else {
            // error in creating fragment
           // Log.e("MainActivity", "Error in creating fragment");
        }
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getSupportActionBar().setTitle(mTitle);
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    /*****************************************************************/
    private class getRestuarnts extends AsyncTask<Void, Void, String> {

        private Exception exception;

        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            super.onPreExecute();
            this.exception = null;

        }

        protected String  doInBackground(Void... urls) {

            String API_URL = "http://order.mondocloudsolutions.com/Api/mondo_os/restaurant/" + SaveSharedPreference.getCustomer_id(getApplicationContext());
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

                    JSONObject object = (JSONObject) new JSONTokener(response).nextValue();
                    JSONObject data =(JSONObject) object.getJSONObject("data");
                    JSONArray resturants = data.getJSONArray("restaurants");

                    for(int i=0;i<resturants.length();i++){

                        JSONObject jsonobject= (JSONObject) resturants.get(i);
                        idsList.add(jsonobject.optString("restaurant_id"));
                        namesList.add(jsonobject.optString("restaurant_name"));
                        logoImages.add(jsonobject.optString("restaurant_logo"));
                        locationsList.add(jsonobject.optString("foods_types_name") );
                        coverImages.add(jsonobject.optString("restaurant_image"));
                        restaurantCurrency.add(jsonobject.optString("restaurant_currency_name"));
                        restaurant_tax.add(jsonobject.optString("restaurant_tax"));
                        tax= jsonobject.optString("restaurant_tax");
                    }
                    resturantAdapter.notifyDataSetChanged();
                    listView.setVisibility(View.VISIBLE);


                }catch(Exception e){
                    Toast.makeText(getApplicationContext(),"Error", Toast.LENGTH_LONG).show();
                }

            }
            progressBar.setVisibility(View.GONE);

        }
    }

    Handler GCMhandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case MSG_REGISTER_WITH_GCM:
                    new GCMRegistrationTask().execute();
                    break;
                case MSG_REGISTER_WEB_SERVER:
                    new WebServerRegistrationTask().execute();
                    break;
                case MSG_REGISTER_WEB_SERVER_SUCCESS:
                    //   Toast.makeText(getApplicationContext(),
                    //         "registered with web server", Toast.LENGTH_LONG).show();
                    break;
                case MSG_REGISTER_WEB_SERVER_FAILURE:
                    Toast.makeText(getApplicationContext(),
                            "registration with web server failed",
                            Toast.LENGTH_LONG).show();
                    break;
            }
        };
    };
/*******************************************************************/

private class GCMRegistrationTask extends AsyncTask<Void, Void, String> {

    @Override
    protected String doInBackground(Void... params) {
        // TODO Auto-generated method stub
        if (gcm == null && isGoogelPlayInstalled()) { gcm = GoogleCloudMessaging.getInstance(getApplicationContext());

        }
        try {
            gcmRegId = gcm.register(GCM_SENDER_ID);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return gcmRegId;
    }

    @Override
    protected void onPostExecute(String result) {
        if (result != null) {
            SaveSharedPreference.setReg_id(getApplicationContext(),result);
            GCMhandler.sendEmptyMessage(MSG_REGISTER_WEB_SERVER);
        }
    }

}

    private class WebServerRegistrationTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            URL url = null;
            try {
                url = new URL(WEB_SERVER_URL);
            } catch (MalformedURLException e) {
                e.printStackTrace();
                GCMhandler.sendEmptyMessage(MSG_REGISTER_WEB_SERVER_FAILURE);
            }
            Map<String, String> dataMap = new HashMap<String, String>();
            dataMap.put("regId", gcmRegId);
            dataMap.put("os", "android");
            dataMap.put("customer_id", SaveSharedPreference.getCustomer_id(getApplicationContext()));
            StringBuilder postBody = new StringBuilder();
            Iterator iterator = dataMap.entrySet().iterator();

            while (iterator.hasNext()) {
                Map.Entry param = (Map.Entry) iterator.next();
                postBody.append(param.getKey()).append('=')
                        .append(param.getValue());
                if (iterator.hasNext()) {
                    postBody.append('&');
                }
            }
            String body = postBody.toString();
            byte[] bytes = body.getBytes();

            HttpURLConnection conn = null;
            try {
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoOutput(true);
                conn.setUseCaches(false);
                conn.setFixedLengthStreamingMode(bytes.length);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type",
                        "application/x-www-form-urlencoded;charset=UTF-8");

                OutputStream out = conn.getOutputStream();
                out.write(bytes);
                out.close();

                int status = conn.getResponseCode();
                if (status == 200) {
                    // Request success
                    GCMhandler.sendEmptyMessage(MSG_REGISTER_WEB_SERVER_SUCCESS);
                } else {
                    InputStream error = conn.getErrorStream();
                    throw new IOException("Request failed with error code "
                            + status);
                }
            } catch (ProtocolException pe) {
                pe.printStackTrace();
                GCMhandler.sendEmptyMessage(MSG_REGISTER_WEB_SERVER_FAILURE);
            } catch (IOException io) {
                io.printStackTrace();
                GCMhandler.sendEmptyMessage(MSG_REGISTER_WEB_SERVER_FAILURE);
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }

            return null;
        }
    }
/*****************************************************************/
}

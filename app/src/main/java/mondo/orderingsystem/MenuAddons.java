package mondo.orderingsystem;


import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.AnimatedStateListDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.squareup.picasso.Picasso;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class MenuAddons extends AppCompatActivity {

    private ItemAdapter itemAdapter;
    private ExpandableListView listView;
    private ProgressBar progressBar;
    Toolbar mToolbar;
    TextView quantity;
    public static TextView price;
    JSONArray item_size_array = null;
    public static String[] orderList = new String[9];
    public static ArrayList<String[]> addons_orderList = new ArrayList<>();
    OrderDataBase dataBase ;Intent intent;
    String item_size; String item_size_name;
    EditText instructions; int order_numbers =0;
    public static double quant= 1;
    public static double menu_item_price = 0;
    public static boolean cart_click = false;
    JSONArray complete_Addons_array = null;
    int employee_id;
    boolean scroll =false;
    public static double tax;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_details);
        dataBase = new OrderDataBase(this);
        employee_id = Integer.parseInt(SaveSharedPreference.getEmployeeId(getApplicationContext()));
        order_numbers = dataBase.getOrderCount(employee_id);
        /*******************************/

        //  progressBar = (ProgressBar) findViewById(R.id.menu_progressBar);
        listView = (ExpandableListView) findViewById(R.id.item_list);
        LayoutInflater inflater = getLayoutInflater();
        ViewGroup header = (ViewGroup)inflater.inflate(R.layout.item_details_header, listView, false);
        listView.addHeaderView(header, null, false);

        final ViewGroup footer = (ViewGroup)inflater.inflate(R.layout.item_details_footer, listView, false);
        listView.addFooterView(footer, null, false);

        ImageView logo = (ImageView) header.findViewById(R.id.rest_logo);
        ImageView coverImage = (ImageView) header.findViewById(R.id.rest_cover);
        TextView title = (TextView) header.findViewById(R.id.menu_name);
        TextView desc = (TextView) header.findViewById(R.id.desc);
        RadioGroup itemsizes = (RadioGroup) header.findViewById(R.id.itemsizes);

        price = (TextView) findViewById(R.id.price);
        intent = getIntent();

        String logoimage = intent.getStringExtra("logoimage");
        final String restaurant_name = intent.getStringExtra("restaurant_name");
        String restaurantCurrency = intent.getStringExtra("restaurantCurrency");
        String restaurant_tax = intent.getStringExtra("restaurant_tax");
        if(!restaurant_tax.equals("")) tax = Double.parseDouble(restaurant_tax)/100;
        else if (!MainActivity.tax.equals(""))
            tax = Double.parseDouble(MainActivity.tax)/100;
        else
            tax = 0;

        TextView curr = (TextView) findViewById(R.id.currency);
        curr.setText(restaurantCurrency);
        String cover = intent.getStringExtra("cover");

        String item_name = intent.getStringExtra("item_name");
        String item_desc = intent.getStringExtra("item_desc");
        menu_item_price = Double.parseDouble(intent.getStringExtra("item_price"));

        title.setText(item_name);
        desc.setText(item_desc);
     //   double total = menu_item_price +(menu_item_price* tax);
        double total = menu_item_price ;
        price.setText((Math.round(total * 100d)/100d)+"");
        String Addons = intent.getStringExtra("Addons");
        String item_addons = intent.getStringExtra("item_addons");
        item_size = intent.getStringExtra("item_size");

        JSONArray Addons_array = null; JSONArray item_addons_array = null;
        try {
            Addons_array = new JSONArray(Addons);
            item_addons_array = new JSONArray(item_addons);
            item_size_array = new JSONArray(item_size);
            int checked = 0;
            for(int i = 0 ; i < item_size_array.length();i++){

                RadioButton rb = new RadioButton(getApplicationContext());

                JSONObject jsonobject= (JSONObject) item_size_array.get(i);
                rb.setId(i);
                //   rb.setTag(1, jsonobject.optString("size_price"));
                rb.setText(jsonobject.optString("size_name"));
                rb.setTextColor(Color.BLACK);
                rb.setButtonDrawable(R.drawable.radio_buttons);
                String is_selected = jsonobject.optString("is_selected");
                itemsizes.addView(rb);
                if(is_selected.equals("1")) {
                    if(checked == 0){
                        itemsizes.check(rb.getId());
                       // double total2 = Double.parseDouble(jsonobject.optString("size_price")) +(Double.parseDouble(jsonobject.optString("size_price"))* tax);
                        double total2 = Double.parseDouble(jsonobject.optString("size_price"));
                        price.setText((Math.round(total2 * 100d)/100d)+"");
                        menu_item_price = Double.parseDouble(jsonobject.optString("size_price"));
                        item_size = jsonobject.optString("size_id");
                        item_size_name = jsonobject.optString("size_name");
                    }
                    checked = 1;
                }

            }

            if(item_size_array.length() == 0){
                item_size ="";
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            complete_Addons_array= concatArray(Addons_array,item_addons_array );
        } catch (JSONException e) {
            e.printStackTrace();
        }


        Picasso.with(getApplicationContext()).load("http://order.mondocloudsolutions.com/upload/images/"+ logoimage).resize(80,80).centerCrop().into(logo);
        Picasso.with(getApplicationContext()).load("http://order.mondocloudsolutions.com/upload/images/"+ cover).fit().centerCrop().into(coverImage);

        /*****************resuturant list**************/

        itemAdapter = new ItemAdapter(MenuAddons.this, complete_Addons_array);
       // itemAdapter.notifyDataSetChanged();
        listView.setAdapter(itemAdapter);

        listView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v,
                                        int groupPosition, long id) {
                return true; // This way the expander cannot be collapsed
            }
        });

        for(int i=0; i < itemAdapter.getGroupCount(); i++){
            listView.expandGroup(i);

        }

     listView.setOnScrollListener(new AbsListView.OnScrollListener() {
         @Override
         public void onScrollStateChanged(AbsListView absListView, int i) {

         }

         @Override
         public void onScroll(AbsListView absListView, final int firstVisibleItem,
                              final int visibleItemCount, final int totalItemCount) {
             final int lastItem = firstVisibleItem + visibleItemCount;
             if(lastItem == totalItemCount) {
                 scroll =true;
             }
         }
     });


//        int firstVis  = listView.getFirstVisiblePosition();
//        int lastVis = listView.getLastVisiblePosition();
//
//        int count = firstVis;
//
//        while (count <= lastVis)
//        {
//            long longposition = listView.getExpandableListPosition(count);
//            int type = ExpandableListView.getPackedPositionType(longposition);
//            if (type == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
//                int groupPosition = ExpandableListView.getPackedPositionGroup(longposition);
//                int childPosition = ExpandableListView.getPackedPositionChild(longposition);
//                //  Log.d("Test","group: " + groupPosition + " and child: " + childPosition );
//            }
//            count++;
//
//        }

        mToolbar = (Toolbar) findViewById(R.id.menu_toolbar);
        mToolbar.setTitleTextColor(Color.rgb(5,5,5));
        setSupportActionBar(mToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back);
        getSupportActionBar().setTitle(restaurant_name);

        ImageButton add = (ImageButton) footer.findViewById(R.id.add);
        ImageButton minus = (ImageButton) footer.findViewById(R.id.minus);
        quantity = (TextView) footer.findViewById(R.id.quantity);
        instructions = (EditText) footer.findViewById(R.id.specialInst);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int total = Integer.parseInt(""+quantity.getText()) + 1;
                quantity.setText(""+total);
                double total_price = (menu_item_price + ItemAdapter.total_addons_prices)* total;
               // double total2 = total_price +(total_price* tax);
                price.setText((Math.round(total_price * 100d)/100d) +"");
                quant++;

            }
        });

        minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Integer.parseInt(""+quantity.getText()) > 1){
                    int total = Integer.parseInt(""+quantity.getText()) - 1;
                    quantity.setText(""+total);
                    double total_price = (menu_item_price + ItemAdapter.total_addons_prices)* total;
                   // double total2 = total_price +(total_price* tax);
                    price.setText((Math.round(total_price * 100d)/100d) +"");
                    quant--;
                }

            }
        });

        itemsizes.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                int selectedId = radioGroup.getCheckedRadioButtonId();
                try{
                    JSONObject jsonobject= (JSONObject) item_size_array.get(selectedId);
                    int total = Integer.parseInt(""+quantity.getText());
                    item_size = jsonobject.optString("size_id");
                    item_size_name = jsonobject.optString("size_name");
                    menu_item_price = Double.parseDouble(jsonobject.optString("size_price"));
                    double total1 = ((menu_item_price+ ItemAdapter.total_addons_prices) * total);
                   // double total2 = total1 +(total1* tax);
                    price.setText((Math.round(total1 * 100d)/100d) +"");
                }catch (Exception e){

                }


            }
        });
        RelativeLayout btnCart = (RelativeLayout) findViewById(R.id.button_relative);
        btnCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(scroll) {
                    cart_click = true;
                    addons_orderList.clear();
                    try {

                        int groupCount = listView.getExpandableListAdapter().getGroupCount();

                        for (int i = 0; i < groupCount; i++) {
                            JSONObject jsonobject = (JSONObject) complete_Addons_array.get(i);
                            String mandatory = jsonobject.optString("addons_is_mandatory");
                            int maximum = Integer.parseInt(jsonobject.optString("addons_maxmum"));
                            int minimum = Integer.parseInt(jsonobject.optString("addons_minmum"));
                            String group_name = jsonobject.optString("addons_name");
                            boolean selected = false;
                            int selected_items = 0;
                            int childCount = listView.getExpandableListAdapter().getChildrenCount(i);

                            for (int j = 0; j < childCount; j++) {
                                HashMap<Integer, boolean[]> checkStates = itemAdapter.getCheckedArray();
                                HashMap<Integer, Integer[]> quantities = itemAdapter.getQuantityArray();
                                boolean check = checkStates.get(i)[j];
                                int quant = quantities.get(i)[j];
                                //    Holder holder = ItemAdapter.HolderArray.get(i).get(j);
                                //   CheckBox check = (CheckBox) holder.getView(R.id.type);
                                //   TextView quant = (TextView) holder.getView(R.id.quant);
                                String[] addon = new String[5];
                                JSONObject child = (JSONObject) listView.getExpandableListAdapter().getChild(i, j);

                                if (check) {
                                    // String quantity = quant.getText().toString();
                                    addon[0] = child.optString("item_id");
                                    addon[1] = child.optString("item_name");
                                    addon[2] = quant + "";
                                    addon[3] = child.optString("item_price");
                                    addon[4] = group_name;
                                    addons_orderList.add(addon);
                                    selected = true;
                                    selected_items++;
                                }

                            }
                            if (!selected && mandatory.equals("1")) {
                                throw new Exception("You must select at least one addon from " + group_name);
                            }

                            if (selected_items < minimum && mandatory.equals("1")) {
                                throw new Exception("You must select at least " + minimum + " addon from " + group_name);
                            }

                            if (selected_items > maximum && mandatory.equals("1")) {
                                throw new Exception("You must select at most " + maximum + " addon from " + group_name);
                            }
                        }

                        int item_size_id = 0;
                        if (item_size != "" && item_size != null)
                            item_size_id = Integer.parseInt(item_size);
//                        double total_price = ((menu_item_price + ItemAdapter.total_addons_prices) * quant) +
//                                ((menu_item_price + ItemAdapter.total_addons_prices) * quant * tax);

                        double total_price = ((menu_item_price + ItemAdapter.total_addons_prices) * quant) ;

                        // save to database
                        int id = (int) dataBase.insert_Order(employee_id, Integer.parseInt(intent.getStringExtra("main_menu_id")), intent.getStringExtra("main_menu_name"),
                                Integer.parseInt(intent.getStringExtra("item_id")), intent.getStringExtra("item_name"), item_size_id,
                                item_size_name, Integer.parseInt(quantity.getText().toString()), instructions.getText().toString(),
                                Math.round(total_price * 100d) / 100d, restaurant_name, tax);

                        for (int i = 0; i < addons_orderList.size(); i++) {
                            dataBase.insert_Addons(id, employee_id, Integer.parseInt(addons_orderList.get(i)[0]), addons_orderList.get(i)[1],
                                    Integer.parseInt(addons_orderList.get(i)[2]), Math.round(Double.parseDouble(addons_orderList.get(i)[3]) * 100d) / 100d, addons_orderList.get(i)[4]);
                        }

                        order_numbers++;
                        MenuAddons.this.invalidateOptionsMenu();
                        Toast.makeText(getApplicationContext(), "Added to cart", Toast.LENGTH_LONG).show();

                    } catch (Exception ex) {
                        Toast.makeText(getApplicationContext(), "" + ex.getMessage(), Toast.LENGTH_LONG).show();
                    }
                    cart_click = false;
                    addons_orderList.clear();
                }else{
                    Toast.makeText(getApplicationContext(),"Scroll down to see more addons!",Toast.LENGTH_LONG).show();
                }
            }
        });


    }


    @Override
    public void onResume(){
        super.onResume();
        MenuAddons.this.invalidateOptionsMenu();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_items, menu);
        RelativeLayout badgeLayout = (RelativeLayout) menu.findItem(R.id.cart).getActionView();
        MenuItem item = menu.findItem(R.id.search_item);
        item.setVisible(false);
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
        }else if(id == R.id.cart){
           // Toast.makeText(getApplicationContext(),"One order added to your cart",Toast.LENGTH_LONG).show();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // if nav drawer is opened, hide the action items
        //   boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        //   menu.findItem(R.id.action_settings).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    private JSONArray concatArray(JSONArray... arrs)
            throws JSONException {
        JSONArray result = new JSONArray();
        for (JSONArray arr : arrs) {
            for (int i = 0; i < arr.length(); i++) {
                result.put(arr.get(i));
            }
        }
        return result;
    }
/*****************************************************************/
}

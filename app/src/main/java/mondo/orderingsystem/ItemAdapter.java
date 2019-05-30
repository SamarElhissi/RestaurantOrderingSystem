package mondo.orderingsystem;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Naruto on 7/30/2016.
 */
public class ItemAdapter extends BaseExpandableListAdapter {

    private Context context;

    private JSONArray Addons ;

    private HashMap<String, JSONArray> listDataChild;JSONObject jsonobject2;
    public  static double total_addons_prices = 0;
    public static ArrayList<ArrayList<Holder>> HolderArray = new ArrayList<>();
    private HashMap<Integer, boolean[]> mChildCheckStates;
    private HashMap<Integer, Integer[]> mChildquantity;
    private Holder holder;
    String addons_multiple;

    public ItemAdapter(Context context, JSONArray Addons){
        this.context = context;
        this.Addons = Addons;
        total_addons_prices = 0;
        HolderArray.clear();
        mChildCheckStates = new HashMap<Integer, boolean[]>();
        mChildquantity = new HashMap<Integer, Integer[]>();
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {

        try{
            JSONObject jsonobject= (JSONObject) Addons.get(groupPosition);
            return  jsonobject.getJSONArray("addons_items").get(childPosititon);

        }catch(Exception e){
            return null;
        }


    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        final JSONObject jsonobject = (JSONObject) getChild(groupPosition, childPosition);
       View row = convertView;
        try {
            jsonobject2 = (JSONObject) Addons.get(groupPosition);
            addons_multiple = jsonobject2.optString("addons_multiple");
        }catch (Exception e){

        }
        if (row == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = infalInflater.inflate(R.layout.item_details_child, null);
            holder = new Holder();
            CheckBox title = (CheckBox) row.findViewById(R.id.type);
            ImageButton add = (ImageButton) row.findViewById(R.id.add);
            ImageButton minus = (ImageButton) row.findViewById(R.id.minus);
            TextView quantity = (TextView) row.findViewById(R.id.quant);
            TextView quantity2 = (TextView) row.findViewById(R.id.quant2);
            holder.addView(title);
            holder.addView(add);
            holder.addView(minus);
            holder.addView(quantity);
            holder.addView(quantity2);
            row.setTag(holder);

        }
        Holder holder = (Holder) row.getTag();

        CheckBox title = (CheckBox) holder.getView(R.id.type);
        ImageButton add = (ImageButton) holder.getView(R.id.add);
        ImageButton minus = (ImageButton) holder.getView(R.id.minus);
        TextView quantity = (TextView) holder.getView(R.id.quant);
        TextView quantity2 = (TextView) holder.getView(R.id.quant2);
        if (mChildCheckStates.containsKey(groupPosition)) {

            boolean getChecked[] = mChildCheckStates.get(groupPosition);

            title.setChecked(getChecked[childPosition]);
            if(getChecked[childPosition] == true){
             //   row.setBackgroundColor(Color.rgb(243,243,243));
            }else{
              //  row.setBackgroundColor(Color.rgb(237,237,237));
            }

        } else {


            boolean getChecked[] = new boolean[getChildrenCount(groupPosition)];

            // add getChecked[] to the mChildCheckStates hashmap using mGroupPosition as the key
            mChildCheckStates.put(groupPosition, getChecked);
            title.setChecked(false);
            row.setBackgroundColor(Color.rgb(237,237,237));
        }

        if (mChildquantity.containsKey(groupPosition)) {

        } else {

            Integer quantities[] = new Integer[getChildrenCount(groupPosition)];
            for(int i = 0 ; i<quantities.length;i++){
                quantities[i] = 1;
            }
            // add getChecked[] to the mChildCheckStates hashmap using mGroupPosition as the key
            mChildquantity.put(groupPosition, quantities);
        }

        title.setText("" + jsonobject.optString("item_name"));
        if(convertView == null){

            String is_selected = jsonobject.optString("item_is_selected");
            if (is_selected.equals("1")) {
                boolean getChecked[] = mChildCheckStates.get(groupPosition);
                getChecked[childPosition] = true;
                mChildCheckStates.put(groupPosition, getChecked);
                //row.setBackgroundColor(Color.rgb(243,243,243));
                title.setChecked(true);
                if (addons_multiple.equals("1")) {
                    add.setVisibility(View.VISIBLE);
                } else add.setVisibility(View.GONE);

                minus.setVisibility(View.GONE);
                quantity.setVisibility(View.GONE);
                quantity2.setVisibility(View.GONE);
              //  double total2 = Double.parseDouble(jsonobject.optString("item_price")) +(Double.parseDouble(jsonobject.optString("item_price"))* MenuAddons.tax);
                double total2 = Double.parseDouble(jsonobject.optString("item_price"));
                double price = Double.parseDouble(MenuAddons.price.getText().toString()) + total2;
                if (!MenuAddons.cart_click) {
                    total_addons_prices = total_addons_prices + Double.parseDouble(jsonobject.optString("item_price"));

                    MenuAddons.price.setText((Math.round(price * 100d)/100d) + "");
                }
            } else {
               // row.setBackgroundColor(Color.rgb(237,237,237));
                boolean getChecked[] = mChildCheckStates.get(groupPosition);
                getChecked[childPosition] = false;
                mChildCheckStates.put(groupPosition, getChecked);
                title.setChecked(false);
                add.setVisibility(View.GONE);
                minus.setVisibility(View.GONE);
                quantity.setVisibility(View.GONE);
                quantity2.setVisibility(View.GONE);
            }
        }

        title.setTag(holder);
        title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Holder tagHolder = (Holder) view.getTag();
                CheckBox title = (CheckBox) tagHolder.getView(R.id.type);

                try {
                    jsonobject2 = (JSONObject) Addons.get(groupPosition);
                    addons_multiple = jsonobject2.optString("addons_multiple");
                }catch (Exception e){

                }
                if (title.isChecked()) {

                    if (addons_multiple.equals("1")) {
                        tagHolder.getView(R.id.add).setVisibility(View.VISIBLE);
                    } else {
                        tagHolder.getView(R.id.add).setVisibility(View.GONE);
                    }

                    tagHolder.getView(R.id.minus).setVisibility(View.GONE);
                    tagHolder.getView(R.id.quant).setVisibility(View.GONE);
                    tagHolder.getView(R.id.quant2).setVisibility(View.GONE);
                    double total1 = Double.parseDouble(jsonobject.optString("item_price")) * MenuAddons.quant;
                   // double total2 = total1 +(total1* MenuAddons.tax);
                    double price = Double.parseDouble(MenuAddons.price.getText().toString()) + total1;

                    MenuAddons.price.setText((Math.round(price * 100d)/100d) + "");

                    total_addons_prices = total_addons_prices + Double.parseDouble(jsonobject.optString("item_price"));
                    boolean getChecked[] = mChildCheckStates.get(groupPosition);
                    getChecked[childPosition] = true;
                    mChildCheckStates.put(groupPosition, getChecked);

                } else {

                    boolean getChecked[] = mChildCheckStates.get(groupPosition);
                    getChecked[childPosition] = false;
                    mChildCheckStates.put(groupPosition, getChecked);
                    tagHolder.getView(R.id.add).setVisibility(View.GONE);
                    tagHolder.getView(R.id.minus).setVisibility(View.GONE);
                    tagHolder.getView(R.id.quant).setVisibility(View.GONE);
                    tagHolder.getView(R.id.quant2).setVisibility(View.GONE);

                    TextView tv = (TextView) tagHolder.getView(R.id.quant);
                    double total1 = Double.parseDouble(jsonobject.optString("item_price")) * MenuAddons.quant;
                 //   double tax = (total1 * Double.parseDouble(tv.getText().toString()))* MenuAddons.tax;
                    double total2 = (total1 * Double.parseDouble(tv.getText().toString()));
                    double price = Double.parseDouble(MenuAddons.price.getText().toString()) - total2;

                    MenuAddons.price.setText((Math.round(price * 100d)/100d) + "");

                    total_addons_prices = total_addons_prices - (Double.parseDouble(jsonobject.optString("item_price")) * Double.parseDouble(tv.getText().toString()));

                    tv.setText("1");

                    Integer quantities[] = mChildquantity.get(groupPosition);
                    quantities[childPosition] = (1);
                    mChildquantity.put(groupPosition, quantities);
                }

            }
        });

        add.setTag(holder);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Holder tagHolder = (Holder) view.getTag();

                tagHolder.getView(R.id.minus).setVisibility(View.VISIBLE);
                tagHolder.getView(R.id.quant).setVisibility(View.VISIBLE);
                tagHolder.getView(R.id.quant2).setVisibility(View.VISIBLE);
                TextView tv = (TextView) tagHolder.getView(R.id.quant);
                String x = tv.getText() + "";
                tv.setText("" + (Integer.parseInt(x) + 1));

                double total1 = Double.parseDouble(jsonobject.optString("item_price")) * MenuAddons.quant;
                //double tax = (total1 )* MenuAddons.tax;
                double total2 = (total1 );

                double price = Double.parseDouble(MenuAddons.price.getText().toString()) + total2;

                MenuAddons.price.setText((Math.round(price * 100d)/100d) + "");

                total_addons_prices = total_addons_prices + Double.parseDouble(jsonobject.optString("item_price"));
                Integer quantities[] = mChildquantity.get(groupPosition);
                quantities[childPosition] = (Integer.parseInt(x) + 1);
                mChildquantity.put(groupPosition, quantities);
            }
        });

        minus.setTag(holder);
        minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Holder tagHolder = (Holder) view.getTag();


                TextView tv = (TextView) tagHolder.getView(R.id.quant);
                int x = Integer.parseInt("" + tv.getText()) - 1;
                if (x == 1) {
                    tagHolder.getView(R.id.minus).setVisibility(View.GONE);
                    tagHolder.getView(R.id.quant).setVisibility(View.GONE);
                    tagHolder.getView(R.id.quant2).setVisibility(View.GONE);
                }
                tv.setText(x + "");

                double total1 = Double.parseDouble(jsonobject.optString("item_price")) * MenuAddons.quant;
              //  double tax = (total1 )* MenuAddons.tax;
              //  double total2 = tax +(total1 );

                double price = Double.parseDouble(MenuAddons.price.getText().toString()) - total1;

                MenuAddons.price.setText((Math.round(price * 100d)/100d) + "");

                total_addons_prices = total_addons_prices - Double.parseDouble(jsonobject.optString("item_price"));

                Integer quantities[] = mChildquantity.get(groupPosition);
                quantities[childPosition] = (x);
                mChildquantity.put(groupPosition, quantities);
            }
        });

        return row;
    }



    @Override
    public int getChildrenCount(int groupPosition) {

        try{
            JSONObject jsonobject= (JSONObject) Addons.get(groupPosition);
            return  jsonobject.getJSONArray("addons_items").length();

        }catch(Exception e){
            return 0;
        }
    }

    @Override
    public Object getGroup(int groupPosition) {
        try{
            return this.Addons.get(groupPosition);
        }catch(Exception e){
            return null;
        }

    }

    @Override
    public int getGroupCount() {
        return this.Addons.length();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.item_detail_listitem, null);

        }
        TextView title = (TextView) convertView.findViewById(R.id.addon_name);
        TextView explanation = (TextView) convertView.findViewById(R.id.explanation);

        try{
            JSONObject jsonobject= (JSONObject) Addons.get(groupPosition);
            String mandatory = jsonobject.optString("addons_is_mandatory");
            if(mandatory.equals("1")){
                explanation.setText("(Required)");
            }
            else explanation.setText("");
            title.setText(jsonobject.optString("addons_name"));
        }catch(Exception e){

        }
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public  HashMap<Integer, boolean[]> getCheckedArray(){
        return mChildCheckStates;
    }

    public  HashMap<Integer, Integer[]> getQuantityArray(){
        return mChildquantity;
    }

}

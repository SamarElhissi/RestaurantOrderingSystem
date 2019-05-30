package mondo.orderingsystem;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.Image;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.w3c.dom.NameList;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Naruto on 7/30/2016.
 */
public class LatestOrdersAdapter extends BaseExpandableListAdapter {

    private Context context;

    ArrayList<String>Order_idList ;
    ArrayList<String>Item_nameList ;
    ArrayList<String>Item_sizeList ;
    ArrayList<Integer>QuantityList;
    ArrayList<Double>PricesList ;
    ArrayList<String>StatusList = new ArrayList<>();
    HashMap<String, List <Object>> listDataChild;
    ArrayList<String>Restaurant_nameList;
    ArrayList<String> Order_List;
    public LatestOrdersAdapter(Context context, ArrayList<String>Order_List,ArrayList<String>Order_idList,ArrayList<Double>PricesList,
                       ArrayList<String>Restaurant_nameList,ArrayList<String>StatusList,HashMap<String, List <Object>> listDataChild ){
        this.context = context;
        this.Order_idList = Order_idList;
        this.Item_nameList = Item_nameList;
        this.Item_sizeList = Item_sizeList;
        this.listDataChild = listDataChild;
        this.QuantityList = QuantityList;
        this.PricesList = PricesList;
        this.Restaurant_nameList = Restaurant_nameList;
        this.StatusList = StatusList;
        this.Order_List = Order_List;
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        try{
            return this.listDataChild.get(this.Order_List.get(groupPosition)).get(childPosititon);
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

        Object obj = (Object) getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.latest_orders_child, null);
        }

        TextView addon_name = (TextView) convertView.findViewById(R.id.addon_price);
        TextView addon_quantity = (TextView) convertView.findViewById(R.id.addon_quantity);
        TextView addons_name = (TextView) convertView.findViewById(R.id.addons_name);
        TextView x = (TextView) convertView.findViewById(R.id.x);

        if(obj instanceof Double[]){
            Double[] obj2 = (Double[])obj;
            addon_name.setText(obj2[1]+"");
            addon_quantity.setText("HST");
            addons_name.setText("("+(obj2[0])+"%)");
            x.setVisibility(View.GONE);
        }else{
            JSONObject obj2 = (JSONObject) obj;
            addon_name.setText(obj2.optString("order_total_price"));
            addon_quantity.setText(obj2.optString("order_item_quantity"));
            addons_name.setText(obj2.optString("order_item_name"));
            x.setVisibility(View.VISIBLE);
        }

     //   addon_name.setTypeface(null, Typeface.BOLD);

        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return listDataChild.get(this.Order_List.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.Order_List.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this.Order_List.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(final int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        GroupRowHolder groupRowHolder = null;

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.latest_orders_group, null);
            groupRowHolder = new GroupRowHolder(convertView);
            convertView.setTag(groupRowHolder);
        } else {
            groupRowHolder = (GroupRowHolder) convertView.getTag();
        }

        //  groupRowHolder.menu_name.setText(Menu_nameList.get(groupPosition));
        groupRowHolder.item_name.setText("Order "+Order_idList.get(groupPosition));

      //  groupRowHolder.item_quantity.setText(QuantityList.get(groupPosition)+"");
        groupRowHolder.item_price.setText(PricesList.get(groupPosition)+"");
        groupRowHolder.rest_name.setText(Restaurant_nameList.get(groupPosition)+"");

        if (StatusList.get(groupPosition).equals("0")){
            groupRowHolder.status_image.setImageResource(R.drawable.pending);
        }
        else if (StatusList.get(groupPosition).equals("1")){
            groupRowHolder.status_image.setImageResource(R.drawable.tick);
        }else{
            groupRowHolder.status_image.setImageResource(R.drawable.rejected);
        }

        convertView.setClickable(false);

        return convertView;
    }


    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    class GroupRowHolder {
        TextView menu_name;
        TextView item_name;
        TextView itemsize;
        TextView item_quantity;
        TextView item_price;
        TextView rest_name;
        ImageView status_image;
        TextView instructions ;
        Button orderAgain;

        public GroupRowHolder(View view) {
            // menu_name = (TextView) view.findViewById(R.id.menu_name);
            item_name = (TextView) view.findViewById(R.id.Item_name);
         //   itemsize = (TextView) view.findViewById(R.id.itemsize);
       //     item_quantity = (TextView) view.findViewById(R.id.item_quantity);
            item_price = (TextView) view.findViewById(R.id.item_price);
            rest_name = (TextView) view.findViewById(R.id.rest_name);
            status_image = (ImageView) view.findViewById(R.id.tick);
          //  instructions = (TextView) view.findViewById(R.id.instuctions);
           // orderAgain = (Button) view.findViewById(R.id.orderAgain);
        }
    }


}

package mondo.orderingsystem;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

/**
 * Created by Naruto on 7/30/2016.
 */
public class LatestOrdersItemAdapter extends BaseExpandableListAdapter {

    private Context context;

    ArrayList<String>Order_idList ;
    ArrayList<String>Item_nameList ;
    ArrayList<String>Item_sizeList ;
    ArrayList<Integer>QuantityList;
    ArrayList<Double>PricesList ;
    ArrayList<String>StatusList = new ArrayList<>();
    HashMap<String, List<List<String>>> listDataChild;
    ArrayList<String>Restaurant_nameList;
    ArrayList<String> InstructionsList;
    public LatestOrdersItemAdapter(Context context, ArrayList<String>Order_idList, ArrayList<String>Item_nameList,
                                   ArrayList<String>Item_sizeList , ArrayList<Integer>QuantityList, ArrayList<Double>PricesList,
                                   ArrayList<String>Restaurant_nameList, ArrayList<String> InstructionsList, ArrayList<String>StatusList, HashMap<String, List<List<String>>> listDataChild ){
        this.context = context;
        this.Order_idList = Order_idList;
        this.Item_nameList = Item_nameList;
        this.Item_sizeList = Item_sizeList;
        this.listDataChild = listDataChild;
        this.QuantityList = QuantityList;
        this.PricesList = PricesList;
        this.Restaurant_nameList = Restaurant_nameList;
        this.StatusList = StatusList;
        this.InstructionsList = InstructionsList;
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        try{
            return this.listDataChild.get(this.Order_idList.get(groupPosition)).get(childPosititon);
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

        final List<String> addonList = (List<String>) getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.latest_orders_item_child, null);
        }

        TextView addon_name = (TextView) convertView.findViewById(R.id.addon_name);
        TextView addon_quantity = (TextView) convertView.findViewById(R.id.addon_quantity);
        TextView addons_name = (TextView) convertView.findViewById(R.id.addons_name);

     //   addon_name.setTypeface(null, Typeface.BOLD);
        addon_name.setText(addonList.get(0));
        addon_quantity.setText(addonList.get(1));
        addons_name.setText(addonList.get(2)+":");
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return listDataChild.get(this.Order_idList.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.Order_idList.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this.Order_idList.size();
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
            convertView = infalInflater.inflate(R.layout.latest_orders_item_group, null);
            groupRowHolder = new GroupRowHolder(convertView);
            convertView.setTag(groupRowHolder);
        } else {
            groupRowHolder = (GroupRowHolder) convertView.getTag();
        }

        //  groupRowHolder.menu_name.setText(Menu_nameList.get(groupPosition));
        groupRowHolder.item_name.setText(Item_nameList.get(groupPosition));
        if(Item_sizeList.get(groupPosition).equals("") || Item_sizeList.get(groupPosition) == null || Item_sizeList.get(groupPosition).equals("null")){

            groupRowHolder.itemsize.setText("");
        }else{
            groupRowHolder.itemsize.setText("("+Item_sizeList.get(groupPosition)+")");
        }

        groupRowHolder.item_quantity.setText(QuantityList.get(groupPosition)+"");
        groupRowHolder.item_price.setText(PricesList.get(groupPosition)+"");
        groupRowHolder.rest_name.setText(Restaurant_nameList.get(groupPosition)+"");
        groupRowHolder.rest_name.setVisibility(View.GONE);
        groupRowHolder.status_image.setVisibility(View.GONE);
        groupRowHolder.orderAgain.setVisibility(View.GONE);
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
            itemsize = (TextView) view.findViewById(R.id.itemsize);
            item_quantity = (TextView) view.findViewById(R.id.item_quantity);
            item_price = (TextView) view.findViewById(R.id.item_price);
            rest_name = (TextView) view.findViewById(R.id.rest_name);
            status_image = (ImageView) view.findViewById(R.id.tick);
            instructions = (TextView) view.findViewById(R.id.instuctions);
            orderAgain = (Button) view.findViewById(R.id.orderAgain);
        }
    }


}

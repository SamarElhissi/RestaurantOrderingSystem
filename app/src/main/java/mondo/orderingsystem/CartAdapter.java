package mondo.orderingsystem;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
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

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.w3c.dom.NameList;

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
public class CartAdapter extends BaseExpandableListAdapter {

    private Context context;

    ArrayList<Integer>Order_idList ;
    ArrayList<String>Menu_nameList ;
    ArrayList<String>Item_nameList ;
    ArrayList<String>Item_sizeList ;
    ArrayList<Integer>QuantityList;
    ArrayList<Double>PricesList ;
    ArrayList<String>InstructionsList ;
    HashMap<Integer, List<List<String>>> listDataChild;
    ArrayList<String>Restaurant_nameList;
    private List<ArrayList<Object>> selectedChildren = new ArrayList<ArrayList<Object>>();
    private HashMap<Integer, boolean[]> mChildCheckStates;
    public CartAdapter(Context context, ArrayList<Integer>Order_idList,ArrayList<String>Menu_nameList,ArrayList<String>Item_nameList,
                       ArrayList<String>Item_sizeList ,ArrayList<Integer>QuantityList,ArrayList<Double>PricesList,
                       ArrayList<String>InstructionsList,ArrayList<String>Restaurant_nameList,HashMap<Integer, List<List<String>>> listDataChild ){
        this.context = context;
        this.Order_idList = Order_idList;
        this.Menu_nameList = Menu_nameList;

        this.Item_nameList = Item_nameList;
        this.Item_sizeList = Item_sizeList;
        this.listDataChild = listDataChild;
        this.QuantityList = QuantityList;
        this.PricesList = PricesList;
        this.InstructionsList = InstructionsList;
        this.Restaurant_nameList = Restaurant_nameList;
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
            convertView = infalInflater.inflate(R.layout.cart_child, null);
        }

        TextView addon_name = (TextView) convertView.findViewById(R.id.addon_name);
        TextView addon_quantity = (TextView) convertView.findViewById(R.id.addon_quantity);
        TextView addon_price = (TextView) convertView.findViewById(R.id.addon_price);
        TextView addons_name = (TextView) convertView.findViewById(R.id.addons_name);

        //addon_name.setTypeface(null, Typeface.BOLD);
        addon_name.setText(addonList.get(0));
        addon_quantity.setText(addonList.get(1));
       // addon_price.setText(addonList.get(2));
        addon_price.setText("");
        addons_name.setText(addonList.get(4)+":");
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
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        GroupRowHolder groupRowHolder = null;

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.cart_group, null);
            groupRowHolder = new GroupRowHolder(convertView);
            convertView.setTag(groupRowHolder);
        } else {
            groupRowHolder = (GroupRowHolder) convertView.getTag();
        }



      //  groupRowHolder.menu_name.setText(Menu_nameList.get(groupPosition));
        groupRowHolder.item_name.setText(Item_nameList.get(groupPosition));
        if(Item_sizeList.get(groupPosition) !="" && Item_sizeList.get(groupPosition) != null){
            groupRowHolder.itemsize.setText("("+Item_sizeList.get(groupPosition)+")");
        }else{
            groupRowHolder.itemsize.setText("");
        }

        groupRowHolder.item_quantity.setText(QuantityList.get(groupPosition)+"");
        groupRowHolder.item_price.setText(PricesList.get(groupPosition)+"");
        groupRowHolder.rest_name.setText(Restaurant_nameList.get(groupPosition)+"");
        if(InstructionsList.get(groupPosition).equals("")){

        }else{
            groupRowHolder.instructions.setVisibility(View.VISIBLE);
            groupRowHolder.instructions.setText(InstructionsList.get(groupPosition)+"");
        }

        ArrayList<Object> position = new ArrayList<Object>();
        position.add(groupPosition);
        position.add(convertView);
        position.add(Order_idList.get(groupPosition));
        if (isPositionAleadyAdded(position) != null) {
            selectGroup(position);
        } else {
            deSelectGroup(position);
        }
        convertView.setClickable(false);
        return convertView;
    }

    public ArrayList<Object> isPositionAleadyAdded(ArrayList<Object> position) {

        for (ArrayList<Object> entry : selectedChildren) {
            if (entry.get(0) == position.get(0)) {
                return position;
            }
        }

        return null;
    }
    public void selectGroup(ArrayList<Object> position) {

        if (isPositionAleadyAdded(position) == null) {
            selectedChildren.add(position);
        }
        RelativeLayout groupView = (RelativeLayout) position.get(1);
        ImageView tick = (ImageView) groupView.findViewById(R.id.tick);
        TextView x = (TextView) groupView.findViewById(R.id.x);
        TextView quant = (TextView) groupView.findViewById(R.id.item_quantity);
        TextView name = (TextView) groupView.findViewById(R.id.Item_name);
        TextView size = (TextView) groupView.findViewById(R.id.itemsize);
        TextView rest_name = (TextView) groupView.findViewById(R.id.rest_name);
        TextView price = (TextView) groupView.findViewById(R.id.item_price);
        TextView inst = (TextView) groupView.findViewById(R.id.instuctions);
        tick.setVisibility(View.VISIBLE);
     //   groupView.setBackgroundColor(context.getResources().getColor(R.color.backgroung_expandable_list_view_child));
        x.setTextColor(Color.WHITE);quant.setTextColor(Color.WHITE);name.setTextColor(Color.WHITE);
        size.setTextColor(Color.WHITE);rest_name.setTextColor(Color.WHITE);price.setTextColor(Color.WHITE);
        inst.setTextColor(Color.WHITE);
        groupView.setBackgroundColor(Color.rgb(18,51,84));
    }

    public void deSelectGroup(ArrayList<Object> position) {

        ArrayList<Object> pos = null;
        int elemNo = -1;
        if ((pos = isPositionAleadyAdded(position)) != null) {
            elemNo = getNoElemInSelectedChildred(pos);
            selectedChildren.remove(elemNo);
        }
        RelativeLayout groupView = (RelativeLayout) position.get(1);
        ImageView tick = (ImageView) groupView.findViewById(R.id.tick);
        TextView x = (TextView) groupView.findViewById(R.id.x);
        TextView quant = (TextView) groupView.findViewById(R.id.item_quantity);
        TextView name = (TextView) groupView.findViewById(R.id.Item_name);
        TextView size = (TextView) groupView.findViewById(R.id.itemsize);
        TextView rest_name = (TextView) groupView.findViewById(R.id.rest_name);
        TextView price = (TextView) groupView.findViewById(R.id.item_price);
        TextView inst = (TextView) groupView.findViewById(R.id.instuctions);
        //   groupView.setBackgroundColor(context.getResources().getColor(R.color.backgroung_expandable_list_view_child));
        x.setTextColor(Color.rgb(33,45,68));quant.setTextColor(Color.rgb(33,45,68));name.setTextColor(Color.rgb(33,45,68));
        size.setTextColor(Color.rgb(33,45,68));rest_name.setTextColor(Color.rgb(186,23,28));price.setTextColor(Color.rgb(33,45,68));
        inst.setTextColor(Color.rgb(100,100,100));
        tick.setVisibility(View.GONE);
        groupView.setBackgroundResource(0);
    }

    private int getNoElemInSelectedChildred(ArrayList<Object> position) {

        int index = 0;
        for (ArrayList<Object> entry : selectedChildren) {
            if (entry.get(0) == position.get(0)) {
                return index;
            } else {
                index++;
            }
        }
        return -1;
    }

    /*
     * This method clears the map of all children.
     */
    public void unSelectAllChildren() {

        for (ArrayList<Object> entry : selectedChildren) {
            RelativeLayout groupView = (RelativeLayout) entry.get(1);
            ImageView tick = (ImageView) groupView.findViewById(R.id.tick);
            TextView x = (TextView) groupView.findViewById(R.id.x);
            TextView quant = (TextView) groupView.findViewById(R.id.item_quantity);
            TextView name = (TextView) groupView.findViewById(R.id.Item_name);
            TextView size = (TextView) groupView.findViewById(R.id.itemsize);
            TextView rest_name = (TextView) groupView.findViewById(R.id.rest_name);
            TextView price = (TextView) groupView.findViewById(R.id.item_price);
            TextView inst = (TextView) groupView.findViewById(R.id.instuctions);
            //   groupView.setBackgroundColor(context.getResources().getColor(R.color.backgroung_expandable_list_view_child));
            x.setTextColor(Color.rgb(33,45,68));quant.setTextColor(Color.rgb(33,45,68));name.setTextColor(Color.rgb(33,45,68));
            size.setTextColor(Color.rgb(33,45,68));rest_name.setTextColor(Color.rgb(186,23,28));price.setTextColor(Color.rgb(33,45,68));
            inst.setTextColor(Color.rgb(100,100,100));
            tick.setVisibility(View.GONE);
            groupView.setBackgroundColor(Color.WHITE);
           // childView.setBackgroundColor(context.getResources().getColor(R.color.background_color));
        }
        selectedChildren.clear();
    }

    public List<ArrayList<Object>> getSelectedChildren() {
        return selectedChildren;
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
        TextView instructions;
        TextView item_name;
        TextView itemsize;
        TextView item_quantity;
        TextView item_price;
        TextView rest_name;

        public GroupRowHolder(View view) {
           // menu_name = (TextView) view.findViewById(R.id.menu_name);
            item_name = (TextView) view.findViewById(R.id.Item_name);
            itemsize = (TextView) view.findViewById(R.id.itemsize);
            item_quantity = (TextView) view.findViewById(R.id.item_quantity);
            item_price = (TextView) view.findViewById(R.id.item_price);
            rest_name = (TextView) view.findViewById(R.id.rest_name);
            instructions = (TextView) view.findViewById(R.id.instuctions);
        }
    }


}

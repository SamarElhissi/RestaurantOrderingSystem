package mondo.orderingsystem;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ListView;
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
public class MenuAdapter extends BaseExpandableListAdapter {

    private Context context;

    private ArrayList<String> idsList ;
    private ArrayList<String> namesList ;
    private ArrayList<JSONArray> menuItems ;
    private ArrayList<JSONArray> Addons ;
    private ArrayList<String> coverImages;
    ListView menu_item_list;
    private HashMap<String, JSONArray> listDataChild;

    public MenuAdapter(Context context, ArrayList<String> ids,ArrayList<String> names,ArrayList<String> covers,HashMap<String, JSONArray> menuItems,ArrayList<JSONArray> Addons){
        this.context = context;
        this.idsList = ids;
        this.namesList = names;

        this.coverImages = covers;
        this.Addons = Addons;
        this.listDataChild = menuItems;
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        try{
            return this.listDataChild.get(this.idsList.get(groupPosition)).get(childPosititon);
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

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.item_list_item, null);


        }


        ImageView item_image = (ImageView) convertView.findViewById(R.id.item_image);
        TextView item_name = (TextView) convertView.findViewById(R.id.item_name);
        TextView item_desc = (TextView) convertView.findViewById(R.id.item_desc);
        TextView item_price = (TextView) convertView.findViewById(R.id.item_price);

        item_name.setTypeface(null, Typeface.BOLD);
        item_name.setText(jsonobject.optString("item_name"));
        item_desc.setText(jsonobject.optString("item_desc"));
        item_price.setText(jsonobject.optString("item_price"));

        Picasso.with(context).load("http://order.mondocloudsolutions.com/upload/images/"+ jsonobject.optString("item_image")).resize(60,60).centerCrop().into(item_image);
        convertView.setClickable(false);
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return listDataChild.get(this.idsList.get(groupPosition)).length();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.idsList.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this.idsList.size();
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
            convertView = infalInflater.inflate(R.layout.menu_list_items, null);

        }

        //   ExpandableListView mExpandableListView = (ExpandableListView) parent;
        //    mExpandableListView.expandGroup(groupPosition);
        ImageView cover = (ImageView) convertView.findViewById(R.id.rest_cover);
        TextView title = (TextView) convertView.findViewById(R.id.menu_name);

        //   Picasso.with(context).load("http://order.mondocloudsolutions.com/upload/images/"+ logoImages.get(position)).resize(60,60).centerCrop().into(logo);
        Picasso.with(context).load("http://order.mondocloudsolutions.com/upload/images/"+ coverImages.get(groupPosition)).fit().centerCrop().into(cover);

        title.setText(namesList.get(groupPosition));
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


}

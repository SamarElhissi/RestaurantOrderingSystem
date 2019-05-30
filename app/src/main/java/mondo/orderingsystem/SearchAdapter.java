package mondo.orderingsystem;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Naruto on 7/30/2016.
 */
public class SearchAdapter extends BaseAdapter {

    private Context context;

    private ArrayList<String> namesList = new ArrayList<String>();
    private ArrayList<String> logoImages = new ArrayList<String>();
    private ArrayList<String> descList = new ArrayList<String>();
    private ArrayList<String> pricesList = new ArrayList<String>();
    private ArrayList<String> restaurantNames = new ArrayList<String>();

    public SearchAdapter(Context context, ArrayList<String> names,ArrayList<String> logo,ArrayList<String> descList,ArrayList<String> pricesList, ArrayList<String> restaurantNames){
        this.context = context;
        this.namesList = names;
        this.logoImages = logo;
        this.descList = descList;
        this.pricesList = pricesList;
        this.restaurantNames = restaurantNames;
    }

    @Override
    public int getCount() {
        return namesList.size();
    }

    @Override
    public Object getItem(int position) {
        return namesList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater)
                    context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.search_list_item, null);
        }


        ImageView item_image = (ImageView) convertView.findViewById(R.id.item_image);
        TextView item_name = (TextView) convertView.findViewById(R.id.item_name);
        TextView item_desc = (TextView) convertView.findViewById(R.id.item_desc);
        TextView item_price = (TextView) convertView.findViewById(R.id.item_price);
        TextView rest_name = (TextView) convertView.findViewById(R.id.rest_name);
//
//        item_name.setTypeface(null, Typeface.BOLD);
        item_name.setText(namesList.get(position));
        item_desc.setText(descList.get(position));
        item_price.setText(pricesList.get(position));
        rest_name.setText(restaurantNames.get(position));
//
        Picasso.with(context).load("http://order.mondocloudsolutions.com/upload/images/"+ logoImages.get(position)).resize(60,60).centerCrop().into(item_image);

        return convertView;
    }

}

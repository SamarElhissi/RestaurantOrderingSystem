package mondo.orderingsystem;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
public class ResturantAdapter extends BaseAdapter {

    private Context context;

    private ArrayList<String> idsList ;
    private ArrayList<String> namesList ;
    private ArrayList<String> logoImages ;
    private ArrayList<String> locationsList ;
    private ArrayList<String> coverImages;
    private ArrayList<String> restaurantCurrency;
    private ArrayList<String> restaurant_tax;

    public ResturantAdapter(Context context, ArrayList<String> ids,ArrayList<String> names,ArrayList<String> logo,ArrayList<String> covers,
                            ArrayList<String> locations, ArrayList<String> restaurantCurrency,  ArrayList<String> restaurant_tax){
        this.context = context;
        this.idsList = ids;
        this.namesList = names;
        this.logoImages = logo;
        this.coverImages = covers;
        this.locationsList = locations;
        this.restaurantCurrency = restaurantCurrency;
        this.restaurant_tax = restaurant_tax;
    }

    @Override
    public int getCount() {
        return idsList.size();
    }

    @Override
    public Object getItem(int position) {
        return idsList.get(position);
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
            convertView = mInflater.inflate(R.layout.list_item, null);
        }

        ImageView logo = (ImageView) convertView.findViewById(R.id.logoImage);
        ImageView cover = (ImageView) convertView.findViewById(R.id.coverImage);
        TextView title = (TextView) convertView.findViewById(R.id.name);
        TextView location = (TextView) convertView.findViewById(R.id.location);
        Button order = (Button) convertView.findViewById(R.id.orderNow);

        // TextView txtCount = (TextView) convertView.findViewById(R.id.counter);
        Picasso.with(context).load("http://order.mondocloudsolutions.com/upload/images/"+ logoImages.get(position)).resize(60,60).centerCrop().into(logo);
        Picasso.with(context).load("http://order.mondocloudsolutions.com/upload/images/"+ coverImages.get(position)).fit().centerCrop().into(cover);

        title.setText(namesList.get(position));
        location.setText(locationsList.get(position));

        order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(v.getContext(), ResturantMenu.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("restaurant_id", ""+idsList.get(position));
                intent.putExtra("restaurant_logo", ""+logoImages.get(position));
                intent.putExtra("restaurant_name", ""+namesList.get(position));
                intent.putExtra("restaurantCurrency", ""+restaurantCurrency.get(position));
                intent.putExtra("restaurant_tax", ""+restaurant_tax.get(position));
                v.getContext().startActivity(intent);
            }
        });

        return convertView;
    }

}

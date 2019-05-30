package mondo.orderingsystem;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Naruto on 7/11/2016.
 */
public class NavDrawerListAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<NavDrawerItem> navDrawerItems;
    DrawerHolder drawerHolder;
    public NavDrawerListAdapter(Context context, ArrayList<NavDrawerItem> navDrawerItems){
        this.context = context;
        this.navDrawerItems = navDrawerItems;

    }

    @Override
    public int getCount() {
        return navDrawerItems.size();
    }

    @Override
    public Object getItem(int position) {
        return navDrawerItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater)
                    context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.drawer_list_item, null);
            drawerHolder = new DrawerHolder(convertView);
            convertView.setTag(drawerHolder);
        } else {
            drawerHolder = (DrawerHolder) convertView.getTag();
        }

        drawerHolder.imgIcon.setImageResource(navDrawerItems.get(position).getIcon());
        drawerHolder.txtTitle.setText(navDrawerItems.get(position).getTitle());

        if(position == 0 )  {
            drawerHolder.info.setText(SaveSharedPreference.getEmployeeFN(context)+" "+ SaveSharedPreference.getEmployeeLN(context));
            drawerHolder.info.setVisibility(View.VISIBLE);
        } else if(position == 1){
            drawerHolder.info.setText(SaveSharedPreference.getCompany_name(context));
            drawerHolder.info.setVisibility(View.VISIBLE);
        }else{
            drawerHolder.info.setVisibility(View.GONE);
        }



        return convertView;
    }
    class DrawerHolder {
        ImageView imgIcon;
        TextView txtTitle;
        TextView info;

        public DrawerHolder(View view) {
            // menu_name = (TextView) view.findViewById(R.id.menu_name);
             imgIcon = (ImageView) view.findViewById(R.id.icon);
             txtTitle = (TextView) view.findViewById(R.id.title);
             info = (TextView) view.findViewById(R.id.info);
        }
    }
}

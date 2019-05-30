package mondo.orderingsystem;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;

public class Holder  {

    TextView name, location;
    ImageView logo,cover;
    Button order;
    CardView container;
    CheckBox title;  ImageButton add , minus;TextView quantity;
    private HashMap<Integer, View> storedViews = new HashMap<Integer, View>();
    public Holder(final View v) {

    }

    public Holder()
    {
    }


    public Holder addView(View view)
    {
        int id = view.getId();
        storedViews.put(id, view);
        return this;
    }

    public View getView(int id)
    {
        return storedViews.get(id);
    }
}
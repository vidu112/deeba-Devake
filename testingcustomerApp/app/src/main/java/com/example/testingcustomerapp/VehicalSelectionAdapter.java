package com.example.testingcustomerapp;

import android.content.Context;
import androidx.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class VehicalSelectionAdapter extends ArrayAdapter<VehicalSelection> {
    private static final String TAG = "PersonListAdapter";

    private Context mContext;
    private int mResource;
    private int lastPosition = -1;

    /**
     * Holds variables in a View
     */
    private static class ViewHolder {
        TextView name;
        ImageView image;
    }

    /**
     * Default constructor for the PersonListAdapter
     * @param context
     * @param resource
     * @param objects
     */
    public VehicalSelectionAdapter(Context context, int resource, ArrayList<VehicalSelection> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //get the persons information
        String name = getItem(position).getName();
        String imageUrl = getItem(position).getImageURL();
        String truckChild = getItem(position).getTruckChild();
        String price = getItem(position).getPrice();


        //Create the person object with the information
        VehicalSelection VehicalSelection = new VehicalSelection(name,imageUrl,truckChild,price);

        //create the view result for showing the animation
        final View result;

        //ViewHolder object
        ViewHolder holder;


        if(convertView == null){
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(mResource, parent, false);
            holder= new ViewHolder();
            holder.name = (TextView) convertView.findViewById(R.id.name_view);
            holder.image = (ImageView) convertView.findViewById(R.id.image_view);


            result = convertView;

            convertView.setTag(holder);
        }
        else{
            holder = (ViewHolder) convertView.getTag();
            result = convertView;
        }


        holder.name.setText(VehicalSelection.getName());
        Glide.with(mContext)
                .asBitmap()
                .load(VehicalSelection.getImageURL())
                .into(holder.image);
        return convertView;
    }
}

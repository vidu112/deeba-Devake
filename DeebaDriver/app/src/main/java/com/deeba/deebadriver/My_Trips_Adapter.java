package com.deeba.deebadriver;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class My_Trips_Adapter extends ArrayAdapter<My_Trip_class> {
    private Context mContext;
    int mResource;
    public My_Trips_Adapter(Context context, int resource, ArrayList<My_Trip_class> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource=resource;

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        String startloc=getItem(position).getStartloc();
        String endloc=getItem(position).getEndloc();
        String date=getItem(position).getDate();
        String distance=getItem(position).getDistance();
        String price=getItem(position).getPrice();
        String time=getItem(position).getTime();

        My_Trip_class taskClass=new My_Trip_class(startloc,endloc,date,time,distance,price);
        LayoutInflater inflater=LayoutInflater.from(mContext);
        convertView=inflater.inflate(mResource,parent,false);

        TextView tkStartLoc=(TextView) convertView.findViewById(R.id.my_trip_start);
        TextView tkEndLoc=(TextView) convertView.findViewById(R.id.my_trip_end);
        TextView tkDate=(TextView) convertView.findViewById(R.id.my_trip_date);
        LinearLayout startend=convertView.findViewById(R.id.startendview);
        TextView tkTime=(TextView)convertView.findViewById(R.id.my_trip_time);
        TextView tkDistance=(TextView)convertView.findViewById(R.id.my_trip_distance);
        TextView tkPrice=(TextView)convertView.findViewById(R.id.my_trip_price);


        tkStartLoc.setText(startloc);
        tkEndLoc.setText(endloc);
        tkDate.setText(date);
        tkTime.setText(time);
        tkDistance.setText(distance);
        tkPrice.setText(price);
        return  convertView;
    }
}

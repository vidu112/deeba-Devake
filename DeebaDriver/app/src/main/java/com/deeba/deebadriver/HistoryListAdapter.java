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

public class HistoryListAdapter extends ArrayAdapter<Historyclass> {
    private static  final String TAG="TaskListAdapter";
    private Context mContext;
    int mResource;
    public HistoryListAdapter(Context context, int resource, ArrayList<Historyclass> objects) {
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
        String status=getItem(position).getStatus();
        String time=getItem(position).getTime();

        Historyclass taskClass=new Historyclass(startloc,endloc,date,status,time);
        LayoutInflater inflater=LayoutInflater.from(mContext);
        convertView=inflater.inflate(mResource,parent,false);

        TextView tkStartLoc=(TextView) convertView.findViewById(R.id.start);
        TextView tkEndLoc=(TextView) convertView.findViewById(R.id.end);
        TextView tkDate=(TextView) convertView.findViewById(R.id.date);
        LinearLayout startend=convertView.findViewById(R.id.startendview);
        TextView tkTime=(TextView)convertView.findViewById(R.id.time);

        tkStartLoc.setText(startloc);
        tkEndLoc.setText(endloc);
        tkDate.setText(date);
        tkTime.setText(time);
        return  convertView;
    }
}

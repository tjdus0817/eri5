package com.iri5.medicine_management.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.iri5.medicine_management.Model.CurrentPoint;
import com.iri5.medicine_management.Model.Pharmacy;
import com.iri5.medicine_management.R;
import com.iri5.medicine_management.Utils.Util;

import java.util.ArrayList;

public class PhamarcyAdapter extends BaseAdapter {
    ArrayList<Pharmacy> itemList;
    CurrentPoint currentPoint;
    Context context;


    public PhamarcyAdapter(CurrentPoint currentPoint, ArrayList<Pharmacy> itemList){
        this.itemList       = itemList;
        this.currentPoint   = currentPoint;
    }

    @Override
    public int getCount() {
        return itemList.size();
    }

    @Override
    public Object getItem(int position) {
        return itemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        context = parent.getContext();
        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.shot_item_list, parent, false);
        }

        Pharmacy pharmacy = itemList.get(position);

        TextView tv_title = (TextView) convertView.findViewById(R.id.tv_item_list_title);
        TextView tv_distance = (TextView) convertView.findViewById(R.id.tv_item_list_distance);

        tv_title.setText(pharmacy.getShop_name());
        String distance = String.valueOf(Util.calculateDistance(currentPoint.getLat(),currentPoint.getLon(),pharmacy.getLat(),pharmacy.getLon()));
        tv_distance.setText(distance);
        return convertView;
    }

    public void addItem(Pharmacy pharmacy){
        itemList.add(pharmacy);
        notifyDataSetChanged();
    }
    public void updateCurrent(){
        notify();
    }
}

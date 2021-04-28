package com.example.googleassistantproject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import java.util.ArrayList;
import java.util.List;

public class DeviceArrayAdapter extends ArrayAdapter<Devices> {

    public DeviceArrayAdapter(Context context, ArrayList<Devices> dev) {
        super(context, 0, dev);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Devices dev = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_template, parent, false);
        }
        // Lookup view for data population
        TextView devName = (TextView) convertView.findViewById(R.id.devName);
        TextView devType = (TextView) convertView.findViewById(R.id.devType);
        ImageView devImg = (ImageView) convertView.findViewById(R.id.devImg);
        // Populate the data into the template view using the data object
        devName.setText(dev.getName());
        devType.setText(dev.getTypeToSend());
        devImg.setImageResource(dev.getDevImg());
        // Return the completed view to render on screen
        return convertView;
    }

    public Bitmap decodeToBitmap(byte[] decodedByte) {
        return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
    }
}

package com.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.joserv.Akram.R;
import com.models.PushNotification;

import java.util.ArrayList;

public class NotificationAdapter extends ArrayAdapter<PushNotification> implements View.OnClickListener{

    private ArrayList<PushNotification> dataSet;
    Context mContext;

    // View lookup cache
    private static class ViewHolder {
        TextView txtTitle;
        TextView txtBody;
        TextView txtDate;
    }

    public NotificationAdapter(ArrayList<PushNotification> data, Context context) {
        super(context, R.layout.notification_item_view, data);
        this.dataSet = data;
        this.mContext=context;

    }

    @Override
    public void onClick(View v) {

        int position=(Integer) v.getTag();
        Object object= getItem(position);
        PushNotification dataModel=(PushNotification)object;

        switch (v.getId())
        {
//            case R.id.item_info:
////                Snackbar.make(v, "Release date " +dataModel.getFeature(), Snackbar.LENGTH_LONG)
////                        .setAction("No action", null).show();
//                break;
        }
    }

    private int lastPosition = -1;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        PushNotification dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.notification_item_view, parent, false);
            viewHolder.txtTitle = (TextView) convertView.findViewById(R.id.title);
            viewHolder.txtBody = (TextView) convertView.findViewById(R.id.body);
            viewHolder.txtDate = (TextView)convertView.findViewById(R.id.date);

            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

//        Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
//        result.startAnimation(animation);
        lastPosition = position;

        viewHolder.txtTitle.setText(dataModel.getTitle());
        viewHolder.txtBody.setText(dataModel.getBody());
        viewHolder.txtDate.setText(dataModel.getDate());

        // Return the completed view to render on screen
        return convertView;
    }
}

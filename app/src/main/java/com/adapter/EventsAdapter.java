package com.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.joserv.Akram.R;
import com.joserv.activities.EventDetailsActivity;
import com.models.Event;

public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.recHelper> {

    private final Context cnx;
    private final SparseArray<Event> data;
    private LayoutInflater inf;

    public EventsAdapter(Context cnx, SparseArray<Event> data) {

        this.cnx = cnx;
        this.data = data;
        Log.e("ssssss", String.valueOf(data.size()));
        try {
            inf = LayoutInflater.from(cnx);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("ssssss55", e.getLocalizedMessage());
        }
    }

    @Override
    public recHelper onCreateViewHolder(ViewGroup parent, int viewType) {
        try {
            Log.e("ssssss5", String.valueOf(data.size()));
            View view = inf.inflate(R.layout.event_item, parent, false);
            return new EventsAdapter.recHelper(view);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(recHelper holder, int position) {
        try {
            Log.e("ssssss1", String.valueOf(data.size()));
            Event item = data.get(position);
            holder.eventName.setText(item.getname());
            holder.eventCreatedDate.setText(cnx.getResources().getString(R.string.created) + item.getcreated_date());
            holder.eventStartingDate.setText(cnx.getResources().getString(R.string.starting) + item.getstarting_date());
            holder.eventItemLay.setTag(item.getid());
            Log.e("ssssss2", String.valueOf(data.size()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class recHelper extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView eventName, eventDescription, eventCreatedDate, eventStartingDate;
        private LinearLayout eventItemLay;

        public recHelper(View itemView) {
            super(itemView);

            eventItemLay = (LinearLayout) itemView.findViewById(R.id.eventItemLay);
            eventItemLay.setOnClickListener(this);
            eventName = (TextView) itemView.findViewById(R.id.eventName);
            eventDescription = (TextView) itemView.findViewById(R.id.eventDescription);
            eventCreatedDate = (TextView) itemView.findViewById(R.id.eventCreatedDate);
            eventStartingDate = (TextView) itemView.findViewById(R.id.eventStartingDate);

            Log.e("ssssss6", String.valueOf(data.size()));
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.eventItemLay) {
                try {
                    Intent i = new Intent(cnx, EventDetailsActivity.class);
                    i.putExtra("event_id", String.valueOf(v.getTag()));
                    Log.e("event_id", String.valueOf(v.getTag()));
                    cnx.startActivity(i);
                } catch (Exception e) {
                    e.getLocalizedMessage();
                }
            }
        }
    }
}


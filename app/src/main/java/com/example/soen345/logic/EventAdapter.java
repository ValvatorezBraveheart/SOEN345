package com.example.soen345.logic;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.soen345.Event;
import com.example.soen345.R;
import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {
    private List<Event> events;
    private OnEventClickListener listener;

    public interface OnEventClickListener {
        void onEventClick(Event event);
    }

    public EventAdapter(List<Event> events, OnEventClickListener listener) {
        this.events = events;
        this.listener = listener;
    }

    public void updateData(List<Event> newEvents) {
        this.events = newEvents;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event event = events.get(position);
        holder.name.setText(event.name);
        holder.location.setText(event.location);
        holder.category.setText(event.category);
        holder.itemView.setOnClickListener(v -> listener.onEventClick(event));
    }

    @Override
    public int getItemCount() { return events.size(); }

    static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView name, location, category;
        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tvEventName);
            location = itemView.findViewById(R.id.tvEventLocation);
            category = itemView.findViewById(R.id.tvEventCategory);
        }
    }
}
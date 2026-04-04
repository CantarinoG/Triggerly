package io.github.cantarinog.triggerly.presentation.ui;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import io.github.cantarinog.triggerly.R;
import io.github.cantarinog.triggerly.domain.model.Reminder;

public class ReminderAdapter extends RecyclerView.Adapter<ReminderAdapter.ViewHolder> {

    private final List<Reminder> reminders = new ArrayList<>();
    private final OnReminderClickListener clickListener;
    private final OnReminderLongClickListener longClickListener;
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

    public interface OnReminderClickListener {
        void onReminderClick(Reminder reminder);
    }

    public interface OnReminderLongClickListener {
        void onReminderLongClick(Reminder reminder);
    }

    public ReminderAdapter(OnReminderClickListener clickListener, OnReminderLongClickListener longClickListener) {
        this.clickListener = clickListener;
        this.longClickListener = longClickListener;
    }

    public void setReminders(List<Reminder> newReminders) {
        this.reminders.clear();
        this.reminders.addAll(newReminders);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_reminder, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Reminder reminder = reminders.get(position);
        holder.bind(reminder, clickListener, longClickListener);
    }

    @Override
    public int getItemCount() {
        return reminders.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textViewName;
        private final TextView textViewDescription;
        private final TextView textViewTimeWindow;
        private final TextView textViewCount;
        private final View iconContainer;
        private final ImageView imageViewIcon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.textViewName);
            textViewDescription = itemView.findViewById(R.id.textViewDescription);
            textViewTimeWindow = itemView.findViewById(R.id.textViewTimeWindow);
            textViewCount = itemView.findViewById(R.id.textViewCount);
            iconContainer = itemView.findViewById(R.id.iconContainer);
            imageViewIcon = itemView.findViewById(R.id.imageViewIcon);
        }

        public void bind(Reminder reminder, OnReminderClickListener clickListener, OnReminderLongClickListener longClickListener) {
            textViewName.setText(reminder.name());
            textViewDescription.setText(reminder.description());
            
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("HH:mm");
            String window = reminder.startTime().format(fmt) + " - " + reminder.endTime().format(fmt);
            textViewTimeWindow.setText(window);
            
            textViewCount.setText(String.valueOf(reminder.numReminders()));

            // Dynamic icon and color
            int iconResId = itemView.getContext().getResources().getIdentifier(
                    reminder.iconName(), "drawable", itemView.getContext().getPackageName());
            if (iconResId != 0) {
                imageViewIcon.setImageResource(iconResId);
            } else {
                imageViewIcon.setImageResource(R.drawable.ic_water_drop);
            }

            try {
                int color = Color.parseColor(reminder.colorHex());
                iconContainer.setBackgroundTintList(android.content.res.ColorStateList.valueOf(color));
            } catch (Exception e) {
                // Fallback to default blue if color parsing fails
                iconContainer.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#2962FF")));
            }

            itemView.setOnClickListener(v -> clickListener.onReminderClick(reminder));
            itemView.setOnLongClickListener(v -> {
                longClickListener.onReminderLongClick(reminder);
                return true;
            });
        }
    }
}

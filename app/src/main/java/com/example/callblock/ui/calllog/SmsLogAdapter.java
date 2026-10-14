package com.example.callblock.ui.calllog;

import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.callblock.R;
import com.example.callblock.utils.PhoneNumberUtils;

import java.util.ArrayList;
import java.util.List;

public class SmsLogAdapter extends RecyclerView.Adapter<SmsLogAdapter.ViewHolder> {

    public interface OnSmsBlockClickListener {
        void onBlockClick(SmsLogItem item);
    }

    private List<SmsLogItem> items = new ArrayList<>();
    private final OnSmsBlockClickListener blockClickListener;

    public SmsLogAdapter(OnSmsBlockClickListener blockClickListener) {
        this.blockClickListener = blockClickListener;
    }

    public void setItems(List<SmsLogItem> newItems) {
        this.items = newItems;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sms_log, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SmsLogItem item = items.get(position);

        holder.tvSender.setText(PhoneNumberUtils.formatDisplay(item.getSender()));
        holder.tvSmsBody.setText(item.getBody());

        String timeStr = DateFormat.format("HH:mm", item.getDate()).toString();
        holder.tvSmsTime.setText(timeStr);

        holder.btnBlockSms.setOnClickListener(v -> {
            if (blockClickListener != null) {
                blockClickListener.onBlockClick(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvSender;
        TextView tvSmsBody;
        TextView tvSmsTime;
        ImageView btnBlockSms;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSender = itemView.findViewById(R.id.tvSender);
            tvSmsBody = itemView.findViewById(R.id.tvSmsBody);
            tvSmsTime = itemView.findViewById(R.id.tvSmsTime);
            btnBlockSms = itemView.findViewById(R.id.btnBlockSms);
        }
    }
}

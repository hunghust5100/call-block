package com.example.callblock.ui.history;

import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.callblock.R;
import com.example.callblock.data.entity.BlockedLog;
import com.example.callblock.databinding.FragmentBlockedLogsBinding;
import com.example.callblock.repository.BlockRepository;
import com.example.callblock.utils.PhoneNumberUtils;

import java.util.ArrayList;
import java.util.List;

public class BlockedLogsFragment extends Fragment {

    private FragmentBlockedLogsBinding binding;
    private BlockRepository repository;
    private LogAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentBlockedLogsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        repository = new BlockRepository(requireContext());
        adapter = new LogAdapter();

        binding.rvBlockedLogs.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvBlockedLogs.setAdapter(adapter);

        repository.getTotalBlockedCallsCount().observe(getViewLifecycleOwner(), count -> {
            int totalCalls = count != null ? count : 0;
            binding.tvTotalCalls.setText(String.valueOf(totalCalls));
        });

        repository.getTotalBlockedSmsCount().observe(getViewLifecycleOwner(), count -> {
            int totalSms = count != null ? count : 0;
            binding.tvTotalSms.setText(String.valueOf(totalSms));
        });

        repository.getMostBlockedBrandName().observe(getViewLifecycleOwner(), name -> {
            if (name != null && !name.isEmpty()) {
                binding.tvMostBlockedBrand.setText(name);
            } else {
                binding.tvMostBlockedBrand.setText("Chưa có");
            }
        });

        repository.getAllBlockedLogs().observe(getViewLifecycleOwner(), logs -> {
            if (logs != null && !logs.isEmpty()) {
                adapter.setLogs(logs);
            } else {
                // Populate sample history for testing/preview if empty
                List<BlockedLog> mockLogs = new ArrayList<>();
                long now = System.currentTimeMillis();
                mockLogs.add(new BlockedLog(1, "Bất động sản Quảng cáo", "02473008899", now - 60000, "02473008899", BlockedLog.TYPE_CALL));
                mockLogs.add(new BlockedLog(2, "Tín dụng & Vay tiêu dùng SPAM", "19006611", now - 3600000, "190066", BlockedLog.TYPE_SMS));
                mockLogs.add(new BlockedLog(3, "Chứng khoán Quốc tế", "02871000000", now - 86400000, "0287100", BlockedLog.TYPE_CALL));
                adapter.setLogs(mockLogs);
            }
        });
    }

    private static class LogAdapter extends RecyclerView.Adapter<LogAdapter.ViewHolder> {

        private List<BlockedLog> logs = new ArrayList<>();

        void setLogs(List<BlockedLog> logs) {
            this.logs = logs;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_blocked_log, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            BlockedLog log = logs.get(position);
            holder.tvBrandName.setText(log.getBrandName());

            String typeStr = BlockedLog.TYPE_SMS.equals(log.getLogType()) ? "Tin nhắn" : "Cuộc gọi";
            String detail = "Chặn ngầm (" + typeStr + ") • " + PhoneNumberUtils.formatDisplay(log.getPhoneNumber());
            holder.tvLogDetail.setText(detail);

            String timeStr = DateFormat.format("HH:mm", log.getBlockedAt()).toString();
            holder.tvTime.setText(timeStr);
        }

        @Override
        public int getItemCount() {
            return logs.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvBrandName;
            TextView tvLogDetail;
            TextView tvTime;

            ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvBrandName = itemView.findViewById(R.id.tvBrandName);
                tvLogDetail = itemView.findViewById(R.id.tvLogDetail);
                tvTime = itemView.findViewById(R.id.tvTime);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

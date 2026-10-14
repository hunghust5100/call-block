package com.example.callblock.ui.calllog;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.CallLog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.callblock.databinding.FragmentCallLogBinding;

import java.util.ArrayList;
import java.util.List;

public class CallLogFragment extends Fragment implements CallLogAdapter.OnCallLogClickListener {

    private FragmentCallLogBinding binding;
    private CallLogAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCallLogBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        adapter = new CallLogAdapter(this);
        binding.rvCallLog.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvCallLog.setAdapter(adapter);

        loadCallLogs();
    }

    private void loadCallLogs() {
        List<CallLogItem> items = new ArrayList<>();

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_CALL_LOG) == PackageManager.PERMISSION_GRANTED) {
            try (Cursor cursor = requireContext().getContentResolver().query(
                    CallLog.Calls.CONTENT_URI,
                    new String[]{CallLog.Calls.NUMBER, CallLog.Calls.DURATION, CallLog.Calls.TYPE, CallLog.Calls.DATE},
                    null, null, CallLog.Calls.DATE + " DESC LIMIT 50")) {

                if (cursor != null && cursor.moveToFirst()) {
                    int numberIdx = cursor.getColumnIndex(CallLog.Calls.NUMBER);
                    int durationIdx = cursor.getColumnIndex(CallLog.Calls.DURATION);
                    int typeIdx = cursor.getColumnIndex(CallLog.Calls.TYPE);
                    int dateIdx = cursor.getColumnIndex(CallLog.Calls.DATE);

                    do {
                        String number = cursor.getString(numberIdx);
                        int duration = cursor.getInt(durationIdx);
                        int type = cursor.getInt(typeIdx);
                        long date = cursor.getLong(dateIdx);

                        boolean isMissed = (type == CallLog.Calls.MISSED_TYPE || duration < 5);
                        String detail = isMissed ? "(Nháy máy " + duration + " giây)" : "Di động • Gần đây";

                        items.add(new CallLogItem(number, detail, isMissed, date));
                    } while (cursor.moveToNext());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Add default mock items from BA design if empty or for preview
        if (items.isEmpty()) {
            long now = System.currentTimeMillis();
            items.add(new CallLogItem("02473008899", "(Nháy máy 3 giây)", true, now));
            items.add(new CallLogItem("0901234567", "Di động • Hôm qua", false, now - 86400000));
            items.add(new CallLogItem("02899998888", "(Nháy máy 2 giây)", true, now - 172800000));
        }

        adapter.setItems(items);
    }

    @Override
    public void onBlockClick(CallLogItem item) {
        QuickBlockBottomSheetDialogFragment bottomSheet = QuickBlockBottomSheetDialogFragment.newInstance(item.getPhoneNumber());
        bottomSheet.show(getParentFragmentManager(), "QuickBlockBottomSheet");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

package com.example.callblock.ui.calllog;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CallLog;
import android.provider.Telephony;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.callblock.databinding.FragmentCallLogBinding;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class CallLogFragment extends Fragment implements CallLogAdapter.OnCallLogClickListener, SmsLogAdapter.OnSmsBlockClickListener {

    private FragmentCallLogBinding binding;
    private CallLogAdapter callLogAdapter;
    private SmsLogAdapter smsLogAdapter;
    private boolean isSmsSelected = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCallLogBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        callLogAdapter = new CallLogAdapter(this);
        smsLogAdapter = new SmsLogAdapter(this);

        binding.rvCallLog.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvCallLog.setAdapter(callLogAdapter);

        loadCallLogs();

        binding.tabLogType.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                isSmsSelected = (tab.getPosition() == 1);
                if (isSmsSelected) {
                    binding.rvCallLog.setAdapter(smsLogAdapter);
                    loadSmsLogs();
                } else {
                    binding.rvCallLog.setAdapter(callLogAdapter);
                    loadCallLogs();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
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

        // Default fallback items for testing/preview if empty
        if (items.isEmpty()) {
            long now = System.currentTimeMillis();
            items.add(new CallLogItem("02473008899", "(Nháy máy 3 giây)", true, now));
            items.add(new CallLogItem("0901234567", "Di động • Hôm qua", false, now - 86400000));
            items.add(new CallLogItem("02899998888", "(Nháy máy 2 giây)", true, now - 172800000));
        }

        callLogAdapter.setItems(items);
    }

    private void loadSmsLogs() {
        List<SmsLogItem> items = new ArrayList<>();

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED) {
            try {
                Uri smsUri = Telephony.Sms.CONTENT_URI;
                try (Cursor cursor = requireContext().getContentResolver().query(
                        smsUri,
                        new String[]{"_id", "address", "body", "date"},
                        null, null, "date DESC LIMIT 50")) {

                    if (cursor != null && cursor.moveToFirst()) {
                        int addressIdx = cursor.getColumnIndex("address");
                        int bodyIdx = cursor.getColumnIndex("body");
                        int dateIdx = cursor.getColumnIndex("date");

                        do {
                            String address = cursor.getString(addressIdx);
                            String body = cursor.getString(bodyIdx);
                            long date = cursor.getLong(dateIdx);

                            if (address != null && !address.trim().isEmpty()) {
                                items.add(new SmsLogItem(address, body, date));
                            }
                        } while (cursor.moveToNext());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Default fallback items for testing/preview if empty
        if (items.isEmpty()) {
            long now = System.currentTimeMillis();
            items.add(new SmsLogItem("19006611", "TB: Chuong trinh khuyen mai vay tin dung 0% lai suat. Lien he 190066...", now - 300000));
            items.add(new SmsLogItem("0901234567", "Chao anh/chi, ben em dang ban du an dat nen tai Dong Nai...", now - 86400000));
            items.add(new SmsLogItem("02899998888", "Sàn chứng khoán quốc tế thưởng ngay 100$ khi mở tài khoản...", now - 172800000));
        }

        smsLogAdapter.setItems(items);
    }

    @Override
    public void onBlockClick(CallLogItem item) {
        QuickBlockBottomSheetDialogFragment bottomSheet = QuickBlockBottomSheetDialogFragment.newInstance(item.getPhoneNumber());
        bottomSheet.show(getParentFragmentManager(), "QuickBlockBottomSheet");
    }

    @Override
    public void onBlockClick(SmsLogItem item) {
        QuickBlockBottomSheetDialogFragment bottomSheet = QuickBlockBottomSheetDialogFragment.newInstance(item.getSender());
        bottomSheet.show(getParentFragmentManager(), "QuickBlockBottomSheet");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

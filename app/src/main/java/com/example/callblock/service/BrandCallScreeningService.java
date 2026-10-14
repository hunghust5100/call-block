package com.example.callblock.service;

import android.net.Uri;
import android.os.Build;
import android.telecom.Call;
import android.telecom.CallScreeningService;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.example.callblock.repository.BlockRepository;
import com.example.callblock.utils.NotificationHelper;

@RequiresApi(api = Build.VERSION_CODES.Q)
public class BrandCallScreeningService extends CallScreeningService {

    private BlockRepository repository;

    @Override
    public void onCreate() {
        super.onCreate();
        repository = new BlockRepository(this);
    }

    @Override
    public void onScreenCall(@NonNull Call.Details callDetails) {
        if (callDetails.getCallDirection() != Call.Details.DIRECTION_INCOMING) {
            respondToCall(callDetails, new CallResponse.Builder().build());
            return;
        }

        Uri handle = callDetails.getHandle();
        if (handle == null) {
            respondToCall(callDetails, new CallResponse.Builder().build());
            return;
        }

        String rawPhoneNumber = handle.getSchemeSpecificPart();
        if (rawPhoneNumber == null || rawPhoneNumber.isEmpty()) {
            respondToCall(callDetails, new CallResponse.Builder().build());
            return;
        }

        repository.checkAndLogBlockedCallSync(rawPhoneNumber, (isBlocked, brandName, matchedPattern) -> {
            CallResponse.Builder responseBuilder = new CallResponse.Builder();

            if (isBlocked) {
                responseBuilder.setDisallowCall(true);
                responseBuilder.setRejectCall(true);
                responseBuilder.setSilenceCall(true);
                responseBuilder.setSkipCallLog(false);
                responseBuilder.setSkipNotification(true);

                respondToCall(callDetails, responseBuilder.build());

                // Show custom Push Notification
                NotificationHelper.showBlockedNotification(
                        BrandCallScreeningService.this,
                        brandName != null ? brandName : "Danh sách SPAM",
                        rawPhoneNumber
                );
            } else {
                respondToCall(callDetails, responseBuilder.build());
            }
        });
    }
}

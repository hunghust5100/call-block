package com.example.callblock.ui.settings;

import android.Manifest;
import android.app.role.RoleManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.callblock.databinding.FragmentSettingsBinding;

import java.util.ArrayList;
import java.util.List;

public class SettingsFragment extends Fragment {

    private FragmentSettingsBinding binding;

    private final ActivityResultLauncher<Intent> roleLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                checkRoleStatus();
                if (isRoleGranted()) {
                    Toast.makeText(requireContext(), "Đã cấp quyền chặn cuộc gọi ngầm thành công!", Toast.LENGTH_SHORT).show();
                }
            });

    private final ActivityResultLauncher<String[]> permissionsLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), permissions -> {
                requestCallScreeningRole();
            });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        checkRoleStatus();

        binding.btnReRequestRole.setOnClickListener(v -> requestPermissionsAndRole());
    }

    @Override
    public void onResume() {
        super.onResume();
        checkRoleStatus();
    }

    private boolean isRoleGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            RoleManager roleManager = requireContext().getSystemService(RoleManager.class);
            return roleManager != null && roleManager.isRoleHeld(RoleManager.ROLE_CALL_SCREENING);
        }
        return true;
    }

    private void checkRoleStatus() {
        if (isRoleGranted()) {
            binding.tvRoleStatusSub.setText("Đã kích hoạt dịch vụ chặn ngầm");
        } else {
            binding.tvRoleStatusSub.setText("Chưa cấp vai trò Chặn cuộc gọi");
        }
    }

    private void requestPermissionsAndRole() {
        List<String> neededPermissions = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
            neededPermissions.add(Manifest.permission.READ_CALL_LOG);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                neededPermissions.add(Manifest.permission.POST_NOTIFICATIONS);
            }
        }

        if (!neededPermissions.isEmpty()) {
            permissionsLauncher.launch(neededPermissions.toArray(new String[0]));
        } else {
            requestCallScreeningRole();
        }
    }

    private void requestCallScreeningRole() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            RoleManager roleManager = requireContext().getSystemService(RoleManager.class);
            if (roleManager != null && roleManager.isRoleAvailable(RoleManager.ROLE_CALL_SCREENING)) {
                Intent intent = roleManager.createRequestRoleIntent(RoleManager.ROLE_CALL_SCREENING);
                roleLauncher.launch(intent);
            } else {
                Toast.makeText(requireContext(), "Thiết bị không hỗ trợ Call Screening Role", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(requireContext(), "Yêu cầu Android 10 (API 29) trở lên", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

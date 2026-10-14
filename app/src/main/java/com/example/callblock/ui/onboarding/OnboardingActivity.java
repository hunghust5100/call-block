package com.example.callblock.ui.onboarding;

import android.Manifest;
import android.app.role.RoleManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.callblock.R;
import com.example.callblock.databinding.ActivityOnboardingBinding;
import com.example.callblock.ui.main.MainActivity;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;

public class OnboardingActivity extends AppCompatActivity {

    private ActivityOnboardingBinding binding;

    private final ActivityResultLauncher<Intent> roleRequestLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK) {
                    Toast.makeText(this, "Đã cấp quyền chặn cuộc gọi thành công!", Toast.LENGTH_SHORT).show();
                }
                navigateToMain();
            });

    private final ActivityResultLauncher<String[]> permissionsLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), permissions -> {
                requestCallScreeningRole();
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOnboardingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupViewPager();

        binding.btnGrant.setOnClickListener(v -> requestPermissionsAndRole());
        binding.btnDeny.setOnClickListener(v -> navigateToMain());
    }

    private void setupViewPager() {
        List<SlideItem> slides = new ArrayList<>();
        slides.add(new SlideItem("Chặn SPAM Theo Danh Sách", "Gom nhóm nhiều số điện thoại quảng cáo vào danh sách chặn để tự động từ chối"));
        slides.add(new SlideItem("Nhận diện Cuộc gọi Quấy rỗi", "Tự động phát hiện nháy máy, số lạ và chặn ngầm dưới 50ms không làm phiền"));
        slides.add(new SlideItem("Bảo vệ Quyền riêng tư 100%", "Dữ liệu lưu trữ hoàn toàn Offline trên thiết bị của bạn, an toàn tuyệt đối"));

        binding.viewPager.setAdapter(new RecyclerView.Adapter<RecyclerView.ViewHolder>() {
            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_onboarding_slide, parent, false);
                return new RecyclerView.ViewHolder(view) {};
            }

            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
                SlideItem item = slides.get(position);
                TextView title = holder.itemView.findViewById(R.id.tvSlideTitle);
                TextView desc = holder.itemView.findViewById(R.id.tvSlideDesc);
                ImageView img = holder.itemView.findViewById(R.id.imgSlide);

                title.setText(item.title);
                desc.setText(item.desc);
                img.setImageResource(R.drawable.ic_shield);
            }

            @Override
            public int getItemCount() {
                return slides.size();
            }
        });

        new TabLayoutMediator(binding.tabDots, binding.viewPager, (tab, position) -> {}).attach();
    }

    private void requestPermissionsAndRole() {
        List<String> neededPermissions = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
            neededPermissions.add(Manifest.permission.READ_CALL_LOG);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
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
            RoleManager roleManager = getSystemService(RoleManager.class);
            if (roleManager != null && roleManager.isRoleAvailable(RoleManager.ROLE_CALL_SCREENING)) {
                if (!roleManager.isRoleHeld(RoleManager.ROLE_CALL_SCREENING)) {
                    Intent intent = roleManager.createRequestRoleIntent(RoleManager.ROLE_CALL_SCREENING);
                    roleRequestLauncher.launch(intent);
                    return;
                }
            }
        }
        navigateToMain();
    }

    private void navigateToMain() {
        SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
        prefs.edit().putBoolean("onboarding_shown", true).apply();

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private static class SlideItem {
        String title;
        String desc;

        SlideItem(String title, String desc) {
            this.title = title;
            this.desc = desc;
        }
    }
}

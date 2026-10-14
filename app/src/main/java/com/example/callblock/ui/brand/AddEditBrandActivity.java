package com.example.callblock.ui.brand;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.callblock.R;
import com.example.callblock.databinding.ActivityAddEditBrandBinding;
import com.example.callblock.repository.BlockRepository;
import com.example.callblock.utils.PhoneNumberUtils;
import com.google.android.material.chip.Chip;

import java.util.ArrayList;
import java.util.List;

public class AddEditBrandActivity extends AppCompatActivity {

    public static final String EXTRA_PREFILL_PHONE = "extra_prefill_phone";

    private ActivityAddEditBrandBinding binding;
    private BlockRepository repository;
    private final List<String> addedPatterns = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddEditBrandBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        repository = new BlockRepository(this);

        binding.btnBack.setOnClickListener(v -> finish());

        // Check if prefilled phone number was passed from Call Log quick block
        String prefill = getIntent().getStringExtra(EXTRA_PREFILL_PHONE);
        if (prefill != null && !prefill.isEmpty()) {
            addPatternChip(prefill);
        } else {
            // Add default mock chips from BA design for preview
            addPatternChip("024 7300 8899");
            addPatternChip("024 7300 9900");
            addPatternChip("028 9999 8888");
            addPatternChip("1900 66xx");
        }

        binding.btnAddNumber.setOnClickListener(v -> {
            String input = binding.etPhoneNumber.getText() != null ? binding.etPhoneNumber.getText().toString().trim() : "";
            if (!TextUtils.isEmpty(input)) {
                addPatternChip(input);
                binding.etPhoneNumber.setText("");
            } else {
                Toast.makeText(this, "Vui lòng nhập số điện thoại hoặc đầu số", Toast.LENGTH_SHORT).show();
            }
        });

        binding.btnSaveBrand.setOnClickListener(v -> saveBrand());
    }

    private void addPatternChip(String raw) {
        if (raw == null || raw.trim().isEmpty()) return;
        String formatted = PhoneNumberUtils.formatDisplay(raw);

        if (addedPatterns.contains(formatted)) return;

        addedPatterns.add(formatted);

        Chip chip = new Chip(this);
        chip.setText(formatted);
        chip.setCloseIconVisible(true);
        chip.setChipBackgroundColorResource(R.color.pixel_chip_bg);
        chip.setTextColor(getColor(R.color.pixel_chip_text));

        chip.setOnCloseIconClickListener(v -> {
            addedPatterns.remove(formatted);
            binding.chipGroupNumbers.removeView(chip);
            updateChipCountText();
        });

        binding.chipGroupNumbers.addView(chip);
        updateChipCountText();
    }

    private void updateChipCountText() {
        binding.tvChipCount.setText("Danh sách số thuộc thương hiệu này (" + addedPatterns.size() + " số)");
    }

    private void saveBrand() {
        String brandName = binding.etBrandName.getText() != null ? binding.etBrandName.getText().toString().trim() : "";
        if (TextUtils.isEmpty(brandName)) {
            Toast.makeText(this, "Vui lòng nhập tên Thương hiệu / Dịch vụ SPAM", Toast.LENGTH_SHORT).show();
            return;
        }

        if (addedPatterns.isEmpty()) {
            Toast.makeText(this, "Vui lòng thêm ít nhất 1 số điện thoại hoặc đầu số", Toast.LENGTH_SHORT).show();
            return;
        }

        repository.insertBrandWithNumbers(
                brandName,
                "Thương hiệu tạo bởi người dùng",
                addedPatterns,
                () -> runOnUiThread(() -> {
                    Toast.makeText(this, "Đã lưu và kích hoạt thương hiệu: " + brandName, Toast.LENGTH_SHORT).show();
                    finish();
                })
        );
    }
}

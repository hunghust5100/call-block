package com.example.callblock.ui.brand;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.callblock.R;
import com.example.callblock.data.entity.BlockedNumber;
import com.example.callblock.databinding.ActivityAddEditBrandBinding;
import com.example.callblock.repository.BlockRepository;
import com.example.callblock.utils.PhoneNumberUtils;

import java.util.ArrayList;
import java.util.List;

public class AddEditBrandActivity extends AppCompatActivity {

    public static final String EXTRA_BRAND_ID = "extra_brand_id";
    public static final String EXTRA_PREFILL_PHONE = "extra_prefill_phone";

    private ActivityAddEditBrandBinding binding;
    private BlockRepository repository;
    private final List<String> addedPatterns = new ArrayList<>();
    private long brandId = -1;
    private boolean isEditMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddEditBrandBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Handle System Window Insets programmatically to prevent OS from wiping out side/top/bottom padding
        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, windowInsets) -> {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            float density = getResources().getDisplayMetrics().density;
            int sidePadding = (int) (20 * density);
            int topPadding = insets.top + (int) (12 * density);
            int bottomPadding = insets.bottom + (int) (16 * density);

            v.setPadding(sidePadding, topPadding, sidePadding, bottomPadding);
            return windowInsets;
        });

        repository = new BlockRepository(this);

        binding.btnBackContainer.setOnClickListener(v -> finish());

        brandId = getIntent().getLongExtra(EXTRA_BRAND_ID, -1);
        isEditMode = (brandId != -1);

        if (isEditMode) {
            binding.tvTitle.setText(R.string.title_edit_brand);
            binding.btnDeleteBrand.setVisibility(View.VISIBLE);
            loadBrandDataForEdit();
        } else {
            binding.tvTitle.setText(R.string.title_create_brand);
            binding.btnDeleteBrand.setVisibility(View.GONE);

            String prefill = getIntent().getStringExtra(EXTRA_PREFILL_PHONE);
            if (prefill != null && !prefill.isEmpty()) {
                addPatternNumber(prefill);
            } else {
                updateNumberListView();
            }
        }

        binding.btnAddNumber.setOnClickListener(v -> {
            String input = binding.etPhoneNumber.getText() != null ? binding.etPhoneNumber.getText().toString().trim() : "";
            if (validateAndAddNumber(input)) {
                binding.etPhoneNumber.setText("");
            }
        });

        binding.btnSaveBrand.setOnClickListener(v -> saveBrand());
        binding.btnDeleteBrand.setOnClickListener(v -> confirmDeleteBrand());
    }

    private boolean validateAndAddNumber(String input) {
        if (TextUtils.isEmpty(input)) {
            Toast.makeText(this, "Vui lòng nhập số điện thoại hoặc đầu số", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Strict Validation: Must not contain letters/random text except 'x' or 'X' for prefix wildcard
        String cleanInput = input.trim();

        // Check if string contains illegal letters (anything other than digits, +, -, space, x, X, #, *)
        if (cleanInput.matches(".*[a-wA-Wy-zY-Z].*")) {
            Toast.makeText(this, "Số điện thoại không hợp lệ! Vui lòng chỉ nhập chữ số (ví dụ: 024730..., 190066xx)", Toast.LENGTH_LONG).show();
            return false;
        }

        // Must contain at least 2 digits or wildcard
        String digitsOnly = cleanInput.replaceAll("[^0-9xX]", "");
        if (digitsOnly.length() < 2) {
            Toast.makeText(this, "Số điện thoại / đầu số phải có ít nhất 2 chữ số!", Toast.LENGTH_SHORT).show();
            return false;
        }

        addPatternNumber(cleanInput);
        return true;
    }

    private void loadBrandDataForEdit() {
        repository.getBrandWithNumbersById(brandId).observe(this, brandWithNumbers -> {
            if (brandWithNumbers != null && brandWithNumbers.brand != null) {
                binding.etBrandName.setText(brandWithNumbers.brand.getName());

                addedPatterns.clear();

                if (brandWithNumbers.numbers != null) {
                    for (BlockedNumber number : brandWithNumbers.numbers) {
                        String formatted = PhoneNumberUtils.formatDisplay(number.getNumberPattern());
                        if (!addedPatterns.contains(formatted)) {
                            addedPatterns.add(formatted);
                        }
                    }
                }
                updateNumberListView();
            }
        });
    }

    private void addPatternNumber(String raw) {
        if (raw == null || raw.trim().isEmpty()) return;
        String formatted = PhoneNumberUtils.formatDisplay(raw);

        if (addedPatterns.contains(formatted)) {
            Toast.makeText(this, "Số điện thoại này đã có trong danh sách", Toast.LENGTH_SHORT).show();
            return;
        }

        addedPatterns.add(formatted);
        updateNumberListView();
    }

    private void updateNumberListView() {
        binding.tvChipCount.setText("Danh sách số thuộc danh sách này (" + addedPatterns.size() + " số)");

        if (addedPatterns.isEmpty()) {
            binding.cardEmptyState.setVisibility(View.VISIBLE);
            binding.layoutNumberList.setVisibility(View.GONE);
            binding.layoutNumberList.removeAllViews();
            return;
        }

        binding.cardEmptyState.setVisibility(View.GONE);
        binding.layoutNumberList.setVisibility(View.VISIBLE);
        binding.layoutNumberList.removeAllViews();

        LayoutInflater inflater = LayoutInflater.from(this);
        for (int i = 0; i < addedPatterns.size(); i++) {
            String pattern = addedPatterns.get(i);
            View rowView = inflater.inflate(R.layout.item_number_row, binding.layoutNumberList, false);

            TextView tvPhone = rowView.findViewById(R.id.tvPhoneNumber);
            TextView tvType = rowView.findViewById(R.id.tvNumberType);
            ImageView btnRemove = rowView.findViewById(R.id.btnRemoveNumber);

            tvPhone.setText(pattern);

            boolean isPrefix = PhoneNumberUtils.isPrefixPattern(pattern);
            if (isPrefix) {
                tvType.setText("Đầu số quy tắc (Chặn nhóm)");
            } else {
                tvType.setText("Số điện thoại đầy đủ");
            }

            final String targetPattern = pattern;
            btnRemove.setOnClickListener(v -> {
                addedPatterns.remove(targetPattern);
                updateNumberListView();
            });

            binding.layoutNumberList.addView(rowView);
        }
    }

    private void saveBrand() {
        String brandName = binding.etBrandName.getText() != null ? binding.etBrandName.getText().toString().trim() : "";
        if (TextUtils.isEmpty(brandName)) {
            Toast.makeText(this, "Vui lòng nhập tên Danh sách / Dịch vụ SPAM", Toast.LENGTH_SHORT).show();
            return;
        }

        if (addedPatterns.isEmpty()) {
            Toast.makeText(this, "Vui lòng thêm ít nhất 1 số điện thoại hoặc đầu số", Toast.LENGTH_SHORT).show();
            return;
        }

        if (isEditMode) {
            repository.updateBrandWithNumbers(
                    brandId,
                    brandName,
                    "Danh sách chặn tùy chỉnh",
                    addedPatterns,
                    () -> runOnUiThread(() -> {
                        Toast.makeText(this, "Đã cập nhật danh sách: " + brandName, Toast.LENGTH_SHORT).show();
                        finish();
                    })
            );
        } else {
            repository.insertBrandWithNumbers(
                    brandName,
                    "Danh sách chặn tạo bởi người dùng",
                    addedPatterns,
                    () -> runOnUiThread(() -> {
                        Toast.makeText(this, "Đã lưu và kích hoạt danh sách: " + brandName, Toast.LENGTH_SHORT).show();
                        finish();
                    })
            );
        }
    }

    private void confirmDeleteBrand() {
        new AlertDialog.Builder(this)
                .setTitle("Xóa danh sách")
                .setMessage(R.string.confirm_delete_brand)
                .setPositiveButton("Xóa", (dialog, which) -> {
                    repository.deleteBrandById(brandId, () -> runOnUiThread(() -> {
                        Toast.makeText(this, "Đã xóa danh sách chặn", Toast.LENGTH_SHORT).show();
                        finish();
                    }));
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
}

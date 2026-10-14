package com.example.callblock.ui.calllog;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.callblock.data.entity.BrandWithNumbers;
import com.example.callblock.databinding.BottomSheetQuickBlockBinding;
import com.example.callblock.repository.BlockRepository;
import com.example.callblock.ui.brand.AddEditBrandActivity;
import com.example.callblock.utils.PhoneNumberUtils;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QuickBlockBottomSheetDialogFragment extends BottomSheetDialogFragment {

    private static final String ARG_PHONE_NUMBER = "arg_phone_number";

    private BottomSheetQuickBlockBinding binding;
    private BlockRepository repository;
    private String rawPhoneNumber;
    private long selectedBrandId = -1;
    private final List<BrandWithNumbers> brandList = new ArrayList<>();

    public static QuickBlockBottomSheetDialogFragment newInstance(String phoneNumber) {
        QuickBlockBottomSheetDialogFragment fragment = new QuickBlockBottomSheetDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PHONE_NUMBER, phoneNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            rawPhoneNumber = getArguments().getString(ARG_PHONE_NUMBER);
        }
        repository = new BlockRepository(requireContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = BottomSheetQuickBlockBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String displayPhone = PhoneNumberUtils.formatDisplay(rawPhoneNumber);
        binding.tvPhoneNumber.setText("Số điện thoại: " + displayPhone);

        binding.btnClose.setOnClickListener(v -> dismiss());
        binding.btnCancel.setOnClickListener(v -> dismiss());

        binding.btnCreateNewBrand.setOnClickListener(v -> {
            dismiss();
            Intent intent = new Intent(requireContext(), AddEditBrandActivity.class);
            intent.putExtra(AddEditBrandActivity.EXTRA_PREFILL_PHONE, rawPhoneNumber);
            startActivity(intent);
        });

        repository.getAllBrandsWithNumbers().observe(getViewLifecycleOwner(), brands -> {
            brandList.clear();
            if (brands != null) {
                brandList.addAll(brands);
            }
            updateBrandListUI();
        });

        binding.btnBlockNow.setOnClickListener(v -> performBlock());
    }

    private void updateBrandListUI() {
        if (brandList.isEmpty()) {
            binding.layoutNoBrand.setVisibility(View.VISIBLE);
            binding.rgBrands.setVisibility(View.GONE);
            binding.btnBlockNow.setEnabled(false);
            binding.btnBlockNow.setAlpha(0.5f);
        } else {
            binding.layoutNoBrand.setVisibility(View.GONE);
            binding.rgBrands.setVisibility(View.VISIBLE);
            binding.rgBrands.removeAllViews();
            binding.btnBlockNow.setEnabled(true);
            binding.btnBlockNow.setAlpha(1.0f);

            for (int i = 0; i < brandList.size(); i++) {
                BrandWithNumbers b = brandList.get(i);
                RadioButton rb = new RadioButton(requireContext());
                rb.setId(View.generateViewId());
                rb.setText(b.brand.getName());
                rb.setTextSize(16);
                rb.setPadding(16, 16, 16, 16);

                if (i == 0) {
                    rb.setChecked(true);
                    selectedBrandId = b.brand.getId();
                }

                int finalI = i;
                rb.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    if (isChecked) {
                        selectedBrandId = brandList.get(finalI).brand.getId();
                    }
                });

                binding.rgBrands.addView(rb);
            }
        }
    }

    private void performBlock() {
        if (selectedBrandId == -1) {
            Toast.makeText(requireContext(), "Vui lòng chọn hoặc tạo thương hiệu trước!", Toast.LENGTH_SHORT).show();
            return;
        }

        BrandWithNumbers targetBrand = null;
        for (BrandWithNumbers b : brandList) {
            if (b.brand.getId() == selectedBrandId) {
                targetBrand = b;
                break;
            }
        }

        if (targetBrand != null) {
            String norm = PhoneNumberUtils.normalize(rawPhoneNumber);
            repository.insertBrandWithNumbers(
                    targetBrand.brand.getName(),
                    targetBrand.brand.getDescription(),
                    Collections.singletonList(norm),
                    null
            );
            Toast.makeText(requireContext(), "Đã thêm " + norm + " vào thương hiệu " + targetBrand.brand.getName(), Toast.LENGTH_SHORT).show();
            dismiss();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

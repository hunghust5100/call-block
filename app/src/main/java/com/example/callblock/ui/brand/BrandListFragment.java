package com.example.callblock.ui.brand;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.callblock.data.entity.BlockedNumber;
import com.example.callblock.data.entity.BrandWithNumbers;
import com.example.callblock.databinding.FragmentBrandListBinding;
import com.example.callblock.repository.BlockRepository;

import java.util.ArrayList;
import java.util.List;

public class BrandListFragment extends Fragment implements BrandAdapter.OnBrandToggleListener, BrandAdapter.OnBrandClickListener {

    private FragmentBrandListBinding binding;
    private BrandAdapter adapter;
    private BlockRepository repository;
    private final List<BrandWithNumbers> fullBrandList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentBrandListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        repository = new BlockRepository(requireContext());
        adapter = new BrandAdapter(this, this);

        binding.rvBrands.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvBrands.setAdapter(adapter);

        binding.fabAddBrand.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), AddEditBrandActivity.class);
            startActivity(intent);
        });

        repository.getAllBrandsWithNumbers().observe(getViewLifecycleOwner(), brands -> {
            fullBrandList.clear();
            if (brands != null) {
                fullBrandList.addAll(brands);
            }
            filterList(binding.etSearchBrand.getText() != null ? binding.etSearchBrand.getText().toString() : "");
        });

        binding.etSearchBrand.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterList(s != null ? s.toString() : "");
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void filterList(String query) {
        if (query == null || query.trim().isEmpty()) {
            adapter.setItems(new ArrayList<>(fullBrandList));
            return;
        }

        String lowerQuery = query.trim().toLowerCase();
        List<BrandWithNumbers> filtered = new ArrayList<>();

        for (BrandWithNumbers b : fullBrandList) {
            boolean nameMatch = b.brand.getName() != null && b.brand.getName().toLowerCase().contains(lowerQuery);
            boolean numberMatch = false;

            if (b.numbers != null) {
                for (BlockedNumber num : b.numbers) {
                    if (num.getNumberPattern() != null && num.getNumberPattern().contains(lowerQuery)) {
                        numberMatch = true;
                        break;
                    }
                }
            }

            if (nameMatch || numberMatch) {
                filtered.add(b);
            }
        }

        adapter.setItems(filtered);
    }

    @Override
    public void onToggle(BrandWithNumbers brandWithNumbers, boolean isEnabled) {
        repository.setBrandEnabled(brandWithNumbers.brand.getId(), isEnabled);
    }

    @Override
    public void onBrandClick(BrandWithNumbers brandWithNumbers) {
        Intent intent = new Intent(requireContext(), AddEditBrandActivity.class);
        intent.putExtra(AddEditBrandActivity.EXTRA_BRAND_ID, brandWithNumbers.brand.getId());
        startActivity(intent);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

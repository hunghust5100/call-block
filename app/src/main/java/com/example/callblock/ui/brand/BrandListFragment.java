package com.example.callblock.ui.brand;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.callblock.data.entity.BrandWithNumbers;
import com.example.callblock.databinding.FragmentBrandListBinding;
import com.example.callblock.repository.BlockRepository;

import java.util.ArrayList;
import java.util.List;

public class BrandListFragment extends Fragment implements BrandAdapter.OnBrandToggleListener {

    private FragmentBrandListBinding binding;
    private BrandAdapter adapter;
    private BlockRepository repository;

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
        adapter = new BrandAdapter(this);

        binding.rvBrands.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvBrands.setAdapter(adapter);

        binding.fabAddBrand.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), AddEditBrandActivity.class);
            startActivity(intent);
        });

        repository.getAllBrandsWithNumbers().observe(getViewLifecycleOwner(), brands -> {
            if (brands != null) {
                adapter.setItems(brands);
            } else {
                adapter.setItems(new ArrayList<>());
            }
        });
    }

    @Override
    public void onToggle(BrandWithNumbers brandWithNumbers, boolean isEnabled) {
        repository.setBrandEnabled(brandWithNumbers.brand.getId(), isEnabled);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

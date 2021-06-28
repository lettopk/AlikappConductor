package com.Alikapp.alikappconductor.ui.soporte;

import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.Alikapp.alikappconductor.ActivityBilletera;
import com.Alikapp.alikappconductor.R;
import com.Alikapp.alikappconductor.SoporteActivity;

public class SoporteFragment extends Fragment {

    private SoporteViewModel mViewModel;

    public static SoporteFragment newInstance() {
        return new SoporteFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        android.view.View root = inflater.inflate(R.layout.soporte_fragment, container, false);

        Intent intent = new Intent(getContext(), SoporteActivity.class);
        startActivity(intent);

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(SoporteViewModel.class);
        // TODO: Use the ViewModel
    }

}
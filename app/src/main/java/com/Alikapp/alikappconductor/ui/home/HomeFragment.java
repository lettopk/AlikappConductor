package com.Alikapp.alikappconductor.ui.home;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.Alikapp.alikappconductor.CustomerMapActivity;
import com.Alikapp.alikappconductor.R;

public class HomeFragment extends androidx.fragment.app.Fragment {

    private HomeViewModel homeViewModel;
    Activity inicio;

    public android.view.View onCreateView(@androidx.annotation.NonNull LayoutInflater inflater,
                                          ViewGroup container, android.os.Bundle savedInstanceState) {
        homeViewModel =
                new androidx.lifecycle.ViewModelProvider(this).get(HomeViewModel.class);
        android.view.View root = inflater.inflate(R.layout.fragment_home, container, false);


        return root;
    }
}
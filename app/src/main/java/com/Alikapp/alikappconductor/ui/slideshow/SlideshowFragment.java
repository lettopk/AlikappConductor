package com.Alikapp.alikappconductor.ui.slideshow;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.Alikapp.alikappconductor.CashActivity;
import com.Alikapp.alikappconductor.HistoryActivity;
import com.Alikapp.alikappconductor.LegalActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.Alikapp.alikappconductor.CustomerLoginActivity;
import com.Alikapp.alikappconductor.CustomerMapActivity;
import com.Alikapp.alikappconductor.R;

public class SlideshowFragment extends androidx.fragment.app.Fragment {

    public android.view.View onCreateView(@androidx.annotation.NonNull LayoutInflater inflater,
                                          ViewGroup container, android.os.Bundle savedInstanceState) {
        android.view.View root = inflater.inflate(R.layout.fragment_slideshow, container, false);

        Intent intent = new Intent(getContext(), CashActivity.class);
        startActivity(intent);

        return root;
    }
}
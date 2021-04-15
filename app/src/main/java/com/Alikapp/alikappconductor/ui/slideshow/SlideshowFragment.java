package com.Alikapp.alikappconductor.ui.slideshow;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.Alikapp.alikappconductor.ActivityBilletera;
import com.Alikapp.alikappconductor.R;

public class SlideshowFragment extends androidx.fragment.app.Fragment {

    public android.view.View onCreateView(@androidx.annotation.NonNull LayoutInflater inflater,
                                          ViewGroup container, android.os.Bundle savedInstanceState) {
        android.view.View root = inflater.inflate(R.layout.fragment_slideshow, container, false);

        Intent intent = new Intent(getContext(), ActivityBilletera.class);
        startActivity(intent);

        return root;
    }
}
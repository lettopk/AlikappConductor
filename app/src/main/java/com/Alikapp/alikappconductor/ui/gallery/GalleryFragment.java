package com.Alikapp.alikappconductor.ui.gallery;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.Alikapp.alikappconductor.CustomerMapActivity;
import com.Alikapp.alikappconductor.CustomerSettingsActivity;
import com.Alikapp.alikappconductor.R;

public class GalleryFragment extends androidx.fragment.app.Fragment {

    private GalleryViewModel galleryViewModel;

    public android.view.View onCreateView(@androidx.annotation.NonNull LayoutInflater inflater,
                                          ViewGroup container, android.os.Bundle savedInstanceState) {
        galleryViewModel =
                new androidx.lifecycle.ViewModelProvider(this).get(GalleryViewModel.class);
        android.view.View root = inflater.inflate(R.layout.fragment_gallery, container, false);
        final android.widget.TextView textView = root.findViewById(R.id.text_gallery);
        galleryViewModel.getText().observe(getViewLifecycleOwner(), new androidx.lifecycle.Observer<String>() {
            @Override
            public void onChanged(@androidx.annotation.Nullable String s) {
                textView.setText(s);
            }
        });

        Intent intent = new Intent(getContext(), CustomerSettingsActivity.class);
        intent.putExtra("PrimeraVez", false);
        startActivity(intent);

        return root;
    }
}
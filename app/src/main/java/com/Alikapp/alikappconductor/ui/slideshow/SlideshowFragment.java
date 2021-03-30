package com.Alikapp.alikappconductor.ui.slideshow;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.Alikapp.alikappconductor.LegalActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.Alikapp.alikappconductor.CustomerLoginActivity;
import com.Alikapp.alikappconductor.CustomerMapActivity;
import com.Alikapp.alikappconductor.R;

public class SlideshowFragment extends androidx.fragment.app.Fragment {

    private SlideshowViewModel slideshowViewModel;

    public android.view.View onCreateView(@androidx.annotation.NonNull LayoutInflater inflater,
                                          ViewGroup container, android.os.Bundle savedInstanceState) {
        slideshowViewModel =
                new androidx.lifecycle.ViewModelProvider(this).get(SlideshowViewModel.class);
        android.view.View root = inflater.inflate(R.layout.fragment_slideshow, container, false);
        final android.widget.TextView textView = root.findViewById(R.id.text_slideshow);
        slideshowViewModel.getText().observe(getViewLifecycleOwner(), new androidx.lifecycle.Observer<String>() {
            @Override
            public void onChanged(@androidx.annotation.Nullable String s) {
                textView.setText(s);
            }
        });

        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(getContext(), LegalActivity.class);
        intent.putExtra("PrimeraVez", false);
        startActivity(intent);

        return root;
    }
}
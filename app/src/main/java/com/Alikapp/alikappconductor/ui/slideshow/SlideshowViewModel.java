package com.Alikapp.alikappconductor.ui.slideshow;

import androidx.lifecycle.MutableLiveData;

public class SlideshowViewModel extends androidx.lifecycle.ViewModel {

    private MutableLiveData<String> mText;

    public SlideshowViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is slideshow fragment");
    }

    public androidx.lifecycle.LiveData<String> getText() {
        return mText;
    }
}
package com.Alikapp.alikappconductor.ui.gallery;

import androidx.lifecycle.MutableLiveData;

public class GalleryViewModel extends androidx.lifecycle.ViewModel {

    private MutableLiveData<String> mText;

    public GalleryViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is gallery fragment");
    }

    public androidx.lifecycle.LiveData<String> getText() {
        return mText;
    }
}
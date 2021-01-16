package com.Alikapp.alikappconductor.ui.home;

import androidx.lifecycle.MutableLiveData;

public class HomeViewModel extends androidx.lifecycle.ViewModel {

    private MutableLiveData<String> mText;

    public HomeViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is home fragment");
    }

    public androidx.lifecycle.LiveData<String> getText() {
        return mText;
    }
}
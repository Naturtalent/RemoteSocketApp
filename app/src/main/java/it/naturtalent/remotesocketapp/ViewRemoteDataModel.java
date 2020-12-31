package it.naturtalent.remotesocketapp;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import it.naturtalent.databinding.RemoteData;

public class ViewRemoteDataModel  extends ViewModel
{
    private MutableLiveData<String> currentName;

    public MutableLiveData<String> getCurrentName() {
        if (currentName == null) {
            currentName = new MutableLiveData<String>();
        }
        return currentName;
    }



}

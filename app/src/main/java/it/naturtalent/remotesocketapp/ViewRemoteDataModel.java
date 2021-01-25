package it.naturtalent.remotesocketapp;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ViewRemoteDataModel  extends ViewModel
{
    private MutableLiveData<SocketViewAdapter> dataModel = new MutableLiveData<SocketViewAdapter>();

    public void setDataModel(SocketViewAdapter adapter)
    {
        dataModel.setValue(adapter);
    }

    public LiveData<SocketViewAdapter> getDataModel()
    {
        return dataModel;
    }



}

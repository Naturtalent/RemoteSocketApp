package it.naturtalent.remotesocketapp;

import android.content.ClipData;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.tinkerforge.IPConnection;

/*
    'SharedIPConnectionModel' erweitert ViewModel damit 'ipConnection' ueber Fragmentgrenzen
    kommuniziert werden kann.
 */
public class SharedIPConnectionModel  extends ViewModel
{
    private final MutableLiveData<IPConnection> selected = new MutableLiveData<IPConnection>();
    ;

    public IPConnection getIpConnection()
    {
        return selected.getValue();
    }

    public void setIpConnection(IPConnection ipConnection)
    {
        selected.setValue(ipConnection);
    }
}

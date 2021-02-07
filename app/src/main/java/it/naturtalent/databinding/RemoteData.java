package it.naturtalent.databinding;


import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import it.naturtalent.remotesocketapp.BR;

// durch gradle tool erzeugete Classe BR


/**
 *  View Model
 */
public class RemoteData extends BaseObservable implements Cloneable
{

    private String name;

    private boolean selected;

    private String type;

    private String houseCode;

    private String remoteCode;

    //android:selectedType="@={remotesocket.obvSelectedType}"

    @Bindable
    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
        notifyPropertyChanged(BR.name);
    }

    @Bindable
    public boolean isSelected()
    {
        return selected;
    }

    public void setSelected(boolean selected)
    {
        this.selected = selected;
        notifyPropertyChanged(BR.selected);
    }

    @Bindable
    public String getType()
    {
        android.util.Log.i("RemoteData", "getType: ");
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
        notifyPropertyChanged(BR.type);
        android.util.Log.i("RemoteData", "setType: "+type);
    }

    @Bindable
    public String getHouseCode()
    {
        return houseCode;
    }

    public void setHouseCode(String houseCode)
    {
        this.houseCode = houseCode;
        notifyPropertyChanged(BR.houseCode);
    }

    @Bindable
    public String getRemoteCode()
    {
        return remoteCode;
    }

    public void setRemoteCode(String remoteCode)
    {
        this.remoteCode = remoteCode;
        notifyPropertyChanged(BR.remoteCode);
    }

    public void validate()
    {
        name = validateString (name, "neu");
        houseCode = validateString (houseCode, "0");
        remoteCode = validateString (remoteCode, "0");

        type = validateString (type, "A");
    }

    private String validateString (String value, String defaultValue)
    {
        if(value != null)
            value = value.trim();

        value = ((value == null) || (value.isEmpty())) ? defaultValue : value;
        return value;
    }

    /**
     * Konstruktion
     *
     * @param name
     * @param type
     * @param houseCode
     * @param remoteCode
     */
    public RemoteData(String name, String type, String houseCode, String remoteCode)
    {
        this.name = name;
        this.type = type;
        this.houseCode = houseCode;
        this.remoteCode = remoteCode;
        this.selected = false;
    }

    public Object clone()
    {
        try
        {
            return super.clone();
        }
        catch( CloneNotSupportedException e )
        {
            return null;
        }
    }

}

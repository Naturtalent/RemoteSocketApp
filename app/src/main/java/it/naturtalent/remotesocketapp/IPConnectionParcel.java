package it.naturtalent.remotesocketapp;

import android.os.Parcel;
import android.os.Parcelable;

import com.tinkerforge.IPConnection;

public class IPConnectionParcel extends IPConnection implements Parcelable
{

    private IPConnection ipcon;

    // Constructor
    protected IPConnectionParcel(Parcel in)
    {
    }

    // static final method called CREATOR - implementiert Parceble.Creator interface
    public static final Creator<IPConnectionParcel> CREATOR = new Creator<IPConnectionParcel>()
    {
        @Override
        public IPConnectionParcel createFromParcel(Parcel in)
        {
            return new IPConnectionParcel(in);
        }

        @Override
        public IPConnectionParcel[] newArray(int size)
        {
            return new IPConnectionParcel[size];
        }
    };

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i)
    {
    }
}

package it.naturtalent.multithread;

import androidx.annotation.WorkerThread;

import java.util.ArrayList;
import java.util.List;

import it.naturtalent.databinding.RemoteData;

public class FakeDataFetcher {

    public static class DataFetchException extends Exception {}

    private boolean mIsError = true;

    /*
    @WorkerThread
    public String getData() throws DataFetchException {

        // simulate 2 seconds worth of work
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        mIsError = !mIsError; // error response every other time

        if (mIsError) {
            throw new DataFetchException();
        } else {
            return "fake data";
        }
    }
     */

    @WorkerThread
    public List<RemoteData> getData() throws DataFetchException
    {

        List<RemoteData>remoteData = null;

        // simulate 2 seconds worth of work
        try
        {
            Thread.sleep(6000);

            // die reale Ladefunktion muss im Fehlerfall eine 'InterruptedException e' werfen
            remoteData = loadDataList ();

        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }

        // Fehlerantwort fuer jedes zweite Mal (unklar)
        mIsError = !mIsError;

        if (mIsError)
        {
            throw new DataFetchException();
        } else
        {
            return remoteData;
        }
    }



    // RemmoteSeocketDaten generieren
    public List<RemoteData> loadDataList ()
    {
        List<RemoteData>remoteDateList = new ArrayList<>();


        /*
        RemoteData socket = new RemoteData("Schalter1", "type", "Code", "RemoteCode");
        remoteDateList.add(socket);

        socket = new RemoteData("Schalter2", "type", "Code", "RemoteCode");
        remoteDateList.add(socket);

         */

        int n = 20;
        for(int i = 1; i < n;i++)
        {
            RemoteData socket = new RemoteData("Schalter"+i, "type", "Code", "RemoteCode");
            remoteDateList.add(socket);
        }


        return remoteDateList;
    }

}

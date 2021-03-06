package it.naturtalent.multithread;


import androidx.annotation.UiThread;
import androidx.annotation.WorkerThread;

import com.techyourchance.threadposter.BackgroundThreadPoster;
import com.techyourchance.threadposter.UiThreadPoster;
import com.tinkerforge.IPConnection;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import it.naturtalent.databinding.RemoteData;
import it.naturtalent.remotesocketapp.SocketViewAdapter;

public class ConnectionUseCase
{


    // Interface des Connection Listeners
    // Instancen @see MainActivity
    public interface ConnectionListener
    {
        void onConnectionEstablished(IPConnection ipcon);

        void onConnectionFailed(String message);
    }

    private final FakeDataFetcher mFakeDataFetcher;
    private final BackgroundThreadPoster mBackgroundThreadPoster;
    private final UiThreadPoster mUiThreadPoster;

    // Mao zur Aufnahme aller Connectionlistener
    private final Set<ConnectionListener> mListeners = Collections.newSetFromMap(
            new ConcurrentHashMap<ConnectionListener, Boolean>());

    /**
     * Konstruktion
     * @param fakeDataFetcher
     * @param backgroundThreadPoster
     * @param uiThreadPoster
     */
    public ConnectionUseCase(FakeDataFetcher fakeDataFetcher,
                             BackgroundThreadPoster backgroundThreadPoster,
                             UiThreadPoster uiThreadPoster)
    {
        mFakeDataFetcher = fakeDataFetcher;
        mBackgroundThreadPoster = backgroundThreadPoster;
        mUiThreadPoster = uiThreadPoster;
    }

    public void registerListener(ConnectionListener listener)
    {
        mListeners.add(listener);
    }

    public void unregisterListener(ConnectionListener listener)
    {
        mListeners.remove(listener);
    }

    public void connectWiFi()
    {
        // offload work to background thread
        mBackgroundThreadPoster.post(new Runnable()
        {
            @Override
            public void run()
            {
                connectWiFiSync();
            }
        });
    }

    @WorkerThread
    private void connectWiFiSync()
    {
        try
        {
            final IPConnection ipConnection = mFakeDataFetcher.connectWiFi();

            mUiThreadPoster.post(new Runnable()
            { // notify listeners on UI thread
                @Override
                public void run()
                {
                    notifySuccess(ipConnection);
                }
            });
        } catch (final FakeDataFetcher.ConnectException e)
        {
            mUiThreadPoster.post(new Runnable()
            { // notify listeners on UI thread
                @Override
                public void run()
                {
                    //android.util.Log.d("ConnectionUserCase", "ConnectionException");
                    notifyFailure(e.message);
                }
            });
        }
    }

    // Listener ueber den gescheiterten Verbindungsaufbau informieren
    // @see MainActivity
    @UiThread
    public void notifyFailure(String message)
    {
        for (ConnectionListener listener : mListeners)
        {
            listener.onConnectionFailed(message);
        }
    }

    // Listener ueber den erfolgreichen Verbindungsaufbau informieren
    // @see MainActivity
    @UiThread
    private void notifySuccess(IPConnection ipcon)
    {
        for (ConnectionListener listener : mListeners)
        {
            listener.onConnectionEstablished(ipcon);
        }
    }

}

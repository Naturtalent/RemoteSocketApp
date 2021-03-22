package it.naturtalent.multithread;


import androidx.annotation.UiThread;
import androidx.annotation.WorkerThread;

import com.techyourchance.threadposter.BackgroundThreadPoster;
import com.techyourchance.threadposter.UiThreadPoster;
import com.tinkerforge.BrickletRemoteSwitch;
import com.tinkerforge.IPConnection;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import it.naturtalent.databinding.RemoteData;

/**
 *  Die Schaltbefehle sollen einem eigenen Thread erfolgen um Exception
 *  'android.os.NetworkOnMainThreadException' zu vermeiden.
 *  Exception wurde geworfen beim Ausfuehren der Schreibfunktion
 *  ueber den SocketOutputStream in der Tinkerforge API IPBaseConnection
 */
public class SwitchSocketUseCase
{
    // Interface des SwitchSocket Listeners (informiert ueber den eigentlichen Schaltvorgang)
    // Instancen @see MainActivity
    public interface SwitchSocketListener
    {
        void onSwitchSuccess(boolean switchState);

        void onSwitchFailed(String message);
    }

    private final FakeDataFetcher mFakeDataFetcher;
    private final BackgroundThreadPoster mBackgroundThreadPoster;
    private final UiThreadPoster mUiThreadPoster;

    // Map zur Aufnahme aller Connectionlistener
    private final Set<SwitchSocketListener> mListeners = Collections.newSetFromMap(
            new ConcurrentHashMap<SwitchSocketListener, Boolean>());

    /**
     * Konstruktion
     * @param fakeDataFetcher
     * @param backgroundThreadPoster
     * @param uiThreadPoster
     */
    public SwitchSocketUseCase(FakeDataFetcher fakeDataFetcher,
                               BackgroundThreadPoster backgroundThreadPoster,
                               UiThreadPoster uiThreadPoster)
    {
        mFakeDataFetcher = fakeDataFetcher;
        mBackgroundThreadPoster = backgroundThreadPoster;
        mUiThreadPoster = uiThreadPoster;
    }

    public void registerListener(SwitchSocketListener listener)
    {
        mListeners.add(listener);
    }

    public void unregisterListener(SwitchSocketListener listener)
    {
        mListeners.remove(listener);
    }

    public void switchRemoteSocket(final BrickletRemoteSwitch remoteSwitch, final RemoteData remoteData, final boolean switchState)
    {
        // offload work to background thread
        mBackgroundThreadPoster.post(new Runnable()
        {
            @Override
            public void run()
            {
                doSwitchRemoteSocket(remoteSwitch, remoteData, switchState);
            }
        });
    }

    @WorkerThread
    private void doSwitchRemoteSocket(BrickletRemoteSwitch remoteSwitch, RemoteData remoteData, final boolean switchState)
    {
        try
        {
            mFakeDataFetcher.switchRemoteSocket(remoteSwitch, remoteData, switchState);

            mUiThreadPoster.post(new Runnable()
            { // notify listeners on UI thread
                @Override
                public void run()
                {
                    notifySuccess(switchState);
                }
            });
        } catch (final FakeDataFetcher.SwitchRemoteException e)
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
        for (SwitchSocketListener listener : mListeners)
        {
            listener.onSwitchFailed(message);
        }
    }

    // Listener ueber den erfolgreichen Verbindungsaufbau informieren
    // @see MainActivity
    @UiThread
    private void notifySuccess(boolean switchState)
    {
        for (SwitchSocketListener listener : mListeners)
        {
            listener.onSwitchSuccess(switchState);
        }
    }

}

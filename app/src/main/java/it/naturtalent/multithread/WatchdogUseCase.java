package it.naturtalent.multithread;


import androidx.annotation.UiThread;
import androidx.annotation.WorkerThread;

import com.techyourchance.threadposter.BackgroundThreadPoster;
import com.techyourchance.threadposter.UiThreadPoster;
import com.tinkerforge.IPConnection;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class WatchdogUseCase
{

    // Interface des Wd Listeners
    public interface WatchdogListener
    {
        // Uerwachungszeit ist abgelaufen
        void onWatchdogTimeout(String message);
    }

    private final FakeDataFetcher mFakeDataFetcher;
    private final BackgroundThreadPoster mBackgroundThreadPoster;
    private final UiThreadPoster mUiThreadPoster;

    // Mao zur Aufnahme aller Watchdoglistener
    private final Set<WatchdogListener> mListeners = Collections.newSetFromMap(
            new ConcurrentHashMap<WatchdogListener, Boolean>());

    /**
     * Konstruktion
     * @param fakeDataFetcher
     * @param backgroundThreadPoster
     * @param uiThreadPoster
     */
    public WatchdogUseCase(FakeDataFetcher fakeDataFetcher,
                           BackgroundThreadPoster backgroundThreadPoster,
                           UiThreadPoster uiThreadPoster)
    {
        mFakeDataFetcher = fakeDataFetcher;
        mBackgroundThreadPoster = backgroundThreadPoster;
        mUiThreadPoster = uiThreadPoster;
    }

    public void registerListener(WatchdogListener listener)
    {
        mListeners.add(listener);
    }

    public void unregisterListener(WatchdogListener listener)
    {
        mListeners.remove(listener);
    }

    public void startTimer(final int millisec)
    {
        // offload work to background thread
        mBackgroundThreadPoster.post(new Runnable()
        {
            @Override
            public void run()
            {
                timer(millisec);
            }
        });
    }

    @WorkerThread
    private void timer(int millisec)
    {
        try
        {
            Thread.sleep(millisec);

            mUiThreadPoster.post(new Runnable()
            { // notify listeners on UI thread
                @Override
                public void run()
                {
                    notifySuccess();
                }
            });

        } catch (InterruptedException E)
        {
            mUiThreadPoster.post(new Runnable()
            { // notify listeners on UI thread
                @Override
                public void run()
                {
                    notifyFailure();
                }
            });
        }
    }

    // Listener ueber Watchdog Error informieren
    @UiThread
    public void notifyFailure()
    {
        for (WatchdogListener listener : mListeners)
        {
            listener.onWatchdogTimeout("error watchdog");
        }
    }

    // Watchdog Listener ueber den Ablauf des Timers informieren
    @UiThread
    private void notifySuccess()
    {
        for (WatchdogListener listener : mListeners)
        {
            listener.onWatchdogTimeout("timeout");
        }
    }

}

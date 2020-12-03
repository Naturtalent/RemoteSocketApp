package it.naturtalent.multithread;

import android.app.Application;

import com.techyourchance.threadposter.BackgroundThreadPoster;
import com.techyourchance.threadposter.UiThreadPoster;

import it.naturtalent.multithread.FakeDataFetcher;
import it.naturtalent.multithread.FetchDataUseCase;

public class ThreadPool extends Application {

    /*
      IMPORTANT:
      Both BackgroundThreadPoster and UiThreadPoster should be global objects (single instance).
     */
    private final BackgroundThreadPoster mBackgroundThreadPoster = new BackgroundThreadPoster();
    private final UiThreadPoster mUiThreadPoster = new UiThreadPoster();

    private final FakeDataFetcher mFakeDataFetcher = new FakeDataFetcher();
    private final FetchDataUseCase mFetchDataUseCase =
            new FetchDataUseCase(mFakeDataFetcher, mBackgroundThreadPoster, mUiThreadPoster);

    public FetchDataUseCase getFetchDataUseCase() {
        return mFetchDataUseCase;
    }
}

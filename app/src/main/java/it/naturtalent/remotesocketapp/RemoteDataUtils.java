package it.naturtalent.remotesocketapp;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.ListFragment;

import java.util.List;

import it.naturtalent.databinding.RemoteData;
import it.naturtalent.multithread.FetchDataUseCase;
import it.naturtalent.multithread.PushDataUseCase;
import it.naturtalent.multithread.ThreadPool;

public class RemoteDataUtils
{



    private Context context;
    // private SocketAdapter socketAdapter;
    private ListFragment listFragment;

    private ProgressDialog mProgressBar;

    private Fragment fragment;

    private FragmentActivity fragmentActivity;


    // ThreadPool Parameter
    private static FetchDataUseCase mFetchDataUseCase;
    private InitializeDialogFragment dialogFragment;
    protected static FetchDataUseCase.Listener threadPoolListener;

    private static PushDataUseCase mPushDataUseCase;
    protected static PushDataUseCase.Listener threadPushPoolListener;


    // die gespeicherten Daten
    //private static List<RemoteData> socketData;


    /*
        Listener dient nur dazau den LoadData-Dialog wieder zu beenden sowohl
        bei erfolgreichem Ladevorgang als auch im Abbruchsfall.
     */
    private FetchDataUseCase.Listener ctrlDialogListener = new FetchDataUseCase.Listener()
    {
        @Override
        public void onDataFetched(List<RemoteData> data)
        {
            dialogFragment.dismiss();
        }

        @Override
        public void onDataFetchFailed()
        {
            dialogFragment.dismiss();
        }
    };

    private PushDataUseCase.Listener ctrlPushDialogListener = new PushDataUseCase.Listener()
    {
        @Override
        public void onDataPushed(List<RemoteData> data)
        {
            dialogFragment.dismiss();
        }

        @Override
        public void onDataPushFailed()
        {
            dialogFragment.dismiss();
        }
   };


    /*
        Konstruktion

        Der uebergebene Listener 'threadPoolListerner' liefert die Daten mit seiner
        onDataFetched(List<RemoteData> data) - Funktion
     */
    public RemoteDataUtils(Fragment fragment, FetchDataUseCase.Listener threadPoolListener)
    {
        this.fragment = fragment;
        this.threadPoolListener = threadPoolListener;
    }

    public RemoteDataUtils(FragmentActivity fragmentActivity, PushDataUseCase.Listener threadPushPoolListener)
    {
        this.fragment = fragment;
        this.threadPushPoolListener = threadPushPoolListener;
        this.fragmentActivity = fragmentActivity;
    }


    /*
        interne Klasse realisiert den AlertDialog (Anzeige waehrend des Ladevorgangs)
     */
    public static class InitializeDialogFragment extends DialogFragment
    {
        // Dialog instanziieren (s. showDialog())
        public static InitializeDialogFragment newInstance(int title)
        {
            InitializeDialogFragment frag = new InitializeDialogFragment();
            Bundle args = new Bundle();
            args.putInt("title", title);
            frag.setArguments(args);
            return frag;
        }

        /**
         * Ein Alert-Dialog erzeugen und waehrend des Datenladevorgangs angezeigen.
         *
         * @param savedInstanceState
         * @return
         */
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState)
        {
            int title = getArguments().getInt("title");
            return new AlertDialog.Builder(getActivity())
                    .setIcon(R.drawable.alert_dialog_dart_icon)
                    .setTitle(title)
                    .setView(R.layout.custom_alert)
                    .setCancelable(true)
                    .setNegativeButton(R.string.load_dialog_cancel,
                            new DialogInterface.OnClickListener()
                            {
                                // Listener meldet den Abbruch des Ladevorgangs
                                public void onClick(DialogInterface dialog, int whichButton)
                                {

                                    //it.naturtalent.common.logger.Log.i("FragmentAlertDialog", "Negative click!");
                                    android.util.Log.i("FragmentAlertDialog", "Abbruch!");

                                    // verhindert, dass der Ladethread Daten liefert
                                    mFetchDataUseCase.unregisterListener(threadPoolListener);

                                    // dieses DialogFragment wird geschlossen
                                    mFetchDataUseCase.notifyFailure();
                                }
                            })
                    .create();
        }
    }


    /**
     * Laden der gespeicherten Daten mit dem ThradPool Tool. Der Ladevorgang wird einem
     * separaten Thread ausgefuehrt, deshalb gibt diese Funktion auch keine Daten zurueck sondern
     * ein Listerner informiert ueber den abgeschlossenen Ladevorgang und liefert die Daten.
     * Fuer die Dauer des Ladevorgangs wird ein Alert-Dialog eingeblendet mit die Aktion auch
     * gecancelt werden kann.
     */
    public void loadSocketData()
    {
        mFetchDataUseCase = new ThreadPool().getFetchDataUseCase();

        // den dialogabschalte Listener (s.o.) registrieren
        mFetchDataUseCase.registerListener(ctrlDialogListener);

        // Listener mit dem das ThredPool das Ende des Ladevorgangs meldet registrieren
        mFetchDataUseCase.registerListener(threadPoolListener);

        // die eigentliche Ladefunktion in einem eigenen Thread starten starten
        mFetchDataUseCase.fetchData();

        // Dialog fuer die Dauer des Lade-Threads zeigen
        showDialog();
    }

    public void saveSocketData(SocketViewAdapter mAdapter)
    {
        PushDataUseCase mPushDataUseCase =  new ThreadPool().getPushDataUseCase();


        //den dialogabschalte Listener (s.o.) registrieren
        mPushDataUseCase.registerListener(ctrlPushDialogListener);

        /*

        // Listener mit dem das ThredPool das Ende des Ladevorgangs meldet registrieren
        mFetchDataUseCase.registerListener(threadPoolListener);

         */

        // die eigentliche Speicherfunktion in einem eigenen Thread starten starten
        mPushDataUseCase.pushData(mAdapter);


        // Dialog fuer die Dauer des Speicher-Threads zeigen
        showSaveDialog();
    }


    private void showDialog()
    {
        dialogFragment = InitializeDialogFragment.newInstance(R.string.alert_dialog_loaddata_title);
        dialogFragment.show(fragment.getActivity().getSupportFragmentManager(), "dialog");
    }

    private void showSaveDialog()
    {
        android.util.Log.i("FragmentAlertDialog", "Dialog Speichern zeigen");
        dialogFragment = InitializeDialogFragment.newInstance(R.string.alert_dialog_savedata_title);

        dialogFragment.show(fragmentActivity.getSupportFragmentManager(), "dialog");

    }



}

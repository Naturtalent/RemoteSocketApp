package it.naturtalent.remotesocketapp;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.ListFragment;

import java.util.ArrayList;
import java.util.List;

import it.naturtalent.databinding.RemoteData;
import it.naturtalent.multithread.FetchDataUseCase;
import it.naturtalent.multithread.ThreadPool;

public class RemoteDataUtils
{

    private Context context;
   // private SocketAdapter socketAdapter;
    private ListFragment listFragment;

    private ProgressDialog mProgressBar;

    private Fragment fragment;

    // ThreadPool Parameter
    private static FetchDataUseCase mFetchDataUseCase;
    private InitializeDialogFragment dialogFragment;
    private FetchDataUseCase.Listener threadPoolListerner;

    // die gespeicherten Daten
    private static List<RemoteData> socketData;



    // Listener dient dazau den LoadData-Dialog wieder zu beenden
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


    /*
        Konstruktion
     */
    public RemoteDataUtils(Fragment fragment, FetchDataUseCase.Listener threadPoolListerner)
    {
        this.fragment = fragment;
        this.threadPoolListerner = threadPoolListerner;
    }

    /*
        interne Klasse Lade-Dialog (Anzeige waehrend des Ladevorgangs)
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

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState)
        {
            int title = getArguments().getInt("title");

            return new AlertDialog.Builder(getActivity())
                    .setIcon(R.drawable.alert_dialog_dart_icon)
                    .setTitle(title)
                    .setCancelable(true)
                    .setNegativeButton(R.string.load_dialog_cancel,
                            new DialogInterface.OnClickListener()
                            {
                                public void onClick(DialogInterface dialog,
                                                    int whichButton)
                                {
                                    //it.naturtalent.common.logger.Log.i("FragmentAlertDialog", "Negative click!");
                                    android.util.Log.i("FragmentAlertDialog", "Negative click!");
                                    //dialog.dismiss();
                                }
                            })
                    .create();
        }
    }



    /*
        Laden der gespeicherten Daten mit dem ThradPool Tool

     */
    public List<RemoteData> getSocketData()
    {
        if(socketData == null)
        {
            mFetchDataUseCase = new ThreadPool().getFetchDataUseCase();

            // den dialogabschalte Listener (s.o.) registrieren
            mFetchDataUseCase.registerListener(ctrlDialogListener);

            // Listener mit dem das ThredPool das Ende des Ladevorgangs meldet registrieren
            mFetchDataUseCase.registerListener(threadPoolListerner);

            // die eigentliche Ladefunktion starten
            mFetchDataUseCase.fetchData();

            // Dialog fuer die Dauer des Ladevorgangs
            showDialog();
        }

        return socketData;
    }

    private void showDialog()
    {
        dialogFragment = InitializeDialogFragment.newInstance(R.string.alert_dialog_loaddata_title);
        dialogFragment.show(fragment.getActivity().getSupportFragmentManager(), "dialog");
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

    /*
    public List<RemoteData> getRemoteDateList()
    {
        return remoteDateList;
    }

     */


}

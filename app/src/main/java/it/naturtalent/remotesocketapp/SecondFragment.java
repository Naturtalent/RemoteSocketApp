package it.naturtalent.remotesocketapp;


import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import it.naturtalent.databinding.RemoteData;
import it.naturtalent.multithread.FakeDataFetcher;
import it.naturtalent.multithread.FetchDataUseCase;
import it.naturtalent.multithread.PushDataUseCase;

/*
    Diese Klasse implementiert einen 'FetchDataUseCase.Listener'.
 */
public class SecondFragment extends Fragment implements FetchDataUseCase.Listener
{
    private static final String TAG = "SecondFragment";
    private View rootView;
    private SocketViewAdapter mAdapter;
    private RecyclerView  recyclerView;

    // Modell fuer den Datenaustausch zwischen Fragmenten
    private ViewRemoteDataModel remoteDataModel;

    //private RemoteData selectedRemoteData;
    private int selectedIDX = 0;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // FRagmentResultListener meldet 'ADD'
        getParentFragmentManager().setFragmentResultListener("storeSocketKey", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle bundle)
            {
                if((mAdapter.getmDataSet() != null) && (mAdapter.getmDataSet().size() > 1))
                {
                    android.util.Log.d("SecondFragment", "recieve addRequestKey");

                    PushDataUseCase.Listener threadPushPoolListener = new PushDataUseCase.Listener()
                    {
                        @Override
                        public void onDataPushed(List<RemoteData> data)
                        {
                            android.util.Log.d("EditSocketDialog", "erfolgreich gespeichert");
                        }

                        @Override
                        public void onDataPushFailed()
                        {

                        }
                    };

                    RemoteDataUtils remoteDataUtils = new RemoteDataUtils(getActivity(), threadPushPoolListener);
                    remoteDataUtils.saveSocketData(mAdapter);

                }
            }
        });

        // FRagmentResultListener meldet 'ADD'
        getParentFragmentManager().setFragmentResultListener("addSocketKey", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle bundle)
            {
                if((mAdapter.getmDataSet() != null) && (mAdapter.getmDataSet().size() > 1))
                {
                    //android.util.Log.d("SecondFragment", "recieve addRequestKey");

                    // Editfragment oeffnen
                    NavHostFragment.findNavController(SecondFragment.this)
                            .navigate(R.id.action_SecondFragment_to_socketFragment);
                }
            }
        });

        // FragmentResultListener zur Meldung einer 'EDIT' Aktion vorbereiten
        getParentFragmentManager().setFragmentResultListener("editSocketKey", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle bundle)
            {
                if((mAdapter.getmDataSet() != null) && (mAdapter.getmDataSet().size() > 1))
                {
                    // FragmentResultListener meldet 'EDIT' Aktion
                    android.util.Log.d("SecondFragment", "Edit Aktion  'editSocketKey'");

                    //RemoteData remoteSocket = mAdapter.getmDataSet().get(mAdapter.getSelectedPos());

                    // einen EditDialog erzeugen, anzeigen und Datensatz editieren
                    DialogFragment dialogFragment = EditSocketDialog.newInstance(R.string.title_dialog_edit);
                    dialogFragment.show(getActivity().getSupportFragmentManager(),"dialog");


                    /*
                    // Editfragment oeffnen
                    NavHostFragment.findNavController(SecondFragment.this)
                            .navigate(R.id.action_SecondFragment_to_socketFragment);
                     */
                }
            }
        });

        // FragmentResultListener zur Meldung einer 'DELETE' Aktion vorbereiten
        getParentFragmentManager().setFragmentResultListener("deleteSocketKey", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle bundle) {

                selectedIDX = mAdapter.getSelectedPos();
                if(selectedIDX > 0)
                {
                    // FragmentResultListener meldet 'DELETE' Aktion
                    new AlertDialog.Builder(getActivity())
                            .setIcon(R.drawable.delete_icon_gray)
                            .setTitle(R.string.title_dialog_delete)
                            .setCancelable(true)
                            .setPositiveButton(R.string.alert_dialog_ok,
                                    new DialogInterface.OnClickListener()
                                    {
                                        public void onClick(DialogInterface dialog,
                                                            int whichButton)
                                        {
                                            // den selektierten Eintrag loeschen
                                            mAdapter.notifyItemRemoved(selectedIDX);
                                            mAdapter.notifyItemChanged(selectedIDX-1);
                                            //mAdapter.setSelectedPos(0);
                                        }
                                    })
                            .setNegativeButton(R.string.alert_dialog_cancel,
                                    new DialogInterface.OnClickListener()
                                    {
                                        public void onClick(DialogInterface dialog,
                                                            int whichButton)
                                        {
                                            // do nothing
                                        }
                                    })
                            .create().show();
                }
            }
        });

    }



    @Override
    public View onCreateView(
            LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState)
    {
        // Wurzelview mit dem Layout dieses Fragments
        rootView = inflater.inflate(R.layout.fragment_second, container, false);

        // RecycleView-Adapter erzeugen
        mAdapter = new SocketViewAdapter(null);
        mAdapter.setClickInterface(new SocketViewAdapter.ClickInterface()
        {
            // Klick erfolgte auf die erste Spalte (Icon)
            // obj1' ist das selektierte Element in der Liste
            @Override
            public void clickEventOne(Object obj)
            {
                //android.util.Log.d("SecondFragment", "Click Icon  "+ ((RemoteData)obj).getName());
            }

            // Klick erfolgte auf die zweite Spalte (Namen)
            // obj1' ist das selektierte Element in der Liste
            // 'obj2' View ('TextView') der selektierten Spalte
            @Override
            public void clickEventTwo(Object obj1, Object obj2)
            {
                //android.util.Log.d("SecondFragment", "Click Socket: "+((RemoteData)obj1).getName());
            }


        });

        // RecycleView-Adapter in das AdapterRemoteDataModel eintragen
        remoteDataModel = new ViewModelProvider(requireActivity()).get(ViewRemoteDataModel.class);
        remoteDataModel.setDataModel(mAdapter);

        // RecyclerView-Adapter in RecyclerView-Fragment einbinden
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);
        recyclerView.setAdapter(mAdapter);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);

        // die Daten abfragen (ggf. den Datenladevorgang starten) - Ergebnis @see onDataFetched(()
        //android.util.Log.d("SecondFragment", "Check Data: "+ mAdapter.getmDataSet());
        RemoteDataUtils remoteDataUtils = new RemoteDataUtils(this, this);
        remoteDataUtils.loadSocketData();

        android.util.Log.d("SecondFragment", "start loading");

        return rootView;
    }


    /*
        Listener meldet erfogreichen Datenladevorgang

        Die geladenen Daten in einem RecyclerView anzeigen.
     */
    @Override
    public void onDataFetched(List<RemoteData> socketData)
    {
        android.util.Log.d("SecondFragment", "Load Data OK - " + socketData.size()+" Datensaetze");
        mAdapter.setmDataSet(socketData);
        mAdapter.notifyDataSetChanged();
    }


    /*
        Listener meldet erfoglosen Datenladevorgang
     */
    @Override
    public void onDataFetchFailed()
    {

        /*
        // ToDo evtl. Error-Dialog
        Toast.makeText(getContext(), "Fehler beim Lesen der Daten", Toast.LENGTH_SHORT).show();
        NavHostFragment.findNavController(SecondFragment.this).popBackStack();

         */


        Dialog dialogSaveFail = new AlertDialog.Builder(getActivity())
                .setIcon(R.drawable.save)
                .setTitle("Fehler beim Laden")
                //.setView(R.layout.fragment_socket)
                //.setView(view)
                .setCancelable(true)
                .setPositiveButton(R.string.continue_with_default,
                        new DialogInterface.OnClickListener()
                        {
                            // Listener meldet OK Ende des Dialogs
                            public void onClick(DialogInterface dialog, int whichButton)
                            {
                                android.util.Log.i("Loadfailer Dialog", "weiter mit Defaultwerten  ");

                                // die Daten abfragen (ggf. den Datenladevorgang starten) - Ergebnis @see onDataFetched(()
                                //android.util.Log.d("SecondFragment", "Check Data: "+ mAdapter.getmDataSet());


                                List<RemoteData>remoteData = new FakeDataFetcher().getDefaultModel();
                                mAdapter.setmDataSet(remoteData);
                                mAdapter.notifyDataSetChanged();

                            }
                        })
                // nicht speicheern -> weiter
                .setNegativeButton(R.string.dialog_label_continue,
                        new DialogInterface.OnClickListener()
                        {
                            // Listener meldet ABBRUCH Ende des Dialogs
                            public void onClick(DialogInterface dialog, int whichButton)
                            {
                                NavHostFragment.findNavController(SecondFragment.this).popBackStack();
                            }
                        })

                .create();
        dialogSaveFail.show();



    }


}
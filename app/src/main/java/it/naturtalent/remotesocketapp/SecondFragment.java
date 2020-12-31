package it.naturtalent.remotesocketapp;



import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentResultListener;
import androidx.fragment.app.FragmentResultOwner;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import it.naturtalent.databinding.RemoteData;
import it.naturtalent.multithread.FetchDataUseCase;

/*
    Diese Klasse implementiert einen 'FetchDataUseCase.Listener'.
 */
public class SecondFragment extends Fragment implements FetchDataUseCase.Listener
{
    private static final String TAG = "SecondFragment";
    private View rootView;
    private SocketViewAdapter mAdapter;
    private RecyclerView  recyclerView;

    private List<RemoteData> mDataSet;

    //private RemoteData selectedRemoteData;
    private int selectedIDX = 0;

    //private LinearLayout linearLayout;
    //private ViewGroup container;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Fragment parent = getParentFragment();
        FragmentManager fragmentManager = getParentFragmentManager();
        List<Fragment> fragmentList = fragmentManager.getFragments();

        // Listener meldet  FloatingActionButton 'DELETE'
        getParentFragmentManager().setFragmentResultListener("requestKey", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle bundle) {

                selectedIDX = mAdapter.getSelectedPos();
                if(selectedIDX > 0)
                {
                    // Bestaedigungsdialog 'Loeschen'
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
        rootView = inflater.inflate(R.layout.recycler_view_frag, container, false);

        //mAdapter = createAdapter();

        mAdapter = new SocketViewAdapter(null);
        mAdapter.setClickInterface(new SocketViewAdapter.ClickInterface()
        {
            // Klick erfolgte auf die erste Spalte (Icon)
            // obj1' ist das selektierte Element in der Liste
            @Override
            public void clickEventOne(Object obj)
            {
                RemoteData socket = (RemoteData) obj;
                android.util.Log.d("SecondFragment", "Click Icon  "+socket.getName());
            }

            // Klick erfolgte auf die zweite Spalte (Icon)
            // obj1' ist das selektierte Element in der Liste
            // 'obj2' View ('TextView') der selektierten Spalte
            @Override
            public void clickEventTwo(Object obj1, Object obj2)
            {
                //android.util.Log.d("SecondFragment", "Click Socket: "+((RemoteData)obj2).getName());
                /*
                int selectedIDX = mAdapter.getSelectedPos();
                selectedRemoteData = (RemoteData) obj1;
                android.util.Log.d("SecondFragment", "Socket: "+selectedRemoteData.getName()+" Index: "+selectedIDX);

                 */
            }


        });

        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);
        recyclerView.setAdapter(mAdapter);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);

        mDataSet = new ArrayList<>();
        RemoteData socket = new RemoteData("New Schalter", "type", "Code", "RemoteCode");
        mDataSet.add(socket);
        mAdapter.setmDataSet(mDataSet);

        // die Daten abfragen (ggf. den Datenladevorgang starten)
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
        android.util.Log.i("Datenladevorgang", "Datenladevorgang erfolglos");
    }


}
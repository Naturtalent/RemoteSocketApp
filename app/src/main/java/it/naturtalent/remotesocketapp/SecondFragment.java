package it.naturtalent.remotesocketapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import it.naturtalent.databinding.RemoteData;
import it.naturtalent.multithread.FetchDataUseCase;

public class SecondFragment extends Fragment implements FetchDataUseCase.Listener
{
    private static final String TAG = "SecondFragment";
    private View rootView;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    )
    {
        // rootView =  inflater.inflate(R.layout.fragment_second, container, false);
        rootView =  inflater.inflate(R.layout.recycler_view_frag, container, false);
        rootView.setTag(TAG);

        //rootView = inflater.inflate(R.layout.recycler_view_activity, container, false);

        // Daten werden geladen
        RemoteDataUtils remoteDataUtils = new RemoteDataUtils(this, this );
        remoteDataUtils.getSocketData();

        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_second, container, false);
        return rootView;
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        /*
        view.findViewById(R.id.button_second).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                NavHostFragment.findNavController(SecondFragment.this)
                        .navigate(R.id.action_SecondFragment_to_FirstFragment);
            }
        });
        */

    }

    /*
        Listener meldet erfogreichen Datenladevorgang

        Die geladenen Daten in einem RecyclerView anzeigen.
     */
    @Override
    public void onDataFetched(List<RemoteData> socketData)
    {
        android.util.Log.d("ThreadPoster", "Load Data OK - " + socketData.size()+" Datensaetze");

        // Adapter und Listener erzeugen (wird vom RecyclerView benoetigt)
        SocketViewAdapter mAdapter = new SocketViewAdapter(socketData);
        mAdapter.setClickInterface(new SocketViewAdapter.ClickInterface()
        {
            @Override
            public void clickEventOne(Object obj)
            {
                RemoteData socket = (RemoteData) obj;
                android.util.Log.d("SecondFragment", "Click Icon"+socket.getName());
            }

            @Override
            public void clickEventTwo(Object obj1, Object obj2)
            {
                //android.util.Log.d("SecondFragment", "Click Socket: "+((RemoteData)obj2).getName());
                RemoteData socket = (RemoteData) obj1;
                android.util.Log.d("SecondFragment", "Socket: "+socket.getName());
            }
        });

        // Adapter dem View hinzufuegen
        RecyclerView  mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);
        mRecyclerView.setAdapter(mAdapter);

        //mLayoutManager = new GridLayoutManager(getActivity(), SPAN_COUNT);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        // mCurrentLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER;
        mRecyclerView.setLayoutManager(mLayoutManager);


    }

     /*
        Listener meldet erfoglosen Datenladevorgang
     */
    @Override
    public void onDataFetchFailed()
    {

    }
}
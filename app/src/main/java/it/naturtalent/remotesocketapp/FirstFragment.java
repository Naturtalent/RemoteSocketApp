package it.naturtalent.remotesocketapp;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.navigation.fragment.NavHostFragment;

public class FirstFragment extends Fragment
{

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // fragmentspezifische Items hinzufuegen @see onCreateOptionsMenu
        setHasOptionsMenu(false);  // momentan unwirksam

        // FloatingActionButton-Listener meldet  FloatingActionButton 'ADD'
        getParentFragmentManager().setFragmentResultListener("addSocketKey", this, new FragmentResultListener()
        {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle bundle)
            {
                new AlertDialog.Builder(getActivity())
                        .setIcon(R.drawable.add_icon)
                        .setTitle(R.string.title_dialog_add)
                        .setCancelable(true)
                        .setPositiveButton(R.string.alert_dialog_ok,
                                new DialogInterface.OnClickListener()
                                {
                                    public void onClick(DialogInterface dialog,
                                                        int whichButton)
                                    {
                                        // den selektierten Eintrag loeschen

                                        //mAdapter.setSelectedPos(0);
                                    }
                                })
                        .create().show();

            }
        });

        // FloatingActionButton-Listener meldet  FloatingActionButton 'DELETE'
        getParentFragmentManager().setFragmentResultListener("deleteSocketKey", this, new FragmentResultListener()
        {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle bundle)
            {
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

                                        //mAdapter.setSelectedPos(0);
                                    }
                                })
                        .create().show();

            }
        });

    }

    /*
        Fragmentspezifische Menuitems
        Momentan unwirksam
     */
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater)
    {
        inflater.inflate(R.menu.menu_main, menu);
        //super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    )
    {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_first, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        // OnClickListener des Button navigiert zu SecondFragment
        view.findViewById(R.id.button_first).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                NavHostFragment.findNavController(FirstFragment.this)
                        .navigate(R.id.action_FirstFragment_to_SecondFragment);
            }
        });


        // FragmentResultListener meldet 'EDIT'
        getParentFragmentManager().setFragmentResultListener("editSocketKey", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle bundle)
            {
               android.util.Log.d("FirstFragment", "Edit Toolbar-Action");
             }
        });



    }
}
package it.naturtalent.remotesocketapp;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

import it.naturtalent.databinding.RemoteData;

public class EditSocketDialog  extends DialogFragment implements AdapterView.OnItemSelectedListener
{
    public static String TAG = "EditSocketDialog";

    private String[] users = { "A", "B", "C"};

    private Object alertDialog;

    // Dialog instanziieren (s. showDialog())
    public static EditSocketDialog newInstance(int title)
    {
        EditSocketDialog dialog = new  EditSocketDialog();
        Bundle args = new Bundle();
        args.putInt("title", title);
        dialog.setArguments(args);
        return dialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState)
    {
        Dialog dialog;

        LayoutInflater layoutInflater = onGetLayoutInflater(savedInstanceState);
        View view = layoutInflater.inflate(R.layout.fragment_socket, null);

        int title = getArguments().getInt("title");
        dialog = new AlertDialog.Builder(getActivity())
                .setIcon(R.drawable.alert_dialog_dart_icon)
                .setTitle(title)
                //.setView(R.layout.fragment_socket)
                .setView(view)
                .setCancelable(true)
                .setPositiveButton(R.string.load_dialog_ok,
                        new DialogInterface.OnClickListener()
                        {
                            // Listener meldet den Abbruch des Ladevorgangs
                            public void onClick(DialogInterface dialog, int whichButton)
                            {

                                //it.naturtalent.common.logger.Log.i("FragmentAlertDialog", "Negative click!");
                                android.util.Log.i("FragmentAlertDialog", "Abbruch!");

                                // verhindert, dass der Ladethread Daten liefert
                                //mFetchDataUseCase.unregisterListener(threadPoolListener);

                                // dieses DialogFragment wird geschlossen
                                //mFetchDataUseCase.notifyFailure();
                            }
                        })
                .setNegativeButton(R.string.load_dialog_cancel,
                        new DialogInterface.OnClickListener()
                        {
                            // Listener meldet den Abbruch des Ladevorgangs
                            public void onClick(DialogInterface dialog, int whichButton)
                            {

                                //it.naturtalent.common.logger.Log.i("FragmentAlertDialog", "Negative click!");
                                android.util.Log.i("FragmentAlertDialog", "Abbruch!");

                                // verhindert, dass der Ladethread Daten liefert
                                //mFetchDataUseCase.unregisterListener(threadPoolListener);

                                // dieses DialogFragment wird geschlossen
                                //mFetchDataUseCase.notifyFailure();
                            }
                        })

                .create();

        initialize(view);

        return dialog;

    }


    private void initialize(View view)
    {
        ViewRemoteDataModel viewRemoteDataModel = new ViewModelProvider(requireActivity()).get(ViewRemoteDataModel.class);
        SocketViewAdapter mAdapter = viewRemoteDataModel.getDataModel().getValue();

        // die Eingqabefelder explizit
        TextInputEditText editorName = view.findViewById(R.id.edit_socket_name);
        TextInputEditText editorHouse = view.findViewById(R.id.edit_socket_housecode);
        TextInputEditText editorRemote = view.findViewById(R.id.edit_socket_remotecode);
        Spinner spin = (Spinner) view.findViewById(R.id.type_spinner);

        if(mAdapter.getSelectedPos() >= 0)
        {
            // der selektierte Datensatz
            RemoteData remoteData = mAdapter.getmDataSet().get(mAdapter.getSelectedPos());

            editorName.setText(remoteData.getName());
            editorHouse.setText(remoteData.getHouseCode());
            editorRemote.setText(remoteData.getRemoteCode());

            List<String> values = new ArrayList<>();
            values.add(remoteData.getType());
            for(String type : users)
                if(!values.contains(type))
                    values.add(type);

            users = values.toArray(new String[values.size()]);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, users);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spin.setAdapter(adapter);
            spin.setOnItemSelectedListener(this);


        }
        else
        {
            // neuer Datensatz
            editorName.setText("neuer Schalter");
            editorHouse.setText("1");
            editorRemote.setText("A");
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
    {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView)
    {

    }
}

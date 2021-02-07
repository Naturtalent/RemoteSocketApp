package it.naturtalent.remotesocketapp;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.InverseBindingListener;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.TextInputEditText;

import it.naturtalent.databinding.RemoteData;
import it.naturtalent.remotesocketapp.databinding.FragmentSocketBinding;

/**
 *  Konstruktion
 *  implementiert Listener fuer Socket-Type Spinner
 */
public class EditSocketDialog  extends DialogFragment implements AdapterView.OnItemSelectedListener, TextInputEditText.OnEditorActionListener
{
    public static String TAG = "EditSocketDialog";

    private SocketViewAdapter mAdapter;

    // Klasse entsteht durch '<data>' - Definition in 'fragment_socket.xml'
    private FragmentSocketBinding binding;

    // Datensatz in Bearbeitung
    private RemoteData remoteData;

    // eine Kopie zur Wiederherstellung des Orginals
    private RemoteData remoteDataClone;

    private InverseBindingListener inverseBindingListener;

    /**
     * Konstruktion
     * @param
     */
    /*
    public EditSocketDialog(SocketViewAdapter mAdapter)
    {
        this.mAdapter = mAdapter;
    }
     */

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
        //View view = layoutInflater.inflate(R.layout.fragment_socket, null);

        binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_socket, null, false);
        View view = binding.getRoot();

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
                            // Listener meldet OK Ende des Dialogs
                            public void onClick(DialogInterface dialog, int whichButton)
                            {

                                android.util.Log.i("FragmentAlertDialog", "Ok!  ");

                                // den editierten Eintrag in der Liste anzeigen
                                mAdapter.notifyItemChanged(mAdapter.getSelectedPos());
                            }
                        })
                .setNegativeButton(R.string.load_dialog_cancel,
                        new DialogInterface.OnClickListener()
                        {
                            // Listener meldet ABBRUCH Ende des Dialogs
                            public void onClick(DialogInterface dialog, int whichButton)
                            {

                                //it.naturtalent.common.logger.Log.i("FragmentAlertDialog", "Negative click!");
                                android.util.Log.i("FragmentAlertDialog", "Abbruch!");

                                // bei Abbruch den urspruenglichen Datensatz zurueckspeichern
                                mAdapter.getmDataSet().set(mAdapter.getSelectedPos(), remoteDataClone);
                                mAdapter.notifyItemChanged(mAdapter.getSelectedPos());
                            }
                        })

                .create();

        initialize(view);

        return dialog;

    }

    private void initialize(View view)
    {
        ViewRemoteDataModel viewRemoteDataModel = new ViewModelProvider(requireActivity()).get(ViewRemoteDataModel.class);
        mAdapter = viewRemoteDataModel.getDataModel().getValue();

        if (mAdapter.getSelectedPos() >= 0)
        {
            remoteData = mAdapter.getmDataSet().get(mAdapter.getSelectedPos());
            remoteDataClone = (RemoteData) remoteData.clone();
            binding.setRemotesocket(remoteData);

            // Spinner fuer Socket-TYPE
            Spinner spinner = (Spinner) view.findViewById(R.id.type_spinner);

            // Create an ArrayAdapter using the string array and a default spinner layout
            // arg: android.R.layout.simple_spinner_item = default-Layout zur Darstellung der gewählten Auswahl
            // die SpinnerItems sind in 'R.array.sockettype_array'
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                    R.array.sockettype_array, android.R.layout.simple_spinner_item);
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            // Apply the adapter to the spinner
            spinner.setAdapter(adapter);
            // Listener (this) informiert ueber Typeselektion
            spinner.setOnItemSelectedListener(this);

            // Spinner aktualisieren (Abgleich mit Modell-Daten)
            int pos = getTypeSpinnerPositionInAdapter(adapter, remoteData);
            if(pos >= 0)
                spinner.setSelection(pos);
        }
    }


    // Reaktion auf eine TypeSpinner-Selektion
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long l)
    {
       //android.util.Log.i("EditSocketDialog", "selected Type: "+parent.getItemAtPosition(pos));

       // die Spinnerselektion wird uebernommen
       Object obj = parent.getItemAtPosition(pos);
       if((obj instanceof String) && (remoteData != null))
       {
           remoteData.setType((String) obj);
           mAdapter.getmDataSet().set(mAdapter.getSelectedPos(), remoteData);
       }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView)
    {

    }

    @Override
    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent)
    {
        return false;
    }

    private static int getTypeSpinnerPositionInAdapter(ArrayAdapter<CharSequence> adapter, RemoteData remoteData)
    {
        if (adapter != null && remoteData != null)
        {
            for (int i = 0; i < adapter.getCount(); i++)
            {
                String adapterType = adapter.getItem(i).toString();
                if (adapterType != null && remoteData.getName() != null && adapterType.equals(remoteData.getType()))
                {
                    return i;
                }
            }
        }
        return (-1);
    }

    /*
    @BindingAdapter(value = {"android:type", "android:typeAttrChanged"}, requireAll = false)
    public static void bindPlanetSelected(final AppCompatSpinner spinner, RemoteData remoteDataByViewModel,
                                          final InverseBindingListener inverseBindingListener)
    {
        android.util.Log.i("EditSocketDialog", "BindingAdapter: "+spinner.getSelectedItem());
    }

 */




/*
    @InverseBindingAdapter(attribute = "android:type", event = "android:typeAttrChanged")
    public static RemoteData captureSelectedType(AppCompatSpinner spinner)
    {
        android.util.Log.i("EditSocketDialog", "InverseBindingAdapter: "+spinner.getSelectedItem());
        return (RemoteData) spinner.getSelectedItem();
    }

 */

}

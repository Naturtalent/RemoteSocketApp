package it.naturtalent.remotesocketapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.NavGraph;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

import it.naturtalent.databinding.RemoteData;
import it.naturtalent.multithread.FetchDataUseCase;
import it.naturtalent.multithread.PushDataUseCase;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SocketFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SocketFragment extends Fragment implements AdapterView.OnItemSelectedListener
{

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private SocketViewAdapter mAdapter;

    public SocketFragment()
    {
        // Required empty public constructor
        android.util.Log.d("SocketFragment", "Standard Konstrukt");
    }

    public SocketFragment(Bundle bundle)
    {
        // Required empty public constructor
        android.util.Log.d("SocketFragment", "Bundle Konstrukt");
    }

    private PushDataUseCase.Listener ctrlPushDialogListener = new PushDataUseCase.Listener()
    {
        @Override
        public void onDataPushed(List<RemoteData> data)
        {

        }

        @Override
        public void onDataPushFailed()
        {

        }
    };

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SocketFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SocketFragment newInstance(String param1, String param2)
    {
        SocketFragment fragment = new SocketFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);



        if (getArguments() != null)
        {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    String[] users = { "A", "B", "C"};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_socket, container, false);
        initialize(view);

        /*
        // OnClickListener des Save-Button navigiert zurueck
        view.findViewById(R.id.save_button).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                //mAdapter.notifyItemChanged(mAdapter.getSelectedPos());

                RemoteDataUtils remoteDataUtils = new RemoteDataUtils(SocketFragment.this, ctrlPushDialogListener);
                remoteDataUtils.saveSocketData();
                android.util.Log.d("SecondFragment", "start save");

                NavHostFragment.findNavController(SocketFragment.this)
                        .popBackStack();

            }
        });

         */

        /*
        // OnClickListener des Cancel-Button navigiert zurueck
        view.findViewById(R.id.cancel_button).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                NavHostFragment.findNavController(SocketFragment.this)
                        .popBackStack();
            }
        });

         */


        return view;
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

            List<String>values = new ArrayList<>();
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

    public void setmAdapter(SocketViewAdapter mAdapter)
    {
        this.mAdapter = mAdapter;
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
package it.naturtalent.remotesocketapp;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavAction;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.NavGraph;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.tinkerforge.IPConnection;

import java.util.List;
import java.util.Map;

import it.naturtalent.databinding.RemoteData;
import it.naturtalent.multithread.ConnectionUseCase;
import it.naturtalent.multithread.FetchDataUseCase;
import it.naturtalent.multithread.ThreadPool;
import it.naturtalent.multithread.WatchdogUseCase;


public class MainActivity extends AppCompatActivity  implements FetchDataUseCase.Listener
{
    // UID des Remote Switch Bricklet
    private static final String UID = "v1T";

    public static final int SUB_ACTIVITY_CREATE_USER = 10;

    private static Context context;

    //private DialogFragment connectionDialog;
    private Dialog connectionDialog;

    private IPConnection ipcon;

    // wird zum Zeigen diverse DialogFragmente benoetigt
    public static FragmentManager fragmentManager;

    private WatchdogUseCase.WatchdogListener watchdogListener = new WatchdogUseCase.WatchdogListener()
    {
        @Override
        public void onWatchdogTimeout(String message)
        {
            if(connectionDialog != null)
            {
                connectionDialog.dismiss();
                connectionDialog = null;

                new AlertDialog.Builder(MainActivity.this)
                        .setIcon(R.drawable.alert_dialog_dart_icon)
                        .setTitle("Timeout")
                        .setView(R.layout.custom_alert)
                        .setCancelable(true)
                        .setPositiveButton(R.string.alert_dialog_ok,
                                new DialogInterface.OnClickListener()
                                {
                                    public void onClick(DialogInterface dialog, int whichButton)
                                    {
                                        // tut nichts
                                    }
                                })
                        .create().show();
            }
        }
    };

    /*
        Listener informiert ueber das Ergebnis des WiFi-Verbindungsaufbau
        see ConnectionUseCase
     */
    private ConnectionUseCase.ConnectionListener connectionListener = new ConnectionUseCase.ConnectionListener()
    {
        // WiFi-Verbindung wurde erfolgreich aufgebaut
        @Override
        public void onConnectionEstablished(IPConnection ipcon)
        {
            //android.util.Log.i("MainActivity: ", "Connection hergestellt!");

            MainActivity.this.ipcon = ipcon;

            // Dialogfenster schliessen
            if(connectionDialog != null)
            {
                connectionDialog.dismiss();
                connectionDialog = null;
            }
        }


        // WiFi - Verbindungsaufbau ist gescheitert
        @Override
        public void onConnectionFailed(String message)
        {
            //android.util.Log.i("MainActivity: ", "Fehler beim Connectionaufbau!");

            MainActivity.this.ipcon = null;

            // Dialogfenster schliessen
            if(connectionDialog != null)
            {
                connectionDialog.dismiss();
                connectionDialog = null;
            }

            // Ueber den misslungen Verbindungversuch informieren
            new AlertDialog.Builder(MainActivity.this)
                    .setIcon(R.drawable.delete_icon_gray)
                    .setTitle(message)
                    .setCancelable(true)
                    .setPositiveButton(R.string.alert_dialog_ok,
                            new DialogInterface.OnClickListener()
                            {
                                public void onClick(DialogInterface dialog, int whichButton)
                                {
                                }
                            })
                    .create().show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = getApplicationContext();

        // Toolbar erzeugen und ActionBar ersetzen (Titel aus AndroidManifest)
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.title_toolbar);
        setSupportActionBar(toolbar);

        // FloatingActionButton Reaktion auf 'einschalten' - Klick
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (ipcon != null)
                {
                    // Schaltfunktion anstossen
                    String actionKey = getAppContext().getString(R.string.actionkey_switch_single_on);
                    doFloatingAction(actionKey);
                }
                else
                {
                    // keine WiFi-Verbindung
                    noConnectionDialog();
                }
            }
        });

        // FloatingActionButton Reaktion auf 'ausschalten' - Klick
        FloatingActionButton fabDelete = findViewById(R.id.fab_delete);
        fabDelete.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (ipcon != null)
                {
                    // Schaltfunktion anstossen
                    String actionKey = getAppContext().getString(R.string.actionkey_switch_single_off);
                    doFloatingAction(actionKey);
                }
                else
                {
                    // keine WiFi-Verbindung
                    noConnectionDialog();
                }
            }
        });

        fragmentManager = MainActivity.this.getSupportFragmentManager();
    }

    /**
        Beim Starten eine WiFi Verbindung aufbauen
     */
    @Override
    protected void onStart()
    {
        super.onStart();
        doWiFiConnection();
    }



    /*
            ausfuehren der Schaltfunktion der beiden FloatingActionButton (Ein-/Ausschalten)
            Es wird lediglich eine fragmentübergreifendes Ereignis mit dem 'actionKey' geworfen.
            Die eigentliche physikalische Schaltfunktkion passiert in den jeweiligen Fragmenten.
         */
    private void doFloatingAction(String actionKey)
    {
        // IPConnection kann durch das Speichern in einem ViewModel an die Fragmente
        // kommuniziert werden
        ViewModelProvider viewModelProvider = new ViewModelProvider(MainActivity.this);
        SharedIPConnectionModel ipConnectionModel = viewModelProvider.get(SharedIPConnectionModel.class);
        ipConnectionModel.setIpConnection(ipcon);

        // mit dem 'actionKey' eine Aktion in den Fragmenten porvozieren
        fragmentManager.getFragments().get(0).getChildFragmentManager().setFragmentResult(actionKey, new Bundle());
    }

    /*
        Der Dialog zeigt an, dass keine WiFi - Verbindung besteht (ipcon == null)
     */
    private void noConnectionDialog()
    {
        // keine WiFi-Verbindung
        new AlertDialog.Builder(MainActivity.this)
                .setIcon(R.drawable.delete_icon_gray)
                .setTitle(R.string.alert_dialog_noconnection_title)
                .setCancelable(true)
                .setPositiveButton(R.string.alert_dialog_ok,
                        new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog,
                                                int whichButton)
                            {
                                // den selektierten Eintrag loeschen

                            }

                        })
                .create().show();

    }

    /*
        Das Hauptmenue zur Toolbar hinzufuegen
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items ('settings', 'delete', 'reload' ...) to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    /*
        Toolbar - Aktionen (Add, Edit, ...) werden hier abgefangen und via 'key' und
        FragmentResultListener an andere Fragmente gemeldet.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        FragmentManager fragmentManager = MainActivity.this.getSupportFragmentManager();
        List<Fragment> fragments = fragmentManager.getFragments();

        // mit dem Fragmentmanager und abhaengig won der Selektion werden 'keys' gesendet, die in
        // den Fragmenten mit dem FragmentResultListener abgefragt werden koennen
        switch (item.getItemId())
        {
            case R.id.action_store:
                fragments.get(0).getChildFragmentManager().setFragmentResult("storeSocketKey", new Bundle());
                break;

            case R.id.action_add:
                fragments.get(0).getChildFragmentManager().setFragmentResult("addSocketKey", new Bundle());
                break;

            case R.id.action_delete:
                fragments.get(0).getChildFragmentManager().setFragmentResult("deleteSocketKey", new Bundle());
                break;

            case R.id.action_edit:
                fragments.get(0).getChildFragmentManager().setFragmentResult("editSocketKey", new Bundle());
                break;

            case R.id.action_reload:
                fragments.get(0).getChildFragmentManager().setFragmentResult("reloadSocketKey", new Bundle());
                break;

            // Preaferenzen (Settings) definieren
            case R.id.action_settings:

                getSupportFragmentManager().beginTransaction()
                        .replace(android.R.id.content, new PrefsFragment())
                        //.setReorderingAllowed(true)
                        .addToBackStack("name") // name can be null
                        .commit();
                break;

            // Wifi-Verbindung aufbauen
            case R.id.action_connection:

                android.util.Log.d("MainActivity", "Connection Action");

                ConnectionUseCase mConnectionUseCase =  new ThreadPool().getConnectionUseCase();

                // Listener ueberwacht die Connectionaktion (Dialog 'connectionDialog' ausschalten)
                mConnectionUseCase.registerListener(connectionListener);

                // Verbindungsaufbau im separaten Thread
                mConnectionUseCase.connectWiFi();

                // Dialog fuer die Dauer des Verbindungsversuchs
                showConnectDialog();

                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void doWiFiConnection()
    {
        android.util.Log.d("MainActivity", "Connection Action");

        ConnectionUseCase mConnectionUseCase =  new ThreadPool().getConnectionUseCase();

        // Listener ueberwacht die Connectionaktion (Dialog 'connectionDialog' ausschalten)
        mConnectionUseCase.registerListener(connectionListener);

        // Watchdog begrenzt die Dauer bei Verbindungsaufbau (Timeout error Tinkerforge)
        //WatchdogUseCase mWatchdogCase =  new ThreadPool().getWatchdogCase();
        //mWatchdogCase.registerListener(watchdogListener);
        //mWatchdogCase.startTimer(4000);

        // Verbindungsaufbau im separaten Thread
        mConnectionUseCase.connectWiFi();

        // Dialog fuer die Dauer des Verbindungsversuchs
        showConnectDialog();

    }


    /*
        Der Datenladevorgang wurde beendet, die Daten werden geliefert.
     */
    @Override
    public void onDataFetched(List<RemoteData> socketData)
    {
        android.util.Log.d("MainActivity", "Reload Load Ok");
       // mAdapter.setmDataSet(socketData);
       // mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDataFetchFailed()
    {
        android.util.Log.d("MainActivity", "Load failed");
    }


    public static Context getAppContext() {
        return MainActivity.context;
    }


    /**
     * Dialog wird waehrend des Verbindungsaufbaus gezeigt.
     * Dialog wird vom ConnectListener geschlossen.
     * Dialog wird auch vom Watchdog Timer eschlossen.
     *
     * @return
     */
    private void showConnectDialog()
    {
        //android.util.Log.i("FragmentAlertDialog", "Dialog Speichern zeigen");

        //Watchdog begrenzt die Dauer bei Verbindungsaufbau (Timeout error Tinkerforge)
        WatchdogUseCase mWatchdogCase =  new ThreadPool().getWatchdogCase();
        mWatchdogCase.registerListener(watchdogListener);
        mWatchdogCase.startTimer(4000);

        // der Dialog
        connectionDialog = new AlertDialog.Builder(this)
                .setIcon(R.drawable.alert_dialog_dart_icon)
                .setTitle("WiFi Verbindungsaufbau")
                .setView(R.layout.custom_alert)
                .setCancelable(true)
                .create();

        connectionDialog.show();
    }

    /*
    private DialogFragment showConnectDialog()
    {
        //android.util.Log.i("FragmentAlertDialog", "Dialog Speichern zeigen");

        DialogFragment dialogFragment = RemoteDataUtils.InitializeDialogFragment.newInstance(R.string.alert_dialog_connect_title);
        dialogFragment.show(MainActivity.this.getSupportFragmentManager(), "dialog");
        return dialogFragment;
    }

     */



    /*
    // ein-/ausschalten
    private void doSwitchSocket(boolean switchState)
    {
        if (ipcon != null)
        {
            short switchCode = (switchState) ? BrickletRemoteSwitch.SWITCH_TO_ON : BrickletRemoteSwitch.SWITCH_TO_OFF;
            BrickletRemoteSwitch rs = new BrickletRemoteSwitch(UID, ipcon);

            List<RemoteData> remoteSockets = ((InteractiveArrayAdapter) adapter).getList();
            if ((remoteSockets != null) && (!remoteSockets.isEmpty()))
            {
                for (RemoteData remoteSocket : remoteSockets)
                {
                    if (remoteSocket.isSelected())
                    {
                        // nur die selektierten Sockets werden geschaltet
                        try
                        {
                            rs.switchSocketA(new Short(remoteSocket.getHouseCode()).shortValue(), new Short(remoteSocket.getRemoteCode()).shortValue(), switchCode);
                            Thread.sleep(500);
                            System.out.println(remoteSocket.getName() + " schalten");
                        } catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

     */

    /*
        experimentell
     */
    private Fragment getCurrentFragment()
    {
        FragmentManager fragmentManager = MainActivity.fragmentManager.getFragments().get(0).getChildFragmentManager();
        return fragmentManager.getFragments().get(0);
    }

    /*
        experimentell
     */
    private Fragment getSelectedFragment()
    {
        Fragment selectedFrangment = null;

        //Fragment fragNavhost = MainActivity.this.getSupportFragmentManager().getPrimaryNavigationFragment();
        //FragmentManager fragmentManager = MainActivity.this.getSupportFragmentManager();

        //Fragment fragNavhost = fragmentManager.getPrimaryNavigationFragment();
        //FragmentManager fragmentManager = MainActivity.this.getSupportFragmentManager();
        List<Fragment> fragments = fragmentManager.getFragments();
        if(fragments != null)
        {
            for (Fragment fragment : fragments)
            {
                if (fragment != null && fragment.isVisible())
                {
                    // die Daten abfragen (ggf. den Datenladevorgang starten)
                    //RemoteDataUtils remoteDataUtils = new RemoteDataUtils(fragment, this );
                    //remoteDataUtils.loadSocketData();

                    Context context = getApplicationContext();


                    NavController navController = NavHostFragment.findNavController(fragment);
                    NavDestination dest = navController.getCurrentDestination();

                    CharSequence seq =  dest.getLabel();
                    String navName = dest.getNavigatorName();
                    NavGraph navGraph = dest.getParent();
                    NavAction navAction1 = dest.getAction(R.id.action_FirstFragment_to_SecondFragment);
                    NavAction navAction2 = dest.getAction(R.id.action_FirstFragment_to_SecondFragment);
                    Map mapArgMap = dest.getArguments();

                    NavOptions opt = null;
                    if(navAction2 != null)
                        opt = navAction2.getNavOptions();

                    android.util.Log.d("MainActivity", "Action Opt: " + opt);


                    int destId = dest.getId();
                    if((destId == R.id.SecondFragment) || (destId == R.id.FirstFragment))
                    {
                        Object obj = getWindow().findViewById(destId);

                        android.util.Log.d("MainActivity", "DestID: " + destId);

                        selectedFrangment = fragment;

                    }

                    break;
                }
            }
        }

        return selectedFrangment;
    }


}
package it.naturtalent.remotesocketapp;

import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavAction;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.NavGraph;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;
import java.util.Map;

import it.naturtalent.databinding.RemoteData;
import it.naturtalent.multithread.FetchDataUseCase;


public class MainActivity extends AppCompatActivity  implements FetchDataUseCase.Listener
{
    public static final int SUB_ACTIVITY_CREATE_USER = 10;

    private static Context context;

    //private Fragment secondFragment;

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

        // FloatingActionButton 'Add'
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Bundle result = new Bundle();
                //result.putString("bundleKey", "result");
                //getSelectedFragment().getChildFragmentManager().setFragmentResult("requestKey", result);

                result.putString("addSocketKey", "result");
                FragmentManager fragmentManager = MainActivity.this.getSupportFragmentManager();
                List<Fragment> fragments = fragmentManager.getFragments();
                fragments.get(0).getChildFragmentManager().setFragmentResult("addSocketKey", result);
            }
        });



        // FloatingActionButton 'Delete'
        FloatingActionButton fabDelete = findViewById(R.id.fab_delete);
        fabDelete.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Bundle result = new Bundle();
                //result.putString("bundleKey", "result");
                //getSelectedFragment().getChildFragmentManager().setFragmentResult("requestKey", result);

                result.putString("deleteSocketKey", "result");
                FragmentManager fragmentManager = MainActivity.this.getSupportFragmentManager();
                List<Fragment> fragments = fragmentManager.getFragments();
                fragments.get(0).getChildFragmentManager().setFragmentResult("deleteSocketKey", result);


                //getSelectedFragment().getChildFragmentManager().setFragmentResult("requestKey", result);
            }
        });

        

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


        //Bundle result = new Bundle();
        //result.putString("addSocketKey", "result");
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

        }




        // Toolbar Settings Aktion
        if (id == R.id.action_settings)
        {
            android.util.Log.d("MainActivity", "Settings Action");
            return true;
        }



        /*
        if (id == R.id.action_store)
        {
            fragments.get(0).getChildFragmentManager().setFragmentResult("storeSocketKey", result);
            return true;
        }

         */

        /*
        // Toolbar ADD Aktion
        if (id == R.id.action_add)
        {
            android.util.Log.d("MainActivity", "Add Action");

            // reicht die weitere Verarbeitung via FragmentResult am das aktuelle Fragment weiter
            Bundle result = new Bundle();
            result.putString("addSocketKey", "result");
            FragmentManager fragmentManager = MainActivity.this.getSupportFragmentManager();
            List<Fragment> fragments = fragmentManager.getFragments();
            fragments.get(0).getChildFragmentManager().setFragmentResult("addSocketKey", result);

            return true;
        }
*/


        // Toolbar Delete Aktion
        /*
        if (id == R.id.action_delete)
        {
            android.util.Log.d("MainActivity", "Delete Action");

            Bundle result = new Bundle();
            result.putString("deleteSocketKey", "result");
            FragmentManager fragmentManager = MainActivity.this.getSupportFragmentManager();
            List<Fragment> fragments = fragmentManager.getFragments();
            fragments.get(0).getChildFragmentManager().setFragmentResult("deleteSocketKey", result);

            return true;
        }
        */

        /*

        // Toolbar 'EDIT' Aktion
        if (id == R.id.action_edit)
        {
            //android.util.Log.d("MainActivity", "Edit Action");

            // reicht die weitere Verarbeitung via FragmentResult am das aktuelle Fragment weiter
            Bundle result = new Bundle();
            result.putString("editSocketKey", "result");
            FragmentManager fragmentManager = MainActivity.this.getSupportFragmentManager();
            List<Fragment> fragments = fragmentManager.getFragments();
            fragments.get(0).getChildFragmentManager().setFragmentResult("editSocketKey", result);

            return true;
        }



         */


        return super.onOptionsItemSelected(item);
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


    private Fragment getSelectedFragment()
    {
        Fragment selectedFrangment = null;

        Fragment fragNavhost = MainActivity.this.getSupportFragmentManager().getPrimaryNavigationFragment();
        FragmentManager fragmentManager = MainActivity.this.getSupportFragmentManager();
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

    public static Context getAppContext() {
        return MainActivity.context;
    }


}
package it.naturtalent.remotesocketapp;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavAction;
import androidx.navigation.NavArgument;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.NavGraph;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;

import android.view.Menu;
import android.view.MenuItem;

import java.util.List;
import java.util.Map;

import it.naturtalent.databinding.RemoteData;
import it.naturtalent.multithread.FetchDataUseCase;
import it.naturtalent.remotesocketapp.R;


public class MainActivity extends AppCompatActivity  implements FetchDataUseCase.Listener
{
    public static final int SUB_ACTIVITY_CREATE_USER = 10;

    private Fragment secondFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // FloatingActionButton 'Add'
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
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
                getSelectedFragment().getChildFragmentManager().setFragmentResult("requestKey", result);
            }
        });

        

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            android.util.Log.d("MainActivity", "Settings Action");
            return true;
        }

        if (id == R.id.action_reload)
        {
            android.util.Log.d("MainActivity", "Reload");

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

                        NavController frReload = NavHostFragment.findNavController(fragment);

                        NavDestination dest = frReload.getCurrentDestination();
                        int idDest = dest.getId();


                        if(idDest == R.id.SecondFragment)
                        {
                            android.util.Log.d("MainActivity", "Id: " + idDest);

                            secondFragment = fragment;

                            // die Daten erneut laden, Datenladedialog im SecondFragment anzeigen
                            // @see onDataFetched()
                            RemoteDataUtils remoteDataUtils = new RemoteDataUtils(secondFragment, this);
                            remoteDataUtils.loadSocketData();
                        }
                        else
                        {
                            if(idDest == R.id.FirstFragment)
                            {
                                android.util.Log.d("MainActivity", "Id: " + idDest);

                                NavHostFragment.findNavController(fragment)
                                        .navigate(R.id.action_FirstFragment_to_SecondFragment);
                            }

                        }


                        break;
                    }
                }
            }


            return true;
        }

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

    /*

     */
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
                    NavAction navAction2 = dest.getAction(R.id.action_SecondFragment_to_FirstFragment);
                    Map mapArgMap = dest.getArguments();

                    NavOptions opt = null;
                    if(navAction2 != null)
                        opt = navAction2.getNavOptions();

                    android.util.Log.d("MainActivity", "Action Opt: " + opt);

                    /*
                    Map<String, NavArgument>map = dest.getArguments();

                    android.util.Log.d("MainActivity", "vor Navigate: " + dest.toString());
                    NavHostFragment.findNavController(fragment)
                            .navigate(dest.getId());
                    android.util.Log.d("MainActivity", "nach Navigate: " + dest.toString());

                     */


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
package it.naturtalent.remotesocketapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

public class PrefsFragment extends PreferenceFragmentCompat
{
    // aktuelles View unsichtbar machen, damit das PrefsFragment nicht ueberlappt
    private View visibleView;

    SharedPreferences sharedPreferences;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey)
    {
        // Laed das Praeferenzlayout aus der XML Ressourece
        setPreferencesFromResource(R.xml.root_preferences, rootKey);

        // Preferenzdaten uebernehmen oder Defaultwerte vorbesetzen
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        onSharedPreferenceChanged(sharedPreferences);
    }

    /*
        Uebernahme der Preferenzdaten in das Layout (see 'root_references.xml')
     */
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences)
    {
        String key;
        String value;
        EditTextPreference editTextPreference;

        key = getString(R.string.host_key);
        Preference preference = findPreference(key);
        if (preference instanceof EditTextPreference)
        {
            // Eingabefeld Hostname
            editTextPreference = (EditTextPreference) preference;
            value = sharedPreferences.getString(key, getString(R.string.host_default));
            editTextPreference.setText(value);
        }

        key = getString(R.string.port_key);
        preference = findPreference(key);
        if (preference instanceof EditTextPreference)
        {
            // Eingabefeld Post
            editTextPreference = (EditTextPreference) preference;
            value = sharedPreferences.getString(key, getString(R.string.port_default));
            editTextPreference.setText(value);
        }

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        // sicherstellen, dass das Preferenzfragment die aktuelle View nicht ueberlappt
        visibleView = getVisibleView(container);
        visibleView.setVisibility(View.INVISIBLE);

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onStop()
    {
        // das Originalview wieder sichtbar machen
        visibleView.setVisibility(View.VISIBLE);
        super.onStop();
    }

    private View getVisibleView(ViewGroup container)
    {
        int count = container.getChildCount();
        for (int i = 0; i < count; i++)
        {
            final View child = container.getChildAt(i);
            if (child.getVisibility() == View.VISIBLE)
                return child;
        }
        return  null;
    }

}
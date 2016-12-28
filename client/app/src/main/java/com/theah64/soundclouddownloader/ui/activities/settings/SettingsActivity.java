package com.theah64.soundclouddownloader.ui.activities.settings;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.theah64.soundclouddownloader.R;
import com.theah64.soundclouddownloader.ui.activities.BaseAppCompatActivity;
import com.theah64.soundclouddownloader.utils.App;
import com.theah64.soundclouddownloader.utils.SingletonToast;


@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class SettingsActivity extends BaseAppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Clicked on back button. finish the activity.
                finish();
            }
        });

        getFragmentManager().beginTransaction().add(
                R.id.flSettingsFragContainer,
                new SettingsFragment(),
                SettingsFragment.X).commit();
    }

    @Override
    public boolean isSecureActivity() {
        return true;
    }


    /**
     * ────────────────────────────────────────────────────────────────────────────────────
     * ─██████████████─██████████████─██████──────────██████─██████████████─██████████████─
     * ─██░░░░░░░░░░██─██░░░░░░░░░░██─██░░██████████──██░░██─██░░░░░░░░░░██─██░░░░░░░░░░██─
     * ─██░░██████████─██████░░██████─██░░░░░░░░░░██──██░░██─██░░██████████─██░░██████████─
     * ─██░░██─────────────██░░██─────██░░██████░░██──██░░██─██░░██─────────██░░██─────────
     * ─██░░██████████─────██░░██─────██░░██──██░░██──██░░██─██░░██─────────██░░██████████─
     * ─██░░░░░░░░░░██─────██░░██─────██░░██──██░░██──██░░██─██░░██──██████─██░░░░░░░░░░██─
     * ─██████████░░██─────██░░██─────██░░██──██░░██──██░░██─██░░██──██░░██─██████████░░██─
     * ─────────██░░██─────██░░██─────██░░██──██░░██████░░██─██░░██──██░░██─────────██░░██─
     * ─██████████░░██─────██░░██─────██░░██──██░░░░░░░░░░██─██░░██████░░██─██████████░░██─
     * ─██░░░░░░░░░░██─────██░░██─────██░░██──██████████░░██─██░░░░░░░░░░██─██░░░░░░░░░░██─
     * ─██████████████─────██████─────██████──────────██████─██████████████─██████████████─
     * ────────────────────────────────────────────────────────────────────────────────────
     * ───────────────────────────────────────────────────────────────────────────────────────────────────────────────
     * ─██████████████─████████████████───██████████████─██████──────────██████─██████──────────██████─██████████████─
     * ─██░░░░░░░░░░██─██░░░░░░░░░░░░██───██░░░░░░░░░░██─██░░██████████████░░██─██░░██████████──██░░██─██░░░░░░░░░░██─
     * ─██░░██████████─██░░████████░░██───██░░██████████─██░░░░░░░░░░░░░░░░░░██─██░░░░░░░░░░██──██░░██─██████░░██████─
     * ─██░░██─────────██░░██────██░░██───██░░██─────────██░░██████░░██████░░██─██░░██████░░██──██░░██─────██░░██─────
     * ─██░░██████████─██░░████████░░██───██░░██─────────██░░██──██░░██──██░░██─██░░██──██░░██──██░░██─────██░░██─────
     * ─██░░░░░░░░░░██─██░░░░░░░░░░░░██───██░░██──██████─██░░██──██░░██──██░░██─██░░██──██░░██──██░░██─────██░░██─────
     * ─██░░██████████─██░░██████░░████───██░░██──██░░██─██░░██──██████──██░░██─██░░██──██░░██──██░░██─────██░░██─────
     * ─██░░██─────────██░░██──██░░██─────██░░██──██░░██─██░░██──────────██░░██─██░░██──██░░██████░░██─────██░░██─────
     * ─██░░██─────────██░░██──██░░██████─██░░██████░░██─██░░██──────────██░░██─██░░██──██░░░░░░░░░░██─────██░░██─────
     * ─██░░██─────────██░░██──██░░░░░░██─██░░░░░░░░░░██─██░░██──────────██░░██─██░░██──██████████░░██─────██░░██─────
     * ─██████─────────██████──██████████─██████████████─██████──────────██████─██████──────────██████─────██████─────
     * ───────────────────────────────────────────────────────────────────────────────────────────────────────────────
     */

    public static class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener, Preference.OnPreferenceClickListener {

        public static final String X = SettingsFragment.class.getSimpleName();
        public static final String KEY_STORAGE_LOCATION = "storage_location";
        public static final String KEY_DEVELOPED_BY = "developed_by";
        private SharedPreferences defaultSharedPref;
        private Preference prefStorageLocation;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings_screen);
            this.defaultSharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
            this.prefStorageLocation = findPreference(KEY_STORAGE_LOCATION);

            //Setting current location
            final String currentStorageLocation = defaultSharedPref.getString(KEY_STORAGE_LOCATION, App.getDefaultStorageLocation());
            this.prefStorageLocation.setSummary(currentStorageLocation);
            this.prefStorageLocation.setOnPreferenceClickListener(this);
        }


        @Override
        public void onResume() {
            super.onResume();

            //Log.d(X, "registering listener");
            defaultSharedPref.registerOnSharedPreferenceChangeListener(this);
        }


        @Override
        public void onPause() {
            super.onPause();

            //Log.d(X, "deregistering listener");
            this.defaultSharedPref
                    .unregisterOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, final String key) {
            Log.d(X, "Pref changed : " + key);
        }

        @Override
        public boolean onPreferenceClick(Preference preference) {
            return onCompatPreferenceClick(getActivity(), preference);
        }

        public static boolean onCompatPreferenceClick(final Context context, Preference preference) {

            switch (preference.getKey()) {

                case KEY_STORAGE_LOCATION:
                    final Intent storageIntent = new Intent(Intent.ACTION_VIEW);
                    storageIntent.setDataAndType(Uri.parse(preference.getSummary().toString()), "resource/folder");
                    if (storageIntent.resolveActivityInfo(context.getPackageManager(), 0) != null) {
                        context.startActivity(storageIntent);
                    } else {
                        SingletonToast.makeText(context, R.string.No_file_browser_found, Toast.LENGTH_SHORT).show();
                    }
                    return true;

                case KEY_DEVELOPED_BY:
                    final Intent githubIntent = new Intent(Intent.ACTION_VIEW);
                    githubIntent.setData(Uri.parse(App.GITHUB_URL));
                    context.startActivity(githubIntent);
                    return true;

                default:
                    //Log.d(X, "No custom management found for recently clicked preference");
                    return false;
            }

        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            if (data != null) {
                Log.d(X, "Storage location: " + data.getDataString());
            }
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

}

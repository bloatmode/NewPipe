package org.schabi.newpipe.settings;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.webkit.CookieManager;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import org.schabi.newpipe.R;
import org.schabi.newpipe.SignInActivity;

public class AccountsSettingsFragment extends PreferenceFragmentCompat {
    private ActivityResultLauncher<Intent> activityLauncher;

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), result -> {
                    if (result.getData() == null) {
                        return;
                    }

                    final String site = result.getData().getStringExtra("site");
                    final String cookies = result.getData().getStringExtra("cookies");

                    setCookies(site + "_cookies", cookies);
                });
    }

    @Override
    public void onCreatePreferences(final Bundle savedInstanceState,
                                    final String rootKey) {
        setPreferencesFromResource(R.xml.account_settings, rootKey);

        setupYoutubePreferences();
    }

    private void setupYoutubePreferences() {
        final Preference youTubeSignInPreference =
                findPreference(getString(R.string.youtube_sign_in_settings_key));
        assert youTubeSignInPreference != null;
        youTubeSignInPreference.setOnPreferenceClickListener((Preference p) -> {
            final Intent intent = new Intent(getContext(), SignInActivity.class);
            activityLauncher.launch(intent);
            return true;
        });

        final Preference youTubeClearCookiesPreference =
                findPreference(getString(R.string.youtube_clear_cookies_settings_key));
        assert youTubeClearCookiesPreference != null;
        youTubeClearCookiesPreference.setOnPreferenceClickListener((Preference p) -> {
            setCookies("youtube", null);

            // TODO: is there a "safer" way to reset the browser's cookie for login?
            CookieManager.getInstance().removeAllCookie();

            return true;
        });
    }

    private void setCookies(final String site,
                            final String cookies) {
        final Context context = getContext();
        assert context != null;
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        prefs
                .edit()
                .putString(site, cookies)
                .apply();
    }

    private String getCookies(final String site) {
        final Context context = getContext();
        assert context != null;
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        return prefs.getString(site + "_cookies", null);
    }
}

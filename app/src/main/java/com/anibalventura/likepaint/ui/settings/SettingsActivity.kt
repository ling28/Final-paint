package com.anibalventura.likepaint.ui.settings

import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.anibalventura.likepaint.R
import com.anibalventura.likepaint.utils.Constants.THEME
import com.anibalventura.likepaint.utils.setupTheme
import kotlinx.android.synthetic.main.activity_settings.*

class SettingsActivity : AppCompatActivity(), SharedPreferences.OnSharedPreferenceChangeListener,
    Preference.SummaryProvider<ListPreference> {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.settings, SettingsFragment())
            .commit()

        setupToolbar()

        PreferenceManager.getDefaultSharedPreferences(this)
            .registerOnSharedPreferenceChangeListener(this)
    }

    private fun setupToolbar() {
        setSupportActionBar(toolbar)
        this.title = getString(R.string.option_settings)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }


    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        setupTheme(this)
    }

    class SettingsFragment : PreferenceFragmentCompat() {

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.settings_preferences, rootKey)

            bindSharedPrefSummary(findPreference(THEME)!!)
        }

        companion object {

            private val sBindPreferenceSummaryToValueListener =
                Preference.OnPreferenceChangeListener { preference, value ->

                    val stringValue = value.toString()

                    if (preference is ListPreference) {
                        val index = preference.findIndexOfValue(stringValue)

                        preference.setSummary(
                            when {
                                index >= 0 -> preference.entries[index]
                                else -> null
                            }
                        )
                    } else {

                        preference.summary = stringValue
                    }
                    true
                }

            private fun bindSharedPrefSummary(preference: Preference) {
                preference.onPreferenceChangeListener = sBindPreferenceSummaryToValueListener


                sBindPreferenceSummaryToValueListener.onPreferenceChange(
                    preference,
                    PreferenceManager
                        .getDefaultSharedPreferences(preference.context)
                        .getString(preference.key, "")
                )
            }
        }
    }

    override fun provideSummary(preference: ListPreference?): CharSequence =
        if (preference?.key == THEME) preference.entry
        else "Unknown Preference"
}
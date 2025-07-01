package xcom.niteshray.apps.arogyasathi_ai.utils

import android.content.Context
import android.content.SharedPreferences

class LanguagePreference(context: Context) {

    private val PREFS_NAME = "LanguagePrefs"
    private val KEY_SELECTED_LANGUAGE_CODE = "selected_language_code"
    private val KEY_SELECTED_LANGUAGE_DISPLAY_NAME = "selected_language_display_name"

    private val sharedPrefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun saveSelectedLanguage(languageCode: String, displayName: String) {
        with(sharedPrefs.edit()) {
            putString(KEY_SELECTED_LANGUAGE_CODE, languageCode)
            putString(KEY_SELECTED_LANGUAGE_DISPLAY_NAME, displayName)
            apply()
        }
    }
    fun getSelectedLanguageCode(): String {
        return sharedPrefs.getString(KEY_SELECTED_LANGUAGE_CODE, "en-US") ?: "en-US"
    }
    fun getSelectedLanguageDisplayName(): String {
        return sharedPrefs.getString(KEY_SELECTED_LANGUAGE_DISPLAY_NAME, "English") ?: "English"
    }
}
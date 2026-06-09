package com.example.ajikapps.utils

import android.content.Context
import android.content.SharedPreferences

class PreferenceManager(context: Context) {

    private val sharedPref: SharedPreferences = context.getSharedPreferences(
        PREF_NAME,
        Context.MODE_PRIVATE
    )

    fun isOnboardingCompleted(): Boolean {
        return sharedPref.getBoolean(KEY_ONBOARDING_COMPLETED, false)
    }

    fun setOnboardingCompleted(completed: Boolean) {
        sharedPref.edit().putBoolean(KEY_ONBOARDING_COMPLETED, completed).apply()
    }

    companion object {
        private const val PREF_NAME = "bina_desa_pref"
        private const val KEY_ONBOARDING_COMPLETED = "onboarding_completed"
    }
}

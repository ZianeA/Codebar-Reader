package com.meteoalgerie.autoscan.data

import android.app.Application
import androidx.preference.PreferenceManager
import dagger.Reusable
import io.reactivex.Observable
import javax.inject.Inject

@Reusable
class PreferencesStringStore @Inject constructor(private val app: Application) :
    KeyValueStore<String> {
    override fun observe(key: String, defaultValue: String): Observable<String> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private val preferences
        get() = PreferenceManager.getDefaultSharedPreferences(app)

    override fun get(key: String, defaultValue: String): String {
        return preferences.getString(key, defaultValue)!!
    }

    override fun put(key: String, value: String) {
        preferences.edit().putString(key, value).apply()
    }

    override fun add(key: String, value: String, defaultValue: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun remove(key: String, value: String, defaultValue: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    companion object {
        const val SERVER_URL_KEY = "SERVER_URL"
    }
}
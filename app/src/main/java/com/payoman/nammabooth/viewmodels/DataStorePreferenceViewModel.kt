package com.payoman.nammabooth.viewmodels

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DataStorePreferenceViewModel @Inject constructor(private val dataStore: DataStore<Preferences>): ViewModel() {
    fun provideDataStoreInstance(): DataStore<Preferences> {
        return dataStore
    }
}
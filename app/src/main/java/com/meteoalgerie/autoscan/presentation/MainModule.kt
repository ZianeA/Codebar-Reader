package com.meteoalgerie.autoscan.presentation

import com.meteoalgerie.autoscan.presentation.settings.SettingsFragment
import com.meteoalgerie.autoscan.presentation.settings.SettingsModule
import com.meteoalgerie.autoscan.presentation.desk.DeskFragment
import com.meteoalgerie.autoscan.presentation.desk.DeskModule
import com.meteoalgerie.autoscan.presentation.di.ActivityScope
import com.meteoalgerie.autoscan.presentation.di.FragmentScope
import com.meteoalgerie.autoscan.presentation.equipment.EquipmentFragment
import com.meteoalgerie.autoscan.presentation.equipment.EquipmentModule
import com.meteoalgerie.autoscan.presentation.login.LoginFragment
import com.meteoalgerie.autoscan.presentation.login.LoginModule
import dagger.Module
import dagger.Provides
import dagger.android.ContributesAndroidInjector

@Module
class MainModule {
    @ActivityScope
    @Provides
    fun provideFragNavController(mainActivity: MainActivity) = mainActivity.fragNavController

    @ActivityScope
    @Provides
    fun provideMainView(mainActivity: MainActivity): MainView = mainActivity

    @Module
    interface FragmentBindingModule {
        @FragmentScope
        @ContributesAndroidInjector(modules = [DeskModule::class])
        fun deskFragment(): DeskFragment

        @FragmentScope
        @ContributesAndroidInjector(modules = [LoginModule::class])
        fun loginFragment(): LoginFragment

        @FragmentScope
        @ContributesAndroidInjector(modules = [SettingsModule::class])
        fun settingsFragment(): SettingsFragment

        @FragmentScope
        @ContributesAndroidInjector(modules = [EquipmentModule::class])
        fun equipmentFragment(): EquipmentFragment
    }
}
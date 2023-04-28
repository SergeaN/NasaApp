package ru.sergean.nasaapp.di.app

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.MapKey
import dagger.Module
import dagger.multibindings.IntoMap
import ru.sergean.nasaapp.presentation.ui.favorites.FavoritesViewModel
import ru.sergean.nasaapp.presentation.ui.home.HomeViewModel
import ru.sergean.nasaapp.presentation.ui.intro.IntroViewModel
import ru.sergean.nasaapp.presentation.ui.main.MainViewModel
import kotlin.reflect.KClass

@Module
interface AppViewModelModule {

    @Binds
    @[IntoMap ViewModelKey(MainViewModel::class)]
    fun bindMainViewModel(mainViewModel: MainViewModel): ViewModel

    @Binds
    @[IntoMap ViewModelKey(IntroViewModel::class)]
    fun bindIntroViewModel(introViewModel: IntroViewModel): ViewModel

    @Binds
    @[IntoMap ViewModelKey(HomeViewModel::class)]
    fun bindHomeViewModel(homeViewModel: HomeViewModel): ViewModel

    @Binds
    @[IntoMap ViewModelKey(FavoritesViewModel::class)]
    fun bindFavoritesViewModel(favoritesViewModel: FavoritesViewModel): ViewModel

}

@MustBeDocumented
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@MapKey
annotation class ViewModelKey(val value: KClass<out ViewModel>)

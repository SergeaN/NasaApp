package ru.sergean.nasaapp.di.app

import android.content.Context
import dagger.*
import ru.sergean.nasaapp.di.login.LoginComponent
import ru.sergean.nasaapp.presentation.ui.main.MainActivity
import ru.sergean.nasaapp.presentation.ui.detail.DetailFragment
import ru.sergean.nasaapp.presentation.ui.favorites.FavoritesFragment
import ru.sergean.nasaapp.presentation.ui.home.HomeFragment
import ru.sergean.nasaapp.presentation.ui.intro.IntroFragment
import javax.inject.Scope

@AppScope
@Component(modules = [AppModule::class], dependencies = [FirebaseDependencies::class])
interface AppComponent {

    fun inject(activity: MainActivity)

    fun inject(fragment: IntroFragment)

    fun inject(fragment: HomeFragment)
    fun inject(fragment: FavoritesFragment)
    fun inject(fragment: DetailFragment)

    fun loginComponent(): LoginComponent.Builder

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun applicationContext(@ApplicationContext context: Context): Builder

        fun firebaseDependencies(firebaseDependencies: FirebaseDependencies): Builder

        fun build(): AppComponent
    }
}

@Module(
    includes = [
        NetworkModule::class, DatabaseModule::class,
        AppBindModule::class, AppViewModelModule::class
    ],
    subcomponents = [LoginComponent::class]
)
class AppModule

@Scope
@Retention(AnnotationRetention.RUNTIME)
annotation class AppScope
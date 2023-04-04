package ru.sergean.nasaapp

import android.app.Application
import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import dagger.BindsInstance
import dagger.Component
import dagger.Module
import ru.sergean.nasaapp.presentation.ui.confirmation.ConfirmationFragment
import ru.sergean.nasaapp.presentation.ui.container.ContainerFragment
import ru.sergean.nasaapp.presentation.ui.detail.DetailFragment
import ru.sergean.nasaapp.presentation.ui.favorites.FavoritesFragment
import ru.sergean.nasaapp.presentation.ui.home.HomeFragment
import ru.sergean.nasaapp.presentation.ui.intro.IntroFragment
import ru.sergean.nasaapp.presentation.ui.login.LoginFragment
import ru.sergean.nasaapp.presentation.ui.registration.RegistrationFragment
import javax.inject.Qualifier

const val TAG = "debig"

class NasaApp : Application() {

    lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()

        appComponent = DaggerAppComponent.builder()
            .applicationContext(context = this)
            .build()
    }

}

val Context.appComponent: AppComponent
    get() = when (this) {
        is NasaApp -> appComponent
        else -> this.applicationContext.appComponent
    }

@Component(modules = [AppModule::class])
interface AppComponent {

    fun inject(activity: MainActivity)

    fun inject(fragment: ContainerFragment)

    fun inject(fragment: IntroFragment)

    fun inject(fragment: LoginFragment)
    fun inject(fragment: RegistrationFragment)
    fun inject(fragment: ConfirmationFragment)

    fun inject(fragment: HomeFragment)
    fun inject(fragment: FavoritesFragment)
    fun inject(fragment: DetailFragment)

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun applicationContext(@ApplicationContext context: Context): Builder

        fun build(): AppComponent
    }

}

@Qualifier
annotation class ApplicationContext


@Module(includes = [NetworkModule::class, DatabaseModule::class])
class AppModule


@Module
class NetworkModule

@Module
class DatabaseModule

package ru.sergean.nasaapp.di

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import dagger.BindsInstance
import dagger.Component
import dagger.Module
import ru.sergean.nasaapp.presentation.ui.main.MainActivity
import ru.sergean.nasaapp.presentation.ui.confirmation.ConfirmationFragment
import ru.sergean.nasaapp.presentation.ui.detail.DetailFragment
import ru.sergean.nasaapp.presentation.ui.favorites.FavoritesFragment
import ru.sergean.nasaapp.presentation.ui.home.HomeFragment
import ru.sergean.nasaapp.presentation.ui.intro.IntroFragment
import ru.sergean.nasaapp.presentation.ui.login.LoginFragment
import ru.sergean.nasaapp.presentation.ui.registration.RegistrationFragment
import javax.inject.Qualifier
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class])
interface AppComponent {

    fun inject(activity: MainActivity)

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

        @BindsInstance
        fun firebaseAuth(firebaseAuth: FirebaseAuth): Builder

        @BindsInstance
        fun firebaseDatabase(firebaseDatabase: FirebaseDatabase): Builder

        fun build(): AppComponent
    }
}

@Module(includes = [NetworkModule::class, DatabaseModule::class])
class AppModule

@Target(AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.CONSTRUCTOR)
@Qualifier
annotation class ApplicationContext
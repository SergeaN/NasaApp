package ru.sergean.nasaapp.presentation.ui.main

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.isVisible
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import ru.sergean.nasaapp.R
import ru.sergean.nasaapp.TAG
import ru.sergean.nasaapp.appComponent
import ru.sergean.nasaapp.data.network.NetworkConnectionManager
import ru.sergean.nasaapp.databinding.ActivityMainBinding
import ru.sergean.nasaapp.di.login.LoginComponent
import ru.sergean.nasaapp.presentation.ui.base.arch.BaseViewModelFactory
import javax.inject.Inject

class MainActivity : AppCompatActivity(R.layout.activity_main), LoginScreenCallbacks {

    @Inject
    lateinit var connectionManager: NetworkConnectionManager

    @Inject
    lateinit var viewModelFactory: BaseViewModelFactory

    private val viewModel: MainViewModel by viewModels { viewModelFactory }

    private val binding by viewBinding(ActivityMainBinding::bind)

    private var _loginComponent: LoginComponent? = null

    override val loginComponent: LoginComponent
        get() = checkNotNull(_loginComponent) {
            "LoginComponent isn't initialized"
        }

    override fun createComponent() {
        if (_loginComponent == null) {
            _loginComponent = appComponent.loginComponent().build()
        }
    }

    override fun destroyComponent() {
        _loginComponent = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        appComponent.inject(activity = this)

        val splashScreen = installSplashScreen()
        splashScreen.setKeepOnScreenCondition { false }

        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            viewModel.mode.collect { startMode ->
                Log.d(TAG, "onCreate: $startMode")
                setupNavGraph(startMode)
                cancel()
            }
        }

        connectionManager.startListenNetworkState()

        observeInternetConnection()
    }

    override fun onDestroy() {
        super.onDestroy()
        connectionManager.stopListenNetworkState()
    }

    private fun setupNavGraph(mode: StartMode) {
        val containerFragment = supportFragmentManager.findFragmentById(R.id.container_view)
        val navHostFragment = containerFragment as NavHostFragment

        val navController = navHostFragment.navController

        val graph = navController.navInflater.inflate(R.navigation.nav_graph_container)
        graph.setStartDestination(mode.startDestinationId)
        navController.setGraph(graph, startDestinationArgs = null)

        setupBottomNavView(navController)
    }

    private fun setupBottomNavView(navController: NavController) {
        val bottomNavView = binding.containerBottomNavView
        bottomNavView.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            bottomNavView.isVisible = when (destination.id) {
                R.id.introFragment -> false
                R.id.loginFragment -> false
                R.id.registrationFragment -> false
                R.id.confirmationFragment -> false
                else -> true
            }
        }
    }

    private fun observeInternetConnection() {
        lifecycleScope.launch {
            connectionManager.networkConnectionState.flowWithLifecycle(lifecycle).collect {
                Log.d(TAG, "observeInternetConnection: $it")
                if (!it) {
                    Snackbar.make(
                        binding.root, R.string.no_internet_connection, Snackbar.LENGTH_INDEFINITE
                    ).setAction(R.string.close) {}.show()
                }
            }
        }
    }
}

interface LoginScreenCallbacks {
    val loginComponent: LoginComponent

    fun createComponent()
    fun destroyComponent()
}

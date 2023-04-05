package ru.sergean.nasaapp.presentation.ui.main

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import kotlinx.coroutines.launch
import ru.sergean.nasaapp.R
import ru.sergean.nasaapp.appComponent
import ru.sergean.nasaapp.databinding.ActivityMainBinding
import javax.inject.Inject

class MainActivity : AppCompatActivity(R.layout.activity_main) {

    @Inject
    lateinit var viewModelFactory: MainViewModel.Factory

    private val viewModel: MainViewModel by viewModels { viewModelFactory }

    private val binding by viewBinding(ActivityMainBinding::bind)

    override fun onCreate(savedInstanceState: Bundle?) {
        appComponent.inject(activity = this)

        val splashScreen = installSplashScreen()
        splashScreen.setKeepOnScreenCondition { viewModel.mode.value == null }

        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            viewModel.mode.collect { startMode ->
                if (startMode != null) setupNavGraph(startMode)
            }
        }
    }

    private fun setupNavGraph(mode: StartMode) {
        val containerFragment = supportFragmentManager.findFragmentById(R.id.container_view)
        val navHostFragment = containerFragment as NavHostFragment

        val navController = navHostFragment.navController

        val graph = navController.navInflater.inflate(R.navigation.nav_graph_container)
        graph.setStartDestination(mode.startDestinationId)
        navController.setGraph(graph, null)

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
}


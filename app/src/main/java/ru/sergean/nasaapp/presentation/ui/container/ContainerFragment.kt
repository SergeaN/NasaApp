package ru.sergean.nasaapp.presentation.ui.container

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import ru.sergean.nasaapp.R
import ru.sergean.nasaapp.databinding.FragmentContainerBinding

class ContainerFragment : Fragment(R.layout.fragment_container) {

    private val binding by viewBinding(FragmentContainerBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val containerFragment = childFragmentManager.findFragmentById(R.id.container_view)
        val navHostFragment = containerFragment as NavHostFragment

        val navController = navHostFragment.navController
        val bottomNavView = binding.containerBottomNavView.apply {
            setupWithNavController(navController)
        }

        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            bottomNavView.isVisible = when (destination.id) {
                R.id.introFragment -> false
                R.id.loginFragment -> false
                R.id.registrationFragment -> false
                R.id.confirmationFragment -> false
                else -> true
            }
        }


        val badge = bottomNavView.getOrCreateBadge(R.id.favoritesFragment).run {
            isVisible = true
            number = 100
        }
    }
}
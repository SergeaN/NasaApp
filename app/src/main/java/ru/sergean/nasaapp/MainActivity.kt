package ru.sergean.nasaapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.ViewTreeObserver
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import ru.sergean.nasaapp.presentation.ui.container.ContainerFragment
import ru.sergean.nasaapp.utils.startActivity
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    var contentHasLoaded = false

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        startLoadingContent()
        setUpSplashScreen()

        //startActivity<MainActivity>()
    }

    private fun startLoadingContent() {
        Handler(Looper.getMainLooper()).postDelayed({
            contentHasLoaded = true
        }, 1000)
    }

    private fun setUpSplashScreen() {
        val content: View = findViewById(android.R.id.content)
        content.viewTreeObserver.addOnPreDrawListener(
            object : ViewTreeObserver.OnPreDrawListener {
                override fun onPreDraw(): Boolean {
                    return if (contentHasLoaded) {
                        content.viewTreeObserver.removeOnPreDrawListener(this)
                        true
                    } else false
                }
            }
        )
    }

}
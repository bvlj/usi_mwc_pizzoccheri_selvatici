package ch.usi.inf.mwc.cusi.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import ch.usi.inf.mwc.cusi.R

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
    }
}
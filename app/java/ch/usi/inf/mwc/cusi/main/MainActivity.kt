package ch.usi.inf.mwc.cusi.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import ch.usi.inf.mwc.cusi.R
import ch.usi.inf.mwc.cusi.networking.status.NetworkStatus
import ch.usi.inf.mwc.cusi.networking.status.NetworkStatusAdapter
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            viewModel.sync()
        }
        
        setContentView(R.layout.activity_main)
    }
}
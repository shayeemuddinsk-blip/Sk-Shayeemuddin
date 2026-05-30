package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
import com.example.ui.screens.AppNavigator
import com.example.ui.viewmodel.TuitionViewModel
import com.example.ui.viewmodel.TuitionViewModelFactory

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    val viewModel = ViewModelProvider(
      this, 
      TuitionViewModelFactory(application)
    )[TuitionViewModel::class.java]

    setContent {
      AppNavigator(viewModel = viewModel)
    }
  }
}

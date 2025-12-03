package com.example.cvstakehome

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.example.cvstakehome.ui.navigation.RickAndMortyNavigation
import com.example.cvstakehome.ui.theme.CVSTakehomeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CVSTakehomeTheme {
                RickAndMortyNavigation(
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}
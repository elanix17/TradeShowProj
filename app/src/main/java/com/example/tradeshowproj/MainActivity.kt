package com.example.tradeshowproj

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.tradeshowproj.navigation.AppNav
import com.example.tradeshowproj.ui.theme.TradeShowProjTheme
import com.example.tradeshowproj.zCatalystSDK.ZAuthSDK

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ZAuthSDK.initialize(this)

        enableEdgeToEdge()
        setContent {
            TradeShowProjTheme {
                AppNav()
                }
            }
        }
    }

//
//@Composable
//fun Greeting(name: String, modifier: Modifier = Modifier) {
//    Text(
//        text = "Hello $name!",
//        modifier = modifier
//    )
//}
//
//@Preview(showBackground = true)
//@Composable
//fun GreetingPreview() {
//    TradeShowProjTheme {
//        Greeting("Android")
//    }
//}
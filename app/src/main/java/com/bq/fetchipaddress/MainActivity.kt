package com.bq.fetchipaddress

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bq.fetchipaddress.ui.theme.FetchIPAddressTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject
import java.net.URL

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FetchIPAddressTheme {
                // A surface container using the 'background' color from the theme

                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    Scaffold(topBar = {
                        TopAppBar(colors = topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            titleContentColor = MaterialTheme.colorScheme.primary,
                        ), title = {
                            Text("Fetch IP Address")
                        })
                    }, content = { innerPading ->
                        Greeting("IP Address: ", innerPading)
                    })

                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, paddingValues: PaddingValues, modifier: Modifier = Modifier) {
    var isLoading by remember { mutableStateOf(false) }
    var ipAddress by remember { mutableStateOf("") }

    Column(modifier.padding(paddingValues).fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(modifier = Modifier.height(10.dp))
        if (isLoading) {
            // Show a progress indicator while loading data
            CircularProgressIndicator()
        } else {
            Text(
                modifier=Modifier.fillMaxWidth(),
                text = name + ipAddress
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        
        Button(
            onClick = {
            isLoading = true
            CoroutineScope(Dispatchers.IO).launch {
                ipAddress = getIpAddressFromURL()
                isLoading = false
            }

        }) {
            Text(
                text = "Fetch IP",
            )
        }
    }


}

private fun getIpAddressFromURL(): String {
    val urlsToTry = listOf(
        "https://api.ipify.org",
        "https://checkip.amazonaws.com/",
        "https://ipinfo.io/json",
        "http://whatismyip.akamai.com/",// https not supports
        "http://ip-api.com/json"// https not supports
    )

    for (url in urlsToTry) {
        try {
            val ipAddress = URL(url).readText()
            if (isJson(ipAddress)) {
                return extractIPFromJson(ipAddress) ?: "Unable to find ip"
            }
            return ipAddress
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    return "Error: Unable to fetch IP address from any source"
}

private fun isJson(data: String): Boolean {
    try {
        JSONObject(data) // Attempt to create a JSONObject from the data
        return true
    } catch (e: JSONException) {
        // JSON parsing failed, so it's not JSON data
        return false
    }
}

private fun extractIPFromJson(jsonText: String): String? {
    try {
        val jsonObject = JSONObject(jsonText)
        val ip = jsonObject.optString("ip")
        val query = jsonObject.optString("query")
        val result = if (ip.isNotEmpty()) {
            ip
        } else {
            query
        }
        return result
    } catch (e: JSONException) {
        // Handle JSON parsing errors
        return null
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    FetchIPAddressTheme {
        Scaffold(topBar = {
            TopAppBar(colors = topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                titleContentColor = MaterialTheme.colorScheme.primary,
            ), title = {
                Text("Fetch IP Address")
            })
        }, content = { innerPading ->

            Greeting("IP Addresss: ${getIpAddressFromURL()}", innerPading)

        })
    }
}
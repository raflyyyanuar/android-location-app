package com.example.locationapp

import android.content.Context
import android.os.Bundle
import android.Manifest
import android.app.GameManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.locationapp.ui.theme.LocationAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val viewModel : LocationViewModel = viewModel()
            LocationAppTheme {
                MyApp(viewModel)
            }
        }
    }
}

@Composable
fun MyApp(viewModel: LocationViewModel) {
    val context = LocalContext.current
    val locationUtils = LocationUtils(context)
    LocationDisplay(context, locationUtils, viewModel)
}

@Composable
fun LocationDisplay(
    context : Context,
    locationUtils: LocationUtils,
    viewModel: LocationViewModel
) {
    // Create a launcher to request permissions
    val requestPermissionLauncher = rememberLauncherForActivityResult(
        // The contract definition that we want: multiple permissions
        contract = ActivityResultContracts.RequestMultiplePermissions(),

        // A callback when the user responds to the permission dialog
        onResult = { permissions ->
            // If user has both permission, good
            if(permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
                && permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true){
                // Access granted
            }

            // If either or both permissions are denied,
            // find out if we should ask for permissions again
            else {
                // Cast the passed context to MainActivity
                val cam = context as MainActivity

                // Should the app show a rationale for the denied permission?
                // returns TRUE if the app has requested this permission PREVIOUSLY
                // and the user DENIED the request.

                // returns FALSE if the user turned down the permission request in the past
                // and chose the `Don't ask again` option in the permission request system dialog

                // returns FALSE if a device policy prohibits the app from having that permission.
                val rationaleRequired = ActivityCompat.shouldShowRequestPermissionRationale(
                    cam,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) || ActivityCompat.shouldShowRequestPermissionRationale(
                    cam,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )

                if(rationaleRequired) {
                    Toast.makeText(
                        context,
                        "Rationale IS required.",
                        Toast.LENGTH_LONG,
                    ).show()
                }
                else {
                    Toast.makeText(
                        context,
                        "Please enable location permission in the settings to continue.",
                        Toast.LENGTH_LONG,
                    ).show()
                }
            }
        }
    )

    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            "Your location is unknown.",
            style = TextStyle(fontSize = 24.sp)
        )
        Button(onClick = {
            if(locationUtils.hasLocationPermission(context)) {
                // Get location
            }
            else {
                // request location permission
                requestPermissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                    )
                )
            }
        }) {
            Text(text = "Get location")
        }
    }
}

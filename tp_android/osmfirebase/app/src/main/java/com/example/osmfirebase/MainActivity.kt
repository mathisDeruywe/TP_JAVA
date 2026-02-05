package com.example.osmfirebase

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
import com.example.osmfirebase.ui.theme.OsmfirebaseTheme
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

class MainActivity : AppCompatActivity() {
    private lateinit var map: MapView
    private lateinit var database: DatabaseReference
    private val markers = mutableMapOf<String, Marker>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Configuration OSM
        Configuration.getInstance().load(
            applicationContext,
            getSharedPreferences("osm", MODE_PRIVATE)
        )

        setContentView(R.layout.activity_main)
        map = findViewById(R.id.map)
        map.setMultiTouchControls(true)
        map.controller.setZoom(6.0)
        map.controller.setCenter(GeoPoint(33.5, -7.6)) // Maroc

        database = FirebaseDatabase.getInstance()
            .getReference("positions")
        listenToPositions()
    }

    private fun listenToPositions() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (userSnap in snapshot.children) {
                    val id = userSnap.key ?: continue
                    val lat = userSnap.child("latitude").getValue(Double::class.java)
                    val lon = userSnap.child("longitude").getValue(Double::class.java)

                    if (lat != null && lon != null) {
                        updateMarker(id, lat, lon)
                    }
                }
                map.invalidate()
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun updateMarker(id: String, lat: Double, lon: Double) {
        val point = GeoPoint(lat, lon)

        if (markers.containsKey(id)) {
            markers[id]?.position = point
        } else {
            val marker = Marker(map)
            marker.position = point
            marker.title = "Utilisateur : $id"
            map.overlays.add(marker)
            markers[id] = marker
        }
    }

    override fun onResume() {
        super.onResume()
        map.onResume()
    }

    override fun onPause() {
        super.onPause()
        map.onPause()
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    OsmfirebaseTheme {
        Greeting("Android")
    }
}
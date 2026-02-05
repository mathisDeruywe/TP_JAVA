package com.example.listviewcustom

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
import com.example.listviewcustom.ui.theme.ListviewcustomTheme

import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val listView = findViewById<ListView>(R.id.listViewPerson)
        val persons = listOf(
            Person("Souissi", "Abdelmoghit", R.drawable.photo1),
            Person("El Amrani", "Youssef", R.drawable.photo2),
            Person("Benali", "Sara", R.drawable.photo3)
        )
        val adapter = PersonAdapter(this, persons)
        listView.adapter = adapter
        listView.setOnItemClickListener { _, _, position, _ ->
            val p = persons[position]
            Toast.makeText(
                this,
                "${p.prenom} ${p.nom}",
                Toast.LENGTH_SHORT
            ).show()
        }
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
    ListviewcustomTheme {
        Greeting("Android")
    }
}
package com.example.firebaseapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.firebaseapp.ui.theme.FirebaseappTheme
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class MainActivity : AppCompatActivity() {
    private lateinit var db: DatabaseReference
    private lateinit var list: ArrayList<User>
    private lateinit var adapter: UserAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        db = FirebaseDatabase.getInstance().getReference("users")

        val etNom = findViewById<EditText>(R.id.etNom)
        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etTel = findViewById<EditText>(R.id.etTel)
        val btnAdd = findViewById<Button>(R.id.btnAdd)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)

        list = ArrayList()
        adapter = UserAdapter(list) { user ->
            showUpdateDialog(user)
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        // CREATE
        btnAdd.setOnClickListener {
            val id = db.push().key!!
            val user = User(
                id,
                etNom.text.toString(),
                etEmail.text.toString(),
                etTel.text.toString()
            )
            db.child(id).setValue(user)

            etNom.text.clear()
            etEmail.text.clear()
            etTel.text.clear()
        }

        // READ (Realtime)
        db.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                list.clear()
                for (s in snapshot.children) {
                    val u = s.getValue(User::class.java)
                    list.add(u!!)
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    // UPDATE + DELETE
    private fun showUpdateDialog(user: User) {
        val view = layoutInflater.inflate(R.layout.dialog_update_user, null)

        val etNom = view.findViewById<EditText>(R.id.etNomUpd)
        val etEmail = view.findViewById<EditText>(R.id.etEmailUpd)
        val etTel = view.findViewById<EditText>(R.id.etTelUpd)

        etNom.setText(user.nom)
        etEmail.setText(user.email)
        etTel.setText(user.tel)

        AlertDialog.Builder(this)
            .setTitle("Modifier utilisateur")
            .setView(view)
            .setPositiveButton("Modifier") { _, _ ->
                val newUser = User(
                    user.id,
                    etNom.text.toString(),
                    etEmail.text.toString(),
                    etTel.text.toString()
                )
                db.child(user.id!!).setValue(newUser)
            }
            .setNeutralButton("Supprimer") { _, _ ->
                db.child(user.id!!).removeValue()
            }
            .setNegativeButton("Annuler", null)
            .show()
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
    FirebaseappTheme {
        Greeting("Android")
    }
}
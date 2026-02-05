package com.example.ventefacile.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.ventefacile.viewmodel.MainViewModel

@Composable
fun AuthScreen(viewModel: MainViewModel, onAuthSuccess: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isRegistering by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = if (isRegistering) "Créer un compte" else "Connexion",
            style = MaterialTheme.typography.headlineLarge
        )

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Mot de passe") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (viewModel.isLoading) {
            CircularProgressIndicator()
        } else {
            Button(
                onClick = {
                    if (isRegistering) viewModel.register(email, password, onAuthSuccess)
                    else viewModel.login(email, password, onAuthSuccess)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (isRegistering) "S'inscrire" else "Se connecter")
            }

            TextButton(onClick = { isRegistering = !isRegistering }) {
                Text(if (isRegistering) "Déjà un compte ? Se connecter" else "Pas de compte ? S'inscrire")
            }
        }

        if (viewModel.statusMessage.isNotBlank()) {
            Text(viewModel.statusMessage, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 16.dp))
        }
    }
}
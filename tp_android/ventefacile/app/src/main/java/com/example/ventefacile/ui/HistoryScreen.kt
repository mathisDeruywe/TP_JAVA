package com.example.ventefacile.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.ventefacile.viewmodel.MainViewModel

@Composable
fun HistoryScreen(viewModel: MainViewModel, modifier: Modifier = Modifier) {
    val ventes = viewModel.listeVentes.value

    Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
        Text("Historique des Ventes", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        if (ventes.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Aucune vente enregistrée.")
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(ventes) { vente ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(vente.client.nom, style = MaterialTheme.typography.titleLarge)
                                Text("${vente.total} FCFA", color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.titleLarge)
                            }
                            Text("Date : ${vente.date}", style = MaterialTheme.typography.bodySmall)
                            Text("Produits : ${vente.produits.size}", style = MaterialTheme.typography.bodyMedium)

                            Text(
                                text = "Position : Lat ${vente.client.position.latitude}, Lng ${vente.client.position.longitude}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }
                }
            }
        }
    }
}
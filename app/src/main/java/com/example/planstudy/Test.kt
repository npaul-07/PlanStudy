package com.example.studyplanner

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

data class Contact(
    val name: String,
    val phone: String
)

@Composable
fun Testo() {
    val contacts = remember {
        listOf(
            Contact(
                name = "Harry",
                phone = "982345676"
            ),
            Contact(
                name = "Harry",
                phone = "982345676"
            ),
            Contact(
                name = "Harry",
                phone = "982345676"
            ),
            Contact(
                name = "Harry",
                phone = "982345676"
            ),
            Contact(
                name = "Harry",
                phone = "982345676"
            ),
            Contact(
                name = "Harry",
                phone = "982345676"
            ),
            Contact(
                name = "Harry",
                phone = "982345676"
            ),
            Contact(
                name = "Harry",
                phone = "982345676"
            ),
            Contact(
                name = "Harry",
                phone = "982345676"
            ),
            Contact(
                name = "Harry",
                phone = "982345676"
            ),
            Contact(
                name = "Harry",
                phone = "982345676"
            ),
            Contact(
                name = "Harry",
                phone = "982345676"
            ),
            Contact(
                name = "Harry",
                phone = "982345676"
            ),
            Contact(
                name = "Harry",
                phone = "982345676"
            ),
            Contact(
                name = "Harry",
                phone = "982345676"
            ),
            Contact(
                name = "Harry",
                phone = "982345676"
            ),
            Contact(
                name = "Harry",
                phone = "982345676"
            ),
            Contact(
                name = "Harry",
                phone = "982345676"
            ),
            Contact(
                name = "Harry",
                phone = "982345676"
            ),
            Contact(
                name = "Harry",
                phone = "982345676"
            ),
        )
    }

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(contacts) { contact ->
            Card(modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)) {
                Column(modifier = Modifier.padding(8.dp)) {
                    Text(text = contact.name)
                    Text(text = contact.phone)

                }
            }
        }
    }
}
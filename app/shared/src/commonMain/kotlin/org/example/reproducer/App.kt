package org.example.reproducer

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun App() {
    MaterialTheme {
        val password = rememberTextFieldState()
        MenuTextField(
            state = password,
            label = { Text("Confirm Password") },
            onAction = {},
            secure = true,
            modifier = Modifier.padding(bottom = 4.dp)
        )
    }
}

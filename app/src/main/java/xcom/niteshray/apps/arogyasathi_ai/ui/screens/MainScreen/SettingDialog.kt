package xcom.niteshray.apps.arogyasathi_ai.ui.screens.MainScreen

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import xcom.niteshray.apps.arogyasathi_ai.R
import xcom.niteshray.apps.arogyasathi_ai.ui.theme.graycolor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingDialog(
    currentLanguage: String,
    onDismiss: () -> Unit,
    onConfirm: (displayName: String, languageCode: String) -> Unit
) {

    val languageMap = mapOf(
        "Hindi" to "hi-IN",
        "English" to "en-US",
        "Marathi" to "mr-IN",
        "Tamil" to "ta-IN",
        "Telugu" to "te-IN",
        "Malayalam" to "ml-IN",
        "Kannada" to "kn-IN",
        "Bengali" to "bn-IN",
        "Gujarati" to "gu-IN",
        "Punjabi" to "pa-IN",
        "English (Indian)" to "en-IN"
    )
    val languages = languageMap.keys.toList()
    var expanded by remember { mutableStateOf(false) }
    var selectedLanguage by remember { mutableStateOf(currentLanguage) }
    val arrowRotation by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        animationSpec = tween(350), label = ""
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text(
                    text = "App Language",
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp
                    ),
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Text(
                    text = "Choose your preferred language for the app interface.",
                    style = MaterialTheme.typography.bodyMedium.copy(color = Color.LightGray),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Divider(color = Color.White.copy(alpha = 0.2f))
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Spacer(modifier = Modifier.height(4.dp))
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    TextField(
                        value = selectedLanguage,
                        onValueChange = {},
                        readOnly = true,
                        label = {
                            Text("Language", color = Color.Gray)
                        },
                        trailingIcon = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Image(
                                    painter = painterResource(id = R.drawable.language),
                                    contentDescription = "Language",
                                    colorFilter = ColorFilter.tint(Color.Black),
                                    modifier = Modifier.size(24.dp).padding(end = 4.dp)
                                )
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                            }
                        },
                        colors = ExposedDropdownMenuDefaults.textFieldColors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedLabelColor = Color.Black,
                            focusedIndicatorColor = Color.Black,
                            unfocusedIndicatorColor = Color.Gray
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.background(Color.White).height(200.dp).scrollable(
                            state = rememberScrollState(),
                            orientation = Orientation.Vertical
                        )
                    ) {
                        languages.forEach { language ->
                            DropdownMenuItem(
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(18.dp)
                                                .background(
                                                    color = Color.White.copy(
                                                        alpha = 0.25f
                                                    ),
                                                    shape = CircleShape
                                                )
                                        )
                                        Spacer(modifier = Modifier.size(8.dp))
                                        Text(language, color = Color.Black)
                                    }
                                },
                                onClick = {
                                    selectedLanguage = language
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val selectedCode = languageMap[selectedLanguage] ?: "en-US"
                    onConfirm(selectedLanguage, selectedCode)
                }
            ) {
                Text("OK", color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = Color.White)
            }
        },
        shape = RoundedCornerShape(20.dp),
        tonalElevation = 10.dp,
        containerColor = graycolor
    )
}

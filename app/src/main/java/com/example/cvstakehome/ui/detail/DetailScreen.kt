package com.example.cvstakehome.ui.detail

import android.content.Context
import android.content.Intent
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.cvstakehome.data.model.Character
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun DetailScreen(
    character: Character,
    onBackClick: () -> Unit,
    sharedTransitionScope: SharedTransitionScope? = null,
    animatedContentScope: AnimatedVisibilityScope? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(character.name) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { shareCharacter(context, character) }) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share"
                        )
                    }
                }
            )
        },
        modifier = modifier
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            val imageModifier = Modifier
                .fillMaxWidth()
                .height(300.dp)

            AsyncImage(
                model = character.image,
                contentDescription = character.name,
                modifier = if (sharedTransitionScope != null && animatedContentScope != null) {
                    with(sharedTransitionScope) {
                        imageModifier.sharedBounds(
                            sharedContentState = rememberSharedContentState(key = "image-${character.id}"),
                            animatedVisibilityScope = animatedContentScope
                        )
                    }
                } else {
                    imageModifier
                },
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(16.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                DetailItem(label = "Species", value = character.species)
                Spacer(Modifier.height(12.dp))

                DetailItem(label = "Status", value = character.status)
                Spacer(Modifier.height(12.dp))

                DetailItem(label = "Origin", value = character.origin.name)
                Spacer(Modifier.height(12.dp))

                if (character.type.isNotEmpty()) {
                    DetailItem(label = "Type", value = character.type)
                    Spacer(Modifier.height(12.dp))
                }

                DetailItem(
                    label = "Created",
                    value = safeFormatDate(character.created)
                )

                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun DetailItem(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

private fun safeFormatDate(isoDate: String): String {
    return try {
        val parsed = ZonedDateTime.parse(isoDate)
        val formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy 'at' h:mm a")
        parsed.format(formatter)
    } catch (e: Exception) {
        isoDate
    }
}

private fun shareCharacter(context: Context, character: Character) {
    val shareText = buildShareText(character)

    val sendIntent = Intent(Intent.ACTION_SEND).apply {
        putExtra(Intent.EXTRA_TEXT, shareText)
        putExtra(Intent.EXTRA_TITLE, "Character: ${character.name}")
        type = "text/plain"
    }

    val shareIntent = Intent.createChooser(sendIntent, "Share ${character.name}")
    context.startActivity(shareIntent)
}

private fun buildShareText(character: Character): String {
    return buildString {
        appendLine("Name: ${character.name}")
        appendLine("Species: ${character.species}")
        appendLine("Status: ${character.status}")
        appendLine("Origin: ${character.origin.name}")
        if (character.type.isNotEmpty()) appendLine("Type: ${character.type}")
        appendLine("Created: ${safeFormatDate(character.created)}")
        appendLine()
        appendLine("Image: ${character.image}")
        appendLine()
        append("From the Rick and Morty Character Search App")
    }
}



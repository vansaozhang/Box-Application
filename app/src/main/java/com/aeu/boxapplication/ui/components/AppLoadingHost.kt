package com.aeu.boxapplication.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.compose.material3.CircularProgressIndicator

data class AppLoadingData(
    val scrimColor: Color = Color(0xA6111827),
    val indicatorColor: Color = Color(0xFF1E88E5)
)

@Stable
class AppLoadingHostState {
    var currentLoading by mutableStateOf<AppLoadingData?>(null)
        private set

    fun show(loading: AppLoadingData = AppLoadingData()) {
        currentLoading = loading
    }

    fun show(
        scrimColor: Color = Color(0xA6111827),
        indicatorColor: Color = Color(0xFF1E88E5)
    ) {
        show(
            AppLoadingData(
                scrimColor = scrimColor,
                indicatorColor = indicatorColor
            )
        )
    }

    fun hide() {
        currentLoading = null
    }
}

@Composable
fun rememberAppLoadingHostState(): AppLoadingHostState {
    return remember { AppLoadingHostState() }
}

val LocalAppLoadingHostState = staticCompositionLocalOf<AppLoadingHostState> {
    error("AppLoadingHostState was not provided.")
}

@Composable
fun AppGlobalLoadingEffect(
    isVisible: Boolean,
    scrimColor: Color = Color(0xA6111827),
    indicatorColor: Color = Color(0xFF1E88E5)
) {
    val loadingHostState = LocalAppLoadingHostState.current

    LaunchedEffect(isVisible, scrimColor, indicatorColor) {
        if (isVisible) {
            loadingHostState.show(
                scrimColor = scrimColor,
                indicatorColor = indicatorColor
            )
        } else {
            loadingHostState.hide()
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            loadingHostState.hide()
        }
    }
}

@Composable
fun AppLoadingHost(
    hostState: AppLoadingHostState,
    modifier: Modifier = Modifier
) {
    val loading = hostState.currentLoading

    AnimatedVisibility(
        visible = loading != null,
        enter = fadeIn(),
        exit = fadeOut(),
        modifier = modifier
            .fillMaxSize()
            .zIndex(20f)
    ) {
        loading?.let {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(it.scrimColor)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = {}
                    ),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(42.dp),
                    color = it.indicatorColor,
                    strokeWidth = 4.dp
                )
            }
        }
    }
}

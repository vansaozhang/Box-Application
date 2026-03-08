package com.aeu.boxapplication.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.WarningAmber
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class AppNotificationPosition {
    Top,
    Bottom
}

data class AppNotificationData(
    val title: String,
    val message: String? = null,
    val tone: AppStatusTone = AppStatusTone.Info,
    val label: String? = null,
    val position: AppNotificationPosition = AppNotificationPosition.Top,
    val durationMillis: Long = 4000L,
    val onDismiss: (() -> Unit)? = null
)

@Stable
class AppNotificationHostState(
    private val scope: CoroutineScope
) {
    var currentNotification by mutableStateOf<AppNotificationData?>(null)
        private set

    private var autoDismissJob: Job? = null

    fun show(notification: AppNotificationData) {
        autoDismissJob?.cancel()
        currentNotification = notification

        if (notification.durationMillis > 0) {
            autoDismissJob = scope.launch {
                delay(notification.durationMillis)
                dismiss()
            }
        }
    }

    fun show(
        title: String,
        message: String? = null,
        tone: AppStatusTone = AppStatusTone.Info,
        label: String? = null,
        position: AppNotificationPosition = AppNotificationPosition.Top,
        durationMillis: Long = 4000L,
        onDismiss: (() -> Unit)? = null
    ) {
        show(
            AppNotificationData(
                title = title,
                message = message,
                tone = tone,
                label = label,
                position = position,
                durationMillis = durationMillis,
                onDismiss = onDismiss
            )
        )
    }

    fun dismiss() {
        autoDismissJob?.cancel()
        autoDismissJob = null
        val dismissedNotification = currentNotification
        currentNotification = null
        dismissedNotification?.onDismiss?.invoke()
    }
}

@Composable
fun rememberAppNotificationHostState(): AppNotificationHostState {
    val scope = rememberCoroutineScope()
    return remember(scope) { AppNotificationHostState(scope) }
}

val LocalAppNotificationHostState = staticCompositionLocalOf<AppNotificationHostState> {
    error("AppNotificationHostState was not provided.")
}

@Composable
fun AppNotificationHost(
    hostState: AppNotificationHostState,
    modifier: Modifier = Modifier
) {
    val notification = hostState.currentNotification
    val position = notification?.position ?: AppNotificationPosition.Top
    val alignment = when (position) {
        AppNotificationPosition.Top -> Alignment.TopCenter
        AppNotificationPosition.Bottom -> Alignment.BottomCenter
    }

    val topPadding = WindowInsets.statusBars.asPaddingValues().calculateTopPadding() + 12.dp
    val bottomPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding() + 12.dp

    Box(
        modifier = modifier
            .fillMaxSize()
            .zIndex(10f)
    ) {
        AnimatedVisibility(
            visible = notification != null,
            enter = slideInVertically(
                initialOffsetY = { fullHeight ->
                    if (notification?.position == AppNotificationPosition.Bottom) {
                        fullHeight
                    } else {
                        -fullHeight
                    }
                },
                animationSpec = spring(dampingRatio = 0.82f, stiffness = 420f)
            ) + fadeIn(),
            exit = slideOutVertically(
                targetOffsetY = { fullHeight ->
                    if (notification?.position == AppNotificationPosition.Bottom) {
                        fullHeight
                    } else {
                        -fullHeight
                    }
                }
            ) + fadeOut(),
            modifier = Modifier
                .align(alignment)
                .padding(
                    start = 16.dp,
                    end = 16.dp,
                    top = if (position == AppNotificationPosition.Top) topPadding else 0.dp,
                    bottom = if (position == AppNotificationPosition.Bottom) bottomPadding else 0.dp
                )
                .widthIn(max = 560.dp)
        ) {
            notification?.let {
                AppFloatingNotificationCard(
                    title = it.title,
                    message = it.message,
                    tone = it.tone,
                    label = it.label,
                    onDismiss = hostState::dismiss
                )
            }
        }
    }
}

@Composable
private fun AppFloatingNotificationCard(
    title: String,
    message: String?,
    tone: AppStatusTone,
    label: String?,
    onDismiss: () -> Unit
) {
    val style = tone.notificationStyle()
    val shape = RoundedCornerShape(16.dp)

    Surface(
        shape = shape,
        color = style.containerColor,
        shadowElevation = 16.dp,
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, style.borderColor, shape)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .padding(top = 1.dp)
                    .size(32.dp)
                    .background(style.iconChipColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = style.icon,
                    contentDescription = null,
                    tint = style.accentColor,
                    modifier = Modifier.size(17.dp)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                if (!label.isNullOrBlank()) {
                    Box(
                        modifier = Modifier
                            .background(style.badgeBackground, RoundedCornerShape(999.dp))
                            .padding(horizontal = 7.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = label.uppercase(),
                            color = style.accentColor,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    Spacer(modifier = Modifier.size(5.dp))
                }

                Text(
                    text = title,
                    color = style.titleColor,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )

                if (!message.isNullOrBlank()) {
                    Spacer(modifier = Modifier.size(4.dp))
                    Text(
                        text = message,
                        color = style.messageColor,
                        fontSize = 12.sp,
                        lineHeight = 16.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.width(6.dp))

            Box(
                modifier = Modifier
                    .background(style.closeChipColor, CircleShape)
                    .size(28.dp),
                contentAlignment = Alignment.Center
            ) {
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Close,
                        contentDescription = "Dismiss notification",
                        tint = style.dismissTint,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

private data class AppNotificationStyle(
    val containerColor: Color,
    val accentColor: Color,
    val borderColor: Color,
    val titleColor: Color,
    val messageColor: Color,
    val dismissTint: Color,
    val iconChipColor: Color,
    val closeChipColor: Color,
    val badgeBackground: Color,
    val icon: ImageVector
)

private fun AppStatusTone.notificationStyle(): AppNotificationStyle {
    return when (this) {
        AppStatusTone.Error -> AppNotificationStyle(
            containerColor = Color(0xFF162033),
            accentColor = Color(0xFFFF7272),
            borderColor = Color(0xFF2A3548),
            titleColor = Color(0xFFF8FAFC),
            messageColor = Color(0xFFD2D9E5),
            dismissTint = Color(0xFFAAB6C8),
            iconChipColor = Color(0x26FF7272),
            closeChipColor = Color(0x14FFFFFF),
            badgeBackground = Color(0x1FFF7272),
            icon = Icons.Outlined.ErrorOutline
        )

        AppStatusTone.Warning -> AppNotificationStyle(
            containerColor = Color(0xFF162033),
            accentColor = Color(0xFFF4B740),
            borderColor = Color(0xFF2A3548),
            titleColor = Color(0xFFF8FAFC),
            messageColor = Color(0xFFD2D9E5),
            dismissTint = Color(0xFFAAB6C8),
            iconChipColor = Color(0x26F4B740),
            closeChipColor = Color(0x14FFFFFF),
            badgeBackground = Color(0x1FF4B740),
            icon = Icons.Outlined.WarningAmber
        )

        AppStatusTone.Success -> AppNotificationStyle(
            containerColor = Color(0xFF162033),
            accentColor = Color(0xFF34D399),
            borderColor = Color(0xFF2A3548),
            titleColor = Color(0xFFF8FAFC),
            messageColor = Color(0xFFD2D9E5),
            dismissTint = Color(0xFFAAB6C8),
            iconChipColor = Color(0x2634D399),
            closeChipColor = Color(0x14FFFFFF),
            badgeBackground = Color(0x1F34D399),
            icon = Icons.Outlined.CheckCircle
        )

        AppStatusTone.Info -> AppNotificationStyle(
            containerColor = Color(0xFF162033),
            accentColor = Color(0xFF60A5FA),
            borderColor = Color(0xFF2A3548),
            titleColor = Color(0xFFF8FAFC),
            messageColor = Color(0xFFD2D9E5),
            dismissTint = Color(0xFFAAB6C8),
            iconChipColor = Color(0x2660A5FA),
            closeChipColor = Color(0x14FFFFFF),
            badgeBackground = Color(0x1F60A5FA),
            icon = Icons.Outlined.Info
        )
    }
}

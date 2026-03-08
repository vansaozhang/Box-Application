package com.aeu.boxapplication.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class AppStatusTone {
    Error,
    Warning,
    Success,
    Info
}

@Composable
fun AppStatusBanner(
    title: String,
    message: String? = null,
    modifier: Modifier = Modifier,
    tone: AppStatusTone = AppStatusTone.Info,
    label: String? = null,
    onDismiss: (() -> Unit)? = null
) {
    val style = tone.bannerStyle()
    val bannerShape = RoundedCornerShape(18.dp)

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, style.borderColor, bannerShape),
        shape = bannerShape,
        color = style.containerColor,
        shadowElevation = 10.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(style.iconBackgroundColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = style.icon,
                    contentDescription = null,
                    tint = style.iconTintColor,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.size(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = (label ?: tone.defaultLabel()).uppercase(),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = style.labelColor
                )
                Spacer(modifier = Modifier.size(4.dp))
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = style.titleColor
                )
                if (!message.isNullOrBlank()) {
                    Spacer(modifier = Modifier.size(4.dp))
                    Text(
                        text = message,
                        fontSize = 12.sp,
                        lineHeight = 18.sp,
                        color = style.messageColor
                    )
                }
            }

            if (onDismiss != null) {
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Close,
                        contentDescription = "Dismiss message",
                        tint = style.labelColor,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

private data class AppStatusBannerStyle(
    val containerColor: Color,
    val borderColor: Color,
    val iconBackgroundColor: Color,
    val iconTintColor: Color,
    val labelColor: Color,
    val titleColor: Color,
    val messageColor: Color,
    val icon: ImageVector
)

private fun AppStatusTone.bannerStyle(): AppStatusBannerStyle {
    return when (this) {
        AppStatusTone.Error -> AppStatusBannerStyle(
            containerColor = Color(0xFFFFF5F5),
            borderColor = Color(0xFFF4C9CF),
            iconBackgroundColor = Color(0xFFFFE3E6),
            iconTintColor = Color(0xFFD92D20),
            labelColor = Color(0xFFB42318),
            titleColor = Color(0xFF7A271A),
            messageColor = Color(0xFFA63A2B),
            icon = Icons.Outlined.ErrorOutline
        )
        AppStatusTone.Warning -> AppStatusBannerStyle(
            containerColor = Color(0xFFFFFAEB),
            borderColor = Color(0xFFFCD79C),
            iconBackgroundColor = Color(0xFFFFE9B3),
            iconTintColor = Color(0xFFD97706),
            labelColor = Color(0xFFB45309),
            titleColor = Color(0xFF92400E),
            messageColor = Color(0xFFB45309),
            icon = Icons.Outlined.WarningAmber
        )
        AppStatusTone.Success -> AppStatusBannerStyle(
            containerColor = Color(0xFFF0FDF4),
            borderColor = Color(0xFFB7E4C0),
            iconBackgroundColor = Color(0xFFDDF8E4),
            iconTintColor = Color(0xFF16A34A),
            labelColor = Color(0xFF15803D),
            titleColor = Color(0xFF166534),
            messageColor = Color(0xFF15803D),
            icon = Icons.Outlined.CheckCircle
        )
        AppStatusTone.Info -> AppStatusBannerStyle(
            containerColor = Color(0xFFF4F8FF),
            borderColor = Color(0xFFC7D8F6),
            iconBackgroundColor = Color(0xFFE3EEFF),
            iconTintColor = Color(0xFF2563EB),
            labelColor = Color(0xFF1D4ED8),
            titleColor = Color(0xFF1E3A8A),
            messageColor = Color(0xFF3B5AA3),
            icon = Icons.Outlined.Info
        )
    }
}

private fun AppStatusTone.defaultLabel(): String {
    return when (this) {
        AppStatusTone.Error -> "Error"
        AppStatusTone.Warning -> "Warning"
        AppStatusTone.Success -> "Success"
        AppStatusTone.Info -> "Info"
    }
}

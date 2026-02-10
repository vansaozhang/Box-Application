package com.aeu.boxapplication.presentation.subscriber

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Store
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class SubscriberBottomNavItem {
    Home,
    History,
    Shop,
    Profile
}

@Composable
fun SubscriberBottomNav(
    selected: SubscriberBottomNavItem,
    onHomeClick: () -> Unit = {},
    onHistoryClick: () -> Unit = {},
    onShopClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = Color.White,
        shadowElevation = 6.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp, horizontal = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomNavItem(
                icon = Icons.Outlined.Home,
                label = "Home",
                selected = selected == SubscriberBottomNavItem.Home,
                onClick = onHomeClick
            )
            BottomNavItem(
                icon = Icons.Outlined.History,
                label = "History",
                selected = selected == SubscriberBottomNavItem.History,
                onClick = onHistoryClick
            )
            BottomNavItem(
                icon = Icons.Outlined.Store,
                label = "Shop",
                selected = selected == SubscriberBottomNavItem.Shop,
                onClick = onShopClick
            )
            BottomNavItem(
                icon = Icons.Outlined.Person,
                label = "Profile",
                selected = selected == SubscriberBottomNavItem.Profile,
                onClick = onProfileClick
            )
        }
    }
}

@Composable
private fun BottomNavItem(
    icon: ImageVector,
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val tint = if (selected) Color(0xFF1E88E5) else Color(0xFF9AA1AE)
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = tint,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            fontSize = 10.sp,
            color = tint
        )
    }
}

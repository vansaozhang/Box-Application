package com.aeu.boxapplication.presentation.subscription

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.LocalShipping
import androidx.compose.material.icons.outlined.Percent
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aeu.boxapplication.R
import com.aeu.boxapplication.ui.components.AppPrimaryButton

@Composable
fun SubscriptionsEmptyScreen(
    onBack: () -> Unit = {},
    onExplorePlans: () -> Unit = {},
    onRestorePurchases: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.Outlined.ArrowBack,
                        contentDescription = "Back",
                        tint = Color(0xFF1E88E5)
                    )
                }
                Text(
                    text = "Subscriptions",
                    modifier = Modifier
                        .padding(end = 48.dp)
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF2F3A4A)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Surface(
                shape = RoundedCornerShape(18.dp),
                color = Color(0xFFEDF6FF),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.welcome),
                    contentDescription = "Subscriptions illustration",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "No Active Subscriptions",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2F3A4A),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "You aren’t subscribed to any products yet.\nStart a plan to automate your orders and save\nmoney.",
                fontSize = 13.sp,
                color = Color(0xFF7B8794),
                textAlign = TextAlign.Center,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            FeatureRow(
                icon = Icons.Outlined.LocalShipping,
                title = "Free Shipping",
                subtitle = "On every single recurring order, no\nminimums."
            )

            Spacer(modifier = Modifier.height(14.dp))

            FeatureRow(
                icon = Icons.Outlined.Percent,
                title = "Save 15% Instantly",
                subtitle = "Subscribers get our best price compared\nto one-time purchases."
            )

            Spacer(modifier = Modifier.height(14.dp))

            FeatureRow(
                icon = Icons.Outlined.CalendarMonth,
                title = "Full Control",
                subtitle = "Pause, skip, or cancel your deliveries at\nany time."
            )

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = "Restore Purchases",
                fontSize = 13.sp,
                color = Color(0xFF1E88E5),
                modifier = Modifier
                    .padding(top = 4.dp)
                    .clickable(
                        onClick = onRestorePurchases,
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    )
            )

            Spacer(modifier = Modifier.height(18.dp))

            AppPrimaryButton(
                text = "Explore Plans  →",
                onClick = onExplorePlans
            )

            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

@Composable
private fun FeatureRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(Color(0xFFEAF3FF), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color(0xFF1E88E5),
                modifier = Modifier.size(18.dp)
            )
        }
        Spacer(modifier = Modifier.size(12.dp))
        Column {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF2F3A4A)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                fontSize = 12.sp,
                color = Color(0xFF7B8794),
                lineHeight = 16.sp
            )
        }
    }
}

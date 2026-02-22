    package com.aeu.boxapplication.presentation.subscriber

    import androidx.compose.foundation.background
    import androidx.compose.foundation.border
    import androidx.compose.foundation.clickable
    import androidx.compose.foundation.layout.*
    import androidx.compose.foundation.rememberScrollState
    import androidx.compose.foundation.shape.CircleShape
    import androidx.compose.foundation.shape.RoundedCornerShape
    import androidx.compose.foundation.verticalScroll
    import androidx.compose.material.icons.Icons
    import androidx.compose.material.icons.outlined.AccountCircle
    import androidx.compose.material.icons.outlined.ArrowForward
    import androidx.compose.material.icons.outlined.Notifications
    import androidx.compose.material3.*
    import androidx.compose.runtime.Composable
    import androidx.compose.ui.Alignment
    import androidx.compose.ui.Modifier
    import androidx.compose.ui.draw.clip
    import androidx.compose.ui.graphics.Brush
    import androidx.compose.ui.graphics.Color
    import androidx.compose.ui.text.font.FontWeight
    import androidx.compose.ui.text.style.TextAlign
    import androidx.compose.ui.unit.dp
    import androidx.compose.ui.unit.sp
    import androidx.navigation.NavController
    import com.aeu.boxapplication.domain.model.OrderHistoryItem
    import com.aeu.boxapplication.domain.model.dummyOrders

    @Composable
    fun OrderHistoryScreen(
        navController: NavController,
        onOrderClick: (String) -> Unit = {}, // FIXED: Now takes a String ID
        onLoadPrevious: () -> Unit = {},
        onNotificationsClick: () -> Unit = {}
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF7F7F9))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 16.dp)
                    .padding(bottom = 84.dp)
            ) {
                OrderHistoryHeader(onNotificationsClick = onNotificationsClick)
                Spacer(modifier = Modifier.height(18.dp))
                OrderHistoryTitle()
                Spacer(modifier = Modifier.height(16.dp))

                // FIXED: Passing the lambda to the list
                OrderHistoryList(onOrderClick = onOrderClick)

                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Load previous orders",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF1E88E5),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = onLoadPrevious)
                        .padding(vertical = 6.dp),
                    textAlign = TextAlign.Center
                )
            }
        }
    }

    @Composable
    private fun OrderHistoryHeader(onNotificationsClick: () -> Unit) {
        Row(
            modifier = Modifier.fillMaxWidth().statusBarsPadding(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE3F2FD)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.AccountCircle,
                        contentDescription = null,
                        tint = Color(0xFF1E88E5),
                        modifier = Modifier.size(30.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text("Sarah", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Text("Member since 2021", fontSize = 12.sp, color = Color.Gray)
                }
            }
            IconButton(onClick = onNotificationsClick) {
                Icon(Icons.Outlined.Notifications, contentDescription = null)
            }
        }
    }

    @Composable
    private fun OrderHistoryTitle() {
        Column {
            Text("History", fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Text("Track your past boxes and re-orders", fontSize = 12.sp, color = Color.Gray)
        }
    }

    @Composable
    private fun OrderHistoryList(onOrderClick: (String) -> Unit) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            // Explicitly tell Kotlin to use the Domain OrderHistoryItem
            dummyOrders.forEach { order: com.aeu.boxapplication.domain.model.OrderHistoryItem ->
                OrderHistoryCard(
                    order = order, // This should now be blue (valid)
                    onClick = { onOrderClick(order.id) }
                )
            }
        }
    }
    @Composable
    private fun OrderHistoryCard(
        order: OrderHistoryItem,
        onClick: () -> Unit
    ) {
        Surface(
            onClick = onClick,
            shape = RoundedCornerShape(18.dp),
            color = Color.White,
            shadowElevation = 1.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(70.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Brush.linearGradient(order.imageColors)),
                    contentAlignment = Alignment.BottomStart
                ) {
                    Text(
                        text = order.imageLabel,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(8.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(order.title, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    Text("${order.date} • ${order.type}", fontSize = 11.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(order.price, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Box(
                            modifier = Modifier.clip(RoundedCornerShape(12.dp)).background(order.statusBg).padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(order.status, fontSize = 10.sp, color = order.statusColor, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Icon(
                    imageVector = Icons.Outlined.ArrowForward,
                    contentDescription = null,
                    tint = Color(0xFF1E88E5),
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }


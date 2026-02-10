package com.aeu.boxapplication.presentation.subscriber

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aeu.boxapplication.ui.components.AppPrimaryButton

@Composable
fun ProductDetailsScreen(
    onBack: () -> Unit = {},
    onReOrderClick: () -> Unit = {}
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
                .padding(horizontal = 20.dp, vertical = 14.dp)
                .padding(bottom = 88.dp)
        ) {
            TopBar(onBack = onBack)
            Spacer(modifier = Modifier.height(14.dp))
            ProductHero()
            Spacer(modifier = Modifier.height(12.dp))
            ProductThumbs()
            Spacer(modifier = Modifier.height(16.dp))
            ProductHeader()
            Spacer(modifier = Modifier.height(10.dp))
            ProductMeta()
            Spacer(modifier = Modifier.height(18.dp))
            AboutSection()
            Spacer(modifier = Modifier.height(18.dp))
            ReviewsSection()
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                QuantitySelector()
                AppPrimaryButton(
                    text = "Re-Order Item",
                    onClick = onReOrderClick,
                    enabled = true,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun TopBar(onBack: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(onClick = onBack) {
            Icon(
                imageVector = Icons.Outlined.ArrowBack,
                contentDescription = "Back"
            )
        }
        Text(
            text = "Product Details",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF111827)
        )
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(Color(0xFFF1F5F9), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.FavoriteBorder,
                contentDescription = "Favorite",
                tint = Color(0xFF111827),
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
private fun ProductHero() {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .background(
                    Brush.linearGradient(
                        listOf(Color(0xFF1B1712), Color(0xFF493A2B))
                    )
                ),
            contentAlignment = Alignment.BottomEnd
        ) {
            Box(
                modifier = Modifier
                    .padding(10.dp)
                    .background(Color.Black.copy(alpha = 0.7f), RoundedCornerShape(14.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "1/4",
                    fontSize = 11.sp,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
private fun ProductThumbs() {
    Row(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        ThumbItem(selected = true)
        ThumbItem(selected = false)
        ThumbItem(selected = false)
    }
}

@Composable
private fun ThumbItem(selected: Boolean) {
    Box(
        modifier = Modifier
            .size(56.dp)
            .background(Color(0xFFF3F4F6), RoundedCornerShape(12.dp))
            .border(
                width = if (selected) 2.dp else 1.dp,
                color = if (selected) Color(0xFF1E88E5) else Color(0xFFE5E7EB),
                shape = RoundedCornerShape(12.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "IMG",
            fontSize = 10.sp,
            color = Color(0xFF9AA1AE)
        )
    }
}

@Composable
private fun ProductHeader() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Ethiopian Yirgacheffe",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF111827)
            )
            Text(
                text = "Whole Bean • Medium Roast • 12oz",
                fontSize = 12.sp,
                color = Color(0xFF6B7280)
            )
        }
        Text(
            text = "$18.00",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1E88E5)
        )
    }
    Spacer(modifier = Modifier.height(6.dp))
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .background(Color(0xFFE6F7EE), RoundedCornerShape(12.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(
                text = "In Stock",
                fontSize = 11.sp,
                color = Color(0xFF1E9E62),
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun ProductMeta() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Outlined.Star,
            contentDescription = null,
            tint = Color(0xFFF59E0B),
            modifier = Modifier.size(16.dp)
        )
        Text(
            text = "4.8",
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF111827),
            modifier = Modifier.padding(start = 6.dp)
        )
        Text(
            text = "(128 reviews)",
            fontSize = 12.sp,
            color = Color(0xFF6B7280),
            modifier = Modifier.padding(start = 6.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Box(
            modifier = Modifier
                .size(4.dp)
                .background(Color(0xFFD1D5DB), CircleShape)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = "Freshly Roasted",
            fontSize = 12.sp,
            color = Color(0xFF6B7280)
        )
    }
}

@Composable
private fun AboutSection() {
    Text(
        text = "About this Coffee",
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF111827)
    )
    Spacer(modifier = Modifier.height(8.dp))
    Text(
        text = "Sourced from the high-altitude regions of Yirgacheffe, this coffee offers a delicate, tea-like body with distinct floral notes. You'll experience bright acidity with hints of jasmine, bergamot, and citrus. Perfect for pour-over brewing methods to highlight its complex flavor profile.",
        fontSize = 12.sp,
        color = Color(0xFF6B7280),
        lineHeight = 18.sp
    )
    Spacer(modifier = Modifier.height(12.dp))
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        TagChip(text = "Floral")
        TagChip(text = "Citrus")
        TagChip(text = "Jasmine")
    }
}

@Composable
private fun TagChip(text: String) {
    Box(
        modifier = Modifier
            .background(Color(0xFFF3F4F6), RoundedCornerShape(16.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = text,
            fontSize = 11.sp,
            color = Color(0xFF4B5563)
        )
    }
}

@Composable
private fun ReviewsSection() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Customer Reviews",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF111827)
        )
        Text(
            text = "View All",
            fontSize = 12.sp,
            color = Color(0xFF1E88E5),
            fontWeight = FontWeight.SemiBold
        )
    }
    Spacer(modifier = Modifier.height(10.dp))
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        shadowElevation = 1.dp
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .background(Color(0xFFE5E7EB), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "JD",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF6B7280)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "John D.",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF111827)
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        repeat(5) {
                            Icon(
                                imageVector = Icons.Outlined.Star,
                                contentDescription = null,
                                tint = Color(0xFFF59E0B),
                                modifier = Modifier.size(12.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "2 days ago",
                    fontSize = 10.sp,
                    color = Color(0xFF9AA1AE)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Absolutely love the floral notes in this roast. It's become my morning staple. Highly recommend for anyone who enjoys lighter roasts.",
                fontSize = 12.sp,
                color = Color(0xFF6B7280),
                lineHeight = 18.sp
            )
        }
    }
    Spacer(modifier = Modifier.height(12.dp))
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "✍  Write a Review",
                fontSize = 12.sp,
                color = Color(0xFF1E88E5),
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun QuantitySelector() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .background(Color(0xFFF3F4F6), RoundedCornerShape(14.dp))
            .padding(horizontal = 8.dp, vertical = 6.dp)
    ) {
        Text(
            text = "−",
            fontSize = 16.sp,
            color = Color(0xFF6B7280)
        )
        Text(
            text = "1",
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF111827)
        )
        Text(
            text = "+",
            fontSize = 16.sp,
            color = Color(0xFF6B7280)
        )
    }
}

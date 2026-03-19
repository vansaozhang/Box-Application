package com.aeu.boxapplication.presentation.subscriber

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

private val DetailPrimary = Color(0xFF1E88E5)
private val DetailTitle = Color(0xFF2F3A4A)
private val DetailBody = Color(0xFF7B8794)
private val DetailStroke = Color(0xFFE3E8EF)
private val DetailTint = Color(0xFFEAF3FF)
private val DetailBackground = Color(0xFFF3F5F9)
private val DetailGreen = Color(0xFF2E7D32)
private val DetailBadgeBg = Color(0xFFE8F5E9)

@Composable
fun PlanDetailsScreen(
    plan: ShopPlanUiModel,
    onBack: () -> Unit,
    hasCurrentSubscription: Boolean = false,
    onManageCurrentSubscription: () -> Unit = {},
    onSubscribe: (ShopPlanUiModel, ShopPlanFrequencyOptionUiModel?) -> Unit
) {
    val defaultOption = remember(plan.id) { defaultSelectedFrequencyOption(plan) }
    val hasSelectableFrequencyOptions = plan.frequencyOptions.size > 1

    var selectedFrequencyOptionId by rememberSaveable(plan.id) {
        mutableStateOf(defaultOption?.id)
    }

    val selectedFrequencyOption = if (hasSelectableFrequencyOptions) {
        plan.frequencyOptions.firstOrNull { it.id == selectedFrequencyOptionId } ?: defaultOption
    } else {
        null
    }
    val displayedPriceLabel = selectedFrequencyOption?.priceLabel ?: plan.priceLabel
    val displayedPeriodLabel = selectedFrequencyOption?.periodLabel ?: plan.periodLabel
    val subscribeFrequencyOption = if (hasSelectableFrequencyOptions) {
        selectedFrequencyOption
    } else {
        plan.frequencyOptions.firstOrNull()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 104.dp)
        ) {
            PlanHeroHeader(
                title = plan.title,
                imageUrl = plan.imageUrl,
                onBack = onBack
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(horizontal = 24.dp, vertical = 22.dp),
                verticalArrangement = Arrangement.spacedBy(26.dp)
            ) {
                PlanSummaryContent(
                    plan = plan,
                    displayedPriceLabel = displayedPriceLabel,
                    displayedPeriodLabel = displayedPeriodLabel,
                    selectedFrequencyOptionId = selectedFrequencyOptionId,
                    onSelectFrequencyOption = { selectedFrequencyOptionId = it }
                )

                if (plan.products.isNotEmpty()) {
                    ProductGridSection(products = plan.products)
                }
            }
        }

        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(),
            color = Color.White,
            shadowElevation = 18.dp
        ) {
            Button(
                onClick = {
                    if (hasCurrentSubscription) {
                        onManageCurrentSubscription()
                    } else {
                        onSubscribe(plan, subscribeFrequencyOption)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp)
                    .height(54.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = DetailPrimary)
            ) {
                Text(
                    text = if (hasCurrentSubscription) {
                        "View Current Subscription"
                    } else {
                        "Subscribe for $displayedPriceLabel$displayedPeriodLabel"
                    },
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
private fun PlanHeroHeader(
    title: String,
    imageUrl: String?,
    onBack: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(284.dp)
            .background(DetailTint)
    ) {
        if (!imageUrl.isNullOrBlank()) {
            AsyncImage(
                model = imageUrl,
                contentDescription = title,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Text(
                    text = planMonogram(title),
                    fontSize = 72.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = DetailPrimary
                )
            }
        }

        if (!imageUrl.isNullOrBlank()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(DetailTint.copy(alpha = 0.18f))
            )
        }

        IconButton(
            onClick = onBack,
            modifier = Modifier
                .statusBarsPadding()
                .padding(start = 10.dp, top = 6.dp)
                .size(44.dp)
                .clip(CircleShape)
                .background(Color(0xFFADB3BC).copy(alpha = 0.96f))
        ) {
            Icon(
                imageVector = Icons.Filled.ArrowBack,
                contentDescription = "Back",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
private fun PlanSummaryContent(
    plan: ShopPlanUiModel,
    displayedPriceLabel: String,
    displayedPeriodLabel: String,
    selectedFrequencyOptionId: String?,
    onSelectFrequencyOption: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (!plan.badge.isNullOrBlank()) {
            Surface(
                shape = RoundedCornerShape(999.dp),
                color = DetailBadgeBg
            ) {
                Text(
                    text = plan.badge,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = DetailGreen,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                )
            }
        }

        Text(
            text = plan.title,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = DetailTitle
        )

        if (plan.subtitle.isNotBlank()) {
            Text(
                text = plan.subtitle,
                fontSize = 14.sp,
                color = DetailBody,
                lineHeight = 21.sp
            )
        }

        if (plan.rating > 0) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = null,
                    tint = Color(0xFFFFC107),
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = plan.ratingLabel,
                    fontSize = 13.sp,
                    color = DetailBody,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Row(verticalAlignment = Alignment.Bottom) {
            Text(
                text = displayedPriceLabel,
                fontSize = 34.sp,
                fontWeight = FontWeight.ExtraBold,
                color = DetailPrimary
            )
            Spacer(modifier = Modifier.size(6.dp))
            Text(
                text = displayedPeriodLabel,
                fontSize = 15.sp,
                color = DetailBody,
                modifier = Modifier.padding(bottom = 5.dp)
            )
        }

        if (plan.frequencyOptions.size > 1) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "DELIVERY FREQUENCY",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = DetailTitle
                )

                val rows = if (plan.frequencyOptions.size <= 3) {
                    listOf(plan.frequencyOptions)
                } else {
                    plan.frequencyOptions.chunked(2)
                }

                rows.forEach { rowOptions ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        rowOptions.forEach { option ->
                            FrequencyOptionChip(
                                option = option,
                                selected = option.id == selectedFrequencyOptionId,
                                onClick = { onSelectFrequencyOption(option.id) },
                                modifier = Modifier.weight(1f)
                            )
                        }

                        if (rowOptions.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FrequencyOptionChip(
    option: ShopPlanFrequencyOptionUiModel,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val containerColor = if (selected) Color(0xFFEAF3FF) else Color.White
    val titleColor = DetailTitle
    val priceColor = DetailPrimary
    val borderColor = if (selected) DetailPrimary else DetailStroke
    val interactionSource = remember { MutableInteractionSource() }

    Surface(
        shape = RoundedCornerShape(14.dp),
        color = containerColor,
        modifier = modifier
            .border(
                width = 1.2.dp,
                color = borderColor,
                shape = RoundedCornerShape(14.dp)
            )
            .clip(RoundedCornerShape(14.dp))
            .clickable(
                onClick = onClick,
                indication = null,
                interactionSource = interactionSource
            )
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(0.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 10.dp)
        ) {
            Text(
                text = option.label,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = titleColor
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = option.priceLabel,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = priceColor
            )
        }
    }
}

@Composable
private fun ProductGridSection(
    products: List<ShopProductUiModel>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = "What's inside",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = DetailTitle
            )
            Text(
                text = "${products.size} products included",
                fontSize = 13.sp,
                color = DetailBody
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(18.dp)) {
            products.chunked(2).forEach { rowProducts ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    rowProducts.forEach { product ->
                        ProductDetailCard(
                            product = product,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    if (rowProducts.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
private fun ProductDetailCard(
    product: ShopProductUiModel,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = DetailTint,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
        ) {
            if (!product.imageUrl.isNullOrBlank()) {
                AsyncImage(
                    model = product.imageUrl,
                    contentDescription = product.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(
                        text = product.name.take(2).uppercase(),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = DetailPrimary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = product.name,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = DetailTitle,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            lineHeight = 19.sp,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 38.dp)
        )

        if (product.price > 0) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = product.priceLabel,
                fontSize = 12.sp,
                color = DetailBody
            )
        }
    }
}

private fun defaultSelectedFrequencyOption(plan: ShopPlanUiModel): ShopPlanFrequencyOptionUiModel? {
    return plan.frequencyOptions.firstOrNull { it.periodLabel == "/mo" }
        ?: plan.frequencyOptions.firstOrNull()
}

private fun planMonogram(title: String): String {
    return title
        .split(" ")
        .filter { it.isNotBlank() }
        .take(2)
        .joinToString("") { it.take(1).uppercase() }
        .ifBlank { "BX" }
}

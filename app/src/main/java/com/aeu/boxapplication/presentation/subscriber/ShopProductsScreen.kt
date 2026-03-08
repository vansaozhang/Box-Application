package com.aeu.boxapplication.presentation.subscriber

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aeu.boxapplication.ui.components.AppGlobalLoadingEffect
import com.aeu.boxapplication.ui.components.AppStatusBanner
import com.aeu.boxapplication.ui.components.AppStatusTone

private val ShopPrimary = Color(0xFF1E88E5)
private val ShopTitle = Color(0xFF2F3A4A)
private val ShopBody = Color(0xFF7B8794)
private val ShopStroke = Color(0xFFE3E8EF)
private val ShopTint = Color(0xFFEAF3FF)
private val ShopBackground = Color(0xFFFFFFFF)
val BoxlyTeal = Color(0xFF1CE5D1)
val BoxlyBackground = Color(0xFFF8FAFB)
val BoxlyDarkText = Color(0xFF1A1C1E)

@Composable
fun ShopProductsScreen(
    viewModel: ShopProductsViewModel,
    onSelectPlan: (ShopPlanUiModel) -> Unit
) {
    val uiState = viewModel.uiState

    LaunchedEffect(Unit) {
        viewModel.loadStorefront()
    }

    AppGlobalLoadingEffect(
        isVisible = uiState.isLoading && uiState.featuredPlan == null && uiState.plans.isEmpty()
    )

    Scaffold(
        containerColor = ShopBackground,
        topBar = {
            TopHeader(
                onRefresh = { viewModel.loadStorefront(forceRefresh = true) }
            )
        }
    ) { paddingValues ->
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .background(ShopBackground)
                .padding(paddingValues)
        ) {
            val isCompactHeight = maxHeight < 780.dp
            val horizontalPadding = if (isCompactHeight) 16.dp else 20.dp

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                if (uiState.isRefreshing) {
                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth(),
                        color = ShopPrimary,
                        trackColor = ShopPrimary.copy(alpha = 0.14f)
                    )
                }

                SearchBarSection(
                    query = uiState.searchQuery,
                    onQueryChange = viewModel::updateSearchQuery,
                    horizontalPadding = horizontalPadding
                )

                CategoryChipsSection(
                    categories = uiState.categories,
                    selectedCategory = uiState.selectedCategory,
                    onCategorySelected = viewModel::selectCategory,
                    isCompact = isCompactHeight,
                    horizontalPadding = horizontalPadding
                )

                uiState.errorMessage?.let { message ->
                    AppStatusBanner(
                        title = "Plan catalog unavailable",
                        message = message,
                        tone = AppStatusTone.Error,
                        onDismiss = viewModel::dismissError,
                        modifier = Modifier.padding(horizontal = horizontalPadding, vertical = 8.dp)
                    )
                }

                FeaturedBoxCard(
                    plan = uiState.featuredPlan,
                    isCompact = isCompactHeight,
                    horizontalPadding = horizontalPadding,
                    onSelectPlan = onSelectPlan
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = horizontalPadding,
                            vertical = if (isCompactHeight) 12.dp else 16.dp
                        ),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Discover All Boxes",
                        fontSize = if (isCompactHeight) 20.sp else 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = ShopTitle
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { viewModel.toggleSort() }
                    ) {
                        Text(
                            text = "Sort by",
                            color = ShopPrimary,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Icon(
                            imageVector = if (uiState.sort == ShopCatalogSort.Rating) {
                                Icons.Default.Star
                            } else {
                                Icons.Outlined.ShoppingCart
                            },
                            contentDescription = "Toggle sort",
                            tint = ShopPrimary,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }

                Column(modifier = Modifier.padding(horizontal = horizontalPadding)) {
                    if (uiState.featuredPlan == null && uiState.plans.isEmpty() && !uiState.isLoading) {
                        EmptyCatalogCard(
                            onReload = { viewModel.loadStorefront(forceRefresh = true) }
                        )
                    } else {
                        uiState.plans.forEach { plan ->
                            SubscriptionItem(
                                plan = plan,
                                onSelectPlan = onSelectPlan,
                                isCompact = isCompactHeight
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(if (isCompactHeight) 20.dp else 28.dp))
            }
        }
    }
}

@Composable
private fun TopHeader(onRefresh: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(ShopPrimary, RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Outlined.ShoppingCart,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "Boxly",
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                color = ShopTitle
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onRefresh) {
                Icon(
                    Icons.Outlined.ShoppingCart,
                    contentDescription = "Refresh plans",
                    tint = ShopTitle
                )
            }
            IconButton(onClick = onRefresh) {
                Icon(
                    Icons.Default.Notifications,
                    contentDescription = "Refresh plans",
                    tint = ShopTitle
                )
            }
        }
    }
}

@Composable
private fun SearchBarSection(
    query: String,
    onQueryChange: (String) -> Unit,
    horizontalPadding: Dp
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = horizontalPadding),
        placeholder = { Text("Search boxes, brands, or categories...") },
        leadingIcon = {
            Icon(Icons.Default.Search, contentDescription = null, tint = ShopBody)
        },
        singleLine = true,
        shape = RoundedCornerShape(25.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            disabledContainerColor = Color.White,
            focusedBorderColor = ShopStroke,
            unfocusedBorderColor = ShopStroke
        )
    )
}

@Composable
private fun CategoryChipsSection(
    categories: List<String>,
    selectedCategory: String,
    onCategorySelected: (String) -> Unit,
    isCompact: Boolean,
    horizontalPadding: Dp
) {
    Row(
        modifier = Modifier
            .padding(vertical = if (isCompact) 12.dp else 16.dp)
            .horizontalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.width(horizontalPadding))
        categories.forEach { category ->
            val isSelected = category == selectedCategory
            Surface(
                modifier = Modifier.padding(end = 8.dp),
                shape = RoundedCornerShape(20.dp),
                color = if (isSelected) ShopPrimary else Color.White,
                border = BorderStroke(1.dp, ShopStroke)
            ) {
                Text(
                    text = category,
                    modifier = Modifier
                        .clickable { onCategorySelected(category) }
                        .padding(
                            horizontal = if (isCompact) 20.dp else 24.dp,
                            vertical = if (isCompact) 8.dp else 10.dp
                        ),
                    color = if (isSelected) Color.White else ShopBody,
                    fontWeight = FontWeight.Medium,
                    fontSize = if (isCompact) 14.sp else 16.sp
                )
            }
        }
    }
}

@Composable
private fun FeaturedBoxCard(
    plan: ShopPlanUiModel?,
    isCompact: Boolean,
    horizontalPadding: Dp,
    onSelectPlan: (ShopPlanUiModel) -> Unit
) {
    if (plan == null) {
        return
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(if (isCompact) 228.dp else 250.dp)
            .padding(horizontal = horizontalPadding),
        shape = RoundedCornerShape(28.dp),
        color = Color.White,
        border = BorderStroke(1.dp, ShopStroke)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                ShopPrimary.copy(alpha = 0.95f),
                                ShopTitle
                            )
                        )
                    )
            ) {
                Surface(
                    color = ShopTint,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(start = 20.dp, top = 16.dp)
                ) {
                    Text(
                        text = plan.featuredLabel ?: "FEATURED",
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = ShopPrimary
                    )
                }

                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(horizontal = 20.dp, vertical = 16.dp)
                ) {
                    Text(
                        text = plan.title,
                        color = Color.White,
                        fontSize = if (isCompact) 22.sp else 26.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Column(
                modifier = Modifier.padding(
                    horizontal = 20.dp,
                    vertical = if (isCompact) 12.dp else 16.dp
                )
            ) {
                Text(
                    text = plan.subtitle,
                    color = ShopBody,
                    fontSize = if (isCompact) 12.sp else 13.sp,
                    lineHeight = if (isCompact) 16.sp else 18.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(if (isCompact) 10.dp else 14.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = plan.priceLabel,
                            color = ShopTitle,
                            fontSize = if (isCompact) 22.sp else 26.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = plan.periodLabel,
                            color = ShopBody,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(start = 4.dp, bottom = 3.dp)
                        )
                    }
                    Button(
                        onClick = { onSelectPlan(plan) },
                        modifier = Modifier.height(if (isCompact) 40.dp else 44.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ShopPrimary),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text(
                            text = "Get Started",
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = if (isCompact) 14.sp else 16.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SubscriptionItem(
    plan: ShopPlanUiModel,
    onSelectPlan: (ShopPlanUiModel) -> Unit,
    isCompact: Boolean
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = if (isCompact) 6.dp else 8.dp),
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        border = BorderStroke(1.dp, ShopStroke)
    ) {
        Row(
            modifier = Modifier.padding(if (isCompact) 10.dp else 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(if (isCompact) 88.dp else 100.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF1F5F9))
            ) {
                plan.badge?.let { badge ->
                    Surface(
                        color = ShopPrimary.copy(alpha = 0.18f),
                        modifier = Modifier.padding(8.dp),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = badge,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = ShopPrimary,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 3.dp)
                        )
                    }
                }
            }

            Column(
                modifier = Modifier
                    .padding(start = 12.dp)
                    .weight(1f)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = plan.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = if (isCompact) 15.sp else 16.sp,
                        color = ShopTitle,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Star,
                            contentDescription = null,
                            tint = ShopPrimary,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = plan.ratingLabel,
                            fontSize = if (isCompact) 11.sp else 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = ShopTitle
                        )
                    }
                }

                Text(
                    text = plan.subtitle,
                    fontSize = if (isCompact) 11.sp else 12.sp,
                    color = ShopBody,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(if (isCompact) 6.dp else 8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = plan.priceLabel,
                        fontWeight = FontWeight.Bold,
                        fontSize = if (isCompact) 15.sp else 16.sp,
                        color = ShopTitle
                    )
                    Text(
                        text = plan.periodLabel,
                        fontSize = 10.sp,
                        color = ShopBody
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = ShopTint,
                        border = BorderStroke(1.dp, ShopStroke),
                        modifier = Modifier.clickable { onSelectPlan(plan) }
                    ) {
                        Text(
                            text = "View Details",
                            modifier = Modifier.padding(
                                horizontal = if (isCompact) 10.dp else 12.dp,
                                vertical = if (isCompact) 5.dp else 6.dp
                            ),
                            fontSize = if (isCompact) 11.sp else 12.sp,
                            color = ShopPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyCatalogCard(onReload: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, ShopStroke)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "No packages match this filter.",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = ShopTitle
            )
            Text(
                text = "Try another category or refresh the package catalog.",
                fontSize = 12.sp,
                color = ShopBody,
                modifier = Modifier.padding(top = 6.dp)
            )
            TextButton(onClick = onReload) {
                Text("Reload", color = ShopPrimary, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

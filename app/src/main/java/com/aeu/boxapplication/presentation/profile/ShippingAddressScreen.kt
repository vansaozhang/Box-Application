package com.aeu.boxapplication.presentation.profile

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

private val AddressPrimary = Color(0xFF1E88E5)
private val AddressTitle = Color(0xFF2F3A4A)
private val AddressBody = Color(0xFF7B8794)
private val AddressStroke = Color(0xFFE3E8EF)
private val AddressTint = Color(0xFFEAF3FF)

private data class ShippingAddressUi(
    val id: String,
    val label: String,
    val fullName: String,
    val address: String,
    val phoneNumber: String,
    val isDefault: Boolean
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShippingAddressScreen(navController: NavController) {
    val addresses = remember {
        listOf(
            ShippingAddressUi(
                id = "home",
                label = "Home",
                fullName = "Sarah Jenkins",
                address = "123 Sunshine Boulevard, Apt 4B\nLos Angeles, CA 90001",
                phoneNumber = "+1 234 567 890",
                isDefault = true
            ),
            ShippingAddressUi(
                id = "office",
                label = "Office",
                fullName = "Sarah Jenkins",
                address = "456 Corporate Way, Level 12\nSanta Monica, CA 90401",
                phoneNumber = "+1 098 765 432",
                isDefault = false
            )
        )
    }

    Scaffold(
        containerColor = Color.White,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Shipping Address",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = AddressTitle
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = AddressTitle
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /* Handle Add New Address */ }) {
                        Icon(
                            imageVector = Icons.Outlined.Add,
                            contentDescription = "Add",
                            tint = AddressPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            Surface(
                color = Color.White,
                shadowElevation = 8.dp
            ) {
                Button(
                    onClick = { /* Add Address */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 14.dp)
                        .height(56.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AddressPrimary,
                        contentColor = Color.White
                    )
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Add,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Add New Address", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 14.dp, bottom = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    text = "Saved Addresses",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = AddressBody
                )
            }
            items(addresses, key = { it.id }) { address ->
                AddressCard(
                    label = address.label,
                    fullName = address.fullName,
                    address = address.address,
                    phoneNumber = address.phoneNumber,
                    isDefault = address.isDefault,
                    onEditClick = { /* Navigate to Edit */ }
                )
            }
        }
    }
}

@Composable
fun AddressCard(
    label: String,
    fullName: String,
    address: String,
    phoneNumber: String,
    isDefault: Boolean,
    onEditClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        border = BorderStroke(if (isDefault) 1.5.dp else 1.dp, if (isDefault) AddressPrimary else AddressStroke),
        shadowElevation = if (isDefault) 2.dp else 1.dp
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(AddressTint, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (label == "Home") Icons.Outlined.Home else Icons.Outlined.LocationOn,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = AddressPrimary
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(text = label, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = AddressTitle)
                }

                if (isDefault) {
                    Surface(
                        color = AddressTint,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            "DEFAULT",
                            color = AddressPrimary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(text = fullName, fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = AddressTitle)
            Text(
                text = address,
                color = AddressBody,
                fontSize = 13.sp,
                lineHeight = 19.sp,
                modifier = Modifier.padding(vertical = 4.dp)
            )
            Text(text = phoneNumber, color = AddressBody, fontSize = 13.sp)

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 12.dp),
                color = AddressStroke
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onEditClick) {
                    Text("Edit", color = AddressPrimary, fontWeight = FontWeight.SemiBold)
                }
                TextButton(onClick = { /* Delete logic */ }) {
                    Text("Delete", color = AddressBody.copy(alpha = 0.55f))
                }
            }
        }
    }
}

package com.example.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ElectricRickshaw
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material.icons.filled.TwoWheeler
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BookingOrder
import com.example.data.model.VehicleCatalog
import com.example.data.model.VehicleTier
import com.example.ui.theme.AmberContainer
import com.example.ui.theme.AmberPrimary
import com.example.ui.theme.BorderLight
import com.example.ui.theme.LogisticsBlue
import com.example.ui.theme.LogisticsBlueContainer
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceLight
import com.example.ui.theme.SurfaceTertiary
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextMuted

@Composable
fun BookingFlowScreen(
    pickupAddress: String,
    dropoffAddress: String,
    selectedVehicleId: String,
    selectedGoodsType: String,
    isHelperRequired: Boolean,
    selectedPaymentMethod: String,
    onPickupChange: (String) -> Unit,
    onDropoffChange: (String) -> Unit,
    onVehicleSelect: (String) -> Unit,
    onGoodsSelect: (String) -> Unit,
    onToggleHelper: () -> Unit,
    onPaymentSelect: (String) -> Unit,
    calculateFare: (String) -> Double,
    onConfirmBooking: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val totalFare = calculateFare(selectedVehicleId)
    val selectedTier = VehicleCatalog.tiers.find { it.id == selectedVehicleId } ?: VehicleCatalog.tiers[0]

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SurfaceLight)
            .statusBarsPadding()
    ) {
        // Top App Bar
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = SurfaceCard,
            shadowElevation = 2.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = TextDark
                    )
                }
                Text(
                    text = "Confirm Freight Booking",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )
            }
        }

        // Scrollable Form
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Pickup & Dropoff Inputs Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp)
                ) {
                    // Pickup Input
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .background(SuccessGreen, CircleShape)
                        )
                        OutlinedTextField(
                            value = pickupAddress,
                            onValueChange = onPickupChange,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 10.dp)
                                .testTag("pickup_address_input"),
                            label = { Text("Pickup Location", fontSize = 12.sp) },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = AmberPrimary,
                                unfocusedBorderColor = BorderLight,
                                focusedContainerColor = SurfaceTertiary,
                                unfocusedContainerColor = SurfaceTertiary
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Dropoff Input
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .background(AmberPrimary, CircleShape)
                        )
                        OutlinedTextField(
                            value = dropoffAddress,
                            onValueChange = onDropoffChange,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 10.dp)
                                .testTag("dropoff_address_input"),
                            label = { Text("Drop-off Location", fontSize = 12.sp) },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = AmberPrimary,
                                unfocusedBorderColor = BorderLight,
                                focusedContainerColor = SurfaceTertiary,
                                unfocusedContainerColor = SurfaceTertiary
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Vehicle Tier Selector
            Text(
                text = "Select Vehicle Capacity",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark
            )
            Text(
                text = "Dynamic pricing based on weight & distance",
                fontSize = 12.sp,
                color = TextMuted,
                modifier = Modifier.padding(bottom = 10.dp)
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(VehicleCatalog.tiers) { tier ->
                    val isSelected = tier.id == selectedVehicleId
                    VehicleSelectorCard(
                        tier = tier,
                        isSelected = isSelected,
                        estimatedFare = calculateFare(tier.id),
                        onClick = { onVehicleSelect(tier.id) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Goods Type Chips
            Text(
                text = "Category of Goods",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark,
                modifier = Modifier.padding(bottom = 10.dp)
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(VehicleCatalog.goodsCategories) { category ->
                    val isSelected = category == selectedGoodsType
                    GoodsTypeChip(
                        category = category,
                        isSelected = isSelected,
                        onClick = { onGoodsSelect(category) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Helper Assistance Toggle
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onToggleHelper() }
                    .testTag("helper_toggle_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isHelperRequired) AmberContainer else SurfaceCard
                ),
                border = androidx.compose.foundation.BorderStroke(
                    width = 1.dp,
                    color = if (isHelperRequired) AmberPrimary else BorderLight
                )
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(
                                    if (isHelperRequired) AmberPrimary else SurfaceTertiary,
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.PersonAdd,
                                contentDescription = "Helper",
                                tint = if (isHelperRequired) Color.White else TextDark,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Column(modifier = Modifier.padding(start = 12.dp)) {
                            Text(
                                text = "Need Loading Help? (+₹80)",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextDark
                            )
                            Text(
                                text = "Trained driver helper for ground-to-ground loading",
                                fontSize = 11.sp,
                                color = TextMuted
                            )
                        }
                    }

                    Checkbox(
                        checked = isHelperRequired,
                        onCheckedChange = { onToggleHelper() },
                        colors = CheckboxDefaults.colors(
                            checkedColor = AmberPrimary,
                            uncheckedColor = TextMuted
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Payment Methods
            Text(
                text = "Payment Method",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark,
                modifier = Modifier.padding(bottom = 10.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                VehicleCatalog.paymentOptions.forEach { option ->
                    val isSelected = option == selectedPaymentMethod
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .border(
                                width = 1.dp,
                                color = if (isSelected) AmberPrimary else BorderLight,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable { onPaymentSelect(option) },
                        color = if (isSelected) AmberContainer else SurfaceCard
                    ) {
                        Column(
                            modifier = Modifier.padding(vertical = 10.dp, horizontal = 8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Payments,
                                contentDescription = option,
                                tint = if (isSelected) AmberPrimary else TextMuted,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = option.split(" ")[0],
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) AmberPrimary else TextDark,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Fare Breakdown Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceTertiary)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Fare Estimate Details",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    FareRow(label = "Base Fare (${selectedTier.name})", value = "₹${selectedTier.baseFare.toInt()}")
                    FareRow(label = "Estimated Distance (6.4 km)", value = "₹${(6.4 * selectedTier.perKmRate).toInt()}")
                    if (isHelperRequired) {
                        FareRow(label = "Helper Assistance", value = "₹80")
                    }
                    FareRow(label = "Tolls & GST", value = "Included")

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = BorderLight)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Estimated Total",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = TextDark
                        )
                        Text(
                            text = "₹${totalFare.toInt()}",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = AmberPrimary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }

        // Sticky Bottom CTA Bar
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding(),
            color = SurfaceCard,
            shadowElevation = 8.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "TOTAL ESTIMATE",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextMuted
                    )
                    Text(
                        text = "₹${totalFare.toInt()}",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = AmberPrimary
                    )
                }

                Button(
                    onClick = onConfirmBooking,
                    modifier = Modifier
                        .height(52.dp)
                        .testTag("confirm_booking_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AmberPrimary)
                ) {
                    Text(
                        text = "Confirm Booking",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = Color.White
                    )
                }
            }
        }
    }
}

@Composable
private fun FareRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 12.sp, color = TextMuted)
        Text(text = value, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextDark)
    }
}

@Composable
private fun VehicleSelectorCard(
    tier: VehicleTier,
    isSelected: Boolean,
    estimatedFare: Double,
    onClick: () -> Unit
) {
    val borderColor by animateColorAsState(
        targetValue = if (isSelected) AmberPrimary else BorderLight,
        label = "border"
    )
    val containerColor by animateColorAsState(
        targetValue = if (isSelected) AmberContainer else SurfaceCard,
        label = "container"
    )

    val icon: ImageVector = when (tier.id) {
        "bike" -> Icons.Default.TwoWheeler
        "auto" -> Icons.Default.ElectricRickshaw
        else -> Icons.Default.LocalShipping
    }

    Card(
        modifier = Modifier
            .width(155.dp)
            .clickable { onClick() }
            .testTag("vehicle_tier_${tier.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        border = androidx.compose.foundation.BorderStroke(width = 1.5.dp, color = borderColor),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 3.dp else 1.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(
                            if (isSelected) AmberPrimary else SurfaceTertiary,
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = tier.name,
                        tint = if (isSelected) Color.White else AmberPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                if (isSelected) {
                    Box(
                        modifier = Modifier
                            .size(18.dp)
                            .background(AmberPrimary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Selected",
                            tint = Color.White,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = tier.name,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark,
                maxLines = 1
            )
            Text(
                text = tier.capacity,
                fontSize = 11.sp,
                color = TextMuted
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "₹${estimatedFare.toInt()}",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (isSelected) AmberPrimary else TextDark
                )
                Text(
                    text = "${tier.etaMinutes}m ETA",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = SuccessGreen
                )
            }
        }
    }
}

@Composable
private fun GoodsTypeChip(
    category: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .border(
                width = 1.dp,
                color = if (isSelected) AmberPrimary else BorderLight,
                shape = RoundedCornerShape(20.dp)
            )
            .clickable { onClick() }
            .testTag("goods_chip_${category.take(4)}"),
        color = if (isSelected) AmberPrimary else SurfaceCard
    ) {
        Text(
            text = category,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) Color.White else TextDark,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
        )
    }
}

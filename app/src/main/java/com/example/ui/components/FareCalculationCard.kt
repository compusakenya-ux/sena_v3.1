package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.ChildCare
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.People
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.SenaBorder
import com.example.ui.theme.SenaElectricCyan
import com.example.ui.theme.SenaOrangeCTA
import com.example.ui.theme.SenaPeach
import com.example.ui.theme.SenaSuccessGreen
import com.example.ui.theme.SenaSurface
import com.example.ui.theme.SenaTextMuted
import com.example.ui.theme.SenaTextPrimary
import com.example.ui.theme.SenaTextSecondary

enum class PassengerOption(
    val label: String,
    val factor: Float,
    val description: String,
    val icon: ImageVector
) {
    ONE_ADULT("1 Adult", 1.0f, "1.0 Passenger Factor", Icons.Default.Person),
    TWO_ADULTS("2 Adults", 1.5f, "1.5 Passengers (SDR FR-C013)", Icons.Default.People),
    CHILD("1 Child", 0.5f, "0.5 Passenger (SDR FR-C013)", Icons.Default.ChildCare),
    PARCEL("Parcel Delivery", 0.5f, "0.5 Passenger Cargo", Icons.Default.LocalShipping)
}

/**
 * Calculates fare according to SDR v3.1.0 FR-C013 & FR-P006:
 * TOTAL FARE = (PASSENGER_FACTOR * BASE_FARE) + (BASE_FARE * DPI) + (KM ABOVE 1.5KM * RATE_PER_KM)
 */
fun computeSenaFare(
    vehicleType: String,
    passengerFactor: Float,
    distanceKm: Double,
    dpiMultiplier: Float
): Double {
    val baseFare = if (vehicleType == "TukTuk" || vehicleType.contains("Tuk-Tuk", ignoreCase = true)) 30.0 else 40.0
    val ratePerKm = if (vehicleType == "TukTuk" || vehicleType.contains("Tuk-Tuk", ignoreCase = true)) 20.0 else 22.0

    val kmAbove1_5 = if (distanceKm > 1.5) distanceKm - 1.5 else 0.0

    val passengerComponent = passengerFactor * baseFare
    val dpiComponent = baseFare * (dpiMultiplier - 1.0f).coerceAtLeast(0.0f)
    val distanceComponent = kmAbove1_5 * ratePerKm

    return passengerComponent + (baseFare * 1.0f) + dpiComponent + distanceComponent
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FareCalculationCard(
    vehicleName: String,
    baseFare: Double,
    ratePerKm: Double,
    distanceKm: Double,
    passengerOption: PassengerOption,
    dpiMultiplier: Float,
    totalFare: Double,
    onPassengerOptionSelect: (PassengerOption) -> Unit,
    modifier: Modifier = Modifier
) {
    val kmAbove1_5 = if (distanceKm > 1.5) distanceKm - 1.5 else 0.0
    val passengerCost = passengerOption.factor * baseFare
    val dpiCost = baseFare * dpiMultiplier
    val distanceCost = kmAbove1_5 * ratePerKm

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(22.dp))
            .padding(18.dp)
            .testTag("fare_calculation_formula_card")
    ) {
        Column {
            // Card Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Calculate,
                        contentDescription = "Fare Formula",
                        tint = SenaPeach,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "FARE CALCULATION (SDR FR-C013)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp,
                        color = SenaPeach
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "DPI: ${dpiMultiplier}x",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = SenaElectricCyan
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Passenger / Cargo Selection Chips (FlowRow)
            Text(
                text = "PASSENGERS / CARGO CONFIGURATION",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.2.sp,
                color = SenaTextMuted
            )

            Spacer(modifier = Modifier.height(8.dp))

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                PassengerOption.values().forEach { option ->
                    val isSelected = option == passengerOption
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (isSelected) SenaPeach.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant)
                            .border(
                                width = if (isSelected) 1.5.dp else 1.dp,
                                color = if (isSelected) SenaPeach else MaterialTheme.colorScheme.outline,
                                shape = RoundedCornerShape(14.dp)
                            )
                            .clickable { onPassengerOptionSelect(option) }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                            .testTag("passenger_chip_${option.name.lowercase()}")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = option.icon,
                                contentDescription = option.label,
                                tint = if (isSelected) SenaPeach else SenaTextSecondary,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "${option.label} (${option.factor}x)",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) SenaPeach else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Automated GPS Route Distance Box (Replacing manual slider)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .border(1.dp, SenaElectricCyan.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
                    .padding(horizontal = 12.dp, vertical = 10.dp)
                    .testTag("automated_gps_distance_card")
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(SenaElectricCyan.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "Automated GPS Distance",
                                tint = SenaElectricCyan,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "AUTOMATED GPS DISTANCE",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp,
                                color = SenaTextMuted
                            )
                            Text(
                                text = if (distanceKm <= 1.5) "No Surcharge (≤1.5 KM Base Included)" else "Automated via Route Telemetry",
                                fontSize = 10.sp,
                                color = if (distanceKm <= 1.5) SenaElectricCyan else SenaPeach
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (distanceKm <= 1.5) SenaElectricCyan.copy(alpha = 0.15f) else SenaPeach.copy(alpha = 0.15f))
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = "${String.format("%.1f", distanceKm)} KM",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            color = if (distanceKm <= 1.5) SenaElectricCyan else SenaPeach
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Exact Mathematical Formula Breakdown Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f))
                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(14.dp))
                    .padding(12.dp)
            ) {
                Column {
                    Text(
                        text = "Formula: (Pax × Base) + (Base × DPI) + (KM > 1.5 × Rate/KM)",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = SenaTextSecondary,
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "1. Passenger (${passengerOption.factor} × KES ${baseFare.toInt()}):",
                            fontSize = 11.sp,
                            color = SenaTextSecondary,
                            modifier = Modifier.weight(1f, fill = false),
                            maxLines = 1,
                            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "KES ${String.format("%,.1f", passengerCost)}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = SenaTextPrimary,
                            maxLines = 1
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "2. Base Fare + DPI Surge (${dpiMultiplier}x):",
                            fontSize = 11.sp,
                            color = SenaTextSecondary,
                            modifier = Modifier.weight(1f, fill = false),
                            maxLines = 1,
                            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "KES ${String.format("%,.1f", dpiCost)}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = SenaElectricCyan,
                            maxLines = 1
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "3. Distance (>1.5KM: ${String.format("%.1f", kmAbove1_5)} KM × KES ${ratePerKm.toInt()}):",
                            fontSize = 11.sp,
                            color = SenaTextSecondary,
                            modifier = Modifier.weight(1f, fill = false),
                            maxLines = 1,
                            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "KES ${String.format("%,.1f", distanceCost)}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = SenaTextPrimary,
                            maxLines = 1
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(SenaBorder)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "TOTAL CALCULATED FARE:",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = SenaPeach,
                            modifier = Modifier.weight(1f, fill = false),
                            maxLines = 1,
                            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "KES ${String.format("%,.0f", totalFare)}",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Black,
                            color = SenaPeach,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}

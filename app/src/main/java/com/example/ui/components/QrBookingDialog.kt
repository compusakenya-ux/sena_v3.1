package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Money
import androidx.compose.material.icons.filled.Moped
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Pin
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.TwoWheeler
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.SenaBorder
import com.example.ui.theme.SenaElectricCyan
import com.example.ui.theme.SenaOrangeCTA
import com.example.ui.theme.SenaPeach
import com.example.ui.theme.SenaSuccessGreen
import com.example.ui.theme.SenaSurface
import com.example.ui.theme.SenaTextMuted
import com.example.ui.theme.SenaTextPrimary
import com.example.ui.theme.SenaTextSecondary

/**
 * QR Web Booking Channel Dialog (SDR Section 4 & QR-001 through QR-018).
 * Simulates the destination-first PWA flow hosted at https://book.sena.ke/
 */
@Composable
fun QrBookingDialog(
    initialDestination: String = "Nyali Beach Resort",
    dpiMultiplier: Float = 1.2f,
    onCompleteBooking: (destination: String, passengerOption: PassengerOption, paymentMethod: String, fare: Double) -> Unit,
    onDismiss: () -> Unit
) {
    var step by remember { mutableStateOf(1) } // 1: Destination & Fare, 2: OTP Verification, 3: Completed
    var destination by remember { mutableStateOf(initialDestination) }
    var selectedVehicle by remember { mutableStateOf("Standard") }
    var passengerOption by remember { mutableStateOf(PassengerOption.ONE_ADULT) }
    var distanceKm by remember { mutableStateOf(3.2) }
    var phoneNumber by remember { mutableStateOf("254712345678") }
    var otpCode by remember { mutableStateOf("8421") }
    var paymentMethod by remember { mutableStateOf("M-Pesa") }
    var isProcessing by remember { mutableStateOf(false) }

    val baseFare = if (selectedVehicle == "TukTuk") 30.0 else 40.0
    val ratePerKm = if (selectedVehicle == "TukTuk") 20.0 else 22.0
    val totalFare = computeSenaFare(selectedVehicle, passengerOption.factor, distanceKm, dpiMultiplier)

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .clip(RoundedCornerShape(26.dp))
                .background(SenaSurface)
                .border(1.dp, SenaBorder, RoundedCornerShape(26.dp))
                .padding(20.dp)
                .testTag("qr_web_booking_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header: Web PWA simulation badge
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.QrCode2,
                                contentDescription = null,
                                tint = SenaPeach,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "SENA WEB QR BOOKING",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = SenaPeach
                            )
                        }
                        Text(
                            text = "https://book.sena.ke/?qr=NYALI001",
                            fontSize = 10.sp,
                            color = SenaElectricCyan
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = SenaTextMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                if (step == 1) {
                    // STEP 1: Destination-first flow (SDR QR-002: Direct to From-To window)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xFF131826))
                            .padding(10.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = null,
                                tint = SenaElectricCyan,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Dual Channel: Booking via Driver QR Card (Omar • KMC-412A)",
                                fontSize = 11.sp,
                                color = SenaTextSecondary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = destination,
                        onValueChange = { destination = it },
                        label = { Text("Where to in Mombasa?") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = null,
                                tint = SenaPeach
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SenaPeach,
                            unfocusedBorderColor = SenaBorder,
                            focusedTextColor = SenaTextPrimary,
                            unfocusedTextColor = SenaTextPrimary
                        ),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth().testTag("qr_destination_input")
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Vehicle Type Selector (SDR FR-C011: Standard Bike vs 3 Seater Tuk-Tuk)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (selectedVehicle == "Standard") SenaPeach.copy(alpha = 0.15f) else Color(0xFF141824))
                                .border(
                                    1.dp,
                                    if (selectedVehicle == "Standard") SenaPeach else SenaBorder,
                                    RoundedCornerShape(16.dp)
                                )
                                .clickable { selectedVehicle = "Standard" }
                                .padding(12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.TwoWheeler,
                                    contentDescription = null,
                                    tint = if (selectedVehicle == "Standard") SenaPeach else SenaTextMuted,
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Standard Bike",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (selectedVehicle == "Standard") SenaPeach else SenaTextPrimary
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (selectedVehicle == "TukTuk") SenaPeach.copy(alpha = 0.15f) else Color(0xFF141824))
                                .border(
                                    1.dp,
                                    if (selectedVehicle == "TukTuk") SenaPeach else SenaBorder,
                                    RoundedCornerShape(16.dp)
                                )
                                .clickable { selectedVehicle = "TukTuk" }
                                .padding(12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.Moped,
                                    contentDescription = null,
                                    tint = if (selectedVehicle == "TukTuk") SenaPeach else SenaTextMuted,
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "3-Seater Tuk-Tuk",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (selectedVehicle == "TukTuk") SenaPeach else SenaTextPrimary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Fare Calculation Card for QR Web channel
                    FareCalculationCard(
                        vehicleName = if (selectedVehicle == "TukTuk") "3-Seater Tuk-Tuk" else "Standard Bike",
                        baseFare = baseFare,
                        ratePerKm = ratePerKm,
                        distanceKm = distanceKm,
                        passengerOption = passengerOption,
                        dpiMultiplier = dpiMultiplier,
                        totalFare = totalFare,
                        onPassengerOptionSelect = { passengerOption = it },
                        onDistanceChange = { distanceKm = it }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { step = 2 },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("qr_proceed_otp_button"),
                        shape = RoundedCornerShape(25.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SenaOrangeCTA)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "ENTER PHONE & VERIFY OTP",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                imageVector = Icons.Default.ArrowForward,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                } else if (step == 2) {
                    // STEP 2: Phone & OTP Verification (SDR FR-C002 & SEC-001)
                    Text(
                        text = "Phone & OTP Verification",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = SenaTextPrimary
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "AWS SNS SMS will deliver a 6-digit OTP to your Kenyan number.",
                        fontSize = 12.sp,
                        color = SenaTextSecondary,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = phoneNumber,
                        onValueChange = { phoneNumber = it },
                        label = { Text("Kenyan Mobile Number (254...)") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SenaPeach,
                            unfocusedBorderColor = SenaBorder,
                            focusedTextColor = SenaTextPrimary,
                            unfocusedTextColor = SenaTextPrimary
                        ),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth().testTag("qr_phone_input")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = otpCode,
                        onValueChange = { otpCode = it },
                        label = { Text("6-Digit OTP Code") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SenaPeach,
                            unfocusedBorderColor = SenaBorder,
                            focusedTextColor = SenaTextPrimary,
                            unfocusedTextColor = SenaTextPrimary
                        ),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth().testTag("qr_otp_input")
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Payment Method for QR Web
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(14.dp))
                                .background(if (paymentMethod == "M-Pesa") SenaPeach.copy(alpha = 0.2f) else Color(0xFF131722))
                                .border(
                                    1.dp,
                                    if (paymentMethod == "M-Pesa") SenaPeach else SenaBorder,
                                    RoundedCornerShape(14.dp)
                                )
                                .clickable { paymentMethod = "M-Pesa" }
                                .padding(10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.PhoneAndroid,
                                    contentDescription = null,
                                    tint = if (paymentMethod == "M-Pesa") SenaPeach else SenaTextMuted,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "M-Pesa STK",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (paymentMethod == "M-Pesa") SenaPeach else SenaTextPrimary
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(14.dp))
                                .background(if (paymentMethod == "Cash") SenaPeach.copy(alpha = 0.2f) else Color(0xFF131722))
                                .border(
                                    1.dp,
                                    if (paymentMethod == "Cash") SenaPeach else SenaBorder,
                                    RoundedCornerShape(14.dp)
                                )
                                .clickable { paymentMethod = "Cash" }
                                .padding(10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Money,
                                    contentDescription = null,
                                    tint = if (paymentMethod == "Cash") SenaPeach else SenaTextMuted,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Cash Payment",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (paymentMethod == "Cash") SenaPeach else SenaTextPrimary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    if (isProcessing) {
                        CircularProgressIndicator(
                            color = SenaPeach,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Matching QR Driver & dispatching ride (source = qr_web)...",
                            fontSize = 12.sp,
                            color = SenaElectricCyan
                        )
                    } else {
                        Button(
                            onClick = {
                                isProcessing = true
                                onCompleteBooking(destination, passengerOption, paymentMethod, totalFare)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("qr_confirm_booking_button"),
                            shape = RoundedCornerShape(25.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = SenaOrangeCTA)
                        ) {
                            Text(
                                text = "CONFIRM & DISPATCH RIDE (KES ${totalFare.toInt()})",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        TextButton(onClick = { step = 1 }) {
                            Text("Back to Fare Breakdown", color = SenaTextMuted)
                        }
                    }
                }
            }
        }
    }
}

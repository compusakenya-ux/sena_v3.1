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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ElectricBike
import androidx.compose.material.icons.filled.Launch
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.TwoWheeler
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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
import com.example.ui.theme.SenaBorder
import com.example.ui.theme.SenaElectricCyan
import com.example.ui.theme.SenaOrangeCTA
import com.example.ui.theme.SenaPeach
import com.example.ui.theme.SenaSuccessGreen
import com.example.ui.theme.SenaSurface
import com.example.ui.theme.SenaTextMuted
import com.example.ui.theme.SenaTextPrimary
import com.example.ui.theme.SenaTextSecondary

@Composable
fun DriverQrCardDialog(
    driverName: String = "Omar",
    driverVehicle: String = "Standard Bike • KMC-412A",
    qrCodeId: String = "NYALI001",
    pwaUrl: String = "https://book.sena.ke/?flow=destination&qr=NYALI001",
    onLaunchQrWebBooking: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(SenaSurface)
                .border(1.dp, SenaBorder, RoundedCornerShape(24.dp))
                .padding(20.dp)
                .testTag("driver_qr_card_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header with Close Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.QrCode2,
                            contentDescription = "Driver QR Card",
                            tint = SenaPeach,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Driver QR Booking Card",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = SenaTextPrimary
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

                // QR Code Vector Matrix
                QrCodeView(
                    data = pwaUrl,
                    sizeDp = 180.dp,
                    dotColor = SenaPeach,
                    accentColor = SenaOrangeCTA
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Driver & Location Details (SDR Section 4 & QR-001)
                Text(
                    text = "Driver: $driverName",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = SenaTextPrimary
                )

                Text(
                    text = driverVehicle,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = SenaElectricCyan
                )

                Spacer(modifier = Modifier.height(8.dp))

                // PWA Link Box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF111420))
                        .border(1.dp, SenaBorder, RoundedCornerShape(12.dp))
                        .padding(10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "SENA PWA-URL (SDR v3.1.0):",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            color = SenaTextMuted
                        )
                        Text(
                            text = pwaUrl,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = SenaPeach,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Info: Dual Booking Logic Explanation
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFF141A28))
                        .padding(12.dp)
                ) {
                    Text(
                        text = "Passengers can scan this QR with any phone camera to open Sena Web Booking directly without installing the app. Automatically dispatches to Omar.",
                        fontSize = 11.sp,
                        color = SenaTextSecondary,
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // CTA: Launch QR Web Booking Simulator
                Button(
                    onClick = {
                        onDismiss()
                        onLaunchQrWebBooking()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("test_qr_web_booking_btn"),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SenaOrangeCTA)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Launch,
                            contentDescription = "Test QR Web Booking",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "TEST QR WEB BOOKING FLOW",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                TextButton(onClick = onDismiss) {
                    Text("Close Card", color = SenaTextMuted, fontSize = 13.sp)
                }
            }
        }
    }
}

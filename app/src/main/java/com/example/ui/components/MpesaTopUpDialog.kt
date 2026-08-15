package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.SenaBackground
import com.example.ui.theme.SenaBorder
import com.example.ui.theme.SenaElectricCyan
import com.example.ui.theme.SenaOrangeCTA
import com.example.ui.theme.SenaPeach
import com.example.ui.theme.SenaSuccessGreen
import com.example.ui.theme.SenaSurface
import com.example.ui.theme.SenaTextMuted
import com.example.ui.theme.SenaTextPrimary

@Composable
fun MpesaTopUpDialog(
    amount: String,
    phoneNumber: String,
    isProcessing: Boolean,
    successMessage: String?,
    onAmountChange: (String) -> Unit,
    onPhoneNumberChange: (String) -> Unit,
    onTriggerStkPush: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(SenaSurface)
                .border(1.dp, SenaBorder, RoundedCornerShape(24.dp))
                .padding(24.dp)
                .testTag("mpesa_top_up_dialog")
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.PhoneAndroid,
                        contentDescription = "M-Pesa Express",
                        tint = SenaSuccessGreen,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "M-Pesa Express Refill",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = SenaTextPrimary
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (successMessage != null) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Success",
                        tint = SenaSuccessGreen,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = successMessage,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = SenaSuccessGreen
                    )
                } else if (isProcessing) {
                    CircularProgressIndicator(
                        color = SenaPeach,
                        modifier = Modifier.size(44.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Sending M-Pesa STK Push prompt...",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = SenaElectricCyan
                    )
                    Text(
                        text = "Please enter your M-Pesa PIN on your phone.",
                        fontSize = 12.sp,
                        color = SenaTextMuted,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                } else {
                    OutlinedTextField(
                        value = amount,
                        onValueChange = onAmountChange,
                        label = { Text("Amount (KES)") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SenaPeach,
                            unfocusedBorderColor = SenaBorder,
                            focusedTextColor = SenaTextPrimary,
                            unfocusedTextColor = SenaTextPrimary
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("mpesa_amount_input")
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = phoneNumber,
                        onValueChange = onPhoneNumberChange,
                        label = { Text("M-Pesa Phone Number") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SenaPeach,
                            unfocusedBorderColor = SenaBorder,
                            focusedTextColor = SenaTextPrimary,
                            unfocusedTextColor = SenaTextPrimary
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("mpesa_phone_input")
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = onDismiss) {
                            Text(text = "CANCEL", color = SenaTextMuted)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Button(
                            onClick = onTriggerStkPush,
                            colors = ButtonDefaults.buttonColors(containerColor = SenaOrangeCTA),
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier.testTag("send_stk_push_button")
                        ) {
                            Text(
                                text = "STK PUSH",
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}

package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.ElectricBike
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.DriverQrCardDialog
import com.example.ui.components.DriverWithdrawDialog
import com.example.ui.components.PrivacyDataTermsDialog
import com.example.ui.components.QrBookingDialog
import com.example.ui.components.SenaBottomNav
import com.example.ui.components.SenaNavTab
import com.example.ui.components.SenaTopAppBar
import com.example.ui.screens.CheckoutScreen
import com.example.ui.screens.HistoryScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LaunchScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.RatingScreen
import com.example.ui.screens.TrackingScreen
import com.example.ui.screens.WalletScreen
import com.example.ui.theme.SenaBackground
import com.example.ui.theme.SenaBorder
import com.example.ui.theme.SenaElectricCyan
import com.example.ui.theme.SenaPeach
import com.example.ui.theme.SenaSurface
import com.example.ui.theme.SenaTextMuted
import com.example.ui.theme.SenaTextPrimary
import com.example.ui.theme.SenaTextSecondary
import com.example.ui.theme.SenaTheme
import com.example.ui.viewmodel.ScreenState
import com.example.ui.viewmodel.SenaViewModel
import androidx.compose.material3.MaterialTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: SenaViewModel = viewModel()
            val isDarkMode by viewModel.isDarkMode.collectAsState()
            SenaTheme(darkTheme = isDarkMode) {
                SenaMainApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun SenaMainApp(
    viewModel: SenaViewModel = viewModel()
) {
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    val currentScreen by viewModel.currentScreen.collectAsState()
    val selectedTab by viewModel.selectedTab.collectAsState()

    val pickupLocation by viewModel.pickupLocation.collectAsState()
    val destinationLocation by viewModel.destinationLocation.collectAsState()
    val selectedRideCategory by viewModel.selectedRideCategory.collectAsState()
    val passengerOption by viewModel.passengerOption.collectAsState()
    val tripDistanceKm by viewModel.tripDistanceKm.collectAsState()
    val selectedPaymentMethod by viewModel.selectedPaymentMethod.collectAsState()
    val surgeMultiplier by viewModel.surgeMultiplier.collectAsState()

    val driverWalletBalance by viewModel.driverWalletBalance.collectAsState()
    val driverTotalRides by viewModel.driverTotalRides.collectAsState()
    val driverGrossEarnings by viewModel.driverGrossEarnings.collectAsState()
    val userXp by viewModel.userXp.collectAsState()
    val userTier = viewModel.userTier

    val minutesLeft by viewModel.trackingMinutesLeft.collectAsState()

    val ratingGiven by viewModel.ratingGiven.collectAsState()
    val selectedChips by viewModel.selectedFeedbackChips.collectAsState()
    val reviewText by viewModel.reviewText.collectAsState()

    val showWithdrawDialog by viewModel.showWithdrawDialog.collectAsState()
    val withdrawAmount by viewModel.withdrawAmount.collectAsState()
    val withdrawPhoneNumber by viewModel.withdrawPhoneNumber.collectAsState()
    val isWithdrawProcessing by viewModel.isWithdrawProcessing.collectAsState()
    val withdrawSuccessMessage by viewModel.withdrawSuccessMessage.collectAsState()
    val withdrawErrorMessage by viewModel.withdrawErrorMessage.collectAsState()

    val showQrCardDialog by viewModel.showQrCardDialog.collectAsState()
    val showQrBookingDialog by viewModel.showQrBookingDialog.collectAsState()

    // Privacy & KDPA Consent State
    val showPrivacyDialog by viewModel.showPrivacyDialog.collectAsState()
    val gpsConsent by viewModel.gpsConsent.collectAsState()
    val financialConsent by viewModel.financialConsent.collectAsState()
    val fleetAnalyticsConsent by viewModel.fleetAnalyticsConsent.collectAsState()
    val marketingConsent by viewModel.marketingConsent.collectAsState()
    val exportNotice by viewModel.exportNotice.collectAsState()
    val erasureNotice by viewModel.erasureNotice.collectAsState()

    val rideHistory by viewModel.rideHistory.collectAsState()
    val walletTransactions by viewModel.walletTransactions.collectAsState()

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    if (currentScreen == ScreenState.LAUNCH) {
        LaunchScreen(
            onLaunchClick = { viewModel.launchJourney() }
        )
    } else {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet(
                    drawerContainerColor = MaterialTheme.colorScheme.surface,
                    drawerContentColor = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.width(310.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.surface)
                            .padding(22.dp)
                    ) {
                        Spacer(modifier = Modifier.height(28.dp))

                        // Drawer Header
                        Text(
                            text = "SENA TRANSIT",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 2.sp,
                            color = SenaPeach
                        )
                        Text(
                            text = "Powering Mobility-Safely",
                            fontSize = 12.sp,
                            color = SenaTextSecondary
                        )

                        Spacer(modifier = Modifier.height(18.dp))
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                        Spacer(modifier = Modifier.height(16.dp))

                        // Drawer Items
                        DrawerMenuItem(
                            label = "Home & Map Booking",
                            icon = Icons.Default.Explore,
                            onClick = {
                                viewModel.selectNavTab(SenaNavTab.HOME)
                                scope.launch { drawerState.close() }
                            }
                        )

                        DrawerMenuItem(
                            label = "Driver QR Card (PWA URL)",
                            icon = Icons.Default.QrCode2,
                            onClick = {
                                scope.launch { drawerState.close() }
                                viewModel.openQrCardDialog()
                            }
                        )

                        DrawerMenuItem(
                            label = "QR Web Booking (Dual Channel)",
                            icon = Icons.Default.QrCodeScanner,
                            onClick = {
                                scope.launch { drawerState.close() }
                                viewModel.openQrBookingDialog()
                            }
                        )

                        DrawerMenuItem(
                            label = "Ride History (App & QR)",
                            icon = Icons.Default.History,
                            onClick = {
                                viewModel.selectNavTab(SenaNavTab.HISTORY)
                                scope.launch { drawerState.close() }
                            }
                        )

                        DrawerMenuItem(
                            label = "Driver Wallet (85/15 Payouts)",
                            icon = Icons.Default.AccountBalanceWallet,
                            onClick = {
                                viewModel.selectNavTab(SenaNavTab.WALLET)
                                scope.launch { drawerState.close() }
                            }
                        )

                        DrawerMenuItem(
                            label = "Account Profile",
                            icon = Icons.Default.Person,
                            onClick = {
                                viewModel.selectNavTab(SenaNavTab.PROFILE)
                                scope.launch { drawerState.close() }
                            }
                        )

                        DrawerMenuItem(
                            label = "Privacy & Data Terms (KDPA)",
                            icon = Icons.Default.Shield,
                            onClick = {
                                scope.launch { drawerState.close() }
                                viewModel.openPrivacyDialog()
                            }
                        )

                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        ) {
            Scaffold(
                topBar = {
                    SenaTopAppBar(
                        onMenuClick = {
                            scope.launch { drawerState.open() }
                        },
                        onProfileClick = {
                            viewModel.selectNavTab(SenaNavTab.PROFILE)
                        }
                    )
                },
                bottomBar = {
                    SenaBottomNav(
                        selectedTab = selectedTab,
                        onTabSelected = { tab ->
                            viewModel.selectNavTab(tab)
                        }
                    )
                },
                containerColor = MaterialTheme.colorScheme.background
            ) { innerPadding ->
                AnimatedContent(
                    targetState = currentScreen,
                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    label = "screen_transition"
                ) { screen ->
                    when (screen) {
                        ScreenState.LAUNCH -> LaunchScreen(
                            onLaunchClick = { viewModel.launchJourney() }
                        )

                        ScreenState.HOME -> HomeScreen(
                            destination = destinationLocation,
                            selectedCategory = selectedRideCategory,
                            rideCategories = viewModel.rideCategories,
                            passengerOption = passengerOption,
                            tripDistanceKm = tripDistanceKm,
                            surgeMultiplier = surgeMultiplier,
                            onDestinationChange = { loc -> viewModel.selectDestination(loc) },
                            onCategorySelect = { cat -> viewModel.selectRideCategory(cat) },
                            onPassengerOptionSelect = { p -> viewModel.selectPassengerOption(p) },
                            onDistanceChange = { d -> viewModel.setTripDistanceKm(d) },
                            onOpenQrCard = { viewModel.openQrCardDialog() },
                            onOpenQrWebBooking = { viewModel.openQrBookingDialog() },
                            onProceedToCheckout = { viewModel.proceedToCheckout() }
                        )

                        ScreenState.CHECKOUT -> {
                            val computedFare = viewModel.calculateCurrentFare()
                            CheckoutScreen(
                                fareKes = computedFare,
                                pickupLocation = pickupLocation,
                                destinationLocation = destinationLocation,
                                selectedPaymentMethod = selectedPaymentMethod,
                                walletBalance = driverWalletBalance,
                                passengerOption = passengerOption,
                                tripDistanceKm = tripDistanceKm,
                                surgeMultiplier = surgeMultiplier,
                                onPaymentMethodSelect = { method ->
                                    viewModel.selectPaymentMethod(method)
                                },
                                onConfirmAndPay = { viewModel.confirmAndPay() }
                            )
                        }

                        ScreenState.TRACKING -> TrackingScreen(
                            minutesLeft = minutesLeft,
                            destination = destinationLocation,
                            onSafetyCenterClick = { viewModel.openPrivacyDialog() },
                            onShareTripClick = { }
                        )

                        ScreenState.RATING -> RatingScreen(
                            rating = ratingGiven,
                            selectedChips = selectedChips,
                            reviewText = reviewText,
                            onRatingChange = { r -> viewModel.setRating(r) },
                            onChipToggle = { chip -> viewModel.toggleFeedbackChip(chip) },
                            onReviewTextChange = { txt -> viewModel.updateReviewText(txt) },
                            onSubmitFeedback = { viewModel.submitFeedback() }
                        )

                        ScreenState.WALLET -> WalletScreen(
                            balance = driverWalletBalance,
                            driverTotalRides = driverTotalRides,
                            driverGrossEarnings = driverGrossEarnings,
                            userTier = userTier,
                            transactions = walletTransactions,
                            onWithdrawClick = { viewModel.openDriverWithdrawDialog() }
                        )

                        ScreenState.HISTORY -> HistoryScreen(
                            rideList = rideHistory
                        )

                        ScreenState.PROFILE -> ProfileScreen(
                            userXp = userXp,
                            userTier = userTier,
                            isDarkMode = isDarkMode,
                            onToggleDarkMode = { dark -> viewModel.toggleDarkMode(dark) },
                            onOpenPrivacyTerms = { viewModel.openPrivacyDialog() }
                        )
                    }
                }
            }

            // Kenya Data Protection & Privacy Dialog (KDPA 2019 / ODPC)
            if (showPrivacyDialog) {
                PrivacyDataTermsDialog(
                    gpsConsent = gpsConsent,
                    financialConsent = financialConsent,
                    fleetAnalyticsConsent = fleetAnalyticsConsent,
                    marketingConsent = marketingConsent,
                    exportNotice = exportNotice,
                    erasureNotice = erasureNotice,
                    onGpsConsentToggle = { viewModel.setGpsConsent(it) },
                    onFinancialConsentToggle = { viewModel.setFinancialConsent(it) },
                    onFleetAnalyticsToggle = { viewModel.setFleetAnalyticsConsent(it) },
                    onMarketingToggle = { viewModel.setMarketingConsent(it) },
                    onExportDataClick = { viewModel.exportPersonalData() },
                    onErasureRequestClick = { viewModel.requestDataErasure() },
                    onDismiss = { viewModel.dismissPrivacyDialog() }
                )
            }

            // Driver B2C Withdrawal Dialog
            if (showWithdrawDialog) {
                DriverWithdrawDialog(
                    availableBalance = driverWalletBalance,
                    amount = withdrawAmount,
                    phoneNumber = withdrawPhoneNumber,
                    isProcessing = isWithdrawProcessing,
                    successMessage = withdrawSuccessMessage,
                    errorMessage = withdrawErrorMessage,
                    onAmountChange = { viewModel.setWithdrawAmount(it) },
                    onPhoneNumberChange = { viewModel.setWithdrawPhoneNumber(it) },
                    onTriggerWithdrawal = { viewModel.triggerDriverB2cWithdrawal() },
                    onDismiss = { viewModel.dismissDriverWithdrawDialog() }
                )
            }

            // Driver QR Card Dialog (SDR Section 4 & QR-001)
            if (showQrCardDialog) {
                DriverQrCardDialog(
                    driverName = "Omar",
                    driverVehicle = "Standard Bike • KMC-412A",
                    qrCodeId = "NYALI001",
                    pwaUrl = "https://book.sena.ke/?flow=destination&qr=NYALI001",
                    onLaunchQrWebBooking = { viewModel.openQrBookingDialog() },
                    onDismiss = { viewModel.dismissQrCardDialog() }
                )
            }

            // QR Web Booking Simulation Dialog (SDR Section 4 Dual Channel)
            if (showQrBookingDialog) {
                QrBookingDialog(
                    initialDestination = destinationLocation,
                    dpiMultiplier = surgeMultiplier,
                    onCompleteBooking = { dest, pax, payMethod, fare ->
                        viewModel.completeQrWebBooking(dest, pax, payMethod, fare)
                    },
                    onDismiss = { viewModel.dismissQrBookingDialog() }
                )
            }
        }
    }
}

@Composable
private fun DrawerMenuItem(
    label: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(vertical = 11.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF222736)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = SenaPeach,
                    modifier = Modifier.size(17.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = label,
                fontSize = 13.5.sp,
                fontWeight = FontWeight.Medium,
                color = SenaTextPrimary
            )
        }

        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = SenaTextMuted,
            modifier = Modifier.size(16.dp)
        )
    }
}

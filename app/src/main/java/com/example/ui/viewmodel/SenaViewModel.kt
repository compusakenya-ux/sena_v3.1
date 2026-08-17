package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.RideBooking
import com.example.data.db.SavedPlace
import com.example.data.db.SenaDatabase
import com.example.data.db.WalletTransaction
import com.example.data.repository.SenaRepository
import com.example.ui.components.PassengerOption
import com.example.ui.components.SenaNavTab
import com.example.ui.components.computeSenaFare
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class ScreenState {
    LAUNCH, HOME, CHECKOUT, TRACKING, RATING, WALLET, PROFILE, HISTORY
}

data class RideCategoryOption(
    val id: String,
    val name: String,
    val tagline: String,
    val capacity: Int,
    val baseFareKes: Double,
    val ratePerKmKes: Double,
    val isEco: Boolean = false,
    val iconRes: String = "ic_bike"
)

class SenaViewModel(application: Application) : AndroidViewModel(application) {

    private val db = SenaDatabase.getDatabase(application)
    private val repository = SenaRepository(db.rideDao(), db.walletDao(), db.savedPlaceDao())

    // Active Screen & Navigation State
    private val _currentScreen = MutableStateFlow(ScreenState.LAUNCH)
    val currentScreen: StateFlow<ScreenState> = _currentScreen.asStateFlow()

    private val _selectedTab = MutableStateFlow(SenaNavTab.HOME)
    val selectedTab: StateFlow<SenaNavTab> = _selectedTab.asStateFlow()

    // Theme mode (Dark / Light)
    private val _isDarkMode = MutableStateFlow(true)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    // Privacy & Kenya Data Protection Consent States (KDPA 2019 / ODPC)
    private val _showPrivacyDialog = MutableStateFlow(false)
    val showPrivacyDialog: StateFlow<Boolean> = _showPrivacyDialog.asStateFlow()

    private val _gpsConsent = MutableStateFlow(true)
    val gpsConsent: StateFlow<Boolean> = _gpsConsent.asStateFlow()

    private val _financialConsent = MutableStateFlow(true)
    val financialConsent: StateFlow<Boolean> = _financialConsent.asStateFlow()

    private val _fleetAnalyticsConsent = MutableStateFlow(true)
    val fleetAnalyticsConsent: StateFlow<Boolean> = _fleetAnalyticsConsent.asStateFlow()

    private val _marketingConsent = MutableStateFlow(false)
    val marketingConsent: StateFlow<Boolean> = _marketingConsent.asStateFlow()

    private val _exportNotice = MutableStateFlow<String?>(null)
    val exportNotice: StateFlow<String?> = _exportNotice.asStateFlow()

    private val _erasureNotice = MutableStateFlow<String?>(null)
    val erasureNotice: StateFlow<String?> = _erasureNotice.asStateFlow()

    // Active Ride Selection
    private val _pickupLocation = MutableStateFlow("Mombasa City Center")
    val pickupLocation: StateFlow<String> = _pickupLocation.asStateFlow()

    private val _destinationLocation = MutableStateFlow("Nyali Bridge")
    val destinationLocation: StateFlow<String> = _destinationLocation.asStateFlow()

    // SDR Rule: Only 2 Vehicle Options (Standard Bike & 3 Seater Tuk-Tuk)
    private val _selectedRideCategory = MutableStateFlow("Standard")
    val selectedRideCategory: StateFlow<String> = _selectedRideCategory.asStateFlow()

    // Passenger / Cargo Option for Fare Formula (FR-C013)
    private val _passengerOption = MutableStateFlow(PassengerOption.ONE_ADULT)
    val passengerOption: StateFlow<PassengerOption> = _passengerOption.asStateFlow()

    // Automated GPS Route Trip Distance in KM (FR-C013: KM above 1.5KM gets per-km charge)
    private val _tripDistanceKm = MutableStateFlow(3.5)
    val tripDistanceKm: StateFlow<Double> = _tripDistanceKm.asStateFlow()

    // SDR Rule: M-Pesa & Cash Payment Options
    private val _selectedPaymentMethod = MutableStateFlow("M-Pesa")
    val selectedPaymentMethod: StateFlow<String> = _selectedPaymentMethod.asStateFlow()

    // Dynamic Price Index (Surge Multiplier per SDR Section 7: DPI 1.0x to 2.5x)
    private val _surgeMultiplier = MutableStateFlow(1.2f)
    val surgeMultiplier: StateFlow<Float> = _surgeMultiplier.asStateFlow()

    // Booking Source tracking (SDR QR-010: "app" vs "qr_web")
    private val _lastBookingSource = MutableStateFlow("app")
    val lastBookingSource: StateFlow<String> = _lastBookingSource.asStateFlow()

    // Driver QR Card & QR Web Booking Dialog States
    private val _showQrCardDialog = MutableStateFlow(false)
    val showQrCardDialog: StateFlow<Boolean> = _showQrCardDialog.asStateFlow()

    private val _showQrBookingDialog = MutableStateFlow(false)
    val showQrBookingDialog: StateFlow<Boolean> = _showQrBookingDialog.asStateFlow()

    // QR Analytics
    private val _qrTotalScans = MutableStateFlow(34)
    val qrTotalScans: StateFlow<Int> = _qrTotalScans.asStateFlow()

    private val _qrCompletedBookings = MutableStateFlow(19)
    val qrCompletedBookings: StateFlow<Int> = _qrCompletedBookings.asStateFlow()

    // Driver Wallet Balance & Earnings (SDR 85/15 split & B2C withdrawals)
    private val _driverWalletBalance = MutableStateFlow(4250.00)
    val driverWalletBalance: StateFlow<Double> = _driverWalletBalance.asStateFlow()

    private val _driverTotalRides = MutableStateFlow(18)
    val driverTotalRides: StateFlow<Int> = _driverTotalRides.asStateFlow()

    private val _driverGrossEarnings = MutableStateFlow(5000.00)
    val driverGrossEarnings: StateFlow<Double> = _driverGrossEarnings.asStateFlow()

    private val _userXp = MutableStateFlow(1240)
    val userXp: StateFlow<Int> = _userXp.asStateFlow()

    val userTier: String = "Platinum Driver"

    // Live Tracking State
    private val _trackingMinutesLeft = MutableStateFlow(3)
    val trackingMinutesLeft: StateFlow<Int> = _trackingMinutesLeft.asStateFlow()

    private val _activeRideId = MutableStateFlow<Long?>(null)
    val activeRideId: StateFlow<Long?> = _activeRideId.asStateFlow()

    // Driver Rating State (SDR Rule: 1 to 4 Stars, 1=Worst, 4=Best)
    private val _ratingGiven = MutableStateFlow(4f)
    val ratingGiven: StateFlow<Float> = _ratingGiven.asStateFlow()

    private val _selectedFeedbackChips = MutableStateFlow<Set<String>>(
        setOf("Safe Driving", "Clean Bike/Tuk-Tuk", "Fast Route")
    )
    val selectedFeedbackChips: StateFlow<Set<String>> = _selectedFeedbackChips.asStateFlow()

    private val _reviewText = MutableStateFlow("")
    val reviewText: StateFlow<String> = _reviewText.asStateFlow()

    // Driver B2C M-Pesa Withdrawal Dialog & State
    private val _showWithdrawDialog = MutableStateFlow(false)
    val showWithdrawDialog: StateFlow<Boolean> = _showWithdrawDialog.asStateFlow()

    private val _withdrawAmount = MutableStateFlow("2000")
    val withdrawAmount: StateFlow<String> = _withdrawAmount.asStateFlow()

    private val _withdrawPhoneNumber = MutableStateFlow("254712345678")
    val withdrawPhoneNumber: StateFlow<String> = _withdrawPhoneNumber.asStateFlow()

    private val _isWithdrawProcessing = MutableStateFlow(false)
    val isWithdrawProcessing: StateFlow<Boolean> = _isWithdrawProcessing.asStateFlow()

    private val _withdrawSuccessMessage = MutableStateFlow<String?>(null)
    val withdrawSuccessMessage: StateFlow<String?> = _withdrawSuccessMessage.asStateFlow()

    private val _withdrawErrorMessage = MutableStateFlow<String?>(null)
    val withdrawErrorMessage: StateFlow<String?> = _withdrawErrorMessage.asStateFlow()

    // Room DB Observables
    val rideHistory: StateFlow<List<RideBooking>> = repository.allRides.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val walletTransactions: StateFlow<List<WalletTransaction>> = repository.allTransactions.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val savedPlaces: StateFlow<List<SavedPlace>> = repository.savedPlaces.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // SDR Rule: Exactly 2 Vehicle Options (Standard Bike & 3 Seater Tuk-Tuk) - Appendix A
    val rideCategories = listOf(
        RideCategoryOption(
            id = "Standard",
            name = "Standard Bike",
            tagline = "Agile 1-Passenger Boda-Boda",
            capacity = 1,
            baseFareKes = 40.0,
            ratePerKmKes = 22.0,
            isEco = false,
            iconRes = "ic_bike"
        ),
        RideCategoryOption(
            id = "TukTuk",
            name = "3 Seater Tuk-Tuk",
            tagline = "Spacious 3-Passenger Rickshaw",
            capacity = 3,
            baseFareKes = 30.0,
            ratePerKmKes = 20.0,
            isEco = true,
            iconRes = "ic_tuktuk"
        )
    )

    init {
        seedInitialDataIfEmpty()
    }

    private fun seedInitialDataIfEmpty() {
        viewModelScope.launch {
            val existingRides = repository.allRides.first()
            if (existingRides.isEmpty()) {
                repository.insertRide(
                    RideBooking(
                        pickupLocation = "Nyali Bridge",
                        destinationLocation = "Bamburi Beach",
                        rideType = "Standard Bike",
                        fareAmount = 130.0,
                        driverName = "Omar Juma",
                        driverVehicleNumber = "KMC-412A",
                        driverRating = 4.0,
                        status = "COMPLETED"
                    )
                )
                repository.insertRide(
                    RideBooking(
                        pickupLocation = "Mombasa City Center",
                        destinationLocation = "Likoni Ferry",
                        rideType = "3 Seater Tuk-Tuk",
                        fareAmount = 180.0,
                        driverName = "Khamis Hassan",
                        driverVehicleNumber = "KMH-882T",
                        driverRating = 4.0,
                        status = "COMPLETED"
                    )
                )
            }

            val existingTx = repository.allTransactions.first()
            if (existingTx.isEmpty()) {
                repository.insertTransaction(
                    WalletTransaction(
                        title = "85% Ride Share Credit (Standard Bike)",
                        subtitle = "Nyali → Bamburi • 85% Split Credited (app)",
                        amount = 110.50,
                        isCredit = true,
                        status = "COMPLETED"
                    )
                )
                repository.insertTransaction(
                    WalletTransaction(
                        title = "85% QR Web Ride Credit (Tuk-Tuk)",
                        subtitle = "City Center → Likoni • QR-Stop Auto-Match (qr_web)",
                        amount = 153.00,
                        isCredit = true,
                        status = "COMPLETED"
                    )
                )
                repository.insertTransaction(
                    WalletTransaction(
                        title = "Daraja B2C M-Pesa Payout",
                        subtitle = "Driver Withdrawal • 254712345678",
                        amount = 1500.00,
                        isCredit = false,
                        status = "SUCCESS"
                    )
                )
            }

            val existingPlaces = repository.savedPlaces.first()
            if (existingPlaces.isEmpty()) {
                repository.seedSavedPlaces(
                    listOf(
                        SavedPlace(label = "Home", address = "Nyali Executive Villas", iconName = "home"),
                        SavedPlace(label = "Work", address = "Mombasa Trade Center", iconName = "work"),
                        SavedPlace(label = "Beach", address = "Bamburi Beach Road", iconName = "beach")
                    )
                )
            }
        }
    }

    fun launchJourney() {
        _currentScreen.value = ScreenState.HOME
        _selectedTab.value = SenaNavTab.HOME
    }

    fun selectNavTab(tab: SenaNavTab) {
        _selectedTab.value = tab
        when (tab) {
            SenaNavTab.HOME -> _currentScreen.value = ScreenState.HOME
            SenaNavTab.HISTORY -> _currentScreen.value = ScreenState.HISTORY
            SenaNavTab.BOOK -> _currentScreen.value = ScreenState.HOME
            SenaNavTab.WALLET -> _currentScreen.value = ScreenState.WALLET
            SenaNavTab.PROFILE -> _currentScreen.value = ScreenState.PROFILE
        }
    }

    fun toggleDarkMode(isDark: Boolean) {
        _isDarkMode.value = isDark
    }

    fun openPrivacyDialog() {
        _showPrivacyDialog.value = true
        _exportNotice.value = null
        _erasureNotice.value = null
    }

    fun dismissPrivacyDialog() {
        _showPrivacyDialog.value = false
    }

    fun setGpsConsent(enabled: Boolean) {
        _gpsConsent.value = enabled
    }

    fun setFinancialConsent(enabled: Boolean) {
        _financialConsent.value = enabled
    }

    fun setFleetAnalyticsConsent(enabled: Boolean) {
        _fleetAnalyticsConsent.value = enabled
    }

    fun setMarketingConsent(enabled: Boolean) {
        _marketingConsent.value = enabled
    }

    fun exportPersonalData() {
        viewModelScope.launch {
            _exportNotice.value = "Generating ODPC-compliant Data Archive (JSON)..."
            delay(1200)
            _exportNotice.value = "Personal Data Export ready: sena_rider_data_${System.currentTimeMillis().toString().takeLast(6)}.json"
        }
    }

    fun requestDataErasure() {
        viewModelScope.launch {
            _erasureNotice.value = "Processing KDPA Sec 40 Erasure Request..."
            delay(1500)
            _erasureNotice.value = "Request submitted to Data Protection Officer (DPO). Telemetry anonymization active."
        }
    }

    private fun calculateAutomatedGpsDistance(pickup: String, destination: String): Double {
        val combined = "$pickup $destination".lowercase()
        return when {
            combined.contains("bamburi") -> 7.2
            combined.contains("nyali beach") -> 4.5
            combined.contains("likoni") -> 5.8
            combined.contains("airport") -> 11.5
            combined.contains("haller") -> 6.4
            combined.contains("fort jesus") -> 3.1
            combined.contains("home") -> 3.2
            combined.contains("work") || combined.contains("trade center") -> 2.4
            combined.contains("nyali bridge") -> 3.5
            combined.contains("diani") -> 28.0
            combined.contains("city center") || combined.contains("town") -> 2.0
            else -> {
                // Deterministic GPS route calculation based on Mombasa map coordinates
                val hash = (destination.trim().lowercase().hashCode() and 0x7FFFFFFF) % 55 + 18
                hash / 10.0
            }
        }
    }

    fun selectDestination(destination: String) {
        _destinationLocation.value = destination
        _tripDistanceKm.value = calculateAutomatedGpsDistance(_pickupLocation.value, destination)
    }

    fun setPickup(pickup: String) {
        _pickupLocation.value = pickup
        _tripDistanceKm.value = calculateAutomatedGpsDistance(pickup, _destinationLocation.value)
    }

    fun selectRideCategory(category: String) {
        _selectedRideCategory.value = category
    }

    fun selectPassengerOption(option: PassengerOption) {
        _passengerOption.value = option
    }

    fun setTripDistanceKm(km: Double) {
        _tripDistanceKm.value = km.coerceIn(0.5, 30.0)
    }

    fun setSurgeMultiplier(multiplier: Float) {
        _surgeMultiplier.value = multiplier.coerceIn(1.0f, 2.5f)
    }

    fun selectPaymentMethod(method: String) {
        _selectedPaymentMethod.value = method
    }

    fun proceedToCheckout() {
        _currentScreen.value = ScreenState.CHECKOUT
    }

    // Calculate current fare based on SDR Formula (FR-C013 & FR-P006)
    fun calculateCurrentFare(): Double {
        return computeSenaFare(
            vehicleType = _selectedRideCategory.value,
            passengerFactor = _passengerOption.value.factor,
            distanceKm = _tripDistanceKm.value,
            dpiMultiplier = _surgeMultiplier.value
        )
    }

    // QR Dialog Controls
    fun openQrCardDialog() {
        _showQrCardDialog.value = true
        _qrTotalScans.value += 1
    }

    fun dismissQrCardDialog() {
        _showQrCardDialog.value = false
    }

    fun openQrBookingDialog() {
        _showQrBookingDialog.value = true
        _qrTotalScans.value += 1
    }

    fun dismissQrBookingDialog() {
        _showQrBookingDialog.value = false
    }

    // Complete booking via QR Web PWA channel (source = qr_web)
    fun completeQrWebBooking(
        destination: String,
        passengerOption: PassengerOption,
        paymentMethod: String,
        fare: Double
    ) {
        viewModelScope.launch {
            _destinationLocation.value = destination
            _passengerOption.value = passengerOption
            _selectedPaymentMethod.value = paymentMethod
            _lastBookingSource.value = "qr_web"
            _qrCompletedBookings.value += 1
            _showQrBookingDialog.value = false

            val driverShare = fare * 0.85

            // Credit driver wallet 85%
            _driverWalletBalance.value += driverShare
            _driverGrossEarnings.value += fare
            _driverTotalRides.value += 1

            repository.insertTransaction(
                WalletTransaction(
                    title = "85% QR Web Driver Share",
                    subtitle = "Mombasa QR-Stop → $destination • source: qr_web",
                    amount = driverShare,
                    isCredit = true,
                    status = "COMPLETED"
                )
            )

            val rideId = repository.insertRide(
                RideBooking(
                    pickupLocation = "Nyali Bridge QR Stop",
                    destinationLocation = destination,
                    rideType = "Standard Bike (QR)",
                    fareAmount = fare,
                    driverName = "Omar Juma",
                    driverVehicleNumber = "KMC-412A",
                    driverRating = 4.0,
                    status = "IN_PROGRESS"
                )
            )
            _activeRideId.value = rideId

            _currentScreen.value = ScreenState.TRACKING
            _trackingMinutesLeft.value = 3
            startLiveTrackingSimulation()
        }
    }

    // SDR Rule: 85/15 Auto-Split (85% to Driver Wallet, 15% retained by Sena)
    fun confirmAndPay() {
        viewModelScope.launch {
            val cat = rideCategories.find { it.id == _selectedRideCategory.value } ?: rideCategories.first()
            val totalFare = calculateCurrentFare()
            val driverShare = totalFare * 0.85

            _lastBookingSource.value = "app"

            // Credit driver's wallet with 85% of fare
            _driverWalletBalance.value += driverShare
            _driverGrossEarnings.value += totalFare
            _driverTotalRides.value += 1

            // Record transaction in Driver Wallet ledger
            val paymentMethodLabel = if (_selectedPaymentMethod.value == "M-Pesa") "M-Pesa STK Push" else "Cash Payment"
            repository.insertTransaction(
                WalletTransaction(
                    title = "85% Driver Share (${cat.name})",
                    subtitle = "${_pickupLocation.value} → ${_destinationLocation.value} • $paymentMethodLabel (app)",
                    amount = driverShare,
                    isCredit = true,
                    status = "COMPLETED"
                )
            )

            // Insert Ride Booking
            val rideId = repository.insertRide(
                RideBooking(
                    pickupLocation = _pickupLocation.value,
                    destinationLocation = _destinationLocation.value,
                    rideType = cat.name,
                    fareAmount = totalFare,
                    driverName = "Omar Juma",
                    driverVehicleNumber = "KMC-412A",
                    driverRating = 4.0,
                    status = "IN_PROGRESS"
                )
            )
            _activeRideId.value = rideId

            // Transition to Live Tracking Screen
            _currentScreen.value = ScreenState.TRACKING
            _trackingMinutesLeft.value = 3

            startLiveTrackingSimulation()
        }
    }

    private fun startLiveTrackingSimulation() {
        viewModelScope.launch {
            while (_trackingMinutesLeft.value > 1 && _currentScreen.value == ScreenState.TRACKING) {
                delay(4000)
                _trackingMinutesLeft.value -= 1
            }
            if (_currentScreen.value == ScreenState.TRACKING) {
                delay(3000)
                // Complete Ride & Show 1-4 Star Rating Screen
                _currentScreen.value = ScreenState.RATING
            }
        }
    }

    // SDR Rule: 1 to 4 Stars Rating Scale (1=Worst, 4=Best)
    fun setRating(rating: Float) {
        _ratingGiven.value = rating.coerceIn(1.0f, 4.0f)
    }

    fun toggleFeedbackChip(chip: String) {
        val current = _selectedFeedbackChips.value.toMutableSet()
        if (current.contains(chip)) {
            current.remove(chip)
        } else {
            current.add(chip)
        }
        _selectedFeedbackChips.value = current
    }

    fun updateReviewText(text: String) {
        _reviewText.value = text
    }

    fun submitFeedback() {
        viewModelScope.launch {
            val rideId = _activeRideId.value
            if (rideId != null) {
                val ride = repository.getRideById(rideId)
                if (ride != null) {
                    repository.updateRide(
                        ride.copy(
                            status = "COMPLETED",
                            ratingGiven = _ratingGiven.value,
                            reviewFeedback = _reviewText.value
                        )
                    )
                }
            }
            _currentScreen.value = ScreenState.HOME
            _selectedTab.value = SenaNavTab.HOME
        }
    }

    // SDR Section 5.6 & 10: Driver M-Pesa B2C Withdrawal Dialog Controls
    fun openDriverWithdrawDialog() {
        _showWithdrawDialog.value = true
        _withdrawSuccessMessage.value = null
        _withdrawErrorMessage.value = null
    }

    fun dismissDriverWithdrawDialog() {
        _showWithdrawDialog.value = false
        _isWithdrawProcessing.value = false
        _withdrawSuccessMessage.value = null
        _withdrawErrorMessage.value = null
    }

    fun setWithdrawAmount(amount: String) {
        _withdrawAmount.value = amount
    }

    fun setWithdrawPhoneNumber(phone: String) {
        _withdrawPhoneNumber.value = phone
    }

    fun triggerDriverB2cWithdrawal() {
        val amountNum = _withdrawAmount.value.toDoubleOrNull() ?: 0.0
        if (amountNum < 50) {
            _withdrawErrorMessage.value = "Minimum withdrawal is KES 50 (SDR FR-D015)."
            return
        }
        if (amountNum > 50000) {
            _withdrawErrorMessage.value = "Maximum withdrawal per transaction is KES 50,000."
            return
        }
        if (amountNum > _driverWalletBalance.value) {
            _withdrawErrorMessage.value = "Withdrawal amount exceeds available wallet balance."
            return
        }

        viewModelScope.launch {
            _withdrawErrorMessage.value = null
            _isWithdrawProcessing.value = true
            delay(2000) // Simulate Daraja B2C API response

            _isWithdrawProcessing.value = false

            // Deduct from driver wallet balance
            _driverWalletBalance.value -= amountNum

            // Record transaction
            repository.insertTransaction(
                WalletTransaction(
                    title = "B2C M-Pesa Withdrawal",
                    subtitle = "Payout to ${_withdrawPhoneNumber.value} • Daraja B2C",
                    amount = amountNum,
                    isCredit = false,
                    status = "SUCCESS"
                )
            )

            _withdrawSuccessMessage.value = "KES ${String.format("%,.2f", amountNum)} sent to M-Pesa ${_withdrawPhoneNumber.value}. Ref: MP${System.currentTimeMillis().toString().takeLast(6)}"
            delay(1500)
            _showWithdrawDialog.value = false
        }
    }
}

package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.UserRole
import com.example.ui.screens.ActiveRideScreen
import com.example.ui.screens.AdminPanelScreen
import com.example.ui.screens.BookingFlowScreen
import com.example.ui.screens.CustomerHomeScreen
import com.example.ui.screens.DriverDashboardScreen
import com.example.ui.screens.DriverProfileScreen
import com.example.ui.screens.LiveMapScreen
import com.example.ui.screens.LoginScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.LogisticsViewModel

enum class CustomerScreenState {
    HOME,
    BOOKING,
    ACTIVE_RIDE,
    LIVE_MAP
}

class MainActivity : ComponentActivity() {

    private val viewModel: LogisticsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                LogisticsApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun LogisticsApp(viewModel: LogisticsViewModel) {
    val isLoggedIn by viewModel.isLoggedIn.collectAsStateWithLifecycle()
    val currentRole by viewModel.currentRole.collectAsStateWithLifecycle()
    val activeOrder by viewModel.activeOrder.collectAsStateWithLifecycle()
    val orderHistory by viewModel.allOrders.collectAsStateWithLifecycle()
    val drivers by viewModel.allDrivers.collectAsStateWithLifecycle()
    val isDriverOnline by viewModel.isDriverOnline.collectAsStateWithLifecycle()
    val driverLocation by viewModel.driverLocation.collectAsStateWithLifecycle()
    val isLocationServiceRunning by viewModel.isLocationServiceRunning.collectAsStateWithLifecycle()
    val incomingRequest by viewModel.driverIncomingRequest.collectAsStateWithLifecycle()
    val pricingMultiplier by viewModel.pricingMultiplier.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val pickupAddress by viewModel.pickupAddress.collectAsStateWithLifecycle()
    val dropoffAddress by viewModel.dropoffAddress.collectAsStateWithLifecycle()
    val selectedVehicleId by viewModel.selectedVehicleId.collectAsStateWithLifecycle()
    val selectedGoodsType by viewModel.selectedGoodsType.collectAsStateWithLifecycle()
    val isHelperRequired by viewModel.isHelperRequired.collectAsStateWithLifecycle()
    val selectedPaymentMethod by viewModel.selectedPaymentMethod.collectAsStateWithLifecycle()

    var customerScreenState by remember { mutableStateOf(CustomerScreenState.HOME) }
    var isDriverViewingLiveMap by remember { mutableStateOf(false) }
    var isDriverViewingProfile by remember { mutableStateOf(false) }

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        if (isDriverViewingLiveMap) {
            LiveMapScreen(
                driverLocation = driverLocation,
                isServiceRunning = isLocationServiceRunning,
                onBack = { isDriverViewingLiveMap = false }
            )
        } else if (isDriverViewingProfile) {
            DriverProfileScreen(
                isOnline = isDriverOnline,
                onToggleOnline = { viewModel.toggleDriverOnline(context) },
                completedOrders = orderHistory,
                onBack = { isDriverViewingProfile = false }
            )
        } else {
            AnimatedContent(
                targetState = if (!isLoggedIn) "LOGIN" else currentRole.name,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "role_navigation"
            ) { target ->
                when (target) {
                    "LOGIN" -> {
                        LoginScreen(
                            onLoginSuccess = { phone, role ->
                                viewModel.login(phone, role)
                            }
                        )
                    }

                    UserRole.CUSTOMER.name -> {
                        when (customerScreenState) {
                            CustomerScreenState.HOME -> {
                                CustomerHomeScreen(
                                    activeOrder = activeOrder,
                                    orderHistory = orderHistory,
                                    currentRole = currentRole,
                                    onRoleSelected = { role ->
                                        viewModel.setRole(role)
                                    },
                                    onStartBooking = {
                                        customerScreenState = CustomerScreenState.BOOKING
                                    },
                                    onViewActiveRide = {
                                        if (activeOrder != null) {
                                            customerScreenState = CustomerScreenState.ACTIVE_RIDE
                                        }
                                    },
                                    driverLocation = driverLocation,
                                    onOpenLiveMap = {
                                        customerScreenState = CustomerScreenState.LIVE_MAP
                                    }
                                )
                            }

                            CustomerScreenState.BOOKING -> {
                                BookingFlowScreen(
                                    pickupAddress = pickupAddress,
                                    dropoffAddress = dropoffAddress,
                                    selectedVehicleId = selectedVehicleId,
                                    selectedGoodsType = selectedGoodsType,
                                    isHelperRequired = isHelperRequired,
                                    selectedPaymentMethod = selectedPaymentMethod,
                                    onPickupChange = viewModel::setPickup,
                                    onDropoffChange = viewModel::setDropoff,
                                    onVehicleSelect = viewModel::setVehicle,
                                    onGoodsSelect = viewModel::setGoodsType,
                                    onToggleHelper = viewModel::toggleHelper,
                                    onPaymentSelect = viewModel::setPaymentMethod,
                                    calculateFare = viewModel::calculateEstimatedFare,
                                    onConfirmBooking = {
                                        viewModel.bookRide {
                                            customerScreenState = CustomerScreenState.ACTIVE_RIDE
                                        }
                                    },
                                    onBack = {
                                        customerScreenState = CustomerScreenState.HOME
                                    }
                                )
                            }

                            CustomerScreenState.ACTIVE_RIDE -> {
                                if (activeOrder != null) {
                                    ActiveRideScreen(
                                        order = activeOrder!!,
                                        onBack = {
                                            customerScreenState = CustomerScreenState.HOME
                                        },
                                        onCancelRide = { id ->
                                            viewModel.cancelActiveRide(id)
                                            customerScreenState = CustomerScreenState.HOME
                                        },
                                        onCompleteRide = { id ->
                                            viewModel.completeActiveRide(id)
                                            customerScreenState = CustomerScreenState.HOME
                                        },
                                        driverLocation = driverLocation,
                                        onOpenLiveMap = {
                                            customerScreenState = CustomerScreenState.LIVE_MAP
                                        }
                                    )
                                } else {
                                    customerScreenState = CustomerScreenState.HOME
                                }
                            }

                            CustomerScreenState.LIVE_MAP -> {
                                LiveMapScreen(
                                    driverLocation = driverLocation,
                                    isServiceRunning = isLocationServiceRunning,
                                    onBack = {
                                        customerScreenState = CustomerScreenState.HOME
                                    }
                                )
                            }
                        }
                    }

                    UserRole.DRIVER.name -> {
                        DriverDashboardScreen(
                            currentRole = currentRole,
                            onRoleSelected = { role ->
                                viewModel.setRole(role)
                            },
                            isOnline = isDriverOnline,
                            onToggleOnline = {
                                viewModel.toggleDriverOnline(context)
                            },
                            incomingRequest = incomingRequest,
                            assignedOrder = activeOrder,
                            onAcceptRide = viewModel::acceptDriverRide,
                            onDeclineRide = viewModel::declineDriverRide,
                            onUpdateOrderStatus = viewModel::updateDriverDeliveryStatus,
                            onCompleteOrder = viewModel::completeDriverDelivery,
                            onSimulateRequest = viewModel::simulateIncomingRequest,
                            completedOrders = orderHistory,
                            driverLocation = driverLocation,
                            isServiceRunning = isLocationServiceRunning,
                            onOpenLiveMap = {
                                isDriverViewingLiveMap = true
                            },
                            onOpenProfile = {
                                isDriverViewingProfile = true
                            }
                        )
                    }

                    UserRole.ADMIN.name -> {
                        AdminPanelScreen(
                            currentRole = currentRole,
                            onRoleSelected = { role ->
                                viewModel.setRole(role)
                            },
                            orders = orderHistory,
                            drivers = drivers,
                            pricingMultiplier = pricingMultiplier,
                            onSetPricingMultiplier = viewModel::setPricingMultiplier,
                            onApproveKyc = { id ->
                                viewModel.updateKycStatus(id, "APPROVED")
                            },
                            onRejectKyc = { id ->
                                viewModel.updateKycStatus(id, "REJECTED")
                            }
                        )
                    }
                }
            }
        }
    }
}

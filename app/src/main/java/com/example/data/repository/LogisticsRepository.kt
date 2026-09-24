package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.model.BookingOrder
import com.example.data.model.DriverKyc
import kotlinx.coroutines.flow.Flow

class LogisticsRepository(private val database: AppDatabase) {
    private val orderDao = database.orderDao()
    private val driverDao = database.driverDao()

    val allOrders: Flow<List<BookingOrder>> = orderDao.getAllOrders()
    val activeOrder: Flow<BookingOrder?> = orderDao.getActiveOrder()
    val allDrivers: Flow<List<DriverKyc>> = driverDao.getAllDrivers()

    fun getOrderById(orderId: String): Flow<BookingOrder?> = orderDao.getOrderById(orderId)

    suspend fun createOrder(order: BookingOrder) {
        orderDao.insertOrder(order)
    }

    suspend fun updateOrderStatus(orderId: String, status: String) {
        orderDao.updateOrderStatus(orderId, status)
    }

    suspend fun cancelOrder(orderId: String) {
        orderDao.updateOrderStatus(orderId, "CANCELLED")
    }

    suspend fun completeOrder(orderId: String) {
        orderDao.updateOrderStatus(orderId, "DELIVERED")
    }

    suspend fun updateKycStatus(driverId: String, status: String) {
        driverDao.updateKycStatus(driverId, status)
    }

    suspend fun insertDriver(driver: DriverKyc) {
        driverDao.insertDriver(driver)
    }
}

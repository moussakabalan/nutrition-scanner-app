package com.example.nuitration_scanner_app.data.repository

import com.example.nuitration_scanner_app.data.model.ProductResponse
import com.example.nuitration_scanner_app.data.remote.OpenFoodFactsApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ProductRepository {
    private val apiService: OpenFoodFactsApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://world.openfoodfacts.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(OpenFoodFactsApiService::class.java)
    }

    // Main function call for product lookup
    suspend fun GetProductByBarcode(barcode: String): Result<ProductResponse> {
        return try {
            val response = apiService.GetProductByBarcode(barcode)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Product not found for barcode $barcode"))
            }
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }
}

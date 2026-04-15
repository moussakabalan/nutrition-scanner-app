package com.example.nuitration_scanner_app.data.remote

import com.example.nuitration_scanner_app.data.model.ProductResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface OpenFoodFactsApiService {

    @GET("api/v2/product/{barcode}.json")
    suspend fun GetProductByBarcode(
        @Path("barcode") barcode: String
    ): Response<ProductResponse>
}

package com.example.nuitration_scanner_app.data.model

import com.google.gson.annotations.SerializedName

data class ProductResponse(
    @SerializedName("status") val status: Int?,
    @SerializedName("code") val code: String?,
    @SerializedName("product") val product: Product?
)

data class Product(
    @SerializedName("product_name") val productName: String?,
    @SerializedName("brands") val brands: String?,
    @SerializedName("quantity") val quantity: String?,
    @SerializedName("categories") val categories: String?,
    @SerializedName("ingredients_text") val ingredientsText: String?,
    @SerializedName("nutrition_grades") val nutritionGrades: String?,
    @SerializedName("image_url") val imageUrl: String?,
    @SerializedName("nutriments") val nutriments: Nutriments?
)

data class Nutriments(
    @SerializedName("energy-kcal_100g") val energyKcal100g: Double?,
    @SerializedName("energy-kcal_serving") val energyKcalServing: Double?
)

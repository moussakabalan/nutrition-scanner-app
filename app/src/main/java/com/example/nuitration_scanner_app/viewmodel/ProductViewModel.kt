package com.example.nuitration_scanner_app.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nuitration_scanner_app.data.model.ProductResponse
import com.example.nuitration_scanner_app.data.repository.ProductRepository
import com.example.nuitration_scanner_app.util.ResultState
import kotlinx.coroutines.launch

class ProductViewModel(private val repository: ProductRepository) : ViewModel() {
    private val _productState = MutableLiveData<ResultState<ProductResponse>>()
    val productState: LiveData<ResultState<ProductResponse>> = _productState

    // Main function to fetch product info by barcode
    fun GetProductData(barcode: String) {
        _productState.value = ResultState.Loading

        viewModelScope.launch {
            val result = repository.GetProductByBarcode(barcode)
            if (result.isSuccess) {
                val productResponse = result.getOrNull()
                if (productResponse?.status == 1 && productResponse.product != null) {
                    _productState.value = ResultState.Success(productResponse)
                } else {
                    _productState.value = ResultState.Error("Product not found. Try another barcode.")
                }
            } else {
                _productState.value =
                    ResultState.Error(result.exceptionOrNull()?.message ?: "Something went wrong.")
            }
        }
    }
}

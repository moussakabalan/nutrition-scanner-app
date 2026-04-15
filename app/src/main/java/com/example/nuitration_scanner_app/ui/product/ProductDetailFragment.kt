package com.example.nuitration_scanner_app.ui.product

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.example.nuitration_scanner_app.R
import com.example.nuitration_scanner_app.data.model.Product
import com.example.nuitration_scanner_app.data.repository.ProductRepository
import com.example.nuitration_scanner_app.util.ResultState
import com.example.nuitration_scanner_app.viewmodel.ProductViewModel
import com.example.nuitration_scanner_app.viewmodel.ProductViewModelFactory

class ProductDetailFragment : Fragment(R.layout.fragment_product_detail) {
    private val viewModel: ProductViewModel by viewModels {
        ProductViewModelFactory(ProductRepository())
    }

    private var barcode: String = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        barcode = arguments?.getString("barcode").orEmpty()
        GetBarcodeTextView()?.text = getString(R.string.barcode_value, barcode)

        GetScanAnotherButton()?.setOnClickListener {
            findNavController().popBackStack()
        }

        ObserveProductState()
        viewModel.GetProductData(barcode)
    }

    // Main observer-function for loading, success, and error
    private fun ObserveProductState() {
        viewModel.productState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is ResultState.Loading -> ShowLoadingState()
                is ResultState.Success -> ShowProductState(state.data.product)
                is ResultState.Error -> ShowErrorState(state.message)
            }
        }
    }

    private fun ShowLoadingState() {
        GetProgressBar()?.visibility = View.VISIBLE
        GetErrorTextView()?.visibility = View.GONE
    }

    private fun ShowErrorState(message: String) {
        GetProgressBar()?.visibility = View.GONE
        GetErrorTextView()?.visibility = View.VISIBLE
        GetErrorTextView()?.text = message
    }

    private fun ShowProductState(product: Product?) {
        GetProgressBar()?.visibility = View.GONE
        GetErrorTextView()?.visibility = View.GONE

        if (product == null) {
            ShowErrorState(getString(R.string.product_not_found_message))
            return
        }

        GetNameTextView()?.text = getString(
            R.string.field_name,
            product.productName ?: getString(R.string.value_not_available)
        )
        GetBrandTextView()?.text = getString(
            R.string.field_brand,
            product.brands ?: getString(R.string.value_not_available)
        )
        GetQuantityTextView()?.text = getString(
            R.string.field_quantity,
            product.quantity ?: getString(R.string.value_not_available)
        )
        GetCategoriesTextView()?.text = getString(
            R.string.field_categories,
            product.categories ?: getString(R.string.value_not_available)
        )
        GetIngredientsTextView()?.text = getString(
            R.string.field_ingredients,
            product.ingredientsText ?: getString(R.string.value_not_available)
        )
        GetNutritionGradeTextView()?.text = getString(
            R.string.field_nutrition_grade,
            product.nutritionGrades?.uppercase() ?: getString(R.string.value_not_available)
        )
        GetCaloriesTextView()?.text = GetCaloriesText(product)

        if (product.imageUrl.isNullOrBlank()) {
            GetProductImageView()?.setImageResource(R.drawable.ic_launcher_foreground)
        } else {
            LoadProductImage(product.imageUrl)
        }
    }

    // Main function for loading image with timeout and fallback
    private fun LoadProductImage(imageUrl: String) {
        val productImageView = GetProductImageView() ?: return
        Glide.with(this)
            .load(imageUrl)
            .apply(RequestOptions().timeout(10000))
            .placeholder(R.drawable.ic_launcher_foreground)
            .error(R.drawable.ic_launcher_foreground)
            .listener(object : RequestListener<android.graphics.drawable.Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<android.graphics.drawable.Drawable>,
                    isFirstResource: Boolean
                ): Boolean {
                    GetErrorTextView()?.visibility = View.VISIBLE
                    GetErrorTextView()?.text = getString(R.string.image_load_failed_message)
                    return false
                }

                override fun onResourceReady(
                    resource: android.graphics.drawable.Drawable,
                    model: Any,
                    target: Target<android.graphics.drawable.Drawable>?,
                    dataSource: DataSource,
                    isFirstResource: Boolean
                ): Boolean = false
            })
            .into(productImageView)
    }

    private fun GetProgressBar(): ProgressBar? = view?.findViewById(R.id.progressBar)
    private fun GetErrorTextView(): TextView? = view?.findViewById(R.id.tvErrorMessage)
    private fun GetBarcodeTextView(): TextView? = view?.findViewById(R.id.tvBarcode)
    private fun GetNameTextView(): TextView? = view?.findViewById(R.id.tvName)
    private fun GetBrandTextView(): TextView? = view?.findViewById(R.id.tvBrand)
    private fun GetQuantityTextView(): TextView? = view?.findViewById(R.id.tvQuantity)
    private fun GetCategoriesTextView(): TextView? = view?.findViewById(R.id.tvCategories)
    private fun GetIngredientsTextView(): TextView? = view?.findViewById(R.id.tvIngredients)
    private fun GetNutritionGradeTextView(): TextView? = view?.findViewById(R.id.tvNutritionGrade)
    private fun GetProductImageView(): ImageView? = view?.findViewById(R.id.ivProductImage)
    private fun GetScanAnotherButton(): Button? = view?.findViewById(R.id.btnScanAnother)
    private fun GetCaloriesTextView(): TextView? = view?.findViewById(R.id.tvCalories)

    // Main helper for choosing the best available calorie value
    private fun GetCaloriesText(product: Product): String {
        val caloriesPer100g = product.nutriments?.energyKcal100g
        if (caloriesPer100g != null) {
            return getString(
                R.string.field_calories_100g,
                FormatCalorieValue(caloriesPer100g)
            )
        }

        val caloriesPerServing = product.nutriments?.energyKcalServing
        if (caloriesPerServing != null) {
            return getString(
                R.string.field_calories_serving,
                FormatCalorieValue(caloriesPerServing)
            )
        }

        return getString(R.string.field_calories_100g, getString(R.string.value_not_available))
    }

    private fun FormatCalorieValue(value: Double): String {
        return if (value % 1.0 == 0.0) {
            value.toInt().toString()
        } else {
            String.format("%.1f", value)
        }
    }
}

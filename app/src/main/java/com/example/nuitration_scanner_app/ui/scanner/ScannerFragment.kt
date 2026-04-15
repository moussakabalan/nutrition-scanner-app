package com.example.nuitration_scanner_app.ui.scanner

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.nuitration_scanner_app.R
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions

class ScannerFragment : Fragment(R.layout.fragment_scanner) {
    private var scannedBarcode: String? = null

    private val scanLauncher = registerForActivityResult(ScanContract()) { scanResult ->
        val barcodeValue = scanResult.contents
        if (!barcodeValue.isNullOrBlank()) {
            scannedBarcode = barcodeValue

            // Overwrite manual input so camera result is always what you navigate with
            GetManualBarcodeInput()?.setText(barcodeValue)
            GetManualBarcodeInput()?.setSelection(barcodeValue.length)
            
            GetViewProductButton()?.isEnabled = true
        }
    }

    private val cameraPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                StartBarcodeScanner()
            } else {
                GetScannedTextView()?.text = getString(R.string.camera_permission_needed)
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        GetScanButton()?.setOnClickListener { CheckCameraAndScan() }
        GetViewProductButton()?.setOnClickListener { GoToProductDetail() }
    }

    // Main function for checking camera permission before scanning
    private fun CheckCameraAndScan() {
        val isCameraGranted = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

        if (isCameraGranted) {
            StartBarcodeScanner()
        } else {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    // Main function for launching barcode scanner
    private fun StartBarcodeScanner() {
        val scanOptions = ScanOptions().apply {
            setPrompt(getString(R.string.scanner_prompt))
            setOrientationLocked(true)
            setBeepEnabled(true)
            setDesiredBarcodeFormats(ScanOptions.ALL_CODE_TYPES)
            setCaptureActivity(com.journeyapps.barcodescanner.CaptureActivity::class.java)
        }
        scanLauncher.launch(scanOptions)
    }

    // Main function for moving to detail screen
    private fun GoToProductDetail() {
        val manualBarcode = GetManualBarcodeInput()
            ?.text
            ?.toString()
            ?.trim()
            .orEmpty()
        val barcode = if (manualBarcode.isNotBlank()) manualBarcode else scannedBarcode

        if (barcode.isNullOrBlank()) {
            GetScannedTextView()?.text = getString(R.string.enter_or_scan_barcode)
            return
        }

        val args = Bundle().apply {
            putString("barcode", barcode)
        }
        findNavController().navigate(
            R.id.action_scannerFragment_to_productDetailFragment,
            args
        )
    }

    private fun GetScanButton(): Button? = view?.findViewById(R.id.btnScanBarcode)

    private fun GetViewProductButton(): Button? = view?.findViewById(R.id.btnViewProduct)

    private fun GetScannedTextView(): TextView? = view?.findViewById(R.id.tvScannedBarcode)

    private fun GetManualBarcodeInput(): EditText? = view?.findViewById(R.id.etManualBarcode)
}

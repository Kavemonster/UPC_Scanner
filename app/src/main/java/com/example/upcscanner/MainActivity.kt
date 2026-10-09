package com.example.upcscanner

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import android.Manifest
import android.content.pm.PackageManager
import android.provider.Settings
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
//Camera and camera preview
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.Row

//barcode scanning
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.compose.foundation.lazy.itemsIndexed
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import java.lang.annotation.Native

//allowing coroutines:
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

//for saving and loading file
import androidx.compose.ui.platform.LocalContext
import java.io.File

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.foundation.clickable
import androidx.activity.compose.BackHandler

import android.content.Intent
import android.net.Uri
import androidx.core.app.ActivityCompat
import android.app.Activity
import android.app.AlertDialog
import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextButton

//for numeric keyboard
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType

//for card appearance
import androidx.compose.material3.Card
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.remember
import kotlinx.coroutines.selects.select

import androidx.compose.foundation.layout.Spacer

//for little clear button on search bar
import androidx.compose.foundation.combinedClickable
import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.draw.clip

import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear

import androidx.compose.foundation.ExperimentalFoundationApi

class MainActivity : ComponentActivity() {

    private val cameraPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { granted ->
            if (granted) {

            }
            else {
                Toast.makeText(
                    this,
                    "Camera permission is required to scan barcodes, silly.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

    private fun openAppSettings() {
        val intent = Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.parse("package:$packageName")
        )
        startActivity(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) != PackageManager.PERMISSION_GRANTED
        ) {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }

        setContent {
            App()
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun App() {
    var selectedInventory by remember {
        mutableStateOf<String?>(null)
    }

    var inventories by remember {
        mutableStateOf<List<String>>(emptyList())
    }

    var showNewInventoryDialog by remember {
        mutableStateOf(false)
    }

    var newInventoryName by remember {
        mutableStateOf("")
    }

    var newInventoryError by remember {
        mutableStateOf("")
    }

    var deleteInventoryName by remember {
        mutableStateOf<String?>(null)
    }

    var renameInventory by remember {
        mutableStateOf<String?>(null)
    }

    var renameError by remember{
        mutableStateOf("")
    }

    val context = LocalContext.current

    LaunchedEffect(Unit) {
        val inventoriesDir = File(
            context.filesDir,
            "inventories"
        )

        if (inventoriesDir.exists()) {
            inventories = inventoriesDir
                .listFiles()
                ?.filter {it.isDirectory}
                ?.map {it.name}
                ?.sorted()
                ?: emptyList()
        }
    }

    BackHandler(enabled = selectedInventory != null){
        selectedInventory = null
    }

    //Opening Screen:

    if (selectedInventory == null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "My Inventories",
                fontSize = 28.sp,
                modifier = Modifier.padding(bottom = 24.dp)
            )
            inventories.forEach { inventoryName ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(MaterialTheme.colorScheme.primary)
                        .combinedClickable(
                            onClick = {
                                selectedInventory = inventoryName
                            },
                            onLongClick = {
                                deleteInventoryName = inventoryName
                            }
                        )
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = inventoryName,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
            Button(
                onClick = {
                    showNewInventoryDialog = true
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Add New Inventory")
            }
        }
        Text("Start Screen")
    }
    else {
        UPCScannerScreen(
            inventoryName = selectedInventory!!,
            requestCameraPermission = {

            },
            openAppSettings = {

            }
        )
    }

    if (showNewInventoryDialog) {
        AlertDialog(
            onDismissRequest = {
                showNewInventoryDialog = false
            },
            title = {
                Text("New Inventory")
            },
            text = {
                OutlinedTextField(
                    value = newInventoryName,
                    onValueChange = {
                        newInventoryName = it
                    },
                    label = {
                        Text("Inventory Name")
                    },
                    singleLine = true
                )
                if (newInventoryError.isNotEmpty()) {
                    Text(
                        text = newInventoryError,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val trimmedName = newInventoryName.trim()

                        if (trimmedName.isEmpty()) {
                            newInventoryError = "Please enter an inventory name, silly."
                            return@TextButton
                        }

                        if (inventories.contains(trimmedName)) {
                            newInventoryError = "That inventory already exists."
                            return@TextButton
                        }

                        val inventoriesDir = File(
                            context.filesDir,
                            "inventories"
                        )

                        val newInventoryDir = File(
                            inventoriesDir,
                            trimmedName
                        )

                        newInventoryDir.mkdirs()

                        inventories = inventories + trimmedName

                        newInventoryName = ""
                        newInventoryError = ""
                        showNewInventoryDialog = false
                    }
                ) {
                    Text("Create")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showNewInventoryDialog = false
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    if (deleteInventoryName != null) {
        AlertDialog(
            onDismissRequest = {
                deleteInventoryName = null
            },
            title = {
                Text(deleteInventoryName!!)
            },
            text = {
            Text("What would you like to do?")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        renameInventory = deleteInventoryName
                        newInventoryName = deleteInventoryName ?: ""
                        deleteInventoryName = null
                    }
                ) {
                    Text("Rename")
                }
            },

            dismissButton = {
                TextButton(
                    onClick = {
                        val inventoryName = deleteInventoryName

                        if (inventoryName != null) {
                            val inventoriesDir = File(
                                context.filesDir,
                                "inventories"
                            )

                            val inventoryDir = File(
                                inventoriesDir,
                                inventoryName
                            )

                            if (inventoryDir.exists()) {
                                val deleted = inventoryDir.deleteRecursively()

                                if (deleted) {
                                    inventories = inventories.filter {
                                        it != inventoryName
                                    }
                                }
                            }
                            deleteInventoryName = null
                        }
                    }
                ) {
                    Text("Delete")
                }
            }
        )
    }
    if (renameInventory != null) {
        AlertDialog(
            onDismissRequest = {
                renameInventory = null
                newInventoryName = ""
            },
            title = {
                Text("Rename Inventory")
            },
            text = {
                OutlinedTextField(
                    value = newInventoryName,
                    onValueChange = {
                        newInventoryName = it
                    },
                    label = {
                        Text("New Name")
                    },
                    singleLine = true
                )

                if (renameError.isNotEmpty()) {
                    Text(
                        text = renameError,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val trimmedName = newInventoryName.trim()

                        if (trimmedName.isEmpty()) {
                            renameError = "Please enter an inventory name, silly."
                            return@TextButton
                        }

                        if (inventories.contains(trimmedName) && trimmedName != renameInventory) {
                            renameError = "That inventory already exists."
                            return@TextButton
                        }
                        val inventoriesDir = File(
                            context.filesDir,
                            "inventories"
                        )

                        val oldDir = File(
                            inventoriesDir,
                            renameInventory!!
                        )

                        val newDir = File(
                            inventoriesDir,
                            trimmedName
                        )

                        val renamed = oldDir.renameTo(newDir)

                        if (renamed) {
                        inventories = inventories
                            .map {
                                if (it == renameInventory) trimmedName else it
                            }
                            .sorted()

                            renameInventory = null
                            newInventoryName = ""
                            renameError = ""
                        }
                        else {
                            renameError = "Could not rename the inventory."
                        }
                    }
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        renameInventory = null
                        newInventoryName = ""
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}
@Composable

//Screen for each inventory:

fun UPCScannerScreen(
    inventoryName: String,
    requestCameraPermission: () -> Unit,
    openAppSettings: () -> Unit
) {

    val context = LocalContext.current

    val inventoriesDir = File(
        context.filesDir,
        "inventories"
    )

    val currentInventoryDir = File(
        inventoriesDir,
        inventoryName
    )

    currentInventoryDir.mkdirs()

    val inventoryFile = File(
        currentInventoryDir,
        "inventory.txt"
    )

    val customNamesFile = File(
        currentInventoryDir,
        "custom_names.txt"
    )

    var showPermissionDialog by remember {
        mutableStateOf(false)
    }

    var inventory by remember {
        mutableStateOf(
            List(NativeBridge.getSize()) {index ->
                ProductData(
                    upc = NativeBridge.getProductUPC(index),
                    name = NativeBridge.getProductName(index),
                    quantity = NativeBridge.getProductQuantity(index)
                )
            }
        )
    }

    var scanning by remember {
        mutableStateOf(false)
    }

    var detectedBarcode by remember {
        mutableStateOf("")
    }

    var barcodeDetected by remember {
        mutableStateOf(false)
    }

    var selectedIndex by remember {
        mutableStateOf(-1)
    }

    var editIndex by remember {
        mutableStateOf(-1)
    }

    var editName by remember {
        mutableStateOf("")
    }

    var editUPC by remember {
        mutableStateOf("")
    }

    var showAddProductDialog by remember {
        mutableStateOf(false)
    }

    var newProductUPC by remember {
        mutableStateOf("")
    }

    var newProductName by remember {
        mutableStateOf("")
    }

    var searchProduct by remember {
        mutableStateOf("")
    }

    BackHandler(enabled = scanning) {
        scanning = false
    }

    fun refreshInventory(){
        inventory = List(NativeBridge.getSize()){ index ->
            ProductData(
                upc = NativeBridge.getProductUPC(index),
                name = NativeBridge.getProductName(index),
                quantity = NativeBridge.getProductQuantity(index)
            )
        }
    }

    if (showPermissionDialog) {
        AlertDialog(
            onDismissRequest = {
                showPermissionDialog = false
            },
            title = {
                Text("Camera Permission Required")
            },
            text = {
                Text(
                    "UPC Scanner needs camera access to scan barcodes. " +
                    "Please enable the camera permission in Settings to continue."
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showPermissionDialog = false
                        openAppSettings()
                    }
                ) {
                    Text("Open Settings")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showPermissionDialog = false
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
            .clickable(indication = null,
                interactionSource = remember {
                    MutableInteractionSource()
                }
            ) {
                selectedIndex = -1
            },

        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {

        Text(
            text = "$inventoryName",
            fontSize = 28.sp,
            modifier = Modifier.padding(
                top = 30.dp,
                bottom = 16.dp
            )
        )

        Button(
            onClick = {
                if (ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.CAMERA
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    barcodeDetected = false
                    scanning = true
                } else {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(
                            context as Activity,
                            Manifest.permission.CAMERA
                        )
                    ) {
                        requestCameraPermission()
                    } else {
                        showPermissionDialog = true
                    }

                }
            }
        ) {
            Text(text = "Scan Barcode")
        }

        Button(
            onClick = {
                showAddProductDialog = true

            }
        ) {
            Text("Add Product")
        }

        if (scanning) {
            CameraPreview(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .padding(top = 20.dp),
                onBarcodeDetected = { value ->

                    if (!barcodeDetected) {

                        barcodeDetected = true

                        detectedBarcode = value
                        scanning = false

                        CoroutineScope(Dispatchers.IO).launch {

                            val customName = NativeBridge.getCustomName(value)

                            val productName = if (customName.isNotEmpty()) {
                                customName
                            } else {
                                ProductLookup.lookupProduct(value)
                            }

                            NativeBridge.addProduct(
                                value,
                                if (productName.isNotEmpty()) {
                                    productName
                                } else {
                                    "Unknown Product"
                                }
                            )
                            NativeBridge.saveInventory(inventoryFile.absolutePath)

                            refreshInventory()
                        }
                    }
                }
            )
        }


        if (!scanning) {

            //Displays the search box
            OutlinedTextField(
                value = searchProduct,
                onValueChange = {
                    searchProduct = it
                },
                label = {
                    Text("Search")
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    if (searchProduct.isNotEmpty()) {
                        IconButton(
                            onClick = {
                                searchProduct = ""
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear search"
                            )
                        }
                    }
                }
            )

            val filteredIndex = inventory.indices.filter { index ->
                inventory[index].name.contains(searchProduct, ignoreCase = true) ||
                inventory[index].upc.contains(searchProduct)
            }

            //shows the individual items in a list
            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                itemsIndexed(filteredIndex) { _, index, ->

                    val product = inventory[index]

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(2.dp)
                            .clickable (
                                indication = null,
                                interactionSource = remember {
                                    MutableInteractionSource()
                                }
                            ){
                                selectedIndex =
                                    if (selectedIndex == index) {
                                        -1
                                    } else {
                                        index
                                    }
                            },
                        shape = RoundedCornerShape(3.dp),
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 3.dp
                        ),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFDBDBDB)
                        ),
                        border = BorderStroke(
                            1.dp,
                            if (selectedIndex == index) {
                                Color(0xFF4F4F4F)
                            }
                            else {
                                Color(0xFFC6CFCF)
                            }
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    top = 8.dp,
                                    bottom = 8.dp,
                                    start = 4.dp,
                                    end = 4.dp
                                    ),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(text = product.name)
                                Text(text = "UPC: ${product.upc}")
                                Text(text = "Quantity: ${product.quantity}")
                            }
                            if (selectedIndex == index) {

                                Button(
                                    onClick = {
                                        if (product.quantity >= 1) {
                                            NativeBridge.changeQuantity(index, -1)
                                            NativeBridge.saveInventory(inventoryFile.absolutePath)
                                            refreshInventory()
                                        }
                                    }
                                ) {
                                    Text("-")
                                }
                                Button(
                                    onClick = {
                                        NativeBridge.changeQuantity(index, 1)
                                        NativeBridge.saveInventory(inventoryFile.absolutePath)
                                        refreshInventory()
                                    }
                                ) {
                                    Text("+")
                                }
                                Button(
                                    onClick = {
                                        editIndex = index
                                        editName = product.name
                                        editUPC = product.upc
                                    }
                                ) {
                                    Text("Edit")
                                }
                                Button(
                                    onClick = {
                                        NativeBridge.removeProduct(index)
                                        NativeBridge.saveInventory(inventoryFile.absolutePath)
                                        refreshInventory()
                                    }
                                ) {
                                    Text("Remove")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    LaunchedEffect(Unit) {
        NativeBridge.loadInventory(inventoryFile.absolutePath)
        NativeBridge.loadCustomNames(customNamesFile.absolutePath)
        refreshInventory()
    }

    if (showAddProductDialog) {
        AlertDialog(
            onDismissRequest =  {
                showAddProductDialog = false
            },
            title = {
                Text("Add a Product")
            },
            text = {
                Column {
                    OutlinedTextField(
                        value = newProductUPC,
                        onValueChange = {
                            newProductUPC = it.filter {char -> char.isDigit()}
                        },
                        label = {
                            Text("UPC")
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    OutlinedTextField(
                        value = newProductName,
                        onValueChange = {
                            newProductName = it
                        },
                        label = {
                            Text("Product Name")
                        }
                    )

                }
            },
            //for adding a product
            confirmButton = {
                TextButton(
                    onClick = {
                        NativeBridge.addProduct(
                            newProductUPC,
                            newProductName
                        )
                        NativeBridge.saveInventory(
                            inventoryFile.absolutePath
                        )
                        refreshInventory()

                        newProductUPC = ""
                        newProductName = ""

                        showAddProductDialog = false
                    }
                ) {
                    Text("Add")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showAddProductDialog = false
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    if (editIndex >= 0) {
        AlertDialog(
            onDismissRequest = {
                editIndex = -1
            },
            title = {
                Text("Edit Product")
            },
            text = {
                Column {
                    OutlinedTextField(
                        value = editUPC,
                        onValueChange = {
                            editUPC = it.filter {char -> char.isDigit()}
                        },
                        label = {
                            Text("UPC")
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )

                    OutlinedTextField(
                        value = editName,
                        onValueChange = {
                            editName = it
                        },
                        label = {
                            Text("Product Name")
                        },
                        singleLine = true
                    )
                }
            },
            //for editing an existing product
            confirmButton = {
                TextButton(
                    onClick = {

                        val oldUPC = inventory[editIndex].upc

                        val customName = NativeBridge.getCustomName(oldUPC)

                        NativeBridge.editProductUPC(editIndex, editUPC)

                        NativeBridge.editProductName(editIndex, editName)

                        NativeBridge.removeCustomName(oldUPC)

                        if (customName.isNotEmpty()) {
                            NativeBridge.setCustomName(editUPC, customName)
                        }

                        NativeBridge.saveInventory(inventoryFile.absolutePath)

                        NativeBridge.saveCustomNames(customNamesFile.absolutePath)

                        refreshInventory()

                        editIndex = -1
                    }
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        editIndex = -1
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun CameraPreview(modifier: Modifier = Modifier,
                  onBarcodeDetected: (String) -> Unit) {

    AndroidView(
        modifier = modifier,
        factory = { context->
            PreviewView(context).apply {
                val cameraProviderFuture =
                    ProcessCameraProvider.getInstance(context)

                cameraProviderFuture.addListener({
                    val cameraProvider = cameraProviderFuture.get()

                    val preview = Preview.Builder().build()

                    preview.surfaceProvider = surfaceProvider

                    val imageAnalysis = ImageAnalysis.Builder()
                        .setBackpressureStrategy(
                            ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST
                        )
                        .build()

                    imageAnalysis.setAnalyzer(
                        ContextCompat.getMainExecutor(context)
                    ) { imageProxy ->

                        val mediaImage = imageProxy.image

                        if (mediaImage != null) {

                            val image = InputImage.fromMediaImage(
                                mediaImage,
                                imageProxy.imageInfo.rotationDegrees
                            )

                            val scanner = BarcodeScanning.getClient()

                            scanner.process(image)
                                .addOnSuccessListener { barcodes ->
                                    for (barcode in barcodes) {
                                        barcode.rawValue?.let { value ->
                                            onBarcodeDetected(value)
                                        }
                                    }
                                }
                                .addOnFailureListener {
                                    println("Barcode Scan Failed")
                                }
                                .addOnCompleteListener {
                                    imageProxy.close()
                                }

                        }
                        else {
                            imageProxy.close()
                        }
                    }

                    val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                    cameraProvider.unbindAll()

                    cameraProvider.bindToLifecycle(
                        context as androidx.lifecycle.LifecycleOwner,
                        cameraSelector,
                        preview,
                        imageAnalysis
                    )
                }, ContextCompat.getMainExecutor(context))
            }
        }
    )
}
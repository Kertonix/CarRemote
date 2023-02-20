/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter and
 * https://github.com/android/wear-os-samples/tree/main/ComposeAdvanced to find the most up to date
 * changes to the libraries and their usages.
 */

package com.kertonix.carremote.presentation

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import androidx.activity.ComponentActivity

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

import com.kertonix.carremote.R

class MainActivity : ComponentActivity() {

    private val REQUEST_ENABLE_BT = 1
    lateinit var lngList: ArrayList<String>
    private val deviceList: MutableList<String> = mutableListOf()
    private val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()

    companion object {
        private const val REQUEST_BLUETOOTH_PERMISSIONS = 1
    }
    //private ArrayList<String> stringArrayList = new ArrayList<String>();
    //private ArrayAdapter<String> arrayAdapter;
//    private var scanButton: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val scanList = findViewById<ListView>(R.id.scanList)
        val scanButton = findViewById<Button>(R.id.scanButton)
        lngList = ArrayList()


        val adapter: ArrayAdapter<String?> = ArrayAdapter<String?>(
            this@MainActivity,
            R.layout.center_text,
            R.id.textItem,
            lngList as List<String?>
        )
        scanList.adapter = adapter

        scanButton.setOnClickListener {
            searchAndAddDevices(this)
            lngList = ArrayList(deviceList)

            adapter.notifyDataSetChanged()
        }

//        setContent {
//            WearApp("Android")
//        }
    }


    fun searchAndAddDevices(activity: Activity) {
        // Check if Bluetooth is supported on the device
        if (bluetoothAdapter == null) {
            // Bluetooth is not supported, handle this case as needed
            return
        }

        // Check if the app has the required Bluetooth permissions
        if (ContextCompat.checkSelfPermission(
                activity,
                Manifest.permission.BLUETOOTH
            ) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Request the required permissions
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.BLUETOOTH, Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_BLUETOOTH_PERMISSIONS
            )
            return
        }

        // Check if Bluetooth is enabled
        if (!bluetoothAdapter.isEnabled) {
            // Request the user to enable Bluetooth
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            activity.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
            return
        }

        // Start the discovery process
        bluetoothAdapter.startDiscovery()

        // Register a BroadcastReceiver to listen for discovered devices
        val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        activity.registerReceiver(bluetoothReceiver, filter)
    }

    private val bluetoothReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (BluetoothDevice.ACTION_FOUND == action) {
                // Get the BluetoothDevice object from the Intent
                val device =
                    intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                // Add the device name and MAC address to the list of discovered devices
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    // Add the device name and MAC address to the list of discovered devices
                    if (device != null) {
                        deviceList.add(device.name + "\n" + device.address)
                    }
                }
            }
        }
    }
}
@Preview(device = Devices.WEAR_OS_SMALL_ROUND, showSystemUi = true)
@Composable
fun DefaultPreview() {
}
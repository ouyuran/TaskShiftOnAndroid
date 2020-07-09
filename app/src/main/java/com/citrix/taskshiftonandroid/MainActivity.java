package com.citrix.taskshiftonandroid;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.companion.AssociationRequest;
import android.companion.BluetoothDeviceFilter;
import android.companion.CompanionDeviceManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;
import java.util.UUID;


public class MainActivity extends AppCompatActivity {

    //private static final int REQUEST_ENABLE_BT = 1;
    private CompanionDeviceManager deviceManager;
    private AssociationRequest pairingRequest;
    private BluetoothDeviceFilter deviceFilter;

    private static final int SELECT_DEVICE_REQUEST_CODE = 42;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Check for available Bluetooth adapter and enables it if it isn't.
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            setContentView(R.layout.no_bluetooth);
        } else {
            // Enable  bluetooth and discoverability at the same time
            Intent discoverableIntent =
                    new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(discoverableIntent);
            /* if (!bluetoothAdapter.isEnabled())
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);*/
        }
        final Button button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                deviceManager = getSystemService(CompanionDeviceManager.class);
                deviceFilter = new BluetoothDeviceFilter.Builder().build();
                pairingRequest = new AssociationRequest.Builder()
                        .addDeviceFilter(deviceFilter)
                        .setSingleDevice(false)
                        .build();
                deviceManager.associate(pairingRequest,
                        new CompanionDeviceManager.Callback() {
                            @Override
                            public void onDeviceFound(IntentSender chooserLauncher) {
                                try {
                                    startIntentSenderForResult(chooserLauncher,
                                            SELECT_DEVICE_REQUEST_CODE, null, 0, 0, 0);
                                } catch (IntentSender.SendIntentException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onFailure(CharSequence charSequence) {

                            }
                        },
                        null);
            }
        });

    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_DEVICE_REQUEST_CODE &&
                resultCode == Activity.RESULT_OK) {
            // User has chosen to pair with the Bluetooth device.
            BluetoothDevice deviceToPair =
                    data.getParcelableExtra(CompanionDeviceManager.EXTRA_DEVICE);
            deviceToPair.createBond();
            // ... Continue interacting with the paired device.
            Context context = getApplicationContext();


            /*Need to change later
            CharSequence text = "You have shifted " + "Task 3" + " to" + " Haoran Li" + ".";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();*/

            IntentFilter filter = new IntentFilter();
            filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
            DynamicReceiver dynamicReceiver = new DynamicReceiver();
            //注册广播接收
            registerReceiver(dynamicReceiver,filter);
        }
    }


    class DynamicReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            //通过土司验证接收到广播
            int bonded = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.BOND_NONE);
            if (bonded == BluetoothDevice.BOND_BONDED) {
                Toast t = Toast.makeText(context,"配对成功,正在连接", Toast.LENGTH_SHORT);
                t.setGravity(Gravity.TOP,0,0);//方便录屏，将土司设置在屏幕顶端
                t.show();
                BluetoothDevice deviceToConnect =
                        intent.getParcelableExtra(CompanionDeviceManager.EXTRA_DEVICE);
            }
        }
    }

}
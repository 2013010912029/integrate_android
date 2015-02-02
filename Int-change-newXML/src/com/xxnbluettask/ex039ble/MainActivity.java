package com.xxnbluettask.ex039ble;

import java.util.ArrayList;
import java.util.List;
import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	BluetoothAdapter mBluetoothAdapter;
	private ArrayList<Integer> rssis;
	//private LeDeviceListAdapter mLeDeviceListAdapter;
	
	BluetoothDevice terget_device=null;
	
	BluetoothGatt mBluetoothGatt=null;
	
	int REQUEST_ENABLE_BT=1;
	
	Button btn,btn_connect,btn_disconnect;
	ListView lv;
	
	private boolean mScanning;
    private Handler mHandler;

    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 10000;
    
    List<BluetoothGattService> list_service;
	
    LeDeviceListAdapter mleDeviceListAdapter;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// Use this check to determine whether BLE is supported on the device. Then
		// you can selectively disable BLE-related features.
		if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
		    Toast.makeText(this, "不支持BLE", Toast.LENGTH_SHORT).show();
		    finish();
		}
		
		// Initializes Bluetooth adapter.
		final BluetoothManager bluetoothManager =
		        (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
		mBluetoothAdapter = bluetoothManager.getAdapter();
		
		// Ensures Bluetooth is available on the device and it is enabled. If not,
		// displays a dialog requesting user permission to enable Bluetooth.
		if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
		    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
		    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
		}
		
		lv=(ListView)this.findViewById(R.id.listView1);
		
		mleDeviceListAdapter=new LeDeviceListAdapter();
		
		lv.setAdapter(mleDeviceListAdapter);
		
		
		mHandler=new Handler();
		
		btn=(Button)this.findViewById(R.id.button1);
		
		btn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				scanLeDevice(true);
				
			}
		});
		
		
		btn_connect=(Button)this.findViewById(R.id.button2);
		btn_connect.setVisibility(View.GONE);
		
		btn_connect.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				if(terget_device!=null)
				{
					mBluetoothGatt = terget_device.connectGatt(MainActivity.this, false, bleGattCallback);
					System.out.println("连接成功!");
					
					mBluetoothGatt.discoverServices();
					/*
					list_service=mBluetoothGatt.getServices();
					
					for(BluetoothGattService service :list_service )
					{
						System.out.println(service.getUuid().toString());
					}
					*/
				}
				
			}
		});
		
		btn_disconnect=(Button)this.findViewById(R.id.button3);
		btn_disconnect.setVisibility(View.GONE);
		
		btn_disconnect.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
			
				if(mBluetoothGatt!=null){
					mBluetoothGatt.disconnect();
				}
				
			}
		});
		
		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0,  View v, int position, long id) {
				// TODO Auto-generated method stub
				final BluetoothDevice device = mleDeviceListAdapter.getDevice(position);
		        if (device == null) return;
		        final Intent intent = new Intent(MainActivity.this, MyGattDetail.class);
		        intent.putExtra(MyGattDetail.EXTRAS_DEVICE_NAME, device.getName());
		        intent.putExtra(MyGattDetail.EXTRAS_DEVICE_ADDRESS, device.getAddress());
		        intent.putExtra(MyGattDetail.EXTRAS_DEVICE_RSSI, rssis.get(position).toString());
		        if (mScanning) {
		            mBluetoothAdapter.stopLeScan(mLeScanCallback);
		            mScanning = false;
		        }
		        startActivity(intent);
			}
		});
		
	}
	
	private MyGattCallback bleGattCallback=new MyGattCallback();
	

	private void scanLeDevice(final boolean enable) {
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                }
            }, SCAN_PERIOD);

            mScanning = true;
            mBluetoothAdapter.startLeScan(mLeScanCallback);
        } else {
            mScanning = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
    
    }

	
	private BluetoothAdapter.LeScanCallback mLeScanCallback =
	        new BluetoothAdapter.LeScanCallback() {
	    

		@Override
		public void onLeScan(final BluetoothDevice device, final int rssi, byte[] scanRecord) {
			// TODO Auto-generated method stub
				
			runOnUiThread(new Runnable() {
                @Override
                public void run() {
                	mleDeviceListAdapter.addDevice(device,rssi);
                	mleDeviceListAdapter.notifyDataSetChanged();
                }
            });
			
			System.out.println("Address:"+device.getAddress());
			System.out.println("Name:"+device.getName());
			System.out.println("rssi:"+rssi);
			
		}
	};
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	 // Adapter for holding devices found through scanning.
    private class LeDeviceListAdapter extends BaseAdapter {
        private ArrayList<BluetoothDevice> mLeDevices;
        
        private LayoutInflater mInflator;
        
        
        public LeDeviceListAdapter() {
            super();
            rssis=new ArrayList<Integer>();
            mLeDevices = new ArrayList<BluetoothDevice>();
            mInflator = getLayoutInflater();
        }

        public void addDevice(BluetoothDevice device,int rssi) {
            if(!mLeDevices.contains(device)) {
                mLeDevices.add(device);
                rssis.add(rssi);
            }
        }

        public BluetoothDevice getDevice(int position) {
            return mLeDevices.get(position);
        }

        public void clear() {
            mLeDevices.clear();
            rssis.clear();
        }

        @Override
        public int getCount() {
            return mLeDevices.size();
        }

        @Override
        public Object getItem(int i) {
            return mLeDevices.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
          
            // General ListView optimization code.
           
                view = mInflator.inflate(R.layout.listitem, null);
                
                TextView deviceAddress = (TextView) view.findViewById(R.id.tv_deviceAddr);
                TextView deviceName = (TextView) view.findViewById(R.id.tv_deviceName);
                TextView rssi = (TextView) view.findViewById(R.id.tv_rssi);
              

            BluetoothDevice device = mLeDevices.get(i);
            deviceAddress.setText( device.getAddress());
            deviceName.setText(device.getName());
            rssi.setText(""+rssis.get(i));
            
            
           
            return view;
        }
    }


	
}

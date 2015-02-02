package com.xxnbluettask.ex039ble;



import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import android.app.Activity;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;

public class MyGattDetail extends Activity {

	private final static String TAG = MyGattDetail.class.getSimpleName();
	protected static String EXTRAS_DEVICE_NAME ="DEVICE_NAME";;
	protected static String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";
	protected static String EXTRAS_DEVICE_RSSI = "RSSI";
	private final static String UUID_KEY_DATA = "0000ffe1-0000-1000-8000-00805f9b34fb";
	static byte writeByteValue = 0;
    TextView tv_addr,tv_name,tv_status,tv_uuid,tv_rssi;
    private String status="disconnected";
    ExpandableListView lv;
    private static Handler mHandler;
    private BluetoothGatt mBtGatt = null;
    private static BluetoothLeService mBluetoothLeService;
    private boolean mConnected = false;
    private static BluetoothGattCharacteristic mNotifyCharacteristic;
    
    private String mDeviceName;
    private String mDeviceAddress;
    private Bundle b;
    private final String LIST_NAME = "NAME";
    private final String LIST_UUID = "UUID";
    
    private static BluetoothGattCharacteristic target_chara=null;
    
    private ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics =
            new ArrayList<ArrayList<BluetoothGattCharacteristic>>();
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.bleinfo_layout);
		mHandler = new Handler();
		
		tv_addr=(TextView)this.findViewById(R.id.tv_info_deviceaddr);
		tv_name=(TextView)this.findViewById(R.id.tv_info_devicename);
		tv_status=(TextView)this.findViewById(R.id.tv_info_constatus);
		tv_uuid=(TextView)this.findViewById(R.id.tv_targetuuid);
		
		tv_uuid.setText("��û��ѡ��Ŀ������ֵ");
		tv_rssi=(TextView) findViewById(R.id.rssi_value);
		
		lv=(ExpandableListView)this.findViewById(R.id.expandableListView1);
		lv.setOnChildClickListener(servicesListClickListner);
		
		b=getIntent().getExtras();
		tv_addr.setText(b.getString(EXTRAS_DEVICE_ADDRESS));
		mDeviceAddress=b.getString(EXTRAS_DEVICE_ADDRESS);
		tv_name.setText(b.getString(EXTRAS_DEVICE_NAME));
		mDeviceName=b.getString(EXTRAS_DEVICE_NAME);
		tv_rssi.setText(b.getString(EXTRAS_DEVICE_RSSI));
		 Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
	     bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
	}
	     
	     
	
	 // Code to manage Service lifecycle.
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

    	
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                finish();
            }
            // Automatically connects to the device upon successful start-up initialization.
            mBluetoothLeService.connect(mDeviceAddress);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
        
    };
    // Handles various events fired by the Service.
    // ACTION_GATT_CONNECTED: connected to a GATT server.
    // ACTION_GATT_DISCONNECTED: disconnected from a GATT server.
    // ACTION_GATT_SERVICES_DISCOVERED: discovered GATT services.
    // ACTION_DATA_AVAILABLE: received data from the device.  This can be a result of read
    //                        or notification operations.
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                mConnected = true;
                status="connected";
                updateConnectionState(status);
                System.out.println("BroadcastReceiver :"+"device connected");
              
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                mConnected = false;
                status="disconnected";
                updateConnectionState(status);
                System.out.println("BroadcastReceiver :"+"device disconnected");
               
               
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                // Show all the supported services and characteristics on the user interface.
                displayGattServices(mBluetoothLeService.getSupportedGattServices());
            	 System.out.println("BroadcastReceiver :"+"device SERVICES_DISCOVERED");
            } 
//            	 else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
//                //displayData(intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
//            	 System.out.println("BroadcastReceiver onData:"+intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
//            }
        }
    };
    
    private static BluetoothLeService.OnDataAvailableListener mOnDataAvailable = new com.xxnbluettask.ex039ble.BluetoothLeService.OnDataAvailableListener(){

		@Override
		public void onCharacteristicRead(BluetoothGatt gatt,
				BluetoothGattCharacteristic characteristic, int status) {
			if (status == BluetoothGatt.GATT_SUCCESS) {
            	Log.e(TAG,"onCharacteristicRead "+gatt.getDevice().getName()
						+" read "
						+characteristic.getUuid().toString()
						+" -> "
						+Utils.bytesToHexString(characteristic.getValue()));
            	mBluetoothLeService.broadcastUpdate(BluetoothLeService.ACTION_DATA_AVAILABLE, characteristic);
            }
		}

		@Override
		public void onCharacteristicWrite(BluetoothGatt gatt,
				BluetoothGattCharacteristic characteristic) {
                	mBluetoothLeService.broadcastUpdate(BluetoothLeService.ACTION_DATA_AVAILABLE, characteristic);
                	Log.e(TAG,"onCharacteristicWrite "+gatt.getDevice().getName()
        					+" write "
        					+characteristic.getUuid().toString()
        					+" -> "
        					+new String(characteristic.getValue()));
			
		}

		@Override
		public void onCharacteristicChanged(BluetoothGatt gatt,
				BluetoothGattCharacteristic characteristic) {
			Log.e(TAG,"onCharacteristicChanged "+gatt.getDevice().getName()
					+" write "
					+characteristic.getUuid().toString()
					+" -> "
					+new String(characteristic.getValue()));
			
		}
		
    };
    private void updateConnectionState(final String status)
    {
    	runOnUiThread(new Runnable() {
            @Override
            public void run() {
            	tv_status.setText(status);
            }
        });
    }
    
	@Override
	protected void onDestroy() {
		super.onDestroy();
	   
	    unregisterReceiver(mGattUpdateReceiver);
	    mBluetoothLeService = null;
	}
	
	//Activity����ʱ�򣬰󶨹㲥�������������������ӷ��񴫹������¼�
	@Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        if (mBluetoothLeService != null) {
            final boolean result = mBluetoothLeService.connect(mDeviceAddress);
            Log.d(TAG, "Connect request result=" + result);
        }
    }
	
	private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }
	
	 private void displayGattServices(List<BluetoothGattService> gattServices) {
		 
		 if (gattServices == null) return;
	        String uuid = null;
	        String unknownServiceString = "unknown_service";
	        String unknownCharaString = "unknown_characteristic";
		 
	        //��������,����չ�����б�ĵ�һ������
	        ArrayList<HashMap<String, String>> gattServiceData = new ArrayList<HashMap<String, String>>();
	        
	        //�������ݣ�������ĳһ���������������ֵ���ϣ�
	        ArrayList<ArrayList<HashMap<String, String>>> gattCharacteristicData
	                = new ArrayList<ArrayList<HashMap<String, String>>>();
	        
	        //���ֲ�Σ���������ֵ����
	        mGattCharacteristics = new ArrayList<ArrayList<BluetoothGattCharacteristic>>();
	        
	     // Loops through available GATT Services.
	        for (BluetoothGattService gattService : gattServices) {
	        
	        	//��ȡ�����б�
	        	HashMap<String, String> currentServiceData = new HashMap<String, String>();
	            uuid = gattService.getUuid().toString();
	            
	            //������ݸ�uuid��ȡ��Ӧ�ķ������ơ�SampleGattAttributes�������Ҫ�Զ��塣
	            currentServiceData.put(
	                    LIST_NAME, SampleGattAttributes.lookup(uuid, unknownServiceString));
	            currentServiceData.put(LIST_UUID, uuid);
	            gattServiceData.add(currentServiceData);
	            
	            System.out.println("Service uuid:"+uuid);
	        	
	            ArrayList<HashMap<String, String>> gattCharacteristicGroupData =
	                    new ArrayList<HashMap<String, String>>();
	            
	            //�ӵ�ǰѭ����ָ��ķ����ж�ȡ����ֵ�б�
	            List<BluetoothGattCharacteristic> gattCharacteristics =
	                    gattService.getCharacteristics();
	            
	            ArrayList<BluetoothGattCharacteristic> charas =
	                    new ArrayList<BluetoothGattCharacteristic>();
	            
	         // Loops through available Characteristics.
	            //���ڵ�ǰѭ����ָ��ķ����е�ÿһ������ֵ
	            for (final BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
	                charas.add(gattCharacteristic);
	                HashMap<String, String> currentCharaData = new HashMap<String, String>();
	                uuid = gattCharacteristic.getUuid().toString();
                
	                currentCharaData.put(
	                        LIST_NAME, SampleGattAttributes.lookup(uuid, unknownCharaString));
	                currentCharaData.put(LIST_UUID, uuid);
	                if(gattCharacteristic.getUuid().toString().equals(SampleGattAttributes.HEART_RATE_MEASUREMENT)){                    
	                    //���Զ�ȡ��ǰCharacteristic���ݣ��ᴥ��mOnDataAvailable.onCharacteristicRead()  
	                    mHandler.postDelayed(new Runnable() {  
	                        @Override  
	                        public void run() {  
	                        	mBluetoothLeService.readCharacteristic(gattCharacteristic);  
	                        }  
	                    }, 500);  
	                      
	                    //����Characteristic��д��֪ͨ,�յ�����ģ������ݺ�ᴥ��mOnDataAvailable.onCharacteristicWrite()  
	                    mBluetoothLeService.setCharacteristicNotification(gattCharacteristic, true);  
	                    //������������  
	                    //������ģ��д������  
	                    //mBluetoothLeService.writeCharacteristic(gattCharacteristic);  
	                }  
	                List<BluetoothGattDescriptor> descriptors= gattCharacteristic.getDescriptors();
	                for(BluetoothGattDescriptor descriptor:descriptors)
	                {
	                	System.out.println("---descriptor UUID:"+descriptor.getUuid());
	                	//��ȡ����ֵ������
	                	mBluetoothLeService.getCharacteristicDescriptor(descriptor); 
	                	//mBluetoothLeService.setCharacteristicNotification(gattCharacteristic, true);
	                }
	                
	                gattCharacteristicGroupData.add(currentCharaData);
	            }
	            //���Ⱥ�˳�򣬷ֲ�η�������ֵ�����У�ֻ������ֵ
	            mGattCharacteristics.add(charas);
	            //�����ڶ�����չ�б��������������ֵ��
	            gattCharacteristicData.add(gattCharacteristicGroupData);
	            
	        }
	        
	        SimpleExpandableListAdapter gattServiceAdapter = new SimpleExpandableListAdapter(
	                this,
	                gattServiceData,
	                android.R.layout.simple_expandable_list_item_2,
	                new String[] {LIST_NAME, LIST_UUID},
	                new int[] { android.R.id.text1, android.R.id.text2 },
	                gattCharacteristicData,
	                android.R.layout.simple_expandable_list_item_2,
	                new String[] {LIST_NAME, LIST_UUID},
	                new int[] { android.R.id.text1, android.R.id.text2 }
	        );
	        
	        lv.setAdapter(gattServiceAdapter);
	        
	 }

	 /*
	 �ú�����Ҫ��������û�ѡ���б��е�һ�������Ȱ�uuid ��ȡ��������ʾ (tv_uuid)
	 
	 */
	 
	 private final ExpandableListView.OnChildClickListener servicesListClickListner =
	            new ExpandableListView.OnChildClickListener() {
	                @Override
	                public boolean onChildClick(ExpandableListView parent, View v, int groupPosition,
	                                            int childPosition, long id) {
	                    if (mGattCharacteristics != null) {
	                        final BluetoothGattCharacteristic characteristic =
	                                mGattCharacteristics.get(groupPosition).get(childPosition);
	                        
	                        //��ǰĿ������ֵ
	                        target_chara=characteristic;
	                        
	                        
	                        final int charaProp = characteristic.getProperties();
	                      /*  if ((charaProp | BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
	                            if (mNotifyCharacteristic != null) {
	                                mBluetoothLeService.setCharacteristicNotification(
	                                        mNotifyCharacteristic, false);
	                                mNotifyCharacteristic = null;
	                            }
	                            mBluetoothLeService.readCharacteristic(characteristic);
	                           
	                        }*/
	                        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
	                            mNotifyCharacteristic = characteristic;
	                            System.out.println("kkkkkkkkkk+="+characteristic.getUuid());
	                            mBluetoothLeService.setCharacteristicNotification(
	                                    characteristic, true);
	                        }
	                        tv_uuid.setText(characteristic.getUuid().toString());
	                        Intent intent=new Intent();
	                        b.putString("CONNET_SATE", status);
	                        b.putString("UUID", characteristic.getUuid().toString());
	                        intent.putExtras(b);
	                        intent.setClass(MyGattDetail.this, FunctionActivity.class);
	                        startActivity(intent);
	                        return true;
	                    }
	                    return false;
	                }
	    };
	    
	    public static void write(String s)
	    {
	    	final int charaProp = target_chara.getProperties();				
			if ((charaProp | BluetoothGattCharacteristic.PROPERTY_WRITE) > 0) {
				//ע��: ���¶�ȡ��ֵ ͨ�� BluetoothGattCallback#onCharacteristicRead() ��������
				target_chara.setValue(s);
				mBluetoothLeService.writeCharacteristic(target_chara);
			}
		
	    }
	    public static void read()
	    {
	    	mBluetoothLeService.setOnDataAvailableListener(mOnDataAvailable);
	    	mBluetoothLeService.readCharacteristic(target_chara);

		
	    }
}



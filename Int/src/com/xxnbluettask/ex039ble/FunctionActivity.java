package com.xxnbluettask.ex039ble;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class FunctionActivity extends Activity implements OnClickListener {

	private TextView device_name, device_addres, connect_sate, now_rssi,
			goal_uuid, send_recive, recive,zhushouTextView;
	private Button hex_ab1, hex_ab2, send, restart, send2,change,sure,close;
	private TabHost mTabHost;
	private EditText hex_edit,textchange;
	private FrameLayout tabcontent;
	protected static String EXTRAS_DEVICE_NAME = "DEVICE_NAME";;
	protected static String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";
	private BluetoothLeService mBluetoothLeService;
	private String mDeviceAddress;
	private BluetoothGattCharacteristic target_chara = null;
	private int f1=0,f2=0;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.function_activity);
		init();
		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			mDeviceAddress = bundle.getString(EXTRAS_DEVICE_ADDRESS).toString();
			device_addres.setText(mDeviceAddress);
			device_name
					.setText(bundle.getString(EXTRAS_DEVICE_NAME).toString());
			connect_sate.setText(bundle.getString("CONNET_SATE").toString());
			now_rssi.setText(bundle.getString("RSSI").toString());
			goal_uuid.setText(bundle.getString("UUID").toString());
			goal_uuid.setTextColor(Color.RED);
		}

		Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
		bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
		MyGattDetail.read();
		IntentFilter intentFilter = new IntentFilter(
				"com.example.bluetooth.le.ACTION_DATA_AVAILABLE");
		registerReceiver(myReceiver, intentFilter);//ע��㲥
	}

	private final ServiceConnection mServiceConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName componentName,
				IBinder service) {
			mBluetoothLeService = ((BluetoothLeService.LocalBinder) service)
					.getService();
			if (!mBluetoothLeService.initialize()) {
				finish();
			}
			mBluetoothLeService.connect(mDeviceAddress);
		}

		@Override
		public void onServiceDisconnected(ComponentName componentName) {
			mBluetoothLeService = null;
		}
	};

	private void init() {

		device_name = (TextView) findViewById(R.id.device_name);
		device_addres = (TextView) findViewById(R.id.device_addres);
		connect_sate = (TextView) findViewById(R.id.connect_sate);
		now_rssi = (TextView) findViewById(R.id.now_rssi);
		goal_uuid = (TextView) findViewById(R.id.goal_uuid);
		hex_ab1 = (Button) findViewById(R.id.hex_ab1);
		hex_ab1.setOnClickListener(this);
		hex_ab2 = (Button) findViewById(R.id.hex_ab2);
		hex_ab2.setOnClickListener(this);
		restart = (Button) findViewById(R.id.restart);
		restart.setOnClickListener(this);
		send = (Button) findViewById(R.id.send);
		send.setOnClickListener(this);
		send2 = (Button) findViewById(R.id.send2);
		send2.setOnClickListener(this);
		change = (Button) findViewById(R.id.changebutton);
		change.setOnClickListener(this);
		hex_edit = (EditText) findViewById(R.id.hex_edit);
		textchange = (EditText) findViewById(R.id.editTextchange);
		hex_edit.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				String textString=hex_edit.getText().toString();
				if(textString.length()>20)//���ű�������20����
				{
					Toast.makeText(FunctionActivity.this, "����������ѳ���20����", Toast.LENGTH_SHORT).show();
					textString=textString.substring(0,13);
					hex_edit.setText(textString);
				}
				
			}
		});
		send_recive = (TextView) findViewById(R.id.send_recive);
		mTabHost = (TabHost) findViewById(R.id.tabhost);
		mTabHost.setup();
		  TabHost.TabSpec tab1=mTabHost.newTabSpec("tab1");//������ǩ
		  tab1.setIndicator("��������",getResources().getDrawable(android.R.drawable.ic_menu_call));//����tab����
		  tab1.setContent(R.id.tab1);//����Tab�������� mTabHost.addTab(tab1);
		  mTabHost.addTab(tab1);//��tab����TabHost��
		  
		  TabHost.TabSpec tab2=mTabHost.newTabSpec("tab2");
		  tab2.setIndicator("�������",getResources().getDrawable(android.R.drawable.ic_menu_call)); 
		  tab2.setContent(R.id.tab2);
		  mTabHost.addTab(tab2);//��tab����TabHost��
		  
		  
		  TabHost.TabSpec tab3=mTabHost.newTabSpec("tab3");
		  tab3.setIndicator("�¶ȼ�",getResources().getDrawable(android.R.drawable.ic_menu_call)); 
		  tab3.setContent(R.id.tab3);
		  mTabHost.addTab(tab3);//��tab����TabHost��
		  
		  TabHost.TabSpec tab4=mTabHost.newTabSpec("tab4");
		  tab4.setIndicator("���ᴫ����",getResources().getDrawable(android.R.drawable.ic_menu_call)); 
		  tab4.setContent(R.id.tab4);
		  
		  mTabHost.addTab(tab4);//��tab����TabHost�� mTabHost.setCurrentTab(0);
		  RelativeLayout l1=(RelativeLayout)mTabHost.findViewById(R.id.tab1);
		  zhushouTextView=(TextView) l1.findViewById(R.id.tab1_text);
		  mTabHost.setOnTabChangedListener(new OnTabChangeListener() {

			@Override
			public void onTabChanged(String tabId) {

			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.send:
			send();
			break;
		case R.id.send2:
			send2();
			break;
		case R.id.changebutton:
			change();
			break;
		case R.id.hex_ab1:
			hex_ab1();
			break;
		case R.id.hex_ab2:
			hex_ab2();
			break;
		case R.id.restart:
			restart();
			break;
		default:
			break;
		}

	}

	

	private void restart() {
		send_recive.setText("����"+zhushouTextView.getText().toString().getBytes().length
				+"�ֽڣ�"+"����"+hex_edit.getText().toString().getBytes().length+"�ֽ�");
	}

	private void hex_ab2() {
		String s = hex_edit.getText().toString();
		if (s.equals("")) {
			Toast.makeText(FunctionActivity.this, "�������ݲ���Ϊ�գ�",
					Toast.LENGTH_SHORT).show();
		} else {
			f2++;
			if(!s.equals(s)||f2==1)
			hex_edit.setText(ChangeStringToHex(s));
		}
	}

	private void hex_ab1() {
		if (zhushouTextView.getText().toString().equals("")) {
			Toast.makeText(FunctionActivity.this, "�õ�������Ϊ�գ�����ת��Ϊʮ�����ƣ�",
					Toast.LENGTH_SHORT).show();
		} else {
			String ss=zhushouTextView.getText().toString();
			f1++;
			if(!ss.equals(ss)||f1==1)
			zhushouTextView.setText(ChangeStringToHex(ss));
		}
	}
	//��������
	String ok="111";
	String ok2="222";
	private void send2() {
		f2=0;
		MyGattDetail.write(ok2);
		send_recive.setText("����"+hex_edit.getText().toString().length()*2+"�ֽ�");
		hex_edit.setText("");//���������
	}
	private void send() {
		f2=0;
		MyGattDetail.write(ok);
		send_recive.setText("����"+hex_edit.getText().toString().length()*2+"�ֽ�");
		hex_edit.setText("");//���������
	}
	private void change() {
		showDialog();
	}
	
	private void showDialog() {
		LayoutInflater inflater = LayoutInflater.from(this);
		View view = inflater.inflate(R.layout.change_dialog,null);
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("������������");
		builder.setView(view);
		builder.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				textchange = (EditText) findViewById(R.id.editTextchange);
				String changemessage= textchange.getText().toString();
				sendchange(changemessage);
				
			}

			private void sendchange(String changemessage) {
				// TODO Auto-generated method stub
				
					f2=0;
					MyGattDetail.write(changemessage);
					send_recive.setText("����"+hex_edit.getText().toString().length()*2+"�ֽ�");
			}
		});
		
		builder.setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
				
			}
		});
		builder.show();
		
	}

	private BroadcastReceiver myReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(
					BluetoothLeService.ACTION_DATA_AVAILABLE)) {
				f1=0;
				zhushouTextView.setText(intent.getExtras().getString(
						BluetoothLeService.EXTRA_DATA));
				send_recive.setText("����"+intent.getExtras().getString(
						BluetoothLeService.EXTRA_DATA).toString().getBytes().length
						+"�ֽڣ�"+"����"+hex_edit.getText().toString().getBytes().length+"�ֽ�");
			}
		}

	};

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unbindService(mServiceConnection);
		unregisterReceiver(myReceiver);
	}

	/*//�ַ���ת��Ϊ16����
	private String dexTohx(String s)
	{
		StringBuilder stringBuilder = null;
		byte[] data = s.getBytes();
        if (data != null && data.length > 0) {
            stringBuilder = new StringBuilder(data.length);
            for(byte byteChar : data)
            {
            	stringBuilder.append(String.format("%02X ", byteChar));
				
            }
        }
		return stringBuilder.toString();
		
	}*/
	
	//�ַ���ת��Ϊ16����
	public String  ChangeStringToHex(String inputString) {
	    String s = "",str="";
	    if (inputString != null) {
	    	if(inputString.contains("\r\n"))
	    		inputString=inputString.replace("\r\n", "");
	        s = inputString;
	    }
	    for (int i = 0; i < s.length(); i++) {
	        byte[] ba = s.substring(i, i + 1).getBytes();
	        String tmpHex = Integer.toHexString(ba[0] & 0xFF);
	        str+=tmpHex.toUpperCase();str+=" ";
	    }
		return str;
	}
}

package com.xxnbluettask.ex039ble;

import com.xxnbluettask.ex039ble.R.layout;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class ChangeActivity extends Activity{
	private Button sure,cancle;
	private EditText textchange;
	private String content;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.changeactivity);
		textchange = (EditText) findViewById(R.id.editTextchange);
		sure = (Button) findViewById(R.id.buttonsure);
		cancle = (Button) findViewById(R.id.buttoncancle);
		sure.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent data = new Intent();
				content = "c"+textchange.getText().toString();
				data.putExtra("data", content);
				setResult(2, data);
				finish();
			}
		});
		
		cancle.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
				
			}
		});
	}
}
	

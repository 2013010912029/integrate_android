package com.xxnbluettask.ex039ble;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class HistoryActivity extends Activity{
	private Button btClean;
	public static TextView txtHistory;
	String history;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.historyactivity);
		txtHistory = (TextView) findViewById(R.id.textViewhistory);
		history = txtHistory.getText().toString();
		Intent intent = getIntent();
		String text = (String) intent.getSerializableExtra("history");  // 如果报错试一下用getstringextra()
		txtHistory.setText(history+text);
		btClean = (Button) findViewById(R.id.buttonclean);
		btClean.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				txtHistory.setText("开门记录");
				
			}
		});
	}
}

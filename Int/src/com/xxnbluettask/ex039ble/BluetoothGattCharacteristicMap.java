package com.xxnbluettask.ex039ble;

import java.io.Serializable;

import android.bluetooth.BluetoothGattCharacteristic;

public class BluetoothGattCharacteristicMap implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private BluetoothGattCharacteristic bluetoothGattCharacteristic;

	public BluetoothGattCharacteristic getBluetoothGattCharacteristic() {
		return bluetoothGattCharacteristic;
	}

	public void setBluetoothGattCharacteristic(
			BluetoothGattCharacteristic bluetoothGattCharacteristic) {
		this.bluetoothGattCharacteristic = bluetoothGattCharacteristic;
	}
}

package com.lhs.qrblue;

//import android.R;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.content.Intent;
import android.bluetooth.*;

public class QrBlue extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        findViewById(R.id.scanbutton).setOnClickListener(mScan);

    }
    public Button.OnClickListener mScan = new Button.OnClickListener() {
        public void onClick(View v) {
        	Log.v(Tag, "clicking");
            Intent intent = new Intent("com.google.zxing.client.android.SCAN");
            intent.setPackage("com.google.zxing.client.android");
            intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
            startActivityForResult(intent, 0); 
        }
    };
    public String Tag = "QrBlue";
    
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                String contents = intent.getStringExtra("SCAN_RESULT");
                String format = intent.getStringExtra("SCAN_RESULT_FORMAT");
                // Handle successful scan
                String[] list = contents.split(",");
                if (list.length!=3 && list[0]!="bt")
                {
                	Log.v(Tag, "Incorrect number of parameters");
                }
                else
                {
                	Log.v(Tag, contents);
                	Log.v(Tag, "Connecting to"  + list[1]);
                	BluetoothAdapter bta = BluetoothAdapter.getDefaultAdapter();
                	BluetoothDevice btd = bta.getRemoteDevice (list[1]);
                	Method m;
                	Log.v(Tag, btd.getName() );
                	try {
                		m = btd.getClass().getMethod("createRfcommSocket", new Class[]{int.class});
						BluetoothSocket bts = (BluetoothSocket)m.invoke(btd, Integer.valueOf(1));
                		//BluetoothSocket bts = btd.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
						try{
							bts.connect();
							OutputStream btos= bts.getOutputStream();
						} catch (IOException e1){
							try {
								bts.close();
								e1.printStackTrace();
				            } catch (IOException e2) {
				            	e2.printStackTrace();
				            	Log.e(Tag, "unable to close() socket during connection failure", e2);
				            }
							
						}
					} /* catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}*/ catch (SecurityException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (NoSuchMethodException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} 
					
                }
            } else if (resultCode == RESULT_CANCELED) {
                // Handle cancel
            }
        }
    }
}

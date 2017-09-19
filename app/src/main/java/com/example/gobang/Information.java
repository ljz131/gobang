package com.example.gobang;


import android.app.Activity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Information extends Activity {
	public Information() {

	}

	public StoreInfo DB=new StoreInfo(this);
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_information);
		final TextView text_view=(TextView)findViewById(R.id.text_view);
		text_view.setMovementMethod(ScrollingMovementMethod.getInstance());
		Button clear_button=(Button)findViewById(R.id.clear_button);
		clear_button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				DB.DelectAllData();
				text_view.setText("");
			}
		});
			try{
				String tmp=DB.read();
				text_view.setText(tmp);
			}catch(Exception e){
				e.printStackTrace();
			}
			

	}
}

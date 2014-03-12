package com.spydi2kood.prisma;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by jim on 12/3/2014.
 */
public class WelcomeActivity extends ActionBarActivity{
	private ImageView search,define;
	private Intent intent;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_welcome);
		search = (ImageView) findViewById(R.id.welcome_button_search);
		define = (ImageView) findViewById(R.id.welcome_button_define);
		intent = new Intent(this, MainActivity.class);
		search.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				intent.putExtra("fragment",0);
				startActivity(intent);
			}
		});
		define.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				intent.putExtra("fragment",1);
				startActivity(intent);
			}
		});
	}
}

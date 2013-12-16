package ssu.satp.nopaper;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class ChangeColorActivity extends Activity {
	
	LinearLayout change_color_layout;
	ImageView color1, color2, color3, color4, color5, color6, color7, color8;
	SharedPreferences shrdPref;
	SharedPreferences.Editor editor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_change_color);
		
		shrdPref = getSharedPreferences("pref", MODE_PRIVATE);
		editor = shrdPref.edit();
		
		change_color_layout = (LinearLayout) findViewById(R.id.change_color_layout);
		change_color_layout.setBackgroundColor(Color.parseColor(shrdPref.getString("color", "#1865a9")));
		
		findViewById(R.id.color1).setOnClickListener(mClickListener);
		findViewById(R.id.color2).setOnClickListener(mClickListener);
		findViewById(R.id.option).setOnClickListener(mClickListener);
		findViewById(R.id.color4).setOnClickListener(mClickListener);
		findViewById(R.id.color5).setOnClickListener(mClickListener);
		findViewById(R.id.color6).setOnClickListener(mClickListener);
		findViewById(R.id.color7).setOnClickListener(mClickListener);
		findViewById(R.id.color8).setOnClickListener(mClickListener);
		
	}
	
	
	
	OnClickListener mClickListener = new View.OnClickListener() {
		public void onClick(View v) {
			
			switch (v.getId()) {
			case R.id.color1:
				editor.putString("color", "#1865a9");
				editor.commit();
				
				break;
			case R.id.color2:
				editor.putString("color", "#50d090");
				editor.commit();
				break;
			case R.id.option:
				editor.putString("color", "#90c0f0");
				editor.commit();
				break;
			case R.id.color4:
				editor.putString("color", "#a0e050");
				editor.commit();
				break;
			case R.id.color5:
				editor.putString("color", "#c080f0");
				editor.commit();
				break;
			case R.id.color6:
				editor.putString("color", "#d0d020");
				editor.commit();
				break;
			case R.id.color7:
				editor.putString("color", "#f08080");
				editor.commit();
				break;
			case R.id.color8:
				editor.putString("color", "#f080c0");
				editor.commit();
				break;
			}
			onBackPressed();
		}
	};
}

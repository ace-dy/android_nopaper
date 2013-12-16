package ssu.satp.nopaper;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class OptionActivity extends Activity {

	ImageView chg_color_img, chg_passwd_img;
	ServerConnection con;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.option_layout);

		Intent intent = this.getIntent();

		con = (ServerConnection) intent.getExtras().getParcelable("con");
		
		chg_color_img = (ImageView) findViewById(R.id.chg_color_img);
		chg_passwd_img = (ImageView) findViewById(R.id.chg_passwd_img);

		chg_color_img.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(OptionActivity.this,
						ChangeColorActivity.class);
				startActivity(intent);
			}
		});
		
		chg_passwd_img.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(OptionActivity.this,
						ChangePasswdActivity.class);
				intent.putExtra("con", con);
				startActivity(intent);
				
				
			}
		});

	}

	@Override
	protected void onResume() {
		LinearLayout main_activity_layout;
		SharedPreferences shrdPref;

		shrdPref = getSharedPreferences("pref", MODE_PRIVATE);

		main_activity_layout = (LinearLayout) findViewById(R.id.Main_activity_layout);
		main_activity_layout.setBackgroundColor(Color.parseColor(shrdPref
				.getString("color", "#1865a9")));
		super.onResume();
	}

}

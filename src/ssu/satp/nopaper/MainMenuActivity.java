package ssu.satp.nopaper;

import java.util.Calendar;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainMenuActivity extends Activity {

	ServerConnection con;
	TextView lecture_name;
	JSONArray jsonarray;
	int day;
	Button roll_book_btn;
	Button check_btn;
	Button setup_btn;
	Button back_btn;
	SharedPreferences shrdPref;
	private static String mUrl = "http://gss.ssu.ac.kr:8081/subject_send.php";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mainmenu);
		
		shrdPref = getSharedPreferences("pref", MODE_PRIVATE);

		getDataFromServer();
		setLectureName();
		setRollBtn();
		setCheckBtn();
		setSetupBtn();
		setBackBtn();
	}

	private void getDataFromServer() {
		Intent intent = this.getIntent();
		day = intent.getIntExtra("day", 0);

		con = (ServerConnection) intent.getExtras().getParcelable("con");
		con.doInBackground(mUrl);
		try {
			while (!con.isGetEntity()) {
			}
		} catch (IllegalStateException e) {
			e.printStackTrace();
		}
		jsonarray = con.getJsonarray();
	}

	private void setLectureName() {
		lecture_name = (TextView) findViewById(R.id.lecture_name);
		try {
			lecture_name.setText(jsonarray.getJSONObject(0)
					.getString("LECTURE_NAME").toString());
			con.setValue(lecture_name.getText().toString());

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void setRollBtn() {
		roll_book_btn = (Button) findViewById(R.id.roll_book_btn);
		roll_book_btn.setTextColor(Color.parseColor(shrdPref
				.getString("color", "#1865a9")));
		roll_book_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				final Calendar c = Calendar.getInstance();
				int DayOfWeek = c.get(Calendar.DAY_OF_WEEK);

				if (day == DayOfWeek - 1) {
					Log.d("day", Integer.toString(day));
					Log.d("day", Integer.toString(DayOfWeek - 1));

					Intent intent = new Intent(MainMenuActivity.this,
							StudentListForRollBookActivity.class);
					intent.putExtra("con", con);
					startActivity(intent);
				} else {
					Dialog();
				}
			}
		});
	}

	private void Dialog() {
		AlertDialog.Builder alt_bld = new AlertDialog.Builder(this);

		alt_bld.setTitle("선택하신 수업은 오늘이 아닙니다.");
		alt_bld.setCancelable(false).setPositiveButton("확인", null);
		AlertDialog alert = alt_bld.create();
		alert.show();
	}

	private void setCheckBtn() {
		check_btn = (Button) findViewById(R.id.notice);
		check_btn.setTextColor(Color.parseColor(shrdPref
				.getString("color", "#1865a9")));
		check_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainMenuActivity.this,
						StudentListForCheckActivity.class);
				intent.putExtra("con", con);
				startActivity(intent);
			}
		});
	}

	private void setSetupBtn() {
		setup_btn = (Button) findViewById(R.id.btn_ok);
		setup_btn.setTextColor(Color.parseColor(shrdPref
				.getString("color", "#1865a9")));
		setup_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainMenuActivity.this,
						RuleSetupActivity.class);
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

	private void setBackBtn() {
		back_btn = (Button) findViewById(R.id.btn_save);
		back_btn.setTextColor(Color.parseColor(shrdPref
				.getString("color", "#1865a9")));
		back_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
	}
}

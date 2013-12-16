package ssu.satp.nopaper;

import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.Toast;

public class ClassScheduleActivity extends Activity {

	Button option_btn;
	GridView GridSchedule;
	GridView grid;
	ScheduleAdapter adapter;
	GridAdapter gridadap;
	static int deviceHeight;
	ServerConnection con;
	static int[] tableInfo = new int[60];
	JSONArray jsonarray;
	private static String mUrl = "http://gss.ssu.ac.kr:8081/data_send.php";
	private Timer timer;
	private boolean mIsBackButtonTouched = false;

	public void onBackPressed() {
		if (mIsBackButtonTouched == false) {
			Toast t = Toast.makeText(this, "뒤로 버튼을 한번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT);
			t.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
			t.show();
			mIsBackButtonTouched = true;

			TimerTask second = new TimerTask() {
				@Override
				public void run() {
					timer.cancel();
					timer = null;
					mIsBackButtonTouched = false;
				}
			};
			if (timer != null) {
				timer.cancel();
				timer = null;
			}
			timer = new Timer();
			timer.schedule(second, 2000);
		} else
			super.onBackPressed();
	}
	
	public static int getDeviceHeight() {
		return deviceHeight;
	}

	public static int[] getTableInfo() {
		return tableInfo;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.timetable);
		
		setOptionButton();
		getDataFromServer();
		getTable();
		getDisplayMetric();
		gridDay();
		gridSchedule();
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

	private void setOptionButton() {
		option_btn = (Button) findViewById(R.id.option_btn);
		option_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ClassScheduleActivity.this,
						OptionActivity.class);
				intent.putExtra("con", con);
				startActivity(intent);
			}
		});
	}

	private void getDataFromServer() {
		Intent intent = this.getIntent();

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

	private void getTable() {
		int i, leng = jsonarray.length();
		int time, day;
		JSONObject obj = null;
		Arrays.fill(tableInfo, 0);
		try {
			for (i = 0; i < leng; i++) {
				obj = jsonarray.getJSONObject(i);
				day = Integer.parseInt(obj.getString("LECTURE_DAY").toString());
				time = Integer.parseInt(obj.getString("LECTURE_TIME")
						.toString());
				tableInfo[day + (6 * time)] = Integer.parseInt(obj.getString(
						"LECTURE_CODE").toString());
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void getDisplayMetric() {
		DisplayMetrics displayMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		deviceHeight = displayMetrics.heightPixels;
	}

	private void gridDay() {
		grid = (GridView) findViewById(R.id.Grid);
		gridadap = new GridAdapter(this);
		grid.setAdapter(gridadap);
		grid.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_MOVE) {
					return true;
				}
				return false;
			}
		});
	}

	private void gridSchedule() {
		GridSchedule = (GridView) findViewById(R.id.schedule);
		adapter = new ScheduleAdapter(this);
		GridSchedule.setAdapter(adapter);

		GridSchedule.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long id) {
				con.setCode(Integer.toString(tableInfo[position]));
				Intent intent = new Intent(ClassScheduleActivity.this,
						MainMenuActivity.class);
				intent.putExtra("con", con);

				intent.putExtra("day", position % 6);
				startActivity(intent);
			}
		});
	}
}
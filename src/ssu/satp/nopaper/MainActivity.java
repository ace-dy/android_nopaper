package ssu.satp.nopaper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	Button login;
	TextView user_id;
	TextView user_pw;
	CheckBox cb;
	LinearLayout main_activity_layout;
	SharedPreferences shrdPref;
	ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
	private static String mUrl = "http://gss.ssu.ac.kr:8081/login.php";
	ServerConnection con;
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		shrdPref = getSharedPreferences("pref", MODE_PRIVATE);

		main_activity_layout = (LinearLayout) findViewById(R.id.Main_activity_layout);
		main_activity_layout.setBackgroundColor(Color.parseColor(shrdPref.getString("color", "#1865a9")));

		user_id = (TextView) findViewById(R.id.user_id);
		user_pw = (TextView) findViewById(R.id.user_pw);
		login = (Button) findViewById(R.id.login);
		cb = (CheckBox) findViewById(R.id.cb);

		final String userid = shrdPref.getString("id", "");
		final String passwd = shrdPref.getString("pw", "");

		if (userid != null && !userid.equals("")) {
			user_id.setText(userid);
			user_pw.setText(passwd);
			cb.setChecked(true);
		}

		login.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (user_id.getText().toString().trim().equals("")
						|| user_pw.getText().toString().trim().equals("")) {
					
					Toast t = Toast.makeText(MainActivity.this,
							"아이디와 비밀번호는 필수 입력사항 입니다.", Toast.LENGTH_SHORT);
					t.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
					t.show();
					return;
				}
				SharedPreferences.Editor editor = shrdPref.edit();

				if (cb.isChecked()) {
					editor.putString("id", user_id.getText().toString().trim());
					editor.putString("pw", user_pw.getText().toString().trim());
					editor.commit();

				} else {
					editor.remove("id");
					editor.remove("pw");
					editor.commit();
				}

				getDataFromServer();
			}
		});
	}

	private void getDataFromServer() {
		con = (ServerConnection) new ServerConnection();
		con.setId(user_id.getText().toString());
		con.setCode(user_pw.getText().toString());
		con.doInBackground(mUrl);
		try {
			while (!con.isGetEntity()) {
			}
			loginProcess(con.getJsonarray());
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void loginProcess(JSONArray response) throws IllegalStateException,
			IOException {
		String result = null;
		try {
			result = response.getJSONObject(0).getString("result").toString();
		} catch (JSONException e1) {
			e1.printStackTrace();
		}

		try {
			if (result.equals("0")) {
				user_id.setText("");
				user_pw.setText("");
				Toast t = Toast.makeText(MainActivity.this,
						"비밀번호가 틀렷습니다. \n다시한번 확인해 주세요.", Toast.LENGTH_LONG);
				t.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
				t.show();
			} else if (result.equals("1")) {
				Intent intent = new Intent(MainActivity.this,
						ClassScheduleActivity.class);
				intent.putExtra("con", con);
				startActivity(intent);
				finish();
			} else {
				user_id.setText("");
				user_pw.setText("");
				Toast t = Toast.makeText(MainActivity.this, "아이디가 존재하지 않습니다.",
						Toast.LENGTH_LONG);
				t.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
				t.show();
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
}

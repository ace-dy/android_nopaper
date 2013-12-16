package ssu.satp.nopaper;

import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ParseException;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

public class ChangePasswdActivity extends Activity {

	LinearLayout main_activity_layout;
	private EditText etOldPw, etNewPw, etNewPw2;
	private Button btOk, btCancle;
	private String oldPw, newPw, newPw2;
	private ServerConnection con;
	private JSONArray jsonarray;
	SharedPreferences shrdPref;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_change_passwd);

		shrdPref = getSharedPreferences("pref", MODE_PRIVATE);
		main_activity_layout = (LinearLayout) findViewById(R.id.Main_activity_layout);
		main_activity_layout.setBackgroundColor(Color.parseColor(shrdPref.getString("color", "#1865a9")));

		Intent intent = this.getIntent();
		con = (ServerConnection) intent.getExtras().getParcelable("con");
		setEditText();
		setButton();
	}

	private void setEditText() {
		etOldPw = (EditText) findViewById(R.id.et_oldpw);
		etNewPw = (EditText) findViewById(R.id.et_newpw);
		etNewPw2 = (EditText) findViewById(R.id.et_newpw2);
	}

	private void setButton() {
		btOk = (Button) findViewById(R.id.pwok_btn);
		btOk.setTextColor(Color.parseColor(shrdPref.getString("color",
				"#1865a9")));
		btCancle = (Button) findViewById(R.id.bt_canclepw);
		btCancle.setTextColor(Color.parseColor(shrdPref.getString("color",
				"#1865a9")));

		btOk.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				oldPw = etOldPw.getText().toString();
				newPw = etNewPw.getText().toString();
				newPw2 = etNewPw2.getText().toString();
				changePassword();
			}
		});

		btCancle.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
	}

	private void changePassword() {
		String loginUrl = "http://gss.ssu.ac.kr:8081/login.php";

		boolean loginResult = false;

		con.setCode(oldPw);
		serverConnection(loginUrl);
		try {
			loginResult = loginProcess();
		} catch (IllegalStateException e) {

			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		}

		if (loginResult) {
			if (newPw.equals(newPw2)) {
				if (newPw.equals("")) {
					Toast t = Toast.makeText(ChangePasswdActivity.this,
							"새로운 비밀번호를 입력하세요.", Toast.LENGTH_SHORT);
					t.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
					t.show();
					resetField();
				} else {
					updatePw();
				}
			} else {
				Toast t = Toast.makeText(ChangePasswdActivity.this,
						"새로운 비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT);
				t.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
				t.show();
				resetField();
			}
		} else {
			Toast t = Toast.makeText(ChangePasswdActivity.this,
					"기존의 비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT);
			t.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
			t.show();
			resetField();
		}
	}

	private void serverConnection(String mUrl) {

		con.doInBackground(mUrl);
		try {
			while (!con.isGetEntity()) {
			}
		} catch (IllegalStateException e) {
			e.printStackTrace();
		}
		jsonarray = con.getJsonarray();
	}

	private boolean loginProcess() throws IllegalStateException, IOException {
		boolean result = false;
		String value = null;
		try {
			value = jsonarray.getJSONObject(0).getString("result").toString();
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		try {
			if (value.equals("1")) {
				result = true;
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return result;
	}

	private void resetField() {
		etOldPw.setText("");
		etNewPw.setText("");
		etNewPw2.setText("");
	}

	private void updatePw() {

		SharedPreferences.Editor editor = shrdPref.edit();
		editor.putString("pw", newPw);
		editor.commit();

		String changeUrl = "http://gss.ssu.ac.kr:8081/set_password.php";
		con.setValue(newPw);
		serverConnection(changeUrl);
		Toast t = Toast.makeText(ChangePasswdActivity.this, "비밀번호가 변경되었습니다.",
				Toast.LENGTH_SHORT);
		t.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
		t.show();
		onBackPressed();
	}
}

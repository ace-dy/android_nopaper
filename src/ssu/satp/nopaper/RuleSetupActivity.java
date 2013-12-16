package ssu.satp.nopaper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class RuleSetupActivity extends Activity {

	ServerConnection con;
	JSONArray jsonarray;
	private TextView lecture_name;
	EditText editAttend, editAbsent, editLate;
	Button btOk, btSave;
	int mPosition;
	SharedPreferences shrdPref;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_rule_setup);

		shrdPref = getSharedPreferences("pref", MODE_PRIVATE);
		getDataFromServer();
		setLectureName();
		getInfo();
		setButton();
	}

	private void getDataFromServer() {
		String mUrl = "http://gss.ssu.ac.kr:8081/send_rull.php";
		Intent intent = this.getIntent();

		con = (ServerConnection) intent.getExtras().getParcelable("con");
		serverConnection(mUrl);
		jsonarray = con.getJsonarray();
	}

	private void setLectureName() {
		lecture_name = (TextView) findViewById(R.id.lecture_name1);
		lecture_name.setText(con.getValue());
	}

	private void getInfo() {
		editAttend = (EditText) findViewById(R.id.editText1);
		editAbsent = (EditText) findViewById(R.id.editText2);
		editLate = (EditText) findViewById(R.id.editText3);
		
		int leng = jsonarray.length();
		if (leng != 0) {
			JSONObject obj = null;

			try {
				obj = jsonarray.getJSONObject(0);
				editAttend.setText(obj.getString("ATTEND_SCORE"));
				editAbsent.setText(obj.getString("ABSENT_SCORE"));
				editLate.setText(obj.getString("LATE_SCORE"));

			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
	protected void onResume() {
		LinearLayout main_activity_layout;

		main_activity_layout = (LinearLayout) findViewById(R.id.Main_activity_layout);
		main_activity_layout.setBackgroundColor(Color.parseColor(shrdPref
				.getString("color", "#1865a9")));
		super.onResume();
	}

	private void setButton() {
		btOk = (Button) findViewById(R.id.btOk);
		btOk.setTextColor(Color.parseColor(shrdPref
				.getString("color", "#1865a9")));
		btSave = (Button) findViewById(R.id.btSave);
		btSave.setTextColor(Color.parseColor(shrdPref
				.getString("color", "#1865a9")));
		btOk.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});

		btSave.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String mSetUrl = "http://gss.ssu.ac.kr:8081/set_rull.php";
				String temp = editAttend.getText() + "/" + editAbsent.getText()
						+ "/" + editLate.getText();
				con.setSt_id(temp);
				serverConnection(mSetUrl);
				onBackPressed();
			}
		});
	}
	
	private void serverConnection(String mUrl){
		con.doInBackground(mUrl);
		try {
			while (!con.isGetEntity()) {
			}
		} catch (IllegalStateException e) {
			e.printStackTrace();
		}
	}
}
package ssu.satp.nopaper;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class StudentInfoActivity extends Activity {

	private TextView lecture_name;
	private TextView today_text;
	private TextView student_num;
	private TextView student_name;
	private TextView student_major;
	private TextView student_phone;
	private ImageView student_pic;
	private Button attend;
	private Button absent;
	private ServerConnection con;
	private ArrayList<String> studentName;
	private ArrayList<String> studentId;
	private ArrayList<String> studentPhone;
	private ArrayList<String> studentMajor;
	private ArrayList<String> studentPic;
	private int position = 0;
	private int index = 0;
	private int[] order;
	private int[] record;
	private String lectureName;
	private static String mUrl = "http://gss.ssu.ac.kr:8081/set_attendance.php";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.overridePendingTransition(R.anim.start_enter, R.anim.start_exit);
		setContentView(R.layout.student_info);

		getStudentInfo();
		SetLectureName();
		SetTime();
		SetStudentInfo();
		SetStudentPicture();
		getAttendanceInfo();
		SetButton();
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

	private void getStudentInfo() {
		Intent intent = this.getIntent();

		con = (ServerConnection) intent.getExtras().getParcelable("con");
		studentName = intent.getStringArrayListExtra("studentName");
		studentId = intent.getStringArrayListExtra("studentId");
		studentPhone = intent.getStringArrayListExtra("studentPhone");
		studentMajor = intent.getStringArrayListExtra("studentMajor");
		studentPic = intent.getStringArrayListExtra("studentPic");
		position = intent.getIntExtra("position", 0);
		lectureName = intent.getStringExtra("lecture_name");

		order = new int[studentName.size()];

		int i, j = 0;
		for (i = 0; i < order.length; i++) {
			if (i + position < order.length)
				order[i] = i + position;
			else
				order[i] = j++;
		}
	}

	private void SetLectureName() {
		lecture_name = (TextView) findViewById(R.id.lecture_name2);
		lecture_name.setText(lectureName);
	}

	private void SetTime() {

		final Calendar c = Calendar.getInstance();
		int Year = c.get(Calendar.YEAR);
		int Month = c.get(Calendar.MONTH)+1;
		int Day = c.get(Calendar.DAY_OF_MONTH);

		String strTime = Year + "/" + Month + "/" + Day;

		today_text = (TextView) findViewById(R.id.today_text);
		today_text.setText(strTime);
	}

	private void SetStudentInfo() {
		position = order[index++];
		student_name = (TextView) findViewById(R.id.student_name);
		student_num = (TextView) findViewById(R.id.student_num);
		student_major = (TextView) findViewById(R.id.student_major);
		student_phone = (TextView) findViewById(R.id.student_phone);

		student_name.setText("이 름 : \n                "
				+ studentName.get(position));
		student_num.setText("학 번 : \n       " + studentId.get(position));
		student_major.setText("전 공 : \n           "
				+ studentMajor.get(position));
		student_phone.setText("연락처 : \n " + studentPhone.get(position));
	}

	private void SetStudentPicture() {
		student_pic = (ImageView) findViewById(R.id.studentimage);
		try {
			URL url = new URL(studentPic.get(position));
			Bitmap bm;
			bm = BitmapFactory.decodeStream(url.openStream());
			student_pic.setImageBitmap(bm);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void SetButton() {
		attend = (Button) findViewById(R.id.btn_ok);
		attend.setTextColor(Color.WHITE);
		// int id = studentId.indexOf(studentId.get(position));
		if (record[position] == 0) {
			attend.setText("지각");
			attend.setBackgroundColor(Color.parseColor("#FCD76C"));
			attend.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					con.setValue("2");
					record[position] = 2;
					resetData();
				}
			});
		} else {
			attend.setText("출석");
			attend.setBackgroundColor(Color.parseColor("#A0CD56"));
			attend.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					con.setValue("1");
					record[position] = 1;
					resetData();
				}
			});
		}
		absent = (Button) findViewById(R.id.btn_save);
		absent.setTextColor(Color.WHITE);
		absent.setBackgroundColor(Color.parseColor("#F16764"));
		absent.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				con.setValue("0");
				record[position] = 0;
				resetData();
			}
		});
	}

	private void resetData() {
		con.setSt_id(studentId.get(position));
		con.doInBackground(mUrl);
		try {
			while (!con.isGetEntity()) {
			}
		} catch (IllegalStateException e) {
			e.printStackTrace();
		}

		if (index < order.length) {
			SetStudentInfo();
			SetStudentPicture();
			SetButton();
		} else {
			getAttendanceInfo();
			Intent intent = new Intent();
			intent.putExtra("record", record);
			this.setResult(1, intent);

			onBackPressed();
		}
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {

		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			getAttendanceInfo();
			Intent intent = new Intent();
			intent.putExtra("record", record);
			this.setResult(1, intent);

			break;
		default:
			break;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void getAttendanceInfo() {
		String mUrl = "http://gss.ssu.ac.kr:8081/attendance_info_send.php";
		con.doInBackground(mUrl);
		try {
			while (!con.isGetEntity()) {
			}
		} catch (IllegalStateException e) {
			e.printStackTrace();
		}

		JSONArray jsonarray = con.getJsonarray();
		int i = 0, indexR = 0;

		JSONObject obj = null;
		record = new int[order.length];
		for (i = 0; i < order.length; i++) {
			record[i] = 3;
		}
		i=0;
		while (!jsonarray.isNull(i)) {
			try {
				obj = jsonarray.getJSONObject(i);
				indexR = studentId.indexOf(obj.getString("STUDENT_ID"));
				record[indexR] = Integer.parseInt(obj.getString("RECORD"));
			} catch (JSONException e) {
				e.printStackTrace();
			}
			i++;
		}
	}
}
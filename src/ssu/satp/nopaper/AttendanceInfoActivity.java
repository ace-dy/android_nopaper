package ssu.satp.nopaper;

import java.io.IOException;
import java.net.URL;
import java.util.Calendar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class AttendanceInfoActivity extends Activity {

	private TextView lecture_name, today_text, student_num, student_name,
			student_major, student_phone, attend_info;
	private ImageView student_pic;
	private ServerConnection con;
	private String lectureName, studentName, studentId, studentPhone,
			studentMajor, studentPic;
	private Button btn_ok;
	JSONArray jsonarray;
	private Intent mIntent;
	private static String[] mDate;
	private static int[] mRecord;
	private double absentScore = 0, lateScore = 0, totalScore = 0;
	private int attendScore = 0, absent, late, record, index;
	GridView gridAttend;
	AttendAdapter adapter;
	SharedPreferences shrdPref;
	final CharSequence[] items = { "결석", "출석", "지각" };

	public static String[] getDate() {
		return mDate;
	}

	public static int[] getRecord() {
		return mRecord;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_attendance_info);
		
		shrdPref = getSharedPreferences("pref", MODE_PRIVATE);

		getStudentInfo();
		getRecordFromServer();
		getRuleFromServer();
		setStudentInfo();
		setTime();
		setStudentPicture();
		gridAttendInfo();
		setButton();
	}
	
	@Override
	protected void onResume() {
		LinearLayout main_activity_layout;
		main_activity_layout = (LinearLayout) findViewById(R.id.Main_activity_layout);
		main_activity_layout.setBackgroundColor(Color.parseColor(shrdPref
				.getString("color", "#1865a9")));
		super.onResume();
	}

	private void gridAttendInfo() {
		gridAttend = (GridView) findViewById(R.id.gridAttend);
		adapter = new AttendAdapter(this);
		gridAttend.setAdapter(adapter);

		gridAttend.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, final View arg1,
					final int position, long id) {

				AlertDialog.Builder builder = new AlertDialog.Builder(
						AttendanceInfoActivity.this);

				builder.setTitle("변경하고 싶은 상태를 누르세요");
				builder.setSingleChoiceItems(items, -1,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								record = which;
							}
						});
				builder.setNegativeButton("취소",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {

							}

						});
				builder.setPositiveButton("저장",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								String mUpUrl = "http://gss.ssu.ac.kr:8081/update_attendance.php";
								index = position - ((position / 8) + 1) * 4;
								con.setValue(mDate[index] + "," + record);
								serverConnect(mUpUrl);
								refresh();
							}

						});
				builder.show();
			}
		});
	}

	private void refresh() {
		getRecordFromServer();
		totalScore = attendScore - absentScore * absent - lateScore * late;
		attend_info.setText("지각: " + late + "회, 결석: " + absent + "회, 출석점수: "
				+ totalScore + "/" + attendScore + "점");
		gridAttendInfo();
	}

	private void getStudentInfo() {
		mIntent = this.getIntent();

		con = (ServerConnection) mIntent.getExtras().getParcelable("con");

		lectureName = mIntent.getStringExtra("lecture_name");
		studentName = mIntent.getStringExtra("studentName");
		studentId = mIntent.getStringExtra("studentId");
		studentPhone = mIntent.getStringExtra("studentPhone");
		studentMajor = mIntent.getStringExtra("studentMajor");
		studentPic = mIntent.getStringExtra("studentPic");

		con.setSt_id(studentId);
	}

	private void getRecordFromServer() {
		String mUrl = "http://gss.ssu.ac.kr:8081/all_attend_info_send.php";

		serverConnect(mUrl);

		jsonarray = con.getJsonarray();
		int i, size = jsonarray.length();
		mDate = new String[size];
		mRecord = new int[size];
		absent = 0;
		late = 0;
		JSONObject obj = null;
		try {
			for (i = 0; i < size; i++) {
				obj = jsonarray.getJSONObject(i);
				mDate[i] = obj.getString("DATE");
				Log.v("date", mDate[i]);
				mRecord[i] = Integer.parseInt(obj.getString("RECORD"));
				Log.v("record", Integer.toString(mRecord[i]));
				if (mRecord[i] == 0) {
					absent++;
				} else if (mRecord[i] == 2) {
					late++;
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void getRuleFromServer() {
		String mRuleUrl = "http://gss.ssu.ac.kr:8081/send_rull.php";
		serverConnect(mRuleUrl);

		jsonarray = con.getJsonarray();
		
		JSONObject obj = null;
		try {
			obj = jsonarray.getJSONObject(0);
			attendScore = Integer.parseInt(obj.getString("ATTEND_SCORE"));
			absentScore = Double.valueOf(obj.getString("ABSENT_SCORE"));
			lateScore = Double.valueOf(obj.getString("LATE_SCORE"));	
			totalScore = attendScore - absentScore * absent - lateScore * late;
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void setStudentInfo() {

		lecture_name = (TextView) findViewById(R.id.lecture_name2);
		student_num = (TextView) findViewById(R.id.student_num);
		student_name = (TextView) findViewById(R.id.student_name);
		student_major = (TextView) findViewById(R.id.student_major);
		student_phone = (TextView) findViewById(R.id.student_phone);
		student_pic = (ImageView) findViewById(R.id.studentimage);
		attend_info = (TextView) findViewById(R.id.attend_info);

		lecture_name.setText(lectureName);
		student_name.setText(studentName);
		student_num.setText(studentId);
		student_major.setText(studentMajor);
		student_phone.setText(studentPhone);
		
		attend_info.setText("지각: " + late + "회, 결석: " + absent + "회, 출석점수: "
				+ totalScore + "/" + attendScore + "점");
	}

	private void setTime() {

		final Calendar c = Calendar.getInstance();
		int Year = c.get(Calendar.YEAR);
		int Month = c.get(Calendar.MONTH) + 1;
		int Day = c.get(Calendar.DAY_OF_MONTH);

		String strTime = Year + "/" + Month + "/" + Day;

		today_text = (TextView) findViewById(R.id.today_text);
		today_text.setText(strTime);
	}

	private void setStudentPicture() {
		student_pic = (ImageView) findViewById(R.id.studentimage);
		try {
			URL url = new URL(studentPic);
			Bitmap bm;
			bm = BitmapFactory.decodeStream(url.openStream());
			student_pic.setImageBitmap(bm);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void setButton() {
		btn_ok = (Button) findViewById(R.id.btn_ok);
		btn_ok.setTextColor(Color.parseColor(shrdPref
				.getString("color", "#1865a9")));
		btn_ok.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
	}

	private void serverConnect(String url) {
		con.doInBackground(url);
		try {
			while (!con.isGetEntity()) {
			}
		} catch (IllegalStateException e) {
			e.printStackTrace();
		}
	}

}

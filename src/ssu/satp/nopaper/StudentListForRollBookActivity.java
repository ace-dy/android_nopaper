package ssu.satp.nopaper;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class StudentListForRollBookActivity extends Activity {

	private ListView listView;
	private ArrayList<String> arrayList;
	private ArrayList<String> studentName;
	private ArrayList<String> studentId;
	private ArrayList<String> studentPhone;
	private ArrayList<String> studentMajor;
	private ArrayList<String> studentPic;
	private ListCheckAdapter adapter;
	private TextView lecture_name;
	JSONArray jsonarray;
	ServerConnection con;
	private static int[] record;
	private static String mUrl = "http://gss.ssu.ac.kr:8081/student_info_send.php";

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.student_list);

		getDataFromServer();
		setLectureName();
		getArrayList();
		getStudentInfo();
		getAttendanceInfo();

		adapter = new ListCheckAdapter(StudentListForRollBookActivity.this,
				R.layout.text_row, arrayList);

		listView = (ListView) findViewById(R.id.listView1);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
				Intent intent = new Intent(StudentListForRollBookActivity.this,
						StudentInfoActivity.class);
				intent.putExtra("con", con);
				intent.putExtra("studentName", studentName);
				intent.putExtra("studentId", studentId);
				intent.putExtra("studentPhone", studentPhone);
				intent.putExtra("studentMajor", studentMajor);
				intent.putExtra("studentPic", studentPic);
				intent.putExtra("position", position);
				intent.putExtra("lecture_name", lecture_name.getText());
				startActivityForResult(intent, 1);
			}
		});

		Intent intent = new Intent(StudentListForRollBookActivity.this,
				NoticeActivity.class);
		startActivity(intent);
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
		listView.invalidateViews();

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

	private void setLectureName() {
		lecture_name = (TextView) findViewById(R.id.lecture_name1);
		lecture_name.setText(con.getValue());
	}

	private void getArrayList() {
		arrayList = new ArrayList<String>();

		int i, leng = jsonarray.length();
		JSONObject obj = null;
		try {
			for (i = 0; i < leng; i++) {
				obj = jsonarray.getJSONObject(i);
				arrayList.add(obj.getString("STUDENT_ID") + "   "
						+ obj.getString("STUDENT_NAME"));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void getStudentInfo() {
		int numOfInfo = jsonarray.length();
		studentName = new ArrayList<String>();
		studentId = new ArrayList<String>();
		studentPhone = new ArrayList<String>();
		studentMajor = new ArrayList<String>();
		studentPic = new ArrayList<String>();
		JSONObject obj;

		for (int i = 0; i < numOfInfo; i++) {
			try {
				obj = jsonarray.getJSONObject(i);
				studentId.add(obj.getString("STUDENT_ID"));
				studentName.add(obj.getString("STUDENT_NAME"));
				studentPhone.add(obj.getString("PHONE"));
				studentMajor.add(obj.getString("MAJOR"));
				studentPic.add(obj.getString("PICTURE_URL"));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
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

		jsonarray = con.getJsonarray();
		int i = 0, index = 0, rNum;

		JSONObject obj = null;
		rNum = arrayList.size();
		record = new int[rNum];
		for (i = 0; i < rNum; i++) {
			record[i] = 3;
		}
		i=0;
		while (!jsonarray.isNull(i)) {
			try {
				obj = jsonarray.getJSONObject(i);
				index = studentId.indexOf(obj.getString("STUDENT_ID"));
				record[index] = Integer.parseInt(obj.getString("RECORD"));
			} catch (JSONException e) {
				e.printStackTrace();
			}
			i++;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		int i;
		record = new int[data.getIntArrayExtra("record").length];

		for (i = 0; i < data.getIntArrayExtra("record").length; i++) {
			record[i] = data.getIntArrayExtra("record")[i];
		}
	}

	public static int[] getRecord() {
		return record;
	}

}

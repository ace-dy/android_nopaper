package ssu.satp.nopaper;

import java.util.ArrayList;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ListCheckAdapter extends ArrayAdapter<String> {
	private int[] record;
	Context mContext;
	private ArrayList<String> arrayList;
	LayoutInflater vi;

	public ListCheckAdapter(Context context, int textRow,
			ArrayList<String> arrayList) {
		super(context, textRow, arrayList);
		this.arrayList = arrayList;
		mContext = context;
		vi = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {

		return arrayList.size();
	}

	@Override
	public String getItem(int position) {

		return arrayList.get(position);
	}

	@Override
	public long getItemId(int position) {

		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		int length = ssu.satp.nopaper.StudentListForRollBookActivity
				.getRecord().length;

		record = new int[length];
		record = ssu.satp.nopaper.StudentListForRollBookActivity.getRecord();

		View v = convertView;

		if (v == null) {
			v = vi.inflate(R.layout.text_row, null);
		}

		if (arrayList.get(position) != null) {
			TextView tt = (TextView) v.findViewById(R.id.textRow);
			tt.setText(arrayList.get(position));

			if (record[position] == 0) {
				tt.setBackgroundColor(Color.parseColor("#F16764"));
				tt.setTextColor(Color.WHITE);
			} else if (record[position] == 1) {
				tt.setBackgroundColor(Color.parseColor("#A0CD56"));
				// tt.setTextColor(Color.parseColor("#A0CD56"));
				tt.setTextColor(Color.WHITE);
			} else if (record[position] == 2) {
				tt.setBackgroundColor(Color.parseColor("#FCD76C"));
				// tt.setTextColor(Color.parseColor("#FCD76C"));
				tt.setTextColor(Color.WHITE);
			} else {
				tt.setBackgroundColor(Color.WHITE);
				tt.setTextColor(Color.BLACK);
			}
		}

		return v;
	}

}

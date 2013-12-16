package ssu.satp.nopaper;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class AttendAdapter extends BaseAdapter {

	static int deviceHeight;

	Context mContext;
	String[] mDate;
	int[] mRecord;
	int count, index;

	AttendAdapter(Context context) {
		mContext = context;
		mDate = ssu.satp.nopaper.AttendanceInfoActivity.getDate();
		mRecord = ssu.satp.nopaper.AttendanceInfoActivity.getRecord();
		count = 8 * ((mDate.length + 3) / 4);
		index = 0;
	}

	@Override
	public int getCount() {
		return count;
	}

	@Override
	public Object getItem(int arg0) {
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	@Override
	public View getView(int position, View oldView, ViewGroup parent) {
		deviceHeight = ssu.satp.nopaper.ClassScheduleActivity.getDeviceHeight();

		View v = null;
		if (oldView == null) {
			v = new TextView(mContext);
		} else if (position < count) {

			v = new TextView(mContext);
			((TextView) v).setGravity(Gravity.CENTER);
			((TextView) v).setHeight(deviceHeight / 22);
			((TextView) v).setTextColor(Color.BLACK);
			((TextView) v).setClickable(true);
			if (position % 8 < 4) {
				index = position - (position / 8) * 4;

				if (index < mDate.length) {
					v.setBackgroundColor(Color.WHITE);
					((TextView) v).setText(mDate[index].substring(5));
				}
			} else {
				index = position - ((position / 8) + 1) * 4;

				if (index < mDate.length) {
					((TextView) v).setClickable(false);
				
					if (mRecord[index] == 0) {
						((TextView) v).setText("결석");
						v.setBackgroundColor(Color.parseColor("#F16764"));
					} else if (mRecord[index] == 1) {
						((TextView) v).setText("출석");
						v.setBackgroundColor(Color.parseColor("#A0CD56"));
					} else {
						((TextView) v).setText("지각");
						v.setBackgroundColor(Color.parseColor("#FCD76C"));
					}

				}
			}
		} else {
			v = oldView;
		}

		return v;
	}
}

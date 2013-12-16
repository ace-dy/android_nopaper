package ssu.satp.nopaper;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ScheduleAdapter extends BaseAdapter {

	static int deviceHeight;
	Context mContext;
	int count = 60;
	int tableInfo[] = ssu.satp.nopaper.ClassScheduleActivity.getTableInfo();

	ScheduleAdapter(Context context) {
		mContext = context;
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
		} else if (position < 6) {
			v = new TextView(mContext);
			((TextView) v).setHeight(0);
			v.setBackgroundColor(Color.WHITE);
		} else if (position >= 6 && position < count) {
			v = new TextView(mContext);
			((TextView) v).setGravity(Gravity.CENTER);
			((TextView) v).setHeight(deviceHeight / 15);
			((TextView) v).setTextColor(Color.BLACK);
			((TextView) v).setClickable(true);
			v.setBackgroundColor(Color.WHITE);

			if (position % 6 == 0) {
				((TextView) v).setText(Integer.toString((position / 6)) + "±³½Ã");
			} else {
				((TextView) v).setText(" \n ");
				if (tableInfo[position] != 0) {
					v.setBackgroundColor(Color.parseColor("#"
							+ String.format("%06x", tableInfo[position])));
					((TextView) v).setClickable(false);
				}
			}
		} else {
			v = oldView;
		}
		return v;
	}
}

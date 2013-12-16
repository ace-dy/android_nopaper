package ssu.satp.nopaper;

import java.util.Calendar;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class GridAdapter extends BaseAdapter {

	static int deviceHeight;

	Context mContext;
	int count = 6;
	String[] mWeekTitleIds = { "시간", "월", "화", "수", "목", "금" };

	GridAdapter(Context context) {
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

		final Calendar c = Calendar.getInstance();
		int DayOfWeek = c.get(Calendar.DAY_OF_WEEK);

		View v = null;
		if (oldView == null) {
			v = new TextView(mContext);
		} else if (position < 6) {
			v = new TextView(mContext);
			((TextView) v).setGravity(Gravity.CENTER);
			((TextView) v).setClickable(true);
			((TextView) v).setHeight(deviceHeight / 15);
			((TextView) v).setTextColor(Color.BLACK);
			((TextView) v).setText(mWeekTitleIds[position]);
			v.setBackgroundColor(Color.WHITE);
			
			if (DayOfWeek == position + 1) {
				v.setBackgroundColor(Color.YELLOW);
			}
		} else {
			v = oldView;
		}

		return v;
	}
}

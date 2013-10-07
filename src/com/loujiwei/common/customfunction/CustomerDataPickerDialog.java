package com.loujiwei.common.customfunction;

import java.lang.reflect.Field;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.NumberPicker;

/**
 * @author liujun 自定义的日期控件，只有年和月，没有日 2012-10-17 上午11:02:17
 */
public class CustomerDataPickerDialog extends DatePickerDialog {

	private boolean hasYear = false;
	private boolean hasMonth = false;
	private boolean hasDay = false;
	private StringBuffer sbTitle = new StringBuffer();

	public CustomerDataPickerDialog(Context context,
			OnDateSetListener callBack, boolean hasYear, boolean hasMonth,
			boolean hasDay, int year, int monthOfYear, int dayOfMonth) {
		super(context, callBack, year, monthOfYear, dayOfMonth);
		this.hasYear = hasYear;
		this.hasMonth = hasMonth;
		this.hasDay = hasDay;

		if (hasYear) {
			sbTitle.append(year + "年");
		}
		if (hasMonth) {
			sbTitle.append((monthOfYear + 1) + "月");
		}
		if (hasDay) {
			sbTitle.append(dayOfMonth + "日");
		}
		// this.setTitle(year+"年"+(monthOfYear + 1) + "月" );
		// this.setTitle(year + "年");
		this.setTitle(sbTitle);
	}

	@Override
	public void onDateChanged(DatePicker view, int year, int month, int day) {
		super.onDateChanged(view, year, month, day);
		// this.setTitle(year+"年"+(month + 1) + "月" );
		// this.setTitle(year + "年");
		this.setTitle(sbTitle);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.DatePickerDialog#show()
	 */
	@SuppressLint("NewApi")
	@Override
	public void show() {
		// TODO Auto-generated method stub
		super.show();
		DatePicker dp = findDatePicker((ViewGroup) this.getWindow()
				.getDecorView());
		if (dp != null) {
			Class c = dp.getClass();
			Field day, month,year;
			try {
				if (!hasDay) {
					day = c.getDeclaredField("mDaySpinner");
					day.setAccessible(true);
					NumberPicker l = (NumberPicker) day.get(dp);
					l.setVisibility(View.GONE);
				}
				if (!hasMonth) {
					month = c.getDeclaredField("mMonthSpinner");
					month.setAccessible(true);
					NumberPicker lm = (NumberPicker) month.get(dp);
					lm.setVisibility(View.GONE);
				}
				if(!hasYear){
					year=c.getDeclaredField("mYearSpinner");
					year.setAccessible(true);
					NumberPicker ly=(NumberPicker) year.get(dp);
					ly.setVisibility(View.GONE);
				}
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchFieldException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// ((ViewGroup)
			// dp.getChildAt(0)).getChildAt(0).setVisibility(View.GONE);

		}
	}

	/**
	 * 从当前Dialog中查找DatePicker子控件
	 * 
	 * @param group
	 * @return
	 */
	private DatePicker findDatePicker(ViewGroup group) {
		if (group != null) {
			for (int i = 0, j = group.getChildCount(); i < j; i++) {
				View child = group.getChildAt(i);
				if (child instanceof DatePicker) {
					return (DatePicker) child;
				} else if (child instanceof ViewGroup) {
					DatePicker result = findDatePicker((ViewGroup) child);
					if (result != null)
						return result;
				}
			}
		}
		return null;

	}

}
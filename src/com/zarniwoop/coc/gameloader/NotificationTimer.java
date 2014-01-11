package com.zarniwoop.coc.gameloader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.os.CountDownTimer;
import android.widget.TextView;

public class NotificationTimer extends CountDownTimer {

	private List<TextView> tvList;
	private List<Long> futureTimeList;

	public NotificationTimer(long countDownInterval) {
		super(Long.MAX_VALUE, countDownInterval);
		tvList = Collections.synchronizedList(new ArrayList<TextView>());
		futureTimeList = Collections.synchronizedList(new ArrayList<Long>());
	}

	@Override
	public void onFinish() {
	}

	public void addDuration(long futureTime, TextView tv) {
		tvList.add(tv);
		futureTimeList.add(futureTime);
	}
	
	/*public boolean updateDuration(String pass, long futureTime) {
		boolean success = true;

		if (timeMap.containsKey(pass))
			timeMap.put(pass, futureTime);
		else
			success = false;

		return success;
	}*/

	@Override
	public void onTick(long millisUntilFinished) {
		long currentTime = System.currentTimeMillis();
		for (int i = 0; i < tvList.size(); i++) {
			TextView tv = tvList.get(i);
			Long futureTime = futureTimeList.get(i);
			millisUntilFinished = futureTime - currentTime;

			if (millisUntilFinished <= 0L) {
				tvList.remove(i);
				futureTimeList.remove(i);
				if (i > 0)
					i--;
				tv.setText("READY");
				continue;
			}
			
			long numDays = millisUntilFinished / TimerDialog.DAY_MILLIS;
			millisUntilFinished %= TimerDialog.DAY_MILLIS;
			long numHours = millisUntilFinished / TimerDialog.HOUR_MILLIS;
			millisUntilFinished %= TimerDialog.HOUR_MILLIS;
			long numMinutes = millisUntilFinished / TimerDialog.MINUTE_MILLIS;
			millisUntilFinished %= TimerDialog.MINUTE_MILLIS;
			long numSeconds = millisUntilFinished / TimerDialog.SECOND_MILLIS;

			StringBuilder buffer = new StringBuilder();
			if (numDays > 0) {
				buffer.append(numDays).append("d ");
			}
			if (numHours > 0) {
				buffer.append(numHours).append("h ");
			}
			if (numDays == 0 && numMinutes > 0) {
				buffer.append(numMinutes).append("m ");
			}
			if (numDays == 0 && numHours == 0 && numSeconds > 0) {
				buffer.append(numSeconds).append("s ");
			}
			tv.setText(buffer.toString());
		}
	}
}
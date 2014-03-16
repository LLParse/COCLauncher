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
			} else {
				tv.setText(TimerDialog.toString(millisUntilFinished));
			}
		}
		
	}
}

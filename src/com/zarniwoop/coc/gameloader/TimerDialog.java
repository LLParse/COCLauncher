package com.zarniwoop.coc.gameloader;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

public class TimerDialog extends Dialog {

	private static final long DAY_MILLIS = TimeUnit.DAYS.toMillis(1L);
	private static final long HOUR_MILLIS = TimeUnit.HOURS.toMillis(1L);
	private static final long MINUTE_MILLIS = TimeUnit.MINUTES.toMillis(1L);
	private static final long SECOND_MILLIS = TimeUnit.SECONDS.toMillis(1L);

	private final TextView days;
	private final TextView hours;
	private final TextView minutes;

	// in milliseconds, ms
	private long delay;

	public TimerDialog(final Context context, final Credential cred,
			final GameArrayAdapter adapter) {
		super(context);

		setContentView(R.layout.timer);
		setCancelable(true);
		setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface di) {
				long notifyTime = System.currentTimeMillis() + delay;
				cred.setNotifyTime(notifyTime);

				CredentialDAO dao = new CredentialDAO(context);
				dao.updateTime(cred, notifyTime);
			}
		});

		days = (TextView) findViewById(R.id.days);
		hours = (TextView) findViewById(R.id.hours);
		minutes = (TextView) findViewById(R.id.minutes);
		delay = 0;
		updateTextViews();

		AutoTickListener listener = new AutoTickListener();
		int secondsPerUnit = 3;
		listener.register(R.id.plusDay, DAY_MILLIS, 7 / secondsPerUnit);
		listener.register(R.id.minusDay, -DAY_MILLIS, 7 / secondsPerUnit);
		listener.register(R.id.plusHour, HOUR_MILLIS, 24 / secondsPerUnit);
		listener.register(R.id.minusHour, -HOUR_MILLIS, 24 / secondsPerUnit);
		listener.register(R.id.plusMinute, MINUTE_MILLIS, 60 / secondsPerUnit);
		listener.register(R.id.minusMinute, -MINUTE_MILLIS, 60 / secondsPerUnit);
	}

	private void updateTextViews() {
		long numMillis = delay;
		long numDays = numMillis / DAY_MILLIS;
		numMillis %= DAY_MILLIS;
		if (numDays == 1)
			days.setText(numDays + " day,");
		else
			days.setText(numDays + " days,");

		long numHours = numMillis / HOUR_MILLIS;
		numMillis %= HOUR_MILLIS;
		if (numHours == 1)
			hours.setText(numHours + " hr,");
		else
			hours.setText(numHours + " hrs,");

		long numMinutes = numMillis / MINUTE_MILLIS;
		if (numMinutes == 1)
			minutes.setText(numMinutes + " min");
		else
			minutes.setText(numMinutes + " mins");
	}

	public static String toString(long delayInMillis) {
		long numDays = delayInMillis / TimerDialog.DAY_MILLIS;
		delayInMillis %= TimerDialog.DAY_MILLIS;
		long numHours = delayInMillis / TimerDialog.HOUR_MILLIS;
		delayInMillis %= TimerDialog.HOUR_MILLIS;
		long numMinutes = delayInMillis / TimerDialog.MINUTE_MILLIS;
		delayInMillis %= TimerDialog.MINUTE_MILLIS;
		long numSeconds = delayInMillis / TimerDialog.SECOND_MILLIS;

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

		return buffer.toString().trim();
	}

	class AutoTickListener implements View.OnTouchListener,
			View.OnClickListener, View.OnLongClickListener {

		// long press auto tick handler & flag
		private Handler repeatHandler = new Handler();
		private boolean autoTick;

		private Map<View, Long> dxMap = new HashMap<View, Long>();
		private Map<View, Long> delayMap = new HashMap<View, Long>();

		public void register(int viewId, long dx, float ticksPerSecond) {
			View view = findViewById(viewId);
			view.setOnTouchListener(this);
			view.setOnClickListener(this);
			view.setOnLongClickListener(this);
			dxMap.put(view, dx);
			delayMap.put(view, (long) (1000 / ticksPerSecond));
		}

		private void tick(View view) {
			long dx = dxMap.get(view);
			// make sure we don't exceed the max size of a signed long
			if (delay + dx >= 0 && delay + dx < Long.MAX_VALUE) {
				delay += dx;
			}
			updateTextViews();
		}

		@Override
		public boolean onTouch(View view, MotionEvent event) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_UP:
			case MotionEvent.ACTION_CANCEL:
				autoTick = false;
				break;
			}
			return false;
		}

		@Override
		public void onClick(View view) {
			tick(view);
		}

		@Override
		public boolean onLongClick(View view) {
			autoTick = true;
			scheduleTick(view);
			return false;
		}
		
		private void scheduleTick(View view) {
			Runnable r = new Ticker(view);
			long delayMillis = delayMap.get(view);
			repeatHandler.postDelayed(r, delayMillis);
		}

		class Ticker implements Runnable {
			private View view;

			public Ticker(View view) {
				this.view = view;
			}

			@Override
			public void run() {
				tick(view);
				if (autoTick) {
					scheduleTick(view);
				}
			}
		}
	}
}

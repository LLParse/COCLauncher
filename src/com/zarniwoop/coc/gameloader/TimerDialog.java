package com.zarniwoop.coc.gameloader;

import java.util.concurrent.TimeUnit;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class TimerDialog extends Dialog {

	private static final long DAY_MILLIS = TimeUnit.DAYS.toMillis(1L);
	private static final long HOUR_MILLIS = TimeUnit.HOURS.toMillis(1L);
	private static final long MINUTE_MILLIS = TimeUnit.MINUTES.toMillis(1L);

	private long msDelay = 0;

	public TimerDialog(final Context context) {
		super(context);

		setContentView(R.layout.timer);
		setCancelable(true);
		setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface di) {
				long notifyTime = System.currentTimeMillis() + msDelay;
				Toast.makeText(context, "Notify date: " + notifyTime,
						Toast.LENGTH_SHORT).show();
			}
		});

		final TextView days = (TextView) findViewById(R.id.days);
		final TextView hours = (TextView) findViewById(R.id.hours);
		final TextView minutes = (TextView) findViewById(R.id.minutes);
		updateTimeViews(days, hours, minutes, msDelay);

		final Button plusDay = (Button) findViewById(R.id.plusDay);
		final Button plusHour = (Button) findViewById(R.id.plusHour);
		final Button plusMinute = (Button) findViewById(R.id.plusMinute);
		final Button minusDay = (Button) findViewById(R.id.minusDay);
		final Button minusHour = (Button) findViewById(R.id.minusHour);
		final Button minusMinute = (Button) findViewById(R.id.minusMinute);
		plusDay.setOnClickListener(new DifferenceOnClickListener(days, hours,
				minutes, DAY_MILLIS, true));
		plusHour.setOnClickListener(new DifferenceOnClickListener(days, hours,
				minutes, HOUR_MILLIS, true));
		plusMinute.setOnClickListener(new DifferenceOnClickListener(days,
				hours, minutes, MINUTE_MILLIS, true));
		minusDay.setOnClickListener(new DifferenceOnClickListener(days, hours,
				minutes, DAY_MILLIS, false));
		minusHour.setOnClickListener(new DifferenceOnClickListener(days, hours,
				minutes, HOUR_MILLIS, false));
		minusMinute.setOnClickListener(new DifferenceOnClickListener(days,
				hours, minutes, MINUTE_MILLIS, false));
	}

	private void updateTimeViews(TextView days, TextView hours,
			TextView minutes, long msDelay) {
		long numDays = msDelay / DAY_MILLIS;
		msDelay %= DAY_MILLIS;
		if (numDays == 1)
			days.setText(numDays + " day,");
		else
			days.setText(numDays + " days,");

		long numHours = msDelay / HOUR_MILLIS;
		msDelay %= HOUR_MILLIS;
		if (numHours == 1)
			hours.setText(numHours + " hour,");
		else
			hours.setText(numHours + " hours,");
		
		long numMinutes = msDelay / MINUTE_MILLIS;
		if (numMinutes == 1)
			minutes.setText(numMinutes + " minute");
		else
			minutes.setText(numMinutes + " minutes");
	}

	class DifferenceOnClickListener implements View.OnClickListener {

		private TextView days;
		private TextView hours;
		private TextView minutes;
		private long difference;
		private boolean positive;

		public DifferenceOnClickListener(TextView days, TextView hours,
				TextView minutes, long difference, boolean positive) {
			this.days = days;
			this.hours = hours;
			this.minutes = minutes;
			this.difference = difference;
			this.positive = positive;
		}

		@Override
		public void onClick(View v) {
			// make sure we don't exceed the max size of a signed long
			if (positive) {
				if (msDelay + difference > msDelay) {
					msDelay += difference;
				}
			} else {
				if (msDelay - difference > 0) {
					msDelay -= difference;
				}
			}
			updateTimeViews(days, hours, minutes, msDelay);
		}
	}

}

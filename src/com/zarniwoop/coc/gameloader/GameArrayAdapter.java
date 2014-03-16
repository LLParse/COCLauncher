package com.zarniwoop.coc.gameloader;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class GameArrayAdapter extends ArrayAdapter<Credential> {

	private static final int resource = R.layout.gamelist_row;
	private final Context context;
	private final List<Credential> values;
	private final NotificationTimer timer;

	public GameArrayAdapter(Context context, List<Credential> values) {
		super(context, resource, values);
		this.context = context;
		this.values = values;
		timer = new NotificationTimer(1000L);
		timer.start();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(resource, parent, false);
		TextView tvId = (TextView) rowView.findViewById(R.id.tvTime);
		TextView tvCredential = (TextView) rowView
				.findViewById(R.id.tvCredential);
		TextView tvName = (TextView) rowView.findViewById(R.id.tvName);

		Credential cred = values.get(position);
		StringBuilder credb = new StringBuilder();
		credb.append("P: ").append(cred.getPass()).append(" | L: ")
				.append(cred.getLow()).append(" | H: ").append(cred.getHigh());
		tvCredential.setText(credb.toString());
		tvName.setText(cred.getThLevel() + ": " + cred.getName());

		if (cred.getNotifyTime() != null) {
			timer.addDuration(cred.getNotifyTime(), tvId);
			timer.tick();
		} else {
			tvId.setText("READY");
		}
		return rowView;
	}

	@Override
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();
		timer.tick();
		for (Credential value : values) {
			// timer.updateDuration(value.getId(), value.getNotifyTime());
		}
	}
}

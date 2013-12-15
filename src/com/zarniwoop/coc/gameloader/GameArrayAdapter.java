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

	public GameArrayAdapter(Context context, List<Credential> values) {
		super(context, resource, values);
		this.context = context;
		this.values = values;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
	    LayoutInflater inflater = (LayoutInflater) context
	            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	        View rowView = inflater.inflate(resource, parent, false);
	        TextView tvId = (TextView) rowView.findViewById(R.id.tvId);
	        TextView tvCredential = (TextView) rowView.findViewById(R.id.tvCredential);
	        TextView tvLevel = (TextView) rowView.findViewById(R.id.tvLevel);
	        TextView tvName = (TextView) rowView.findViewById(R.id.tvName);

	        Credential cred = values.get(position);
	        tvId.setText(String.valueOf(cred.getId()));
	        StringBuilder credb = new StringBuilder();
	        credb.append("P: ")
	        	 .append(cred.getPass())
	        	 .append(" | L: ")
	        	 .append(cred.getLow())
	        	 .append(" | H: ")
	        	 .append(cred.getHigh());
	        tvCredential.setText(credb.toString());
	        tvLevel.setText("TH" + cred.getThLevel());
	        tvName.setText(cred.getName());
	        
	        return rowView;
	}
}

package com.sysfore.azure;

import java.util.List;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.sysfore.azure.model.HostedService;

public class MonitorMachineAdapter extends ArrayAdapter<HostedService> {
	private final List<HostedService> list;
	private final Activity context;
	boolean checkAll_flag = false;
	boolean checkItem_flag = false;
	int count = 0;

	public MonitorMachineAdapter(Activity context, List<HostedService> list) {
		super(context, R.layout.monitor_machine_list, list);
		this.context = context;
		this.list = list;
	}

	static class ViewHolder {
		protected TextView text;
		protected TextView text1;
		protected CheckBox checkbox;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder viewHolder = null;
		if (convertView == null) {
			LayoutInflater inflator = context.getLayoutInflater();
			convertView = inflator.inflate(R.layout.monitor_machine_list, null);
			viewHolder = new ViewHolder();
			viewHolder.text = (TextView) convertView
					.findViewById(R.id.tvmachinename);
			viewHolder.text1 = (TextView) convertView
					.findViewById(R.id.tvmachinestatus);
			viewHolder.checkbox = (CheckBox) convertView
					.findViewById(R.id.checkmachinestatus);
			viewHolder.checkbox
					.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

						@Override
						public void onCheckedChanged(CompoundButton buttonView,
								boolean isChecked) {
							int getPosition = (Integer) buttonView.getTag();
							list.get(getPosition).setSelected(
									buttonView.isChecked());

							if (buttonView.isChecked()) {
								if (count == 3) {
									buttonView.setChecked(false);
									list.get(getPosition).setSelected(
											buttonView.isChecked());
									Toast.makeText(context,
											"Only 3 machines can be selected",
											Toast.LENGTH_SHORT).show();

								}
								if (count < 3) {

									Log.d("count value b4", "" + count);
									count = count + 1;
									list.get(getPosition).setSelected(
											buttonView.isChecked());
									Log.d("count value after", "" + count);

								}
							} else {
								Log.d("count unchecked value b4", "" + count);
								count = count - 1;
								list.get(getPosition).setSelected(
										buttonView.isChecked());
								Log.d("count unchecked value b4", "" + count);

							}
							Log.d("count ", "" + count);
						}
					});
			convertView.setTag(viewHolder);
			convertView.setTag(R.id.tvmachinename, viewHolder.text);
			convertView.setTag(R.id.tvmachinestatus, viewHolder.text1);
			convertView.setTag(R.id.checkmachinestatus, viewHolder.checkbox);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.checkbox.setTag(position); // This line is important.
		viewHolder.text.setText(list.get(position).getServiceName());
		viewHolder.text1.setText(list.get(position).getPowerState());
		Log.d("checked*********************", ""+list.get(position).getMode().equalsIgnoreCase("Y"));
		if (list.get(position).getMode().equalsIgnoreCase("Y"))
			viewHolder.checkbox.setChecked(true);
		else
			viewHolder.checkbox.setChecked(false);
		return convertView;
	}

	public List<HostedService> getMachinedetails() {
		return list;
	}
}

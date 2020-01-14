package ru.maximsidorov.peopleanddepartaments;

import java.util.List;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;

public class BoxAdapter extends BaseAdapter {

	@Override
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();
	}

	Context ctx;
	LayoutInflater inflater;
	List<Person> objects;
	boolean editMode;

	final String LOG_TAG = "myLogs";

	BoxAdapter(Context context, List<Person> people, boolean ms) {
		ctx = context;
		objects = people;
		inflater = (LayoutInflater) ctx
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		editMode = ms;
	}

	@Override
	public int getCount() {
		return objects.size();
	}

	@Override
	public Person getItem(int position) {
		return ((Person) objects.get(position));
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		if (v == null)
			v = inflater.inflate(R.layout.item, parent, false);

		final Person p = getItem(position);

		TextView tvName = (TextView) v.findViewById(R.id.textViewItemName);
		TextView tvLastname = (TextView) v
				.findViewById(R.id.textViewItemLastname);

		tvName.setText(p.name);

		tvLastname.setText(p.lastname);

		TextView tvSalary = (TextView) v.findViewById(R.id.textViewItemSalary);
		final EditText etSalary = (EditText) v
				.findViewById(R.id.editTextItemSalary);

		if (editMode) {
			etSalary.setText(String.valueOf(p.salary));

			etSalary.addTextChangedListener(new TextWatcher() {
				@Override
				public void afterTextChanged(Editable s) {
					if (etSalary.hasFocus())
						try {
							p.salary = Integer.parseInt(s.toString());
							Log.i(LOG_TAG, "id " + p.id
									+ ": salary is edited on " + p.salary);
						} catch (Exception ex) {
							Log.e(LOG_TAG, "Problem! " + ex.getMessage());
						}
				}

				@Override
				public void beforeTextChanged(CharSequence s, int start,
						int count, int after) {
				}

				@Override
				public void onTextChanged(CharSequence s, int start,
						int before, int count) {
				}
			});

			tvSalary.setVisibility(View.GONE);
		} else {
			tvSalary.setText(String.valueOf(p.salary));
			etSalary.setVisibility(View.GONE);
		}
		return v;
	}
}

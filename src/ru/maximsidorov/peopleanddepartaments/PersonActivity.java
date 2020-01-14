package ru.maximsidorov.peopleanddepartaments;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Toast;

public class PersonActivity extends Activity implements OnClickListener,
		OnItemSelectedListener {

	final String LOG_TAG = "myLogs";

	EditText etName, etLastname, etSalary, etPhone;
	Button butOk, butCancel;
	Spinner spDepart;

	DBHelper dbh;

	boolean edit_mode;
	int idDepart;
	int idPerson;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_person);

		Log.d(LOG_TAG, "----Person Activity is started----\n---onCreate---");

		etName = (EditText) findViewById(R.id.editTextPersonName);
		etLastname = (EditText) findViewById(R.id.editTextPersonLastname);
		etSalary = (EditText) findViewById(R.id.editTextPersonSalary);
		etPhone = (EditText) findViewById(R.id.editTextPersonPhone);
		spDepart = (Spinner) findViewById(R.id.spinnerPersonDepartment);

		butOk = (Button) findViewById(R.id.buttonPersonOK);
		butOk.setOnClickListener(this);

		butCancel = (Button) findViewById(R.id.buttonPersonCancel);
		butCancel.setOnClickListener(this);

		dbh = new DBHelper(this);

		refreshSpinner();
		edit_mode = getIntent().getExtras().getBoolean("edit_mode");

		fillActivity();
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.buttonPersonOK:
			if (checkEditText()) {
				if (edit_mode)
					updatePerson();
				else
					addPerson();

				setResult(RESULT_OK, getIntent());
				finish();
			}
			break;
		case R.id.buttonPersonCancel:
			setResult(RESULT_CANCELED, getIntent());
			break;
		}

		finish();
	}

	private void fillActivity() {
		if (edit_mode) {
			this.setTitle(R.string.editing_person);
			idPerson = getIntent().getExtras().getInt("person_id");
			Person p = dbh.personInfo(idPerson);

			etName.setText(p.name);
			etLastname.setText(p.lastname);
			etSalary.setText("" + p.salary);
			etPhone.setText(p.phone);
			spDepart.setSelection(dbh.positionDepToIp(p.department));
			return;
		}
		this.setTitle(R.string.adding_person);
		spDepart.setSelection(getIntent().getExtras()
				.getInt("spinner_position"));
	}

	private void addPerson() {
		String name = etName.getText().toString();
		String lastname = etLastname.getText().toString();
		int salary = Integer.parseInt(etSalary.getText().toString());
		String phone = etPhone.getText().toString();

		dbh.addPerson(name, lastname, idDepart, salary, phone);
		Toast.makeText(getBaseContext(),
				name + " " + lastname + " " + getString(R.string.added_in_DB),
				Toast.LENGTH_SHORT).show();
	}

	private void updatePerson() {
		String name = etName.getText().toString();
		String lastname = etLastname.getText().toString();
		int dep = dbh.idDepToPosition(spDepart.getSelectedItemPosition());
		int salary = Integer.parseInt(etSalary.getText().toString());
		String phone = etPhone.getText().toString();

		dbh.updatePerson(idPerson, name, lastname, dep, salary, phone);
		Toast.makeText(getBaseContext(), R.string.edited, Toast.LENGTH_SHORT)
				.show();
	}

	private boolean checkEditText() {
		if (etName.length() == 0) {
			Toast.makeText(getBaseContext(), R.string.dont_entered_person_name,
					Toast.LENGTH_SHORT).show();
			return false;
		} else if (etName.length() > 15) {
			Toast.makeText(getBaseContext(), R.string.entered_long_name,
					Toast.LENGTH_SHORT).show();
			return false;
		} else if (etLastname.length() == 0) {
			Toast.makeText(getBaseContext(),
					R.string.dont_entered_person_lastname, Toast.LENGTH_SHORT)
					.show();
			return false;
		} else if (etLastname.length() > 15) {
			Toast.makeText(getBaseContext(),
					R.string.dont_entered_person_lastname, Toast.LENGTH_SHORT)
					.show();
			return false;
		} else if (etSalary.length() == 0) {
			Toast.makeText(getBaseContext(),
					R.string.dont_entered_person_salary, Toast.LENGTH_SHORT)
					.show();
			return false;
		} else if (etPhone.length() == 0) {
			Toast.makeText(getBaseContext(),
					R.string.dont_entered_person_phone, Toast.LENGTH_SHORT)
					.show();
			return false;
		} else if (etPhone.length() > 15) {
			Toast.makeText(getBaseContext(), R.string.entered_long_phone,
					Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}

	private void refreshSpinner() {
		Log.d(LOG_TAG, "---refreshSpinner---");
		// Adapter
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, dbh.departmentName());

		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		spDepart = (Spinner) findViewById(R.id.spinnerPersonDepartment);
		spDepart.setAdapter(adapter);

		spDepart.setOnItemSelectedListener(this);
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View v, int position,
			long id) {
		idDepart = dbh.idDepToPosition(position);
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
	}
}
package ru.maximsidorov.peopleanddepartaments;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class DepartamentActivity extends Activity implements OnClickListener {

	EditText etName;
	Button butOK, butCancel;

	DBHelper dbh;

	int idDepart;
	boolean edit_mode;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_departament);

		etName = (EditText) findViewById(R.id.editTextNameDepartament);

		butOK = (Button) findViewById(R.id.buttonDepOK);
		butOK.setOnClickListener(this);

		butCancel = (Button) findViewById(R.id.buttonDepCancel);
		butCancel.setOnClickListener(this);

		dbh = new DBHelper(this);

		edit_mode = getIntent().getExtras().getBoolean("edit_mode");

		if (edit_mode) {
			idDepart = getIntent().getExtras().getInt("departament_id");
			etName.setText(getIntent().getExtras()
					.getString("departament_name"));
			this.setTitle(R.string.editing_department);
		} else
			this.setTitle(R.string.adding_department);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.buttonDepOK:
			if (edit_mode)
				updateDepartament();
			else
				addDepartament();
			setResult(RESULT_OK);
			break;
		case R.id.buttonDepCancel:
			setResult(RESULT_CANCELED);
			break;
		}
		
		finish();
	}

	private void addDepartament() {
		if (checkEditText()) {
			String name = etName.getText().toString();
			dbh.addDepartment(name);
			Toast.makeText(
					getBaseContext(),
					getString(R.string.department) + " " + name + " "
							+ getString(R.string.added_in_DB),
					Toast.LENGTH_SHORT).show();
		}
	}

	private void updateDepartament() {
		if (checkEditText()) {
			String name = etName.getText().toString();
			dbh.updateDepartment(idDepart, name);
			Toast.makeText(getBaseContext(),
					getString(R.string.name_department) + " " + name,
					Toast.LENGTH_SHORT).show();
		}
	}

	private boolean checkEditText() {
		if (etName.length() != 0)
			return true;

		Toast.makeText(getBaseContext(),
				getString(R.string.dont_entered_department_name),
				Toast.LENGTH_SHORT).show();
		return false;
	}
}

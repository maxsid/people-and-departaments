package ru.maximsidorov.peopleanddepartaments;

import java.util.List;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;

import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ImageButton;

public class MainActivity extends Activity implements OnClickListener,
		OnItemClickListener, OnItemSelectedListener,
		DialogInterface.OnClickListener {

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			int pos = spinner.getSelectedItemPosition();
			refreshListView();
			refreshSpinner();
			Log.d(LOG_TAG, "pos = " + pos);
			spinner.setSelection(pos);
		}
	}

	final String LOG_TAG = "myLogs";

	int idPerson;
	int idDepart = 0;
	boolean editMode = false;
	BoxAdapter box;
	ListView lv;

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.action_refresh:
			refreshListView();
			refreshSpinner();
			return true;
		case R.id.action_exit_from_app:
			finish();
			return true;
		case R.id.action_salary_edit:
			editMode = !editMode;

			refreshListView();
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	final int MAN_DIALOG = 1;

	Spinner spinner;
	ImageButton ibSet;

	DBHelper dbh;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);

		Log.d(LOG_TAG, "---MainActivity.onCreate---");

		dbh = new DBHelper(this);

		Log.d(LOG_TAG, "dbh open...");

		ibSet = (ImageButton) findViewById(R.id.imageButtonMainContextMenu);
		spinner = (Spinner) findViewById(R.id.spinnerMainDepartments);
		registerForContextMenu(spinner);

		refreshSpinner();
		Log.d(LOG_TAG, "RefreshSpinner...");
		refreshListView();
		Log.d(LOG_TAG, "---onCreate completed---");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	private void refreshListView() {
		Log.d(LOG_TAG, "--refreshListView--");
		lv = (ListView) findViewById(R.id.listViewPeople);

		showButtons();

		box = new BoxAdapter(this, dbh.peopleNames(idDepart), editMode);

		lv.setAdapter(box);

		lv.setOnItemClickListener(this);
		Log.d(LOG_TAG, "--refreshListView is Close--");
	}

	private void refreshSpinner() {
		List<String> labels = dbh.departmentName();
		labels.add(0, "*");

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, labels);

		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
		spinner.setOnItemSelectedListener(this);
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View v, int position,
			long id) {
		if (position == 0)
			idDepart = 0;
		else
			idDepart = dbh.idDepToPosition(position - 1);
		refreshListView();
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
	}

	@Override
	@Deprecated
	protected Dialog onCreateDialog(int id) {
		Person p = dbh.personInfo(idPerson);

		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		builder.setTitle(p.name + " " + p.lastname + " (id: " + idPerson + ")")
				.setMessage("").setCancelable(false)
				.setPositiveButton(getString(R.string.cancel), this)
				.setNeutralButton(getString(R.string.delete), this)
				.setNegativeButton(getString(R.string.edit), this);
		return builder.create();
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		switch (which) {
		case DialogInterface.BUTTON_NEUTRAL:
			dbh.delPeople(idPerson);
			refreshListView();
			Toast.makeText(getBaseContext(), R.string.deleted,
					Toast.LENGTH_SHORT).show();
			break;
		case DialogInterface.BUTTON_NEGATIVE:
			Intent intentPerson = new Intent(MainActivity.this,
					PersonActivity.class);

			intentPerson.putExtra("edit_mode", true);
			intentPerson.putExtra("person_id", idPerson);

			startActivityForResult(intentPerson, 0);
			break;
		}

		dialog.cancel();
	}

	@Override
	@Deprecated
	protected void onPrepareDialog(int id, Dialog dialog) {
		switch (id) {
		case MAN_DIALOG:
			Person p = dbh.personInfo(idPerson);

			dialog.setTitle(p.name + " " + p.lastname + " (id: " + idPerson
					+ ")");
			((AlertDialog) dialog).setMessage(getString(R.string.department)
					+ ": " + dbh.departmentName(p.department) + "\n"
					+ getString(R.string.salary) + ": " + p.salary + "\n"
					+ getString(R.string.phone) + ": " + p.phone);
		}
		super.onPrepareDialog(id, dialog);
	}

	public static final int ADD_PERSON = 101;
	public static final int ADD_DEPARTAMENT = 102;
	public static final int EDIT_DEPARTAMENT = 103;
	public static final int DEL_DEPARTAMENT = 104;

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);

		menu.add(Menu.NONE, ADD_PERSON, Menu.NONE,
				getString(R.string.add_person));
		menu.add(Menu.NONE, ADD_DEPARTAMENT, Menu.NONE,
				getString(R.string.add_department));
		menu.add(Menu.NONE, EDIT_DEPARTAMENT, Menu.NONE,
				getString(R.string.edit_department));
		menu.add(Menu.NONE, DEL_DEPARTAMENT, Menu.NONE,
				getString(R.string.delete_department));
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case ADD_PERSON:
			Intent addPerson = new Intent(MainActivity.this,
					PersonActivity.class);

			addPerson.putExtra("edit_mode", false);
			addPerson.putExtra(
					"spinner_position",
					spinner.getSelectedItemPosition() != 0 ? spinner
							.getSelectedItemPosition() - 1 : 0);

			startActivityForResult(addPerson, 0);
			break;
		case ADD_DEPARTAMENT:
			Intent addDep = new Intent(MainActivity.this,
					DepartamentActivity.class);

			addDep.putExtra("edit_mode", false);

			startActivityForResult(addDep, 0);
			break;
		case EDIT_DEPARTAMENT:
			if (idDepart != 0) {
				Intent edDep = new Intent(MainActivity.this,
						DepartamentActivity.class);
				edDep.putExtra("edit_mode", true);
				edDep.putExtra("departament_id", idDepart);
				edDep.putExtra("departament_name", spinner.getSelectedItem()
						.toString());
				startActivityForResult(edDep, 0);
			} else
				Toast.makeText(getBaseContext(),
						getString(R.string.select_an_department),
						Toast.LENGTH_SHORT).show();
			break;
		case DEL_DEPARTAMENT:
			if (idDepart != 0 & lv.getCount() == 0) {
				dbh.delDepartment(idDepart);
				refreshSpinner();
			} else if (idDepart == 0)
				Toast.makeText(getBaseContext(),
						getString(R.string.select_an_department),
						Toast.LENGTH_SHORT).show();
			else if (lv.getCount() != 0)
				Toast.makeText(getBaseContext(),
						getString(R.string.select_an_empty_department),
						Toast.LENGTH_SHORT).show();
			break;
		}

		return super.onContextItemSelected(item);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.buttonEditModeCancel:
			editMode = false;
			refreshListView();
			break;
		case R.id.buttonEditModeApply:
			if (saveItemsBoxInDB()) {
				editMode = false;
				refreshListView();
			} else
				Toast.makeText(
						getBaseContext(),
						getString(R.string.error) + "!\n"
								+ getString(R.string.data_dont_edited),
						Toast.LENGTH_SHORT).show();
			break;
		case R.id.imageButtonMainContextMenu:
			openContextMenu(spinner);
			break;
		}
	}

	private void showButtons() {
		LinearLayout spaceButtons = (LinearLayout) findViewById(R.id.linearLayoutspaceButtons);

		if (editMode) {
			spaceButtons.setVisibility(View.VISIBLE);
			findViewById(R.id.buttonEditModeCancel).setOnClickListener(this);
			findViewById(R.id.buttonEditModeApply).setOnClickListener(this);
		} else
			spaceButtons.setVisibility(View.GONE);
	}

	private boolean saveItemsBoxInDB() {
		try {
			dbh.beginTransaction();

			int itemCount = box.getCount();

			for (int i = 0; i < itemCount; i++) {
				Person p = box.objects.get(i);

				dbh.updatePerson(p.id, p.salary);
			}
			dbh.commitTransaction();
			Log.i(LOG_TAG, "База успешно изменена!");
			return true;
		} catch (Exception ex) {
			dbh.rollbackTransaction();
			Log.e(LOG_TAG, "Sorry, " + ex.getMessage());
			return false;
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
		Log.d(LOG_TAG, "--Выбран номер " + position);
		idPerson = dbh.idPersonToPosition(idDepart, position);

		showDialog(MAN_DIALOG);
	}
}

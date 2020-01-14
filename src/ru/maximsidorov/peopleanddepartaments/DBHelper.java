package ru.maximsidorov.peopleanddepartaments;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper implements BaseColumns {

	private static final String DATABASE_NAME = "data.db";

	SQLiteDatabase db;

	final String LOG_TAG = "myLogs";

	public DBHelper(Context context) {
		super(context, DATABASE_NAME, null, 1);
		db = this.getWritableDatabase();
	}

	public void close() {
		db.close();
		this.close();
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// Создаем таблицы

		db.execSQL("CREATE TABLE Departments (" + _ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT," + "name VARCHAR(100));");
		db.execSQL("CREATE TABLE people (" + _ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT," + "name VARCHAR(20),"
				+ "lastname VARCHAR(20)," + "Department INTEGER,"
				+ "salary INTEGER," + "phone VARCHAR(20));");
		Log.d(LOG_TAG, "---База создана---");

		// Заносим изначальные данные(уберу потом)
		String[] depName = { "Разработка под Windows",
				"Разработка под Android", "Тестирование ПО" };
		for (int i = 0; i < depName.length; i++) {
			db.execSQL("INSERT INTO Departments (name) VALUES ('" + depName[i]
					+ "');");
			Log.d(LOG_TAG, "! В базу занесен отдел " + depName[i]);
		}

		String[] name = { "Максим", "Николай", "Сергей", "Дмитрий", "Иван",
				"Максим", "Сергей", "Илья", "Александр", "Стив" };
		String[] lastname = { "Петров", "Иванов", "Федоров", "Андреев",
				"Сидоров", "Антонов", "Ульянов", "Портьянов", "Эстонов",
				"Балмер" };
		int[] Department = { 1, 1, 2, 1, 2, 3, 1, 2, 3, 2 };
		int[] salary = { 10000, 11000, 12000, 13000, 14000, 15000, 16000,
				17000, 18000, 190000 };
		String[] tel = { "+756", "+962", "+856", "+537", "+12314", "+32123123",
				"+7(920)3123122", "+231", "+622", "+1231" };

		for (int i = 0; i < name.length; i++) {
			db.execSQL("INSERT INTO people (name, lastname, Department, salary, phone) VALUES ('"
					+ name[i]
					+ "','"
					+ lastname[i]
					+ "',"
					+ Department[i]
					+ "," + salary[i] + ",'" + tel[i] + "');");
			Log.d(LOG_TAG, "! В базу занесен человек " + name[i] + " "
					+ lastname[i]);
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}

	// /////////////////////\\\\\\\\\\\\\\\\\\\\\\\\\
	// /////////////Таблица "people"\\\\\\\\\\\\\\\\\\\
	// ///////////////////////\\\\\\\\\\\\\\\\\\\\\\\\\\\
	public void addPerson(String name, String lastname, int Department,
			int salary, String tel) {
		db.execSQL("INSERT INTO people (name, lastname, Department, salary, phone) VALUES ('"
				+ name
				+ "', '"
				+ lastname
				+ "', "
				+ Department
				+ ", "
				+ salary
				+ ", '" + tel + "');");
	}

	public void updatePerson(int idPerson, int salary) {
		db.execSQL("UPDATE people SET salary = " + salary + " WHERE _id = "
				+ idPerson + ";");
	}

	public void updatePerson(int idPerson, String name, String lastname,
			int department, int salary, String tel) {
		db.execSQL("UPDATE people SET name = '" + name + "', lastname = '"
				+ lastname + "', " + "department = " + department
				+ ", salary = " + salary + ", phone = '" + tel + "' "
				+ "WHERE _id = " + idPerson + ";");
	}

	public void delPeople(int id) {
		db.execSQL("DELETE FROM people WHERE _id = " + id + ";");
	}

	public List<Person> peopleNames(int idDepart) {
		Cursor c;
		List<Person> labels = new ArrayList<Person>();

		if (idDepart != 0)
			c = db.rawQuery(
					"SELECT _id, name, lastname, salary, phone FROM people WHERE Department = "
							+ idDepart + ";", null);
		else
			c = db.rawQuery(
					"SELECT _id, name, lastname, salary, phone FROM people;",
					null);

		while (c.moveToNext()) {
			labels.add(new Person(Integer.parseInt(c.getString(0)), c
					.getString(1), c.getString(2), Integer.parseInt(c
					.getString(3)), idDepart));
		}
		c.close();
		return labels;
	}

	public Person personInfo(int id) {
		Cursor c;

		Log.d(LOG_TAG, "DBHerlper.personInfo: Start");
		c = db.rawQuery(
				"SELECT people.name AS name, lastname, people.department AS departamentId"
						+ ", departments.name AS departamentName, salary, phone "
						+ "FROM people INNER JOIN  Departments "
						+ "ON people.Department = Departments._id "
						+ "WHERE people._id = " + id + ";", null);

		Person p = new Person();

		if (c.moveToFirst()) {
			p.id = id;
			p.name = c.getString(c.getColumnIndex("name"));
			p.lastname = c.getString(c.getColumnIndex("lastname"));
			p.salary = c.getInt(c.getColumnIndex("salary"));
			p.department = c.getInt(c.getColumnIndex("departamentId"));
			p.phone = c.getString(c.getColumnIndex("phone"));
		} else {
			return null;
		}

		c.close();
		return p;
	}

	// По позиции в ListView получаем id персоны
	public int idPersonToPosition(int idDepart, int position) {
		Cursor c;

		ArrayList<String> labels = new ArrayList<String>();

		Log.d(LOG_TAG, "---idToPostion---\nidDepart = " + idDepart
				+ "\nposition = " + position);

		if (idDepart != 0)
			c = db.rawQuery("SELECT _id FROM people WHERE Department = "
					+ idDepart + ";", null);
		else
			c = db.rawQuery("SELECT _id FROM people;", null);

		while (c.moveToNext()) {
			labels.add(c.getString(0));
			Log.d(LOG_TAG, c.getString(0));
		}
		Log.d(LOG_TAG, "Выбран " + labels.get(position));

		c.close();

		return Integer.parseInt(labels.get(position));
	}

	// /////////////////////\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
	// /////////////Таблица "departments"\\\\\\\\\\\\\\\\\\\
	// ///////////////////////\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
	public ArrayList<String> departmentName() {
		Cursor c;
		ArrayList<String> labels = new ArrayList<String>();
		c = db.rawQuery("SELECT name FROM Departments;", null);
		while (c.moveToNext())
			labels.add(c.getString(0));

		c.close();

		return labels;
	}

	public void addDepartment(String name) {
		db.execSQL("INSERT INTO Departments (name) VALUES ('" + name + "');");
	}

	public void updateDepartment(int idDepart, String name) {
		db.execSQL("UPDATE Departments SET name = '" + name + "' WHERE _id = "
				+ idDepart + ";");
	}

	public int idDepToPosition(int position) {
		Cursor c;
		ArrayList<String> labels = new ArrayList<String>();
		c = db.rawQuery("SELECT _id FROM Departments;", null);

		while (c.moveToNext())
			labels.add(c.getString(0));

		c.close();
		return Integer.parseInt(labels.get(position));
	}

	public void delDepartment(int idDepart) {
		db.execSQL("DELETE FROM Departments WHERE _id = " + idDepart);
	}

	public int positionDepToIp(int idDepart) {
		Cursor c;
		c = db.rawQuery("SELECT _id FROM Departments;", null);

		int position = 0;
		while (c.moveToNext() && c.getInt(0) != idDepart) {
			Log.d(LOG_TAG, c.getString(0) + " != " + idDepart);
			position++;
		}
		Log.d(LOG_TAG, "positionDepToIp = " + position);
		return position;
	}
	
	public String departmentName(int idDepart)
	{
		Cursor c;
		c = db.rawQuery("SELECT name FROM departments WHERE _id = " + idDepart + ";", null);
		c.moveToFirst();
		String departName = c.getString(c.getColumnIndex("name"));
		return departName;
	}

	// /////////////////////\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
	// /////////////////Транзакции\\\\\\\\\\\\\\\\\\\\\\\\\\
	// ///////////////////////\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

	public void beginTransaction() {
		db.beginTransaction();
	}

	public void commitTransaction() {
		db.setTransactionSuccessful();
		db.endTransaction();
	}

	public void rollbackTransaction() {
		db.endTransaction();
	}
}

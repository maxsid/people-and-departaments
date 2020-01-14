package ru.maximsidorov.peopleanddepartaments;

import android.util.Log;

public class Person {
	int id, salary, department;
	String name, lastname, phone;
	final String LOG_TAG = "myLogs";

	public Person() {
	}

	public Person(int _id, String _name, String _lastname, int _salary,
			int _department) {
		id = _id;
		name = _name;
		lastname = _lastname;
		salary = _salary;
		department = _department;
		Log.d(LOG_TAG, "В people записан " + name + " " + lastname
				+ " с зарплатой " + salary);
	}
}

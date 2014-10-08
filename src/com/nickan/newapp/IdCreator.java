package com.nickan.newapp;

public class IdCreator {
	private static int id = 10000;
	
	private IdCreator() { }
	
	public static int getId() {
		return id++;
	}
}

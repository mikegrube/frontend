package com.camas.message;

public class Command {
	
	String command;
	String args[];
	
	public Command(String command, String[] args) {
		this.command = command;
		this.args = args;
	}
	
	public String getCommand() {
		return command;
	}
	
	public String[] getArgs() {
		return args;
	}
}
package com.camas.message;

//The Command message carries a command
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
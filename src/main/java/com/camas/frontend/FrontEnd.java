package com.camas.frontend;

import java.util.Scanner;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.IOException;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;

import javax.swing.JOptionPane;

//FrontEnd acts as the entry point, providing a crude user interface
public class FrontEnd {

	//Akka to decouple the Actors
	final ActorSystem system = ActorSystem.create("event-sourcing");
	ActorRef commandHandler;

	public FrontEnd() {}

	public static void main(String[] args) {

		FrontEnd fe = new FrontEnd();
		fe.go();

	}

	//Initiate the conversation
	public void go() {

		System.out.println("Welcome to FrontEnd\n");

		//Create the Actor hierarchy
		setUpActors();

		boolean done = false;

		//Just keep on getting commands until the user gives up
		while (!done) {
			String cmd = takeCommand();
			if (cmd.equals("Quit")) {
				done = true;
				system.terminate();   //Stop Akka
			} else if (cmd.startsWith("ProcessFile")) {
				onProcessFile(cmd);
			} else {
				commandHandler.tell(cmd, ActorRef.noSender());
			}
		}
	}

	//
	private void setUpActors() {

		try {
			commandHandler = system.actorOf(CommandHandler.props(), "command-handler");

		} catch (Exception e) {
			System.out.println("Unable to create command handler");
		}
	}

	//Get another command from the user
	String takeCommand() {
		String result = JOptionPane.showInputDialog("Command:");
		if (result == null || result.equals("")) {
			result = "Quit";
		}
		return result;
	}

	//If the command was for processing a file of commands
	private void onProcessFile(String cmd) {

		//Only the one file, sorry  
		final String FILENAME = "commands.txt";

		BufferedReader br = null;
		FileReader fr = null;
		String line;

		try {

			fr = new FileReader(FILENAME);
			br = new BufferedReader(fr);

			//For each line, treat it as a command
			while ((line = br.readLine()) != null) {
				if (!line.startsWith("#")) {
					commandHandler.tell(line, ActorRef.noSender());
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)
					br.close();

				if (fr != null)
					fr.close();

			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}
}
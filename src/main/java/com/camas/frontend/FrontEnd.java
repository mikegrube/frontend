package com.camas.frontend;

import java.util.Scanner;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.IOException;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;

import javax.swing.JOptionPane;

public class FrontEnd {

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

     setUpActors();

    boolean done = false;

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

  private void setUpActors() {
     
     try {
       commandHandler = system.actorOf(CommandHandler.props(), "command-handler");

     } catch (Exception e) {
        System.out.println("Unable to create command handler");
     }
  }

  String takeCommand() {
     String result = JOptionPane.showInputDialog("Command:");
     if (result == null || result.equals("")) {
        result = "Quit";
     }
     return result;
  }
  
  private void onProcessFile(String cmd) {
	  
	final String FILENAME = "commands.txt";

  	BufferedReader br = null;
	FileReader fr = null;
	String line;

  	try {

		fr = new FileReader(FILENAME);
 		br = new BufferedReader(fr);

 		while ((line = br.readLine()) != null) {
            commandHandler.tell(line, ActorRef.noSender());
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
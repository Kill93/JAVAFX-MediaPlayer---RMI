package application.Server;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

import javafx.collections.ObservableList;

/*  Killian Nolan -  R00129172 - DWEB4	 */

public interface MonitorInterface extends Remote {
	
	public Boolean checkBool()throws RemoteException; // returns a bool
	public ArrayList<String> getNames()throws RemoteException; // returns name of all files in folder 1
	public Boolean openFile1( String name)throws RemoteException; //opens a file called name
	public byte [] getB(String songName)throws RemoteException; //gets a byte from currently opened file
	public Boolean closeFile()throws RemoteException; // closes a file
	public Boolean checkForChange(ArrayList<String> items) throws RemoteException;
	public void copyFile (byte[] bytes, String songName) throws FileNotFoundException, IOException, RemoteException;
}

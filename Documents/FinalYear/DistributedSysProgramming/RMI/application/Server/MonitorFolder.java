package application.Server;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Observable;

import javafx.collections.ObservableList;


/*  Killian Nolan -  R00129172 - DWEB4	 */

public class MonitorFolder extends UnicastRemoteObject implements MonitorInterface {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected MonitorFolder() throws RemoteException {

        super();

    }

	private Boolean endf = false;
	private DataInputStream in;

	public Boolean checkBool() {
		return endf;
	}

	public ArrayList<String> getNames () {

		File aDirectory = new File("/Users/killiannolan/Documents/FinalYear/DistributedSysProgramming/Lab1-Sys/Folder1");

		String[] filesInDir = aDirectory.list();
		ArrayList<String> files = new ArrayList<String>();

		for ( int i=0; i<filesInDir.length; i++ )
		{
			if(filesInDir[i].endsWith(".mp3")){
				files.add(filesInDir[i]);
			}
		}
		return files;
	}

	public Boolean openFile1(String name) {
		try {
			in = new DataInputStream(new BufferedInputStream(
					new FileInputStream(name)));
		} catch (IOException e ) {e.printStackTrace();
		}
		return true;
	}

	public byte [] getB( String songName) {

		FileInputStream fis = null;
		File someFile = new File("/Users/killiannolan/Documents/FinalYear/DistributedSysProgramming/Lab1-Sys/Folder1/" + songName);
		
		try {
			fis = new FileInputStream(someFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		byte[] buf = new byte[1024];

		try {
			for (int readNum; (readNum = fis.read(buf)) != -1;) {
				bos.write(buf, 0, readNum); 
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		byte[] bytes = bos.toByteArray();

		return bytes;
	}

	public void copyFile(byte[] bytes, String songName) throws IOException {
		File someFile = new File("/Users/killiannolan/Documents/FinalYear/DistributedSysProgramming/Lab1-Sys/Folder1/" + songName);
		FileOutputStream fos = new FileOutputStream(someFile);
		fos.write(bytes);
		fos.flush();
		fos.close();    
	}

	public Boolean closeFile() {
		try {
			in.close();
		}
		catch (IOException e) 
		{e.printStackTrace(); return false;}
		return true;
	}

	public Boolean checkForChange(ArrayList<String> items) throws RemoteException {

		Boolean bool;

		File aDirectory2 = new File("/Users/killiannolan/Documents/FinalYear/DistributedSysProgramming/Lab1-Sys/Folder1");
		String[] filesInDir2 = aDirectory2.list();

		ArrayList<String> ls2 = new ArrayList<String>(Arrays.asList());

		for ( int i=0; i<filesInDir2.length; i++ )
		{
			if(filesInDir2[i].endsWith(".mp3")){
				ls2.add(filesInDir2[i]);
			}
		}
		if (items.size() != ls2.size()) {
			String serverList="Server";
//			ListObserver ListObserver =new ListObserver(serverList);
//			Server serverClass =new Server();
//			ListObserver.register(serverClass);
//			ListObserver.notifyObserver();
			bool = true;
		}
		else {
			bool = false;
		}
		return bool;
	}


}

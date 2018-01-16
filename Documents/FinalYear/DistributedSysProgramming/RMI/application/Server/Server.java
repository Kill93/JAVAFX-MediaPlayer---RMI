package application.Server;

/*  Killian Nolan -  R00129172 - DWEB4	 */

import java.nio.channels.AlreadyBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Server {
	   private static final int PORT = 5555;
	   private static Registry registry;
	 
	   public static void startRegistry() throws RemoteException {
	       // Create server registry
	       registry =  LocateRegistry.createRegistry(PORT);
	   }
	 
	   public static void registerObject(String name, Remote remoteObj)
	           throws RemoteException, AlreadyBoundException, java.rmi.AlreadyBoundException {      
	       registry.bind(name, remoteObj);
	       System.out.println("Registered: " + name + " -> "
	               + remoteObj.getClass().getName());
	   }
	 
	   public static void main(String[] args) throws Exception {
	       System.out.println("Server starting...");
	       startRegistry();
	       registerObject(MonitorInterface.class.getSimpleName(), new MonitorFolder());
	       System.out.println("Server started!");
	   }
	}


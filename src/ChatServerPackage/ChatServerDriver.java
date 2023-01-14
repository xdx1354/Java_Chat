package ChatServerPackage;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ChatServerDriver {

    public static void main(String[] args) throws RemoteException, MalformedURLException {
        //System.setProperty("java.rmi.server.hostname","192.168.1.2");
        Registry registry = LocateRegistry.createRegistry(1099);
        registry.rebind("RMIChatServer", new ChatServer());
        System.out.println("Server started...");
    }
}

package ChatServerPackage;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ChatServerDriver {

    public static void main(String[] args) throws RemoteException, MalformedURLException {

        Registry registry = LocateRegistry.createRegistry(1099);
        registry.rebind("RMIChatServer", new ChatServer());
        System.out.println("Server started...");
    }
}

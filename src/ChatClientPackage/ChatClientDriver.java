package ChatClientPackage;

import ChatServerPackage.ChatServer;
import ChatServerPackage.ChatServerIF;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class ChatClientDriver {

    public static void main(String[] args) throws MalformedURLException, NotBoundException, RemoteException {

        String chatServerURL = "rmi://localhost:1099/RMIChatServer";
        ChatServerIF chatServer = (ChatServerIF) Naming.lookup(chatServerURL);
        System.out.println("HAAAALO");
        new Thread(new ChatClient(args[0],chatServer)).start();
    }
}

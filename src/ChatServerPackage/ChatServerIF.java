package ChatServerPackage;

import ChatClientPackage.ChatClient;
import ChatClientPackage.ChatClientIF;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface ChatServerIF extends Remote {

    void registerChatClient (ChatClientIF chatClient) throws RemoteException;
    void broadcastMessage(String message) throws RemoteException;
    ArrayList<String> broadcastUsersList() throws RemoteException;


}

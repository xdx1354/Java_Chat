package ChatServerPackage;

import ChatClientPackage.ChatClientIF;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface ChatServerIF extends Remote {

    int registerChatClient (ChatClientIF chatClient) throws RemoteException;
    void unregisterChatClient(String name) throws RemoteException;
    void disconnectChatClient(String client1, String client2) throws RemoteException;
    void broadcastMessage(String message, String client1, String client2) throws RemoteException;
    ArrayList<String> broadcastUsersList() throws RemoteException;
    void connectChatClient(String client1, String client2) throws RemoteException;
    int isClientFree(String name) throws RemoteException;


}

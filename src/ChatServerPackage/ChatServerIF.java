package ChatServerPackage;

import ChatClientPackage.ChatClient;
import ChatClientPackage.ChatClientIF;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ChatServerIF extends Remote {

    void registerChatClient (ChatClientIF chatClient) throws RemoteException;
    void broadcastMessage(String message) throws RemoteException;

}

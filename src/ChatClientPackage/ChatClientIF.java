package ChatClientPackage;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ChatClientIF extends Remote {

    void  retrieveMessage(String message) throws RemoteException;
    String sendName() throws RemoteException;
    void refreshActiveUsers() throws RemoteException;
    void disconnectClient() throws RemoteException;
    void connectClient(String nameOfClientToConnectWith) throws RemoteException;
    int isClientFree()throws RemoteException;
}

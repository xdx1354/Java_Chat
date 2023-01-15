package ChatServerPackage;

import ChatClientPackage.ChatClient;
import ChatClientPackage.ChatClientIF;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;

public class ChatServer extends UnicastRemoteObject implements ChatServerIF{

    //private static final long  serialVersionUID = 1L;
    HashMap<String,ChatClientIF> chatClientsList;
    ArrayList <String> listOfNames;

    protected ChatServer() throws RemoteException {
        chatClientsList = new HashMap<String, ChatClientIF>();
    }


    @Override
    public synchronized void registerChatClient(ChatClientIF chatClient) throws RemoteException {
        this.chatClientsList.put(chatClient.sendName(),chatClient);
        System.out.println("New Client registered");

        chatClientsList.forEach((key,value)-> {         //Refreshing active users list of each client
            try {
                value.refreshActiveUsers();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void disconnectChatClient(String name) throws RemoteException {
        chatClientsList.remove(name);
        chatClientsList.forEach((key,value)-> {         //Refreshing active users list of each client
            try {
                value.refreshActiveUsers();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        });
    }

    public void broadcastMessage(String message) throws RemoteException{
        System.out.println("BROADCASTING");
        chatClientsList.forEach((key,value)-> {
            try {
                value.retrieveMessage(message);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public ArrayList<String> broadcastUsersList() throws RemoteException {     // zwraca aktualna liste imion klientowi
        generateListOfNames();
        return listOfNames;
    }

    private void generateListOfNames() throws RemoteException {
       listOfNames = new ArrayList<>();

       chatClientsList.forEach((key,value)-> {              // iterowanie petla forEach
           try {
               listOfNames.add(value.sendName());
           } catch (RemoteException e) {
               e.printStackTrace();
           }
       });
    }



}

package ChatServerPackage;

import ChatClientPackage.ChatClientIF;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public class ChatServer extends UnicastRemoteObject implements ChatServerIF{

    //private static final long  serialVersionUID = 1L;
    HashMap<String,ChatClientIF> chatClientsList;
    ArrayList <String> listOfNames;

    protected ChatServer() throws RemoteException {
        chatClientsList = new HashMap<String, ChatClientIF>();
    }


    @Override
    public synchronized int registerChatClient(ChatClientIF chatClient) throws RemoteException {

       boolean isNameLegal = true;

        for(String key:chatClientsList.keySet()){
            if (Objects.equals(key, chatClient.sendName()))
                isNameLegal=false;
        }

        if(isNameLegal){
            this.chatClientsList.put(chatClient.sendName(),chatClient);
            System.out.println("New Client registered");

            chatClientsList.forEach((key,value)-> {         //Refreshing active users list of each client
                try {
                    value.refreshActiveUsers();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            });
            return 1;
        }
        return 0;
    }

    @Override
    public void unregisterChatClient(String name) throws RemoteException {
        chatClientsList.remove(name);
        chatClientsList.forEach((key,value)-> {         //Refreshing active users list of each client
            try {
                value.refreshActiveUsers();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void disconnectChatClient(String client1, String client2) throws RemoteException {
        chatClientsList.get(client1).disconnectClient();
        chatClientsList.get(client2).disconnectClient();
    }

    public void broadcastMessage(String message, String client1, String client2) throws RemoteException{
        System.out.println("BROADCASTING message form: " + client1 + " to: " + client2);

        chatClientsList.get(client1).retrieveMessage(message);
        chatClientsList.get(client2).retrieveMessage(message);

    }

    @Override
    public ArrayList<String> broadcastUsersList() throws RemoteException {     // zwraca aktualna liste imion klientowi
        generateListOfNames();
        return listOfNames;
    }

    @Override
    public void connectChatClient(String client1, String client2) throws RemoteException {
        chatClientsList.get(client1).connectClient(client2);
        chatClientsList.get(client2).connectClient(client1);
    }

    @Override
    public int isClientFree(String name) throws RemoteException {
        if(chatClientsList.get(name).isClientFree()==1) return 1;       //gdy wolny zwracam 1
        else return 0;                                                  //gdy zajety zwracam 0
    }

    private void generateListOfNames() throws RemoteException {
       listOfNames = new ArrayList<>();

       chatClientsList.forEach((key,value)-> {
           try {
               listOfNames.add(value.sendName());
           } catch (RemoteException e) {
               e.printStackTrace();
           }
       });
    }



}

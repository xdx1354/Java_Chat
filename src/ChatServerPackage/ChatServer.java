package ChatServerPackage;

import ChatClientPackage.ChatClient;
import ChatClientPackage.ChatClientIF;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

public class ChatServer extends UnicastRemoteObject implements ChatServerIF{

    //private static final long  serialVersionUID = 1L;
    ArrayList<ChatClientIF> chatClientsList;
    ArrayList <String> listOfNames;

    protected ChatServer() throws RemoteException {
        chatClientsList = new ArrayList<ChatClientIF>();
    }


    @Override
    public synchronized void registerChatClient(ChatClientIF chatClient) throws RemoteException {
        this.chatClientsList.add(chatClient);
        System.out.println("New Client registered");
    }

    public void broadcastMessage(String message) throws RemoteException{
        System.out.println("BROADCASTING");
        for(int i=0; i<chatClientsList.size(); i++){
            chatClientsList.get(i).retrieveMessage(message);
            System.out.println(message);

        }
    }

    @Override
    public ArrayList<String> broadcastUsersList() throws RemoteException {     // zwraca aktualna liste imion klientowi
        generateListOfNames();
        return listOfNames;
    }

    private void generateListOfNames() throws RemoteException {
       listOfNames = new ArrayList<>();
        for(ChatClientIF c: chatClientsList){
            listOfNames.add(c.sendName()); // tworzy liste imion
        }
    }

}

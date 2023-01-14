package ChatServerPackage;

import ChatClientPackage.ChatClient;
import ChatClientPackage.ChatClientIF;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

public class ChatServer extends UnicastRemoteObject implements ChatServerIF{

    //private static final long  serialVersionUID = 1L;
    private ArrayList<ChatClientIF> chatClientsList;

    protected ChatServer() throws RemoteException {
        chatClientsList = new ArrayList<ChatClientIF>();
    }


    @Override
    public synchronized void registerChatClient(ChatClientIF chatClient) throws RemoteException {
        this.chatClientsList.add(chatClient);
    }

    public void broadcastMessage(String message) throws RemoteException{
        System.out.println("BROADCASTING");
        for(int i=0; i<chatClientsList.size(); i++){
            chatClientsList.get(i).retrieveMessage(message);
            System.out.println(message);

        }
    }




//    public static void main(String[] args) {
//
//        JFrame chatFrame = new JFrame("RMI CHAT - STANISLAW KURZYP");
//        JPanel messagePanel = new JPanel();
//        JButton sendButton = new JButton("SEND");
//        messagePanel.add(sendButton);
//        chatFrame.add(messagePanel);
//        chatFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
//        chatFrame.setVisible(true);
//        chatFrame.pack();
//
//
//    }
}

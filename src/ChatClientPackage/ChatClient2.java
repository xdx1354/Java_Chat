package ChatClientPackage;

import ChatServerPackage.ChatServerIF;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Objects;

public class ChatClient2 extends UnicastRemoteObject implements ChatClientIF, Runnable{
    private JTextArea textArea;
    private JTextField inputField;
    private JButton sendButton;
    private JButton connectButton;
    private JButton disconnectButton;
    private JTextField nameTf;
    private JButton registerButton;
    private JButton unregisterButton;
    private JComboBox <String> activeUsers;
    private JPanel mainPanel;
    private JFrame clientFrame;

    private final ChatServerIF chatServer;
    private String name = null;
    ArrayList<String> activeUsersArrayList;
    String currentlyConnectedWith;  // Name of client with whom this client is now connected

    protected ChatClient2( ChatServerIF chatServer) throws RemoteException {
        this.chatServer = chatServer;
    }


    @Override
    public void retrieveMessage(String message) throws RemoteException {
//        System.out.println(message);
        textArea.append(message +"\n");
    }

    @Override
    public String sendName() {
        return name;
    }

    @Override
    public void run() {

        clientFrame  = new JFrame();
        clientFrame.add(mainPanel);
        clientFrame.setVisible(true);
        clientFrame.setSize(600,350);
        clientFrame.setTitle("Chatting with no-one");
        clientFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);


        //TEXT AREA - incoming and sent messages
        textArea.setHighlighter(null);
        textArea.setEditable(false);

        //INPUT TEXT FIELD - message to send
        inputField.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {}

            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ENTER){

                    if(Objects.equals(currentlyConnectedWith,null)){
                        JOptionPane.showMessageDialog(mainPanel, "By wysylac wiadomosci nalezy najpierw polazyc sie z odbiorcą!");
                    }
                    else{
                        try {
                            sendButtonPressed();
                        } catch (RemoteException ex) {
                            ex.printStackTrace();
                        }
                    }

                }
            }

            @Override
            public void keyReleased(KeyEvent e) {}
        });

        // SEND BUTTON
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    sendButtonPressed();
                } catch (RemoteException ex) {
                    ex.printStackTrace();
                }
            }
        });


        //LIST OF USERS - lista aktualnie zalogowanych uzytkowników
        try {
            refreshActiveUsers();
        } catch (RemoteException e) {
            e.printStackTrace();
        }


        //CONNECT BUTTON - used to connect with selected client from scrollable list of clients
        connectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    connectButtonPressed();
                } catch (RemoteException ex) {
                    ex.printStackTrace();
                }
            }
        });

        //DISCONNECT BUTTON - used to disconnect from chatting  with currently selected user
        disconnectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(currentlyConnectedWith!=null){
                    try {
                        chatServer.disconnectChatClient(name,currentlyConnectedWith);
                    } catch (RemoteException ex) {
                        ex.printStackTrace();
                    }
                }
                else{
                        JOptionPane.showMessageDialog(mainPanel, "Nie ma z kim sie rozlaczyc");
                }
            }
        });

        //REGISTER BUTTON - used to register current client instance as client with name from nameTf
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    registerButtonPressed();
                } catch (RemoteException ex) {
                    ex.printStackTrace();
                }
            }
        });

        //UNREGISTER BUTTON - used to unregister current client, it should clear all messages!
        unregisterButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    unregisterButtonPressed();
                } catch (RemoteException ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    /*      */
    public void sendButtonPressed() throws RemoteException {
        String message;
        if(Objects.equals(name,null) || Objects.equals(currentlyConnectedWith,null) ){
            JOptionPane.showMessageDialog(mainPanel, "By wysylac wiadomosci nalezy najpierw sie zalogowac i połączyć się z odbiorcą!");
        }
        else{
//            System.out.println(name);
            message = inputField.getText();
            inputField.setText("");
            chatServer.broadcastMessage(name + " : " + message, name, currentlyConnectedWith);
        }
    }

    public void registerButtonPressed() throws RemoteException {
        name = nameTf.getText();
        if(chatServer.registerChatClient(this) == 0){
            JOptionPane.showMessageDialog(mainPanel, "Podana nazwa jest już zajęta.");
        }
        else{
            // setting current Client as client called nameTf.getText()
            name = nameTf.getText();
            //creating current client reference on server
            chatServer.registerChatClient(this);
            nameTf.setEditable(false);
            JOptionPane.showMessageDialog(mainPanel, "Zarejestrowano uzytkownika.");
            refreshActiveUsers();
            textArea.setText(""); // Clearing all old messages, just to be sure
        }
    }

    public void connectButtonPressed() throws RemoteException {
        String selectedName = (String)activeUsers.getSelectedItem();
        if(Objects.equals(selectedName, name)){
            JOptionPane.showMessageDialog(mainPanel,"Błąd. Wybrano wysylanie wiadomosci do samego siebie!");
        }
        else if(Objects.equals(null, currentlyConnectedWith)){         //rozmowa moze zostac rozpoczeta tylko jesli klient nie jest w zadnej innej
            if(chatServer.isClientFree(selectedName)==1){
                chatServer.connectChatClient(name,selectedName);
                currentlyConnectedWith = selectedName;
//                System.out.println(currentlyConnectedWith);
                textArea.setText("");
            }
            else{
                JOptionPane.showMessageDialog(mainPanel,"Błąd. Wybrany klient jest aktualnie w rozmowie z kim innym");
            }
        }
        else{
            JOptionPane.showMessageDialog(mainPanel,"Błąd. Proszę najpierw rozlaczyc sie z rozmowy z poprzednim uzytkownikiem");
        }
    }

    public void refreshActiveUsers() throws RemoteException {
        activeUsersArrayList = chatServer.broadcastUsersList();
        activeUsers.removeAllItems();

        for(String s: activeUsersArrayList){
            activeUsers.addItem(s);
        }
    }

    @Override
    public void disconnectClient() {
        currentlyConnectedWith=null;
        textArea.setText("");
        clientFrame.setTitle("Chatting with no one");
    }

    @Override
    public void connectClient(String nameOfClientToConnectWith) {
        currentlyConnectedWith = nameOfClientToConnectWith;
        clientFrame.setTitle("Chatting with: " + nameOfClientToConnectWith);
    }

    @Override
    public int isClientFree(){
        if(currentlyConnectedWith == null)
            return 1;
        else
            return 0;
    }

    public void unregisterButtonPressed() throws RemoteException {
        if(currentlyConnectedWith!=null){
            chatServer.disconnectChatClient(name,currentlyConnectedWith);   //disconnecting with currently connected client
        }

        chatServer.unregisterChatClient(name);
        name = null;
        nameTf.setEditable(true);
        nameTf.setText("");
        activeUsers.removeAllItems();
        JOptionPane.showMessageDialog(mainPanel, "Wyrejestrowano uzytkownika");
    }
}

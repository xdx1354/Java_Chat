package ChatClientPackage;

import ChatServerPackage.ChatServerIF;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Objects;

public class ChatClient extends UnicastRemoteObject implements ChatClientIF, Runnable {

    private final ChatServerIF chatServer;
    private String name = null;
    JTextArea textArea;
    JTextField inputField;
    JTextField nameTf;
    JFrame clientFrame;
    JComboBox <String> activeUsers;

    ArrayList<String> activeUsersArrayList;
    String currentlyConnectedWith;  // Name of client with whom this client is now connected
    JTextArea textAreaCurrentlyChattingWith;

    protected ChatClient(String name, ChatServerIF chatServer) throws RemoteException {
        //this.name = name;
        this.chatServer = chatServer;
        //chatServer.registerChatClient(this);
    }

    @Override
    public void retrieveMessage(String message) throws RemoteException {
        System.out.println(message);
        textArea.append(message +"\n");
    }

    @Override
    public String sendName() {
        return name;
    }

    @Override
    public void run() {

        clientFrame = new JFrame(name + " frame");
        clientFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        clientFrame.setSize(400,400);
        clientFrame.setLayout(new BorderLayout());
        clientFrame.setVisible(true);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder( 10,  10, 10, 10));
        clientFrame.add(mainPanel, BorderLayout.CENTER);


        /*TEXT AREA PANEL */
        JPanel northPanel = new JPanel();
        northPanel.setBorder(BorderFactory.createEmptyBorder( 10,  10, 10, 10));
        mainPanel.add(northPanel,BorderLayout.WEST);
        //COMPONENTS

        //TEXT FIELD - showing with who you are currently chatting
        textAreaCurrentlyChattingWith = new JTextArea("Currently chatting with: " + currentlyConnectedWith );
        northPanel.add(textAreaCurrentlyChattingWith);

        //TEXT AREA - incoming and sent messages
        textArea = new JTextArea("");
        textArea.setBounds(10,30, 200,200);
        textArea.setSize(200,200);
        textArea.setBackground(new Color(108, 148, 147));
        textArea.setHighlighter(null);
        textArea.setEditable(false);
        northPanel.add(textArea);

        //---------------------------------//

        /*MESSAGE SENDING PANEL - TEXT FIELD AND SEND BUTTON*/
        JPanel southPanel = new JPanel();
        southPanel.setBorder(BorderFactory.createEmptyBorder( 10,  10, 10, 10));
        southPanel.setLayout(new GridLayout(1,2));
        mainPanel.add(southPanel,BorderLayout.SOUTH);
        //COMPONENTS
        //INPUT TEXT FIELD - message to send
        inputField = new JTextField( "input tf");
        inputField.setSize(200,50);
        southPanel.add(inputField);

        // SEND BUTTON
        JButton sendBtn = new JButton("SEND");
        sendBtn.setBounds(50,50,100,50);
        southPanel.add(sendBtn);

        //----------------------------------------------------------------//

        /*LIST OF USERS, CONNECT BUTTON, NAME TEXT FIELD, REGISTER BUTTON*/
        JPanel eastPanel = new JPanel();
        eastPanel.setBorder(BorderFactory.createEmptyBorder( 10,  10, 10, 10));
        GridLayout myGridLayout = new GridLayout(6,1);
        eastPanel.setLayout(myGridLayout);
        mainPanel.add(eastPanel,BorderLayout.EAST);
        //COMPONENTS

        //LIST OF USERS - lista aktualnie zalogowanych uzytkowników
        //activeUsers = new JTextArea();
        activeUsers = new JComboBox<String>();
        eastPanel.add(activeUsers);
        try {
            refreshActiveUsers();
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        //CONNECT BUTTON - used to connect with selected client from scrollable list of clients
        JButton connectButton = new JButton("CONNECT");
        eastPanel.add(connectButton);
        connectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                connectButtonPressed();
                //TODO: WYLACZYC TEN PRZYCISK PO KLIKNIECIU REGISTER I WLACZYC PO KLIKNIECIU UNREGISTER
            }
        });

        //DISCONNECT BUTTON - used to disconnect from chatting  with currently selected user
        JButton disconnectButton = new JButton("DISCONNECT");
        eastPanel.add(disconnectButton);
        //TODO: actionListener for disconnecting

        //NAME TEXT FIELD
        nameTf = new JTextField("Type your username");
        eastPanel.add(nameTf);

        //REGISTER BUTTON - used to register current client instance as client with name from nameTf
        JButton  registerButton = new JButton("REGISTER");
        eastPanel.add(registerButton);
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
        JButton unregisterButton = new JButton("UNREGISTER");
        eastPanel.add(unregisterButton);
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



        sendBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    sendButtonPressed();
                } catch (RemoteException ex) {
                    ex.printStackTrace();
                }
            }
        });
    }




        /*  */
    public void sendButtonPressed() throws RemoteException {
        String message;
        if(name==null){
            JOptionPane.showMessageDialog(clientFrame, "By wysylac wiadomosci nalezy najpierw sie zalogowac!");
        }
        else{
            message = inputField.getText();
            inputField.setText("");
            chatServer.broadcastMessage(name + " : " + message);
        }

    }
    public void registerButtonPressed() throws RemoteException {
        // setting current Client as client called nameTf.getText()
        name = nameTf.getText();
        //creating current client reference on server
        chatServer.registerChatClient(this);
        nameTf.setEditable(false);
        JOptionPane.showMessageDialog(clientFrame, "Zarejestrowano uzytkownika");
        refreshActiveUsers();
        textArea.setText(""); // Clearing all old messages, just to be sure

    }

    public void connectButtonPressed(){
        String selectedName = (String)activeUsers.getSelectedItem();
        if(Objects.equals(selectedName, name)){
            JOptionPane.showMessageDialog(clientFrame,"Wybrano wysylanie wiadomosci do samego siebie!");
        }
        if(!Objects.equals(selectedName, currentlyConnectedWith)){
            // if clinet wants to connect to other user then he was already connceted, chat has to be cleared
            textArea.setText("");
        }
        currentlyConnectedWith = selectedName;
        System.out.println(currentlyConnectedWith);
    }

    public void refreshActiveUsers() throws RemoteException {
       activeUsersArrayList = chatServer.broadcastUsersList();
       activeUsers.removeAllItems();

       for(String s: activeUsersArrayList){
            activeUsers.addItem(s);
       }
    }

    public void unregisterButtonPressed() throws RemoteException {
        //TODO: first it should disconnect with currentlyConnectedClient
        chatServer.disconnectChatClient(name);
        name = null;
        nameTf.setEditable(false);
        nameTf.setText("Type your username");
        activeUsers.removeAllItems();

        JOptionPane.showMessageDialog(clientFrame, "Wyrejestrowano uzytkownika");
        //refreshActiveUsers();
    }
}

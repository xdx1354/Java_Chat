package ChatClientPackage;

import ChatServerPackage.ChatServerIF;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Scanner;

public class ChatClient extends UnicastRemoteObject implements ChatClientIF, Runnable {

    private final ChatServerIF chatServer;
    private String name = null;
    JTextArea textArea;
    JTextField inputField;
    JTextField nameTf;
    JFrame clientFrame;
    JTextArea activeUsers;
    ArrayList<String> activeUsersArrayList;
    int currentlyConnectedWith;  // id of client with whom this client is now connected

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
        //
        //String message;

        clientFrame = new JFrame(name + " frame");
        clientFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        clientFrame.setSize(400,400);
        clientFrame.setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder( 10,  10, 10, 10));
        clientFrame.add(mainPanel, BorderLayout.CENTER);


        /*TEXT AREA PANEL */
        JPanel northPanel = new JPanel();
        northPanel.setBorder(BorderFactory.createEmptyBorder( 10,  10, 10, 10));
        //northPanel.setLayout();
        mainPanel.add(northPanel,BorderLayout.WEST);
        //COMPONENTS
        //TEXT AREA - incoming and sent messages
        textArea = new JTextArea("");
        textArea.setBounds(10,30, 200,200);
        textArea.setSize(200,200);
        textArea.setBackground(new Color(108, 148, 147));
        textArea.setHighlighter(null);
        textArea.setEditable(false);
        northPanel.add(textArea);

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



        /*LIST OF USERS, CONNECT BUTTON, NAME TEXT FIELD, REGISTER BUTTON*/
        JPanel eastPanel = new JPanel();
        eastPanel.setBorder(BorderFactory.createEmptyBorder( 10,  10, 10, 10));
        GridLayout myGridLayout = new GridLayout(4,1);
        eastPanel.setLayout(myGridLayout);
        mainPanel.add(eastPanel,BorderLayout.EAST);
        //COMPONENTS
        //LIST OF USERS - lista aktualnie zalogowanych uzytkowników
        activeUsers = new JTextArea();
        eastPanel.add(activeUsers);
        try {
            refreshActiveUsers();
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        //CONNECT BUTTON - used to connect with selected client from scrollable list of clients
        JButton connectButton = new JButton("CONNECT");
        eastPanel.add(connectButton);


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








//        JButton registerBtn = new JButton("REGISTER");
//        mainPanel.add(registerBtn,BorderLayout.EAST);
//
//        JTextField registerTf = new JTextField("Nickname");
//        mainPanel.add(registerTf);


        clientFrame.setVisible(true);


            sendBtn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        buttonPressed();
                    } catch (RemoteException ex) {
                        ex.printStackTrace();
                    }
                }
            });


            while (true){
                try {
                    refreshActiveUsers();
                    Thread.sleep(500);
                } catch (RemoteException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
    }

    public void buttonPressed() throws RemoteException {
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

    }

    public void refreshActiveUsers() throws RemoteException {
       activeUsersArrayList = chatServer.broadcastUsersList();
       activeUsers.setText("");
       for(String s: activeUsersArrayList){
           activeUsers.append(s + "\n");
       }
    }
}

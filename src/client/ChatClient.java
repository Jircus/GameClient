/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import gui.Game;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Jircus
 */
public class ChatClient implements Runnable {
    
    private final Socket socket;
    private final Game game;
    private ObjectOutputStream output;

    public ChatClient(Socket socket, Game game) {
            this.socket = socket;
            this.game = game;
        try {
            output = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException ex) {
            Logger.getLogger(ChatClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void run() {
        try {
            game.enableTextField();
            ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
            while(true) {
                game.receiveMessage(input.readObject().toString());
                System.out.println("Receiving message");
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ChatClient.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException e) {}
    }
    
    /**
     * Sends message
     * @param message 
     */
    public void sendMessage(String message) {
        try {
            output.writeObject(message);
            System.out.println("Sending message");
        } catch (IOException ex) {
            Logger.getLogger(ChatClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
        
}

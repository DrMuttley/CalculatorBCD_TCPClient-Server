/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.polsl.java.lab3.clientController;

import java.io.*;
import java.net.Socket;
import java.util.Properties;

/**
 * Main class of application - controls the course of the program CalculatorBCD
 * on the client site.
 * 
 * @author Łukasz Nowak
 * @version 3.1
 */

public class ClientController {
    
    /**
     * port number
     */
    private final int PORT;
    
    /**
     * field represents the socket waiting for server connections
     */
    private final Socket socket;
    
    /**
     * Constructor creates the socket.
     * 
     * @param host represents host number
     * @param port represents port number
     * @throws IOException when prot is already bind
     */
    
    ClientController(String host, String port) throws IOException {

        this.PORT = Integer.parseInt(port);
        this.socket = new Socket(host, this.PORT);
    }
          
    /**
     * The main application method, starting connection with the server.
     * 
     * @param commandLineArguments represents two binary numbers and sign of 
     * mathematical operation (example: 1001 + 1000)
     */
    
    public static void main(String[] commandLineArguments) {
        
        //creating variable for port number
        String port = new String();
        //creating vaiable for host number
        String host = new String();
        
        Properties properties = new Properties();
        
        //creating flag for information about status of properties file (found
        //or not foud)
        boolean propertiesFileFound = true;
        
        //reading from properties file
        try (FileInputStream in = new FileInputStream(".properties")) {
            properties.load(in);
            port = properties.getProperty("PORT");
            host = properties.getProperty("HOST");
        } catch (IOException e) {
            e.getMessage();
            System.err.println(".properties (can not find file)");
            propertiesFileFound = false;
        } 
        //creating flag for information about correctness of port number
        boolean portAsNumber = true;
        
        //testing correctness of port number
        if (port != null) {
            for (int i = 0; i < port.length(); i++) {
                if (port.charAt(i) < 48 || port.charAt(i) > 57) {
                    portAsNumber = false;
                    break;
                }
            }
        }
        //starting connection with the server
        if (propertiesFileFound && port != null && !port.equals("") && portAsNumber 
                && host != null && !host.equals("")) {

            try {
                ClientController clientController = new ClientController(host, port);

                try {
                    //connection launch
                    SingleConnection singleConection = new SingleConnection(clientController.socket);
                    //connetion realization
                    singleConection.realize(commandLineArguments);
                } catch (IOException e) {
                    System.err.println(e.getMessage());
                } finally {
                    clientController.socket.close();
                }
            } catch (IOException e) {
                e.getMessage();
                System.err.println("Failed attempt connect to server\n"
                        + "Wrong host address or port number in .properties "
                        + "or server unavailable");
            }
        }else if(propertiesFileFound && !portAsNumber){
            System.err.println(".properties (port isn't a number)");
        }else if(propertiesFileFound){
            System.err.println(".properties (can not find host address or port number)");  
        }    
    }
}

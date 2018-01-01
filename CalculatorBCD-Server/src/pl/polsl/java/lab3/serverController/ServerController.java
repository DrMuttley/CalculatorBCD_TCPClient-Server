/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.polsl.java.lab3.serverController;

import java.io.*;
import java.net.*;
import java.util.Properties;
import pl.polsl.java.lab1.model.Model;

/**
 * Main class of application - controls the course of the program CalculatorBCD
 * on the server site.
 *
 * @author Łukasz Nowak
 * @version 3.1
 */

public class ServerController {

    /**
     * port number
     */
    private final int PORT;

    /**
     * field represents the socket waiting for client connections
     */
    private final ServerSocket serverSocket;
    
    /**
     * The constructor of ServerController class, creates the server socket.
     *
     * @param port represents port numer
     * @throws IOException when prot is already bind
     */
    ServerController(String port) throws IOException{
       
        this.PORT = Integer.parseInt(port);
        this.serverSocket = new ServerSocket(PORT);
    }

    /**
     * The main application method, starting the server and starting service.
     *
     * @param args represents command line input data - unused
     */
    
    public static void main(String[] args){

        //creating model instance
        Model model = new Model();
        
        Socket socket = null;
        ServerController serverController = null;
        
        //creating variable for port number
        String port = new String();
        
        Properties properties = new Properties();
        //creating flag for information about status of properties file (found
        //or not foud)
        boolean propertiesFile = true;
        
        //reading from properties file
        try (FileInputStream in = new FileInputStream(".properties")) {
            properties.load(in);
            port = properties.getProperty("PORT");  
        } catch (IOException e) {
            e.getMessage();
            System.err.println(".properties (can not find file)");
            propertiesFile = false;
        }      
        //creating flag for information about correctness of port number
        boolean portCorrect = true;
        
        //testing correctness of port number
        if (port != null) {
            for (int i = 0; i < port.length(); i++) {
                if (port.charAt(i) < 48 || port.charAt(i) > 57) {
                    portCorrect = false;
                    break;
                }
            }
        }
        //server startup
        if(propertiesFile == true && port != null && !port.equals("") && portCorrect == true){

            try {
                serverController = new ServerController(port);

                System.out.println("Server started");

                while (true) {
                    socket = serverController.serverSocket.accept();
                    try {

                        while(true){
                            //service launch
                            SingleService singleService = new SingleService(socket);
                            //service realization
                            singleService.realize(model);
                        }

                    } catch (IOException e) {
                        socket.close();
                        e.getMessage();
                    }
                }
            } catch (IOException e) {
                e.getMessage();
            } finally {
                if (serverController.serverSocket != null) {
                    try {
                        serverController.serverSocket.close();
                    } catch (IOException e) {
                        e.getMessage();
                    }
                }
            }
        //feedback if port number isn't correct
        }else if(propertiesFile == true && port != null){
            System.err.println(".properties (wrong port number)");
        //feedback if port number wasn't found
        }else if(propertiesFile == true && port == null){
            System.err.println(".properties (can not find port number)");
        }
    }
}

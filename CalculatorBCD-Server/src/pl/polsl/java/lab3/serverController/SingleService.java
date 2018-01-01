/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.polsl.java.lab3.serverController;

import java.io.*;
import java.net.Socket;
import java.util.*;
import pl.polsl.java.lab1.model.*;

/**
 * The server class services a single connection.
 *
 * @author Łukasz Nowak
 * @version 3.1
 */
class SingleService {

    /**
     * socket representing connection to the client
     */
    private final Socket socket;
    /**
     * buffered input character stream
     */
    private final BufferedReader input;
    /**
     * Formatted output character stream
     */
    private final PrintWriter output;

    /**
     * The constructor of the SingleService class. Use the socket as a parameter.
     *
     * @param socket representing connection to the client
     * @throws IOException when the unsuccessful attempt to create a stream
     */
    
    public SingleService(Socket socket) throws IOException {
        this.socket = socket;
        output = new PrintWriter(
                new BufferedWriter(
                        new OutputStreamWriter(
                                socket.getOutputStream())), true);
        input = new BufferedReader(
                new InputStreamReader(
                        socket.getInputStream()));
    }

    /**
     * Method realizes single connection with the client.
     * 
     * @param model represents single instance of model object
     */
    
    public void realize(Model model) {

        System.out.println("\nConnected with new client");

        try {
            //sending welcome message
            output.println("Welcome to Java Sever. Press 'enter' key to continue: ");
            //cleaning input stream
            input.readLine();
            //sending information about waiting for command
            output.println("Enter command or type 'HELP' and press 'enter' key: ");

            //creating program control flags
            boolean connectionRunning = true;
            boolean clientConnectionLost = false;
 
            //creating list for data to calculate from the client
            List<String> inputDataList = new LinkedList();

            while (connectionRunning) {
                
                //getting commands from input stream
                String inputDataLine = input.readLine();

                //protection against the null from input (lose connecton)
                if (inputDataLine == null) {
                    clientConnectionLost = true;
                } else {
                    System.out.println("Client sent: " + inputDataLine);
                    //switch for commands from the client
                    switch (inputDataLine.toUpperCase()) {
                        case "DATA":{
                            clientConnectionLost = getData(inputDataList);
                            break;
                        }
                        case "ADD":{
                            addition(model, inputDataList);
                            break;
                        }
                        case "SUB":{
                            subtraction(model, inputDataList);
                            break;
                        }
                        case "HELP": {
                            help();
                            break;
                        }
                        case "QUIT": {
                            connectionRunning = quit();
                            break;
                        }
                        case "INFO": {
                            info();
                            break;
                        }
                        default: {
                            output.println("\n\r(400) Unrecognized command");
                        }
                    }
                    System.out.println("Answer for client sent");

                    if (connectionRunning) {
                        output.println("\n\rEnter command: ");
                    }
                }
                //ending service after lost connection
                if (clientConnectionLost) {
                    System.out.println("Connection with client was lost");
                    connectionRunning = false;
                }
            }
            System.out.println("Single connection closed\n\rWaiting for new client...");
        } catch (IOException e) {
            e.getMessage();
            System.out.println("Connection with client was lost\n\rSingle connection "
                    + "closed\n\rWaiting for new client...");
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.getMessage();
            }
        }
    }
    
    /**
     * Method gets data - numbers in BCD code from the client. 
     * 
     * @param inputDataList represents list for data from the client
     * @return inputDataAsNull inform about status of downloaded data (false - 
     * data loaded, true - data unloaded) 
     */    
    
    private boolean getData(List<String> inputDataList){
        
        //sending feedback information about recognized command
        output.println("\n\r(210) Recognized command - DATA\n\r\n\rProvide data"
                + " in form 'number number':");
        
        //preparing list for new data
        inputDataList.clear();

        //creating variable for status of downloaded data
        boolean inputDataAsNull = false;

        try {
            //getting data from the client
            String inputLine = input.readLine();
            
            //checking data
            if (inputLine != null) {
                //creating variable for single data (single number)
                String singleInputData = new String();
                
                //separating input data into individual numbers
                for (int i = 0; i < inputLine.length(); i++) {
                    if (inputLine.charAt(i) != ' ') {

                        singleInputData += inputLine.charAt(i);

                        if (i == inputLine.length() - 1) {
                            inputDataList.add(singleInputData);
                        }
                    } else if (inputLine.charAt(i) == ' ') {
                        inputDataList.add(singleInputData);
                        singleInputData = "";
                    }
                }
                output.println("\n\r\n\r(211) Data was received");
            } else {
                inputDataAsNull = true;
            }
        } catch (IOException e) {
            inputDataAsNull = true;
            e.getMessage();
        }          
        return inputDataAsNull;
    }

    /**
     * Method adds two numbers in BCD code.
     * 
     * @param model represents instance of Model class
     * @param dataList represents numbers to add
     */
    
    private void addition(Model model, List<String> dataList){
        
        //sending feedback information about recognized command
        output.println("\n\r(220) Recognized command - ADD\n\r");
        
        //creating variable for result of addition
        String result = new String();    
        
        //additing - if enough numbers
        if (dataList.size() >= 2) {
            result = calculationMap(model).get('+').calculate(dataList.get(0), dataList.get(1));
            showResult(result, dataList, '+');
        }else{
            //sending feedback information if not enough numers
            output.println("Too less data, provide correct amount data by DATA "
                    + "command");
        }
    }
    
    /**
     * Method subtracts two numbers in BCD code.
     * 
     * @param model represents instance of Model class
     * @param dataList represents numbers to subtract
     */    
    
    private void subtraction(Model model, List<String> dataList){
      
        //sending feedback information about recognized command
        output.println("\n\r(230) Recognized command - SUB\n\r");
        
        //creating variable for result of subtracts
        String result = new String();
        
        //subtracting - if enough numbers
        if (dataList.size() >= 2) {
            result = calculationMap(model).get('-').calculate(dataList.get(0), dataList.get(1));
            showResult(result, dataList, '-');
        } else {
            //sending feedback information if not enough numers
            output.println("Too less data, provide correct amount data by GDAT "
                    + "command");
        }
    }
    
    /**
     * Method creates calculation map.
     * 
     * @param model represents instance of Model class
     * @return calculationMap with lambda expression of addition and subtraction
     */
    
    private Map<Character, CalculationInterface> calculationMap(Model model) {
        
        //creating calculations map for mathematical operation
        Map<Character, CalculationInterface> calculationMap = new HashMap<>();

        //lambda expression for addition
        calculationMap.put('+', (a, b) -> {

            String additionResult = new String();

            try {
                additionResult = model.addition(a, b);
            } catch (ModelException e) {
                output.println(e.getMessage() + "\n\r");
            }
            return additionResult;
        });
        //lambda expression for subtraction
        calculationMap.put('-', (a, b) -> {

            String substractionResult = new String();

            try {
                substractionResult = model.subtraction(a, b);
            } catch (ModelException e) {
                output.println(e.getMessage() + "\n\r");
            }
            return substractionResult;
        });
        return calculationMap;
    }
        
    /**
     * Method sends to the client result of addition or subtraction.
     * 
     * @param result represents result of addition or subtraction
     * @param dataList represents list with numbers to add or subtract
     * @param sign represents sign of mathematical operation
     */
    
    private void showResult(String result, List<String> dataList, Character sign){
        output.println("First number:   " + dataList.get(0));
        output.println("Sign:           " + sign);
        output.println("Second number:  " + dataList.get(1));
        output.println("RESULT:         " + result);
    }
            
    /**
     * Method sends to the client result of command HELP.
     */

    private void help() {

        output.println("\n\r(240) Recognized command - HELP\n");
        output.println("Commands recognized by server:\n\r"
                + "DATA - input data to server,\n\r"
                + "ADD  - add two numbers,\n\r"
                + "SUB  - subtract two numbers,\n\r"
                + "HELP - show available commands,\n\r"
                + "INFO - information about aplication,\n\r"
                + "QUIT - exit program");
    }

    /**
     * Method sends to the client result of command INFO.
     */
    
    private void info() {

        output.println("\n\r(250) Recognized command - INFO\n");
        output.println("BCD calculator allows you to add and subtract numbers "
                + "in BCD code.\n\rIf you want to make calculation you must first "
                + "enter the data. \n\rEnter the DATA command and confirm by "
                + "enter key. Then enter first \n\rand second number (example: 1001 "
                + "0001) and confirm by enter key. \n\rIf you want to add numbers "
                + "enter ADD command and confirm by eneter \n\rkey. If you want to "
                + "subtract numbers enter SUB command and confirm \n\rby enter key."
                + "If you did everything correctly, the result will be \n\rdisplayed "
                + "on the screen.");
    }
    
    /**
     * Method sends to the client result of command QUIT.
     * 
     * @return false as a command to close the connection with client
     */

    private boolean quit() {

        output.println("\n\r(260) Recognized command - QUIT\n");
        output.println("Connection closed");
        return false;
    }
}

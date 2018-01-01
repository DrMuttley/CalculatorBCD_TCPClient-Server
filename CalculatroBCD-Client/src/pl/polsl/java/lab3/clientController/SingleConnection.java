/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.polsl.java.lab3.clientController;

import java.io.*;
import java.net.Socket;
import java.util.*;
import pl.polsl.java.lab3.view.View;

/**
 * The client class realizes single connection with the server.
 * 
 * @author Łukasz Nowak
 * @version 3.1
 */

public class SingleConnection {

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
     * The constructor of the SingleConnection class. Use the socket as a parameter.
     *
     * @param socket socket representing connection to the server
     * @throws IOException when the unsuccessful attempt to create a stream
     */
    
    SingleConnection(Socket socket) throws IOException {
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
     * Method realizes information exchange between the client and the server on
     * the client site.
     * 
     * @param commandLineArguments represents input data from command line
     */

    public void realize(String[] commandLineArguments) {
        
        //creating list for data from command line
        List <String> dataFromUserList = new LinkedList();

        dataFromUserList.addAll(Arrays.asList(commandLineArguments));

        //creating instance of view
        View view = new View();
        
        Scanner scanner = new Scanner(System.in);
        
        //creating variable for answer code from server
        String answerCodeFromServer = new String();
        //creating flag for information about the connection status with the server
        boolean connectionServerNoLose = true;
        //creating flag for information flow control
        boolean connection = true;
        
        try {
            //cleanig rubbish from input
            input.readLine();
            output.println("");
            
            //preparing possibility to send a command
            clearInput();
            
            //sending first command
            output.println("DATA");
            //creating dataToCalculationMap for data to send
            Map<String, String> dataToCalculationMap = new HashMap<>();
            
            //creating variable for information about status of model exception 
            //(true - thrown, false - not thrown)
            boolean exceptionFromModel = false;
            //creating variable for information which time data form the user 
            //are preparing to send
            boolean beforeSendFirstData = true;

            //loop for information exchange with the server
            while (connection) {
                //checking the response code from the server
                answerCodeFromServer = checkAnswerCode();

                //protection against no response from the server
                if (answerCodeFromServer.equals("No response")) {
                    connectionServerNoLose = false;
                } else {
                    //switch for answers code from server
                    switch (answerCodeFromServer) {
                        case "210": {
                            dataToCalculationMap = prepareDataToSend(dataFromUserList, 
                                    scanner, view, beforeSendFirstData);
                            output.println(dataToCalculationMap.get("dataToSend"));
                            beforeSendFirstData = false;
                            break;
                        }
                        case "211": {//server received data
                            clearInput();
                            output.println(dataToCalculationMap.get("sign"));
                            break;
                        }
                        case "220": {
                            exceptionFromModel = readResult();
                            connection = reactForResult(exceptionFromModel, connection);
                            break;
                        }
                        case "230": {
                            exceptionFromModel = readResult();
                            connection = reactForResult(exceptionFromModel, connection);
                            break;
                        }
                        default: {
                            clearInput();
                            output.println("DATA");
                        }
                    }
                }
                //information about lost connection
                if (!connectionServerNoLose) {
                    System.err.println("Connection with server has been lost"); 
                    //ending work with server
                    connection = false;
                }
            }
        } catch (IOException e) {
            e.getMessage();        
        } finally {
            try {
                //sending information to server about ending of connection
                output.println("QUIT");
                socket.close();
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
        }
    }
    
    /**
     * Method reacts for result (answer for command ADD or SUB) sent by server 
     * (ends connection with the server or sends command DATA).
     * 
     * @param exceptionFromModel represents information about status of model 
     * exception (true - thrown, false - not thrown)
     * @param connection represents information about status of flow control 
     * flag before reading the results form server
     * @return information about status of flow control flag after reading the 
     * results from server
     */
    
    private boolean reactForResult(boolean exceptionFromModel, boolean connection) {
        
        clearInput();
        if (exceptionFromModel) {
            output.println("DATA");
        } else {
            connection = false;
        }
        return connection;
    }

    /**
     * Method gets data (numbers and sign to calculation) from user if it is 
     * necessary (wrong input data from command line) and prepares them to send.
     * 
     * @param dataFromUserList represents parameters from command line
     * @param scanner represents variable for downloading data from the user
     * @param view represents variable form showing information to the user
     * @param beforeSendFirstData represents information which time data form the 
     * user are preparing to send
     * @return dataToCalculationMap with data to calculation
     */
    
    private Map<String, String> prepareDataToSend(List<String> dataFromUserList, 
            Scanner scanner, View view, boolean beforeSendFirstData){
        
        //creating dataToCalculationMap for data to calculation
        Map<String, String> dataToCalculationMap = new HashMap<>();
        //creating flag for information about correct numbers of data provided by the user
        boolean correctNumberOfParameters = false;
        //creating flag for information about status of sign (found / not found)    
        boolean signFound = false;
        
        //protection against to less amount of input data from command line
        if (dataFromUserList.size() == 3 && beforeSendFirstData) {
            //filling data from the user into the dataToCalculationMap
            signFound = fillInDataToCalculationMap(dataToCalculationMap, dataFromUserList);
            
        //if too less data form command line    
        } else {
            
            do {
                //preparing list for new data
                dataFromUserList.clear();
                
                //prompt for user
                view.show("Provide data in form 'number sign numer':");

                String[] dataTableFromUser = scanner.nextLine().split(" ");
                
                //if correct amount of input data from the user
                if (dataTableFromUser.length == 3) {

                    correctNumberOfParameters = true;
                    
                    dataFromUserList.addAll(Arrays.asList(dataTableFromUser));
                    
                    signFound = fillInDataToCalculationMap(dataToCalculationMap, dataFromUserList);
                    
                    if(!signFound){
                        correctNumberOfParameters = false;
                        view.show("\n\rSign wasn' found, try again.");
                    }
                }else{
                    view.show("\n\rWrong number of parameters, try again.");
                }
            //loop until data from the user will be correct    
            } while (!correctNumberOfParameters && !signFound);
        }
        return dataToCalculationMap;
    }
     
    /**
     * Method fills in data to calculation in to the dataToCalculationMap.
     * 
     * @param dataToCalculationMap represents map for data to calculation
     * @param dataFromUserList represents list with data form the user
     * @return information about status of the singFound flag (true - sign found,
     * false - sign not found)
     */
    
    private boolean fillInDataToCalculationMap(Map<String, String> dataToCalculationMap,
            List<String> dataFromUserList){
        
        //creating flag for information about status of sign (found / not found)
        boolean signFound = false;

        //searching sign in data from the user
        for (String singleData : dataFromUserList) {
            if (singleData.equals("+")) {
                dataToCalculationMap.put("sign", "ADD");
                dataFromUserList.remove(singleData);
                signFound = true;
                break;
            } else if (singleData.equals("-")) {
                dataToCalculationMap.put("sign", "SUB");
                dataFromUserList.remove(singleData);
                signFound = true;
                break;
            }
        }
        dataToCalculationMap.put("dataToSend", dataFromUserList.get(0) + " " + dataFromUserList.get(1));

        return signFound;
    }
    
    /**
     * Method cleans input stream form unnecessary information.
     */
    
    private void clearInput(){
        
        try {
            while (!input.readLine().contains("Enter command")) {
                input.readLine();
            }
        } catch (IOException e) {
            e.getMessage();
        } 
    }
    
    /**
     * Method reads answer from server for command ADD or SUB and shows them the user.
     * 
     * @return information about status of model exception (true - thrown, false - not thrown)
     */
    
    private boolean readResult(){
        
        //creating instance of view
        View view = new View();
        //creating variable for information about status of model exception
        boolean exceptionFromModel = false;
        
        try {
            String inputLine = new String();
            
            while (!(inputLine = input.readLine()).contains("RESULT")){
                if(!inputLine.equals("")){
                    view.show(inputLine);
                }
                if(inputLine.contains("Wrong input data")){
                    exceptionFromModel = true;
                }
            }
            
            if(!exceptionFromModel){
                view.show(inputLine);
            }else{
                view.show("\n\rTry again.");
            }
        } catch (IOException e) {
            e.getMessage();
        } 
        return exceptionFromModel;
    }
    
    /**
     * Method checks answer form server for command sent by the client application.
     * 
     * @return answer code from server
     */
    
    private String checkAnswerCode(){
        
        //creating variable for answer code from server
        String answerCodeFromServer = new String();
        //searching response code
        try {
            while (!answerCodeFromServer.contains("(")) {
                answerCodeFromServer = input.readLine();
            }
        } catch (IOException e) {
            e.getMessage();
            //setting variable if there is no response
            answerCodeFromServer = "No response";
        }
        
        if (!answerCodeFromServer.equals("No response")) {
            //extracting the answer code
            answerCodeFromServer = answerCodeFromServer.substring(1, 4);
        }
        return answerCodeFromServer;
    }
}

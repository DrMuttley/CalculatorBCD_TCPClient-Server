/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.polsl.java.lab1.model;

import java.util.LinkedList;
import java.util.List;

/**
 * Class adds and subtracts numbers in the BCD code.
 *
 * @author Łukasz Nowak
 * @version 3.1
 */
public class Model {

    /**
     * Method manages the addition of two numbers in the BCD code.
     *
     * @param firstNumber - first number in BCD code.
     * @param secondNumber - second number in BCD code.
     * @return result - the result of adding two numbers in BCD code.
     * @throws pl.polsl.java.lab1.model.ModelException - exception can be
     * thrown if method receive parameters containing incorrect data.
     */
    
    public String addition(String firstNumber, String secondNumber) throws ModelException {

        //preparing variable for the result
        String result;

        //verifying input numbers for null
        if ("Not null.".equals(verifyNull(firstNumber, secondNumber))) {

            //verifying correctnes input numbers (digits - only 0 or 1, max 
            //value of tetrad 1001, input number must be a multiple of 4)
            verifyCorrectnessInputData(firstNumber);
            verifyCorrectnessInputData(secondNumber);

            //equalizing length of input numbers
            StringBuilder checkLengthFirstNumber = new StringBuilder(firstNumber);
            StringBuilder checkLengthSecondtNumber = new StringBuilder(secondNumber);

            alignLenghtNumbers(checkLengthFirstNumber, checkLengthSecondtNumber);

            firstNumber = checkLengthFirstNumber.toString();
            secondNumber = checkLengthSecondtNumber.toString();
            
            //creting list for capture transfers in tetrads         
            List<Integer> transferList = new LinkedList();

            //creating variable for addition semi result 
            StringBuilder semiResult = new StringBuilder();

            //addition numbers tetrad by tetrad
            int transfer = addTetrads(firstNumber, secondNumber, semiResult, transferList);

            //transfer occurred on the oldes tetrad
            if (transfer == 1) {
                //adding a new, the oldest tetrad to the semi result
                addOldestTetrad(semiResult);
                
                //offseting values of captured transfers on transfer list
                for(int i : transferList){
                    transferList.set(i, transferList.get(i)+4);
                }
            }
            //creating value which informs about amounts of correct
            int endOfCorrecting = 0;

            //creting list for capture transfers on decimal correction   
            List<Integer> transferList_II = new LinkedList();

            do {
                endOfCorrecting = 0;
                
                //searching tetrads with values bigger than 1001 or in which the
                //transfer took place (unknown state)
                for (int i = semiResult.length() - 1; i >= 0;) {
                    if ((semiResult.charAt(i - 3) == '1'
                            && (semiResult.charAt(i - 2) == '1' || semiResult.charAt(i - 1) == '1'))
                            || transferList.contains(i - 3)) {
                        endOfCorrecting++;
                        
                        //performing a decimal correction
                        transfer = decimalCorrectForAddition(i, semiResult, transferList_II);

                        if (transfer == 1) {//adding new, the oldes tetrad
                            addOldestTetrad(semiResult);
                        }
                    }
                    i -= 4;
                }
                //clearing transfer list from previous transfers
                transferList.clear();
                
                //rewriting transfers from last correction
                transferList = transferList_II;

                transferList_II.clear();

            } while (endOfCorrecting != 0);

            //preparing result to return
            result = semiResult.toString();

        } else {//one of input number is a null
            result = verifyNull(firstNumber, secondNumber);
        }
        return result;
    }

    /**
     * Method manages the substraction of two numbers in the BCD code.
     * 
     * @param firstNumber - first number in BCD code.
     * @param secondNumber - second number in BCD code.
     * @return result - the result of substraction two numbers in BCD code.
     * @throws pl.polsl.java.lab1.model.ModelException - exception can be
     * thrown if method receive parameters containing incorrect data.
     */
    
    public String subtraction(String firstNumber, String secondNumber) throws ModelException {

        //preparing variable for the result
        String result;
        
        //verifying input numbers for null
        if ("Not null.".equals(verifyNull(firstNumber, secondNumber))) {

            //verifying correctnes input numbers (digits - only 0 or 1, max 
            //value of tetrad 1001, input number must be a multiple of 4 ;
            verifyCorrectnessInputData(firstNumber);
            verifyCorrectnessInputData(secondNumber);

            //equalizing length of input numbers
            StringBuilder checkLengthFirstNumber = new StringBuilder(firstNumber);
            StringBuilder checkLengthSecondtNumber = new StringBuilder(secondNumber);

            alignLenghtNumbers(checkLengthFirstNumber, checkLengthSecondtNumber);

            firstNumber = checkLengthFirstNumber.toString();
            secondNumber = checkLengthSecondtNumber.toString();

            //converting the second number for U-1
            secondNumber = replaceZeroAndOne(secondNumber);

            //creting list for capture transfers in tetrads  
            List<Integer> transferList = new LinkedList();
            
            //creating variable for subtraction semi result 
            StringBuilder semiResult = new StringBuilder();

            //addition numbers tetrad by tetrad
            int transfer = addTetrads(firstNumber, secondNumber, semiResult, transferList);
       
            //creating a counter which inform about position in semi result
            int counter = 0;
            //creating variable which inform about status of transfer
            boolean transferredFlag = false;

            //transfer occurred on the oldest tetrad - positive result
            if (transfer == 1) {
                
                //adding '1' from transfer from the oldest tetrad to the 
                //youngest tetrad
                addOneToYoungestTetrad(semiResult, transfer);
                
                //reseting transfer value
                transfer = 0;

                //serching tetrads in which there wasn't transfer
                for (int i = semiResult.length() - 1; i >= 0; i--) {

                    for (int j = 0; j < transferList.size(); j++) {
                        if (i == transferList.get(j)) {
                            transferredFlag = true;
                        }
                    }
                    counter++;
                    //detecting successive tetrads
                    if (i % 4 == 0 && counter < 4) {
                        counter = 0;
                        transferredFlag = false;
                    }

                    //making corrections if conditions are met
                    if (counter == 4 && transferredFlag == false) {

                        decimalCorrectForSubtraction(i, semiResult, transfer);
                        
                        //reseting counter
                        counter = 0;
                        //reseting transfer
                        transfer = 0;
                    }
                }
            //transfer not occurred on the oldest tetrad - negative result 
            } else {
                //converting result for U-1
                StringBuilder afterReplace
                        = new StringBuilder(replaceZeroAndOne(semiResult.toString()));

                semiResult = afterReplace;

                //serching tetrads in which there was transfer
                for (int i = semiResult.length() - 1; i >= 0; i--) {

                    for (int j = 0; j < transferList.size(); j++) {
                        if (i == transferList.get(j)) {
                            transferredFlag = true;
                        }
                    }
                    counter++;

                    //making corrections if conditions are met
                    if (counter == 4 && transferredFlag == true) {
                        decimalCorrectForSubtraction(i, semiResult, transfer);
                        
                        //reseting counter
                        counter = 0;
                        //reseting transfer
                        transfer = 0;
                    }
                    transferredFlag = false;
                }
            }
            //preparing result to return
            result = semiResult.toString();
            
        } else {//one of input number is a null
            result = verifyNull(firstNumber, secondNumber);
        }
        return result;
    }

    /**
     * Method equalizes the length of both input numbers, if lengths are
     * different (adds '0' in the oldest tetrads in the shorter number).
     * 
     * @param firstNumber - first number in BCD code.
     * @param secondNumber - second number in BCD code.
     */
    
    private void alignLenghtNumbers(StringBuilder firstNumber, StringBuilder secondNumber) {

        firstNumber = firstNumber.reverse();
        secondNumber = secondNumber.reverse();

        //creating variable to counting the difference between the lengths
        int counterOf_0;
        
        if (firstNumber.length() > secondNumber.length()) {
            counterOf_0 = firstNumber.length() - secondNumber.length();
            for (int i = 0; i < counterOf_0; i++) {
                secondNumber.append(0);
            }
        } else if (firstNumber.length() < secondNumber.length()) {
            counterOf_0 = secondNumber.length() - firstNumber.length();
            for (int i = 0; i < counterOf_0; i++) {
                firstNumber.append(0);
            }
        }
        //reversing numbers to correct order
        firstNumber = firstNumber.reverse();
        secondNumber = secondNumber.reverse();
    }

    /**
     * Method replace in number to correct '0' on '1' and '1' on '0'.
     * 
     * @param semiResult - number to correct
     * @return semiResult - number after replace
     */
    
    private String replaceZeroAndOne(String semiResult) {

        semiResult = semiResult.replace('0', 'x');
        semiResult = semiResult.replace('1', '0');
        semiResult = semiResult.replace('x', '1');

        return semiResult;
    }
    
    /**
     * Method adds first number with second number tetrad by the tetrad.
     * 
     * @param firstNumber - first number in BCD code.
     * @param secondNumber - second number in BCD code.
     * @param semiResult - result of adding first number with second number.
     * @param transferList - list for capture transfers in tetrads.
     * @return transfer - information about the status of the transfer after 
     * addition.
     */

    private int addTetrads(String firstNumber, String secondNumber,
            StringBuilder semiResult, List<Integer> transferList) {

        //creating variable for the transfer status
        int transfer = 0;

        //adding successive tetrads and capturing transfers on the transfer list
        for (int i = firstNumber.length() - 1; i >= 0; i--) {
            if ((firstNumber.charAt(i) != secondNumber.charAt(i)) 
                    && transfer == 0) {
                
                semiResult.append(1);
                
            } else if ((firstNumber.charAt(i) != secondNumber.charAt(i)) 
                    && transfer == 1) {
                
                semiResult.append(0);
                
                if (i % 4 == 0 || i == 0) {
                    transferList.add(i);
                }
            } else if (firstNumber.charAt(i) == 48 && secondNumber.charAt(i) == 48 
                    && transfer == 0) {
                
                semiResult.append(0);
                
            } else if (firstNumber.charAt(i) == 48 && secondNumber.charAt(i) == 48 
                    && transfer == 1) {
                
                semiResult.append(1);
                transfer = 0;
                
            } else if (firstNumber.charAt(i) == 49 && secondNumber.charAt(i) == 49 
                    && transfer == 0) {
                
                semiResult.append(0);
                transfer = 1;
                
                if (i % 4 == 0 || i == 0) {
                    transferList.add(i);
                } 
            } else if (firstNumber.charAt(i) == 49 && secondNumber.charAt(i) == 49 
                    && transfer == 1) {
                
                semiResult.append(1);
                
                if (i % 4 == 0 || i == 0) {
                    transferList.add(i);
                }
            }
        }
        
        //reverse semi result to correct order
        semiResult = semiResult.reverse();

        //creating counter
        int counter = 0;

        //detection of four '1' in one tetrad - if a single tetrads contains 
        //only '1', she must by added to the transfer list
        for (int i = 0; i < semiResult.length(); i++) {
            if (semiResult.charAt(i) == '1') {
                counter++;
            }
            //test after one tetrad
            if ((i + 1) % 4 == 0 && counter < 4) {
                counter = 0;
            }
            
            if (counter == 4) {
                if (transferList.contains(i - 3)) {
                } else {
                    transferList.add(i);
                }
            }
        }
        return transfer;
    }

    /**
     * Method adds a new, the oldest tetrad in to the number to correct.
     * 
     * @param semiResult - number to correct with new tetrad.
     */
    
    private void addOldestTetrad(StringBuilder semiResult) {
        
        //additing new, the oldest tetrad
        semiResult = semiResult.reverse();
        semiResult.append(1);
        semiResult.append(0);
        semiResult.append(0);
        semiResult.append(0);
        semiResult = semiResult.reverse();
    }

    /**
     * Method adds '1' from transfer from the oldest tetrad to the youngest 
     * tetrad.
     * 
     * @param semiResult - number to correct.
     * @param transfer - information about the status of the transfer before 
     * adding '1' to the youngest tetrad.
     * @return transfer - informing about the status of the transfer after
     * adding '1' to the youngest tetrad.
     */
    
    private int addOneToYoungestTetrad(StringBuilder semiResult, int transfer) {

        //loop making corrections
        for (int i = semiResult.length() - 1; i >= 0; i--) {

            if (semiResult.charAt(i) == 48 && transfer == 1) {
                semiResult.setCharAt(i, '1');
                transfer = 0;
            } else if (semiResult.charAt(i) == 48 && transfer == 0) {
                semiResult.setCharAt(i, '0');
            } else if (semiResult.charAt(i) == 49 && transfer == 1) {
                semiResult.setCharAt(i, '0');
            } else if (semiResult.charAt(i) == 49 && transfer == 0) {
                semiResult.setCharAt(i, '1');
            }
        }
        return transfer;
    }
    
    /**
     * Method performs a decimal correction for substraction for selected tetrad.
     * 
     * @param positionInCorrectedNumber - position in the number to correct.
     * @param semiResult - number to correct.
     * @param transfer - information about the status of the transfer.
     */

    private void decimalCorrectForSubtraction(int positionInCorrectedNumber,
            StringBuilder semiResult, int transfer) {

        //creating string with decimal correction
        String decimalCorrect = "1010";

        //shift to the first position in the selected tetrad
        positionInCorrectedNumber += 3;

        //creating a counter for moving in decimal correction loop
        int loopCounter = 3;

        //loop making corrections
        for (int x = positionInCorrectedNumber; x > positionInCorrectedNumber - 4; x--) {
            if (semiResult.charAt(x) != decimalCorrect.charAt(loopCounter)
                    && transfer == 0) {
                
                semiResult.setCharAt(x, '1');
                
            } else if (semiResult.charAt(x) != decimalCorrect.charAt(loopCounter) 
                    && transfer == 1) {
                
                semiResult.setCharAt(x, '0');
                
            } else if (semiResult.charAt(x) == 48 && decimalCorrect.charAt(loopCounter) == 48 
                    && transfer == 0) {
                
                semiResult.setCharAt(x, '0');
                
            } else if (semiResult.charAt(x) == 48 && decimalCorrect.charAt(loopCounter) == 48 
                    && transfer == 1) {
                
                semiResult.setCharAt(x, '1');
                transfer = 0;
                
            } else if (semiResult.charAt(x) == 49 && decimalCorrect.charAt(loopCounter) == 49 
                    && transfer == 0) {
                
                semiResult.setCharAt(x, '0');
                transfer = 1;
                
            } else if (semiResult.charAt(x) == 49 && decimalCorrect.charAt(loopCounter) == 49 
                    && transfer == 1) {
                
                semiResult.setCharAt(x, '1');
            }
            loopCounter--;
        }
    }
    
    /**
     * Method performs a decimal correction for addition for selected tetrad.
     * 
     * @param positionInCorrectedNumber - position in the number to correct.
     * @param semiResult - number to correct.
     * @param transferList - list for capture transfers in tetrads. 
     * @return transfer - information about the status of the transfer.
     */
    
    private int decimalCorrectForAddition(int positionInCorrectedNumber, 
            StringBuilder semiResult, List<Integer> transferList){
        
        //creating string with decimal correction
        String decimalCorrect = "0110";
        
        //creating variable informing about the status of the transfer
        int transfer = 0;
        
        //creating a counter for moving in decimal correction loop
        int counter = 3;
        
        //loop making corrections
        for (int j = positionInCorrectedNumber; j >= 0; j--) {
            if (semiResult.charAt(j) != decimalCorrect.charAt(counter) 
                    && transfer == 0) {
                
                semiResult.setCharAt(j, '1');
                
            } else if (semiResult.charAt(j) != decimalCorrect.charAt(counter) 
                    && transfer == 1) {
                
                semiResult.setCharAt(j, '0');
                transferList.add(positionInCorrectedNumber);
                
            } else if (semiResult.charAt(j) == 48 && decimalCorrect.charAt(counter) == 48 
                    && transfer == 0) {
                
                semiResult.setCharAt(j, '0');
                
            } else if (semiResult.charAt(j) == 48 && decimalCorrect.charAt(counter) == 48 
                    && transfer == 1) {
                
                semiResult.setCharAt(j, '1');
                transfer = 0;
                
            } else if (semiResult.charAt(j) == 49 && decimalCorrect.charAt(counter) == 49 
                    && transfer == 0) {

                semiResult.setCharAt(j, '0');
                transfer = 1;
                transferList.add(positionInCorrectedNumber);

            } else if (semiResult.charAt(j) == 49 && decimalCorrect.charAt(counter) == 49
                    && transfer == 1) {

                semiResult.setCharAt(j, '1');
                transferList.add(positionInCorrectedNumber);
            }
            //protection against decimal correction loop exit
            if (counter > 0) {
                counter--;
            }
        }
        return transfer;
    }
    
    /**
     * Method checks correctnes input number (digits - only 0 or 1, max value 
     * of tetrad: 1001, input number must be a multiple of 4)
     * 
     * @param inputNumber - number in BCD code.
     * @throws ModelException - exception can be thrown if method receive 
     * parameter containing incorrect data.
     */

    private void verifyCorrectnessInputData(String inputNumber) throws ModelException {

        //verifying digits in input number (permitted only 0 or 1)
        for (int i = 0; i < inputNumber.length(); i++) {
            if (inputNumber.charAt(i) != '0' && inputNumber.charAt(i) != '1') {
                throw new ModelException("Wrong input data. The input number "
                        + "contains a digit other than 0 and 1.");
            }
        }
        //verifying divisibility length number by 4 (correctness length of tetrads) 
        if (inputNumber.length() % 4 != 0) {
            throw new ModelException("Wrong input data - the numbers from "
                    + "input must be a multiple of 4.");
        }

        //verifying correctness write in BCD code (max value: 1001)
        for (int i = inputNumber.length() - 1; i >= 0;) {
            if (inputNumber.charAt(i - 3) == '1'
                    && (inputNumber.charAt(i - 2) == '1'
                    || inputNumber.charAt(i - 1) == '1')) {

                throw new ModelException("Wrong input data - the value of one "
                        + "tetrad greater than admissible (1001).");
            }
            i -= 4;
        }
    }

    /**
     * Method checks the numbers for null.
     * 
     * @param numberOne - first number in BCD code.
     * @param numberTwo - second number in BCD code.
     * @return result - statement "Not null." if both numbers aren't null or 
     * statement that one of the numbers is a null.
     */
    
    private String verifyNull(String numberOne, String numberTwo) {

        String result = "Not null.";

        if (numberOne == null || numberTwo == null) {
            result = "Wrong input data - one of numbers is a null.";
        }
        return result;
    }
}
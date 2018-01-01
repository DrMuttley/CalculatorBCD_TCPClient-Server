/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.polsl.java.lab2.testModel;

import static org.junit.Assert.*;
import org.junit.*;
import pl.polsl.java.lab1.model.*;

/**
 * Class tests all public method from Model class.
 * 
 * @author Łukasz Nowak
 * @version 3.1
 */
public class TestModel {
         
    Model model = new Model();
    String result;
    
    /**
     * Method testAddition tests addition method from class Model.
     */
    
    @Test
    public void testAddition() {
        
        try {          
            //addition 3418 + 659 (expected result: 4077) - testing the 
            //correctness of the result
            result = model.addition("0011010000011000", "0000011001011001");
            assertEquals("Result of compare: ", "0100000001110111", result);

            //addition 0 + 0 (expected result: 0) - testing the correctness 
            //of the result
            result = model.addition("0000", "0000");
            assertEquals("Result of compare: ", "0000", result);

            //addition 6 + 1 (expected result: 7) - testing the correctness of 
            //the result
            result = model.addition("0110", "0001");
            assertEquals("Result of compare: ", "0111", result);

            //addition 6 + 4 (expected result: 10) - testing the addition of a 
            //new, older tetrad in result
            result = model.addition("0110", "0100");
            assertEquals("Result of compare: ", "00010000", result);
            
            //addition 9 + 9 (expected result: 18) - testing the addition of a 
            //two the same numbers
            result = model.addition("1001", "1001");
            assertEquals("Result of compare: ", "00011000", result);
            
            //addition 61 + 88 (expected result: 149) - testing the addition of 
            //a new, older tetrad in result
            result = model.addition("01100001", "10001000");
            assertEquals("Result of compare: ", "000101001001", result);

            //addition 61 + 3 (expected result: 64) - testing alignment input
            //numbers (second number)
            result = model.addition("01100001", "0011");
            assertEquals("Result of compare: ", "01100100", result);

            //addition 3 + 61 (expected result: 64) - testing alignment input
            //numbers (first number)
            result = model.addition("0011", "01100001");
            assertEquals("Result of compare: ", "01100100", result);
             
            //testing null as a first number
            result = model.addition(null, "0000011001011001");
            assertEquals("Result of compare: ", "Wrong input data - one of "
                    + "numbers is a null.", result);
            
            //testing null as a second number
            result = model.addition("0011010000011000", null);
            assertEquals("Result of compare: ", "Wrong input data - one of "
                    + "numbers is a null.", result);
            
            //testing null as a first number and second number
            result = model.addition(null, null);
            assertEquals("Result of compare: ", "Wrong input data - one of "
                    + "numbers is a null.", result);
            
        } catch (ModelException e) {
            e.getMessage();
        }
        
        try {
            //testing receive too short first number
            result = model.addition("001", "0000");
            fail("Exception in addition should be create, because of too short "
                    + "first number.");

        } catch (ModelException e) {
            assertEquals("Result of compare: ", "Wrong input data - the numbers "
                    + "from input must be a multiple of 4.", 
                    e.getMessage());
        }
        
        try {
            //testing receive too short second number
            result = model.addition("0010", "00");
            fail("Exception in addition should be create, because of too short "
                    + "second number.");

        } catch (ModelException e) {
            assertEquals("Result of compare: ", "Wrong input data - the numbers "
                    + "from input must be a multiple of 4.",
                    e.getMessage());
        }
        
        try {
            //testing receive too short both numbers
            result = model.addition("001", "000");
            fail("Exception in addition should be create, because of too short "
                    + "both numbers.");

        } catch (ModelException e) {
            assertEquals("Result of compare: ", "Wrong input data - the numbers "
                    + "from input must be a multiple of 4.",
                    e.getMessage());
        }
        
        try {
            //testing correctness digits in first number
            result = model.addition("0020", "0000");
            fail("Exception in addition should be create, because first number "
                    + "contains digit other than 0 and 1.");

        } catch (ModelException e) {
            assertEquals("Result of compare: ", "Wrong input data. The input "
                    + "number contains a digit other than 0 and 1.",
                    e.getMessage());
        }
        
        try {
            //testing correctness digits in second number
            result = model.addition("0010", "0008");
            fail("Exception in addition should be create, because second number "
                    + "contains digit other than 0 and 1.");

        } catch (ModelException e) {
            assertEquals("Result of compare: ", "Wrong input data. The input "
                    + "number contains a digit other than 0 and 1.",
                    e.getMessage());
        }
        
        try {
            //testing correctness digits in both numbers
            result = model.addition("0ff0", "00z0");
            fail("Exception in addition should be create, because both numbers "
                    + "contains digit other than 0 and 1.");

        } catch (ModelException e) {
            assertEquals("Result of compare: ", "Wrong input data. The input "
                    + "number contains a digit other than 0 and 1.",
                    e.getMessage());
        }
        
        try {
            //testing correctness BCD code writing - testing tetrads greater 
            //than acceptable (1001) - first number
            result = model.addition("1010", "1000");
            fail("Exception in addition should be create, because first number "
                    + "is greater than acceptable (1001).");

        } catch (ModelException e) {
            assertEquals("Result of compare: ", "Wrong input data - the value "
                    + "of one tetrad greater than admissible (1001).",
                    e.getMessage());
        }
        
        try {
            //testing correctness BCD code writing - testing tetrads greater 
            //than acceptable (1001) - second number
            result = model.addition("1000", "1100");
            fail("Exception in addition should be create, because second number "
                    + "is greater than acceptable (1001).");

        } catch (ModelException e) {
            assertEquals("Result of compare: ", "Wrong input data - the value "
                    + "of one tetrad greater than admissible (1001).",
                    e.getMessage());
        }
        
        try {
            //testing correctness BCD code writing - testing tetrads greater 
            //than acceptable (1001) - both numbers
            result = model.addition("1010", "1100");
            fail("Exception in addition should be create, because both numbers "
                    + "are greater than acceptable (1001).");

        } catch (ModelException e) {
            assertEquals("Result of compare: ", "Wrong input data - the value "
                    + "of one tetrad greater than admissible (1001).",
                    e.getMessage());
        }
    }
    
    /**
     * Method testSubtraction tests substraction method from class Model.
     */
    
    @Test
    public void testSubtraction() {
        
        try {
            //subtraction 3418 - 659 (expected result: 2759) - testing the 
            //correctness of the result
            result = model.subtraction("0011010000011000", "0000011001011001");
            assertEquals("Result of compare: ", "0010011101011001", result);

            //subtraction 659 - 3418 (expected result: -2759) - testing the 
            //correctness of the result
            result = model.subtraction("0000011001011001", "0011010000011000");
            assertEquals("Result of compare: ", "0010011101011001", result);
            
            //subtraction 0 - 0 (expected result: 0) - testing the correctness 
            //of the result
            result = model.subtraction("0000", "0000");
            assertEquals("Result of compare: ", "0000", result);
            
            //subtraction 63 - 62 (expected result: 1) - testing the correctness 
            //of the result
            result = model.subtraction("01100011", "01100010");
            assertEquals("Result of compare: ", "00000001", result);

            //subtraction 8 - 14 (expected result: -6) - testing alignment input
            //numbers (first number)
            result = model.subtraction("1000", "00010100");
            assertEquals("Result of compare: ", "00000110", result);
            
            //subtraction 15 - 5 (expected result: 10) - testing alignment input
            //numbers (second number)
            result = model.subtraction("00010101", "0101");
            assertEquals("Result of compare: ", "00010000", result);

            //testing null as a first number
            result = model.subtraction(null, "0000011001011001");
            assertEquals("Result of compare: ", "Wrong input data - one of "
                    + "numbers is a null.", result);
            
            //testing null as a second number
            result = model.subtraction("0011010000011000", null);
            assertEquals("Result of compare: ", "Wrong input data - one of "
                    + "numbers is a null.", result);
            
            //testing null as a first number and second number
            result = model.subtraction(null, null);
            assertEquals("Result of compare: ", "Wrong input data - one of "
                    + "numbers is a null.", result);
        
        } catch (ModelException e) {
            e.getMessage();
        }   
        
        try {
            //testing receive too short first number
            result = model.subtraction("001", "0000");
            fail("Exception in subtraction should be create, because of too "
                    + "short first number.");

        } catch (ModelException e) {
            assertEquals("Result of compare: ", "Wrong input data - the numbers "
                    + "from input must be a multiple of 4.",
                    e.getMessage());
        }
        
        try {
            //testing receive too short second number
            result = model.subtraction("0001", "000");
            fail("Exception in subtraction should be create, because of too "
                    + "short second number.");

        } catch (ModelException e) {
            assertEquals("Result of compare: ", "Wrong input data - the numbers "
                    + "from input must be a multiple of 4.",
                    e.getMessage());
        }
        
        try {
            //testing receive too short both numbers
            result = model.subtraction("00100", "00");
            fail("Exception in subtraction should be create, because of too "
                    + "short both numbers.");

        } catch (ModelException e) {
            assertEquals("Result of compare: ", "Wrong input data - the numbers "
                    + "from input must be a multiple of 4.",
                    e.getMessage());
        }
        
        try {
            //testing correctness digits in fist number
            result = model.subtraction("0700", "0100");
            fail("Exception in subtraction should be create, because first "
                    + "number contains digit other than 0 and 1.");

        } catch (ModelException e) {
            assertEquals("Result of compare: ", "Wrong input data. The input "
                    + "number contains a digit other than 0 and 1.",
                    e.getMessage());
        }
        
        try {
            //testing correctness digits in second
            result = model.subtraction("0010", "0200");
            fail("Exception in subtraction should be create, because second "
                    + "number contains digit other than 0 and 1.");

        } catch (ModelException e) {
            assertEquals("Result of compare: ", "Wrong input data. The input "
                    + "number contains a digit other than 0 and 1.",
                    e.getMessage());
        }
        
        try {
            //testing correctness digits in both numbers
            result = model.subtraction("0x10", "000j");
            fail("Exception in subtraction should be create, because both "
                    + "numbers contains digit other than 0 and 1.");

        } catch (ModelException e) {
            assertEquals("Result of compare: ", "Wrong input data. The input "
                    + "number contains a digit other than 0 and 1.",
                    e.getMessage());
        }
        
        try {
            //testing correctness BCD code writing - testing tetrads greater 
            //than acceptable (1001) - first number
            result = model.subtraction("1100", "1000");
            fail("Exception in subtraction should be create, because first "
                    + "number is greater than acceptable (1001).");

        } catch (ModelException e) {
            assertEquals("Result of compare: ", "Wrong input data - the value "
                    + "of one tetrad greater than admissible (1001).",
                    e.getMessage());
        }
        
        try {
            //testing correctness BCD code writing - testing tetrads greater 
            //than acceptable (1001) - second number
            result = model.subtraction("1000", "1100");
            fail("Exception in subtraction should be create, because second "
                    + "number is greater than acceptable (1001).");

        } catch (ModelException e) {
            assertEquals("Result of compare: ", "Wrong input data - the value "
                    + "of one tetrad greater than admissible (1001).",
                    e.getMessage());
        }
        
        try {
            //testing correctness BCD code writing - testing tetrads greater 
            //than acceptable (1001) - both number
            result = model.subtraction("1010", "1010");
            fail("Exception in subtraction should be create, because both "
                    + "numbers are greater than acceptable (1001).");

        } catch (ModelException e) {
            assertEquals("Result of compare: ", "Wrong input data - the value "
                    + "of one tetrad greater than admissible (1001).",
                    e.getMessage());
        }
     }
}

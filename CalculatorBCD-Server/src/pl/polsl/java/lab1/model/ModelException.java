/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.polsl.java.lab1.model;

/**
 * The exception class that is generated when methods addition or subtraction 
 * receive wrong input data: 
 * - input number (string type) contains digits other than 0 and 1
 * - input number contains tetrad greater than permitted (max value: 1001)
 * - input number isn't a multiple of 4
 * 
 * @author Łukasz Nowak
 * @version 3.1
 */
public class ModelException extends Exception {
    
    /** Constructor without parametric */
    public ModelException() {
    }

    /** Constructor
     *	@param message display message
     */
    public ModelException(String message) {
        super(message);
    }    
}

package com.assem;

import java.io.File;
import java.util.Formatter;
import java.util.Scanner;

/**
 * Created by efetsko on 5/1/2016.
 */
public class Utils {

    //takes a decimal value and the amount of digits desired, then it converts the decimal value to a hexadecimal
    //value of that size
    public static String toHex(int dec, int digits) {
        String hex = Integer.toHexString(dec);
        while (hex.length() < digits)
            hex = "0" + hex;

        return hex;
    }

    //creates a new scanner for the given file and returns it
    public static Scanner openFile(String fileName){
        try {
            Scanner s = new Scanner(new File(fileName));
            return s;
        }
        catch (Exception e) {
            System.out.println("could not find this file");
        }
        return null;
    }

    //closes the designated scanner/file
    public static void closeFile(Scanner scan) {
        scan.close();
    }

    //Writefile functions

    //takes a file name and return a formatter for that file.
    public static Formatter makeFile(String s) {
        try {
            Formatter f = new Formatter(s);
            return f;
        }
        catch(Exception e) {
            System.out.println("Failed to create file.");
        }
        return null;
    }

    //close the formatter/file
    public static void closeFormat(Formatter f) {
        f.close();
    }




}

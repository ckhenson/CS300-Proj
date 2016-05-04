package com.assem;

import java.io.File;
import java.util.Formatter;
import java.util.Scanner;

/**
 * Created by efetsko on 5/1/2016.
 */
public class Utils {

    public static String toHex(int dec, int digits) {
        String hex = Integer.toHexString(dec);
        while (hex.length() < digits)
            hex = "0" + hex;

        return hex;
    }

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

    public static void closeFile(Scanner scan) {
        scan.close();
    }

    //Writefile functions

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

    public static void closeFormat(Formatter f) {
        f.close();
    }




}

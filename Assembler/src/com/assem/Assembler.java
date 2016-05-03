package com.assem;

import java.io.File;
import java.util.Formatter;
import java.util.Scanner;

/**
 * Created by efetsko on 3/15/2016.
 */
public class Assembler {

    private Scanner scan;
    private Formatter inter;
    private int LOCCTR = 0;
    private int machineARCH = 0;
    private static String fileName = "assemblyCode/control_section2.txt";


    public void passOne() {

        //open assembly the file
        scan = Utils.openFile(fileName);
        //make the intermediate file that will be passed onto passTwo
        inter = Utils.makeFile("intermediateFile.txt");

        //Loop as long as there's more data in the file
        while(scan.hasNext()) {

            //Local variables
            String line = scan.nextLine();
            String label = null;
            String opcode = null;
            String parameters = null;
            String address = null;


            //Setup the values for the current line's label, opcode, and parameters if it exists
            if (!line.trim().isEmpty() && !line.startsWith(".")) {

                //First setup the values of this line's label, opcode, and parameters
                String[] values = readLine(line);
                label = values[0];
                opcode = values[1];
                parameters = values[2];

                //write this line to the intermediate file with the current location converted to hexadecimal
                //as the address
                if (opcode.equals("CSECT"))
                    address = toHex(0);
                else
                    address = toHex(LOCCTR);
                writeLine(address, label, opcode, parameters);

                //First we have to handle the labels

                if (label != null) {
                    if (Tables.SYMTAB.get(label) == null)
                        Tables.SYMTAB.put(label, address);
                    else
                        System.out.println("Error! Repeat label " + label + " found in file.");
                }

                if (Tables.ARCH.get(opcode)!= null){
                    machineARCH = 1;
                }

                //Next we need use the opcode to increment the LOCCTR
                if (opcode != null) {
                    incrementCounter(opcode, parameters);
                }

            }

        }


        //close the files
        Utils.closeFile(scan);
        Utils.closeFormat(inter);

        //start pass two
        PassTwo.passTwo("intermediateFile.txt", machineARCH);

    }


    private String[] readLine(String line) {

        String[] values = new String[3];
        //Checks to see if the line starts with a tab
        if (line.startsWith("\u0009")) {
            line = line.trim();
            String[] s = line.split("\u0009");
            values[0] = null;
            values[1] = s[0];
            if (s.length > 1)
                values[2] = s[1];
        }
        else {
            line = line.trim();
            String[] s = line.split("\u0009");
            values[0] = s[0];
            values[1] = s[1];
            if (s.length > 2)
                values[2] = s[2];
        }
        return values;
    }


    private void incrementCounter(String op, String params) {

        if (op.equals("START")) {
            LOCCTR = Integer.parseInt(params);
            System.out.print("Starting location is ");
            System.out.println(LOCCTR);
        }
        else if (Tables.OPTAB.get(op) != null) {
            if (op.equals("CLEAR") || op.equals("COMPR") || op.equals("TIXR"))
                LOCCTR += 2;
            else
                LOCCTR += 3;
        }
        else if (op.startsWith("+")) {
            LOCCTR += 4;
        }
        else if (op.equals("WORD")) {
            LOCCTR += 3;
        }
        else if (op.equals("RESW")) {
            //assign address

            //push the LOCCTR forward by 3 bytes per the amount of words.
            LOCCTR = LOCCTR + (Integer.parseInt(params) * 3);
        }
        else if (op.equals("RESB")) {
            //assign address

            //push the LOCCTR forward by 1 per byte reserved
            LOCCTR += Integer.parseInt(params);
        }
        else if (op.equals("BYTE")) {
            //if X'EF' every two digits in quotes will be a byte
            //if C'F' every digit will be a byte
            if (params.startsWith("X")) {
                int digits = params.substring(2, op.length()).length();
                //System.out.println(digits);
                LOCCTR += digits/2;
            }
            else if (params.startsWith("C")) {
                int digits = params.substring(2, op.length()).length();
                //System.out.println(digits);
                LOCCTR += digits;
            }

        }
        else if (op.equals("CSECT")) {
            LOCCTR = 0;
        }
        else if (op.equals("BASE") || op.equals("LTORG") || op.equals("EXTDEF") || op.equals("EXTREF")) {
            //don't increment the LOCCTR
        }

    }

    //Takes a decimal number and converts it to a 4 digit hexadecimal value
    public String toHex(int dec) {
        String hex = Integer.toHexString(dec);

        while (hex.length() < 4)
            hex = "0" + hex;

        return hex;
    }

    public void writeLine(String address, String label, String opcode, String params) {
        String a;
        String l;
        String o;
        String p;

        //Assigns the variable to an empty string if the variable is null.
        if (address == null)
            a = "";
        else
            a = address;
        if (label == null)
            l = "";
        else
            l = label;
        if (opcode == null)
            o = "";
        else
            o = opcode;
        if (params == null)
            p = "";
        else
            p = params;

        if (opcode.equals("BASE") || opcode.equals("LTORG") || opcode.equals("EXTDEF") || opcode.equals("EXTREF")) {
            a = "";
        }

        inter.format("%s\u0009%s\u0009%s\u0009%s%n", a, l, o, p);
    }

}
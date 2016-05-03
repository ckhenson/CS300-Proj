package com.assem;

import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.Tab;

import javax.rmi.CORBA.Util;
import java.io.File;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Formatter;
import java.util.Scanner;

/**
 * Created by efetsko on 3/28/2016.
 */
public class PassTwo {

    private static Scanner scan;
    private static Formatter form;
    private static final int SIC = 0;
    private static final int IMMEDIATE = 1;
    private static final int INDIRECT = 2;
    private static final int SIMPLE = 3;
    private static int defaultAddressMode;

    //Initialize line variables
    private static String line;
    private static String pcLine = "";

    private static String address;
    private static String label;
    private static String opcode;
    private static String parameters;
    private static String[] operands;
    private static String objCode;
    private static String PC;
    private static int addressingMode;

    //xbpe
    private static int x = 0;
    private static int b = 0;
    private static int p;
    private static int e = 0;
    private static int xbpe;

    //The decimal and hexadecimal versions of the object code digits
    private static int decimalOP;
    private static String hexOP = "";
    private static int decimalDisp;
    private static String hexDisp = "";


    public static void passTwo(String fileName, int machineArch) {

        //Sets up a scanner to read from the intermediate file.
        scan = Utils.openFile("intermediateFile.txt");
        //Set up formatter to make the object code file
        form = Utils.makeFile("objectCode.txt");

        if (machineArch == 0)
            defaultAddressMode = 0;
        else
            defaultAddressMode = 3;


        while(scan.hasNext()) {
            //Initialize variables for new line
            x = 0;
            b = 0;
            e = 0;
            addressingMode = defaultAddressMode;
            operands = null;
            hexOP = "";
            hexDisp = "";
            String mnemonic;


            //To make things easier, we can assume it is p = 1 if it's SICXE
            //If it turns out to be base relative or extended format, we will change p back to 0 later.
            if (machineArch == 1)
                p = 1;
            else
                p = 0;

            //Proceed to the next line until you find a non-empty line
            line = scan.nextLine();
            while (line.trim().isEmpty())
                line = scan.nextLine();

            //Setup the values for the current line's label, opcode, and parameters if it exists
            if (!line.trim().isEmpty()) {
                //First setup the values of this line's label, opcode, and parameters
                String[] values = readLine(line);
                PC = readLine(pcLine)[0];
                address = values[0];
                label = values[1];
                opcode = values[2];
                mnemonic = values[2];
                parameters = values[3];

                //Get the PC for the opcode
                if (opcode != null && address != null)
                    PC = getPC(opcode, parameters, address);

                //Separate the parameters into individual operands and setup the addressing mode.
                getOperands();

                //Prep the front of the object code.  This will be the opcode + the addressing mode
                prepFront();

                //Calculate the disp for PC addressing
                if (parameters != null && e == 0 && addressingMode != IMMEDIATE) {
                    if (Tables.SYMTAB.get(parameters) != null) {
                        hexDisp = calculateDisp(PC, Tables.SYMTAB.get(parameters));
                    }
                    else if (operands != null && addressingMode != IMMEDIATE)
                        hexDisp = calculateOperands();
                }

                //Get the third bit of the object code.  This is xbpe.
                xbpe = x*8 + b*4 + p*2 + e;
                String mid;
                if (mnemonic.equals("CLEAR"))
                    mid = Tables.REGISTERNUMS.get(parameters);
                else
                    mid = toHex(xbpe, 1);

                //set the object code
                if (Tables.NOOBJ.get(mnemonic) != null)
                    objCode = null;
                else
                    objCode = constructCode(hexOP, mid, hexDisp, mnemonic);

                writeLine(address, label, opcode, parameters, objCode);


            }
        }
    Utils.closeFile(scan);
        Utils.closeFormat(form);
    }

    private static String[] separateOperands(String params) {
        String[] s;
        if (params.contains("-"))
            s = params.split("(?=[-.])|(?<=[-.])");
        else if (params.contains("+"))
            s = params.split("(?=[+.])|(?<=[+.])");
        else
            s = params.split(",");
        for (int i = 0; i < s.length; i++) {
            s[i] = s[i].trim();
        }
        return s;
    }

    private static String[] symbolToAddress(String[] operands) {
        for (int i = 0; i < operands.length; i++) {
            if (Tables.SYMTAB.get(operands[i]) != null)
                operands[i] = Tables.SYMTAB.get(operands[i]);
        }
        return operands;

    }

    private static String[] readLine(String line) {

        String[] vals = new String[4];
        String[] s = line.split("\u0009");

        for (int i = 0; i < s.length; i++) {
            if (s[i].equals(""))
                s[i] = null;
            vals[i] = s[i];
        }
        if (s.length < 4) {
            vals[3] = null;
        }
        return vals;
    }

    private static String toHex(int dec, int digits) {
        String hex = Integer.toHexString(dec);
        while (hex.length() < digits)
            hex = "0" + hex;

        return hex;

    }

    public static void writeLine(String address, String label, String opcode, String params, String objCode) {
        String a;
        String l;
        String o;
        String p;
        String c;

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
        if (objCode == null)
            c = "";
        else
            c = objCode;

        form.format("%s\u0009%s\u0009%s\u0009%s\u0009%s%n", a, l, o, p, c);
    }

    private static String constructCode(String lead, String mid, String end, String mnemonic) {
        String code = "";
        if (Tables.FORMAT.get(mnemonic) == null) {
            code = lead + mid + end;
        }
        else if(Tables.FORMAT.get(mnemonic).equals("2")) {
            code = lead + mid;
        }
        else if(Tables.FORMAT.get(mnemonic).equals("1")) {
            code = lead;
        }
        return code;

    }

    private static String calculateDisp(String current, String target) {
        String disp;
        int d;

        int c = Integer.parseInt(current, 16);
        int t = Integer.parseInt(target, 16);
        if (c > t)
            d = 4096 - (c - t);
        else
            d = t - c;
        disp = toHex(d, 3);

        return disp;
    }

    private static String getPC(String op, String params, String address) {

        int pcInt = 0;
        int addInt = Integer.parseInt(address, 16);

        if (op.equals("START")) {
            pcInt = Integer.parseInt(params);
        }
        else if (Tables.OPTAB.get(op) != null) {
            if (op.equals("CLEAR") || op.equals("COMPR") || op.equals("TIXR"))
                pcInt += 2;
            else
                pcInt += 3;
        }
        else if (op.startsWith("+")) {
            pcInt += 4;
        }
        else if (op.equals("WORD")) {
            pcInt += 3;
        }
        else if (op.equals("RESW")) {
            //assign address

            //push the LOCCTR forward by 3 bytes per the amount of words.
            pcInt = pcInt + (Integer.parseInt(params) * 3);
        }
        else if (op.equals("RESB")) {
            //assign address

            //push the LOCCTR forward by 1 per byte reserved
            pcInt += Integer.parseInt(params);
        }
        else if (op.equals("BYTE")) {
            //if X'EF' every two digits in quotes will be a byte
            //if C'F' every digit will be a byte
            if (params.startsWith("X")) {
                int digits = params.substring(2, op.length()).length();
                pcInt += digits/2;
            }
            else if (params.startsWith("C")) {
                int digits = params.substring(2, op.length()).length();
                pcInt += digits;
            }

        }
        else if (op.equals("CSECT")) {
            pcInt = 0;
        }
        else if (op.equals("BASE") || op.equals("LTORG") || op.equals("EXTDEF") || op.equals("EXTREF")) {
            //don't increment the LOCCTR
        }

        pcInt = pcInt + addInt;
        return toHex(pcInt, 4);

    }

    private static void prepFront() {
        if (opcode != null) {
            if (opcode.startsWith("+")) {
                e = 1;
                p = 0;
                b = 0;
                opcode = opcode.substring(1);
                hexDisp = "0" + Tables.SYMTAB.get(parameters);
            }
            //B4
            if (opcode.equals("CLEAR")) {
                opcode = Tables.OPTAB.get(opcode);
                decimalOP = Integer.parseInt(opcode, 16);
                hexOP = toHex(decimalOP, 2);
            }
            else if (Tables.OPTAB.get(opcode) != null) {
                opcode = Tables.OPTAB.get(opcode);
                decimalOP = Integer.parseInt(opcode, 16);
                decimalOP += addressingMode;
                hexOP = toHex(decimalOP, 2);
            }
            else {
                //opcode = Tables.MACROTAB.get(opcode);
            }
        }

    }

    private static void getOperands() {
        if (parameters != null) {
            if(parameters.startsWith("#")) {
                addressingMode = IMMEDIATE;
                parameters = parameters.substring(1);
                decimalDisp = Integer.parseInt(parameters);
                hexDisp = toHex(decimalDisp, 3);
                p = 0;
            }
            else if(parameters.startsWith("@")) {
                addressingMode = INDIRECT;
                parameters = parameters.substring(1);
            }
            operands = separateOperands(parameters);
            operands = symbolToAddress(operands);
        }
    }

    private static String calculateOperands() {
        int result = -1;

        for (int i = 0; i < operands.length; i++) {
            if (Tables.SYMTAB.get(operands[i]) != null)
                operands[i] = Tables.SYMTAB.get(operands[i]);
        }
        if (operands.length >= 3) {
            if (operands[1].equals("-")) {
                result = Integer.parseInt(operands[0], 16) - Integer.parseInt(operands[0], 16);
            }
            else if (operands[1].equals("+")) {
                result = Integer.parseInt(operands[0], 16) + Integer.parseInt(operands[0], 16);
            }
        }
        return toHex(result, 4);

    }


}
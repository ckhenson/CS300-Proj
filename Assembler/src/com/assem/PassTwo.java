package com.assem;

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
    private static String targetAddress;
    private static String label;
    private static String opcode;
    private static String parameters;
    private static String[] operands;
    private static String objCode;
    private static String PC;
    private static String BUFFER = "0000";
    private static int addressingMode;

    //xbpe
    private static int x = 0;
    private static int b = 0;
    private static int p;
    private static int e = 0;
    private static int xbpe;

    //
    private static String startAddress = "0000";

    //The decimal and hexadecimal versions of the object code digits
    private static int decimalOP;
    private static String hexOP = "";
    private static int decimalDisp;
    private static String hexDisp = "";


    public static void passTwo(String fileName, int machineArch, String SA) {

        //Sets up a scanner to read from the intermediate file.
        scan = Utils.openFile("intermediateFile.txt");
        //Set up formatter to make the object code file
        form = Utils.makeFile("objectCode.txt");

        //Set the start address
        startAddress = SA;

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

                //get target address
                targetAddress = getTargetAddress();

                //Calculate the disp for PC addressing
                if (Tables.NO_OBJ.get(mnemonic) == null) {
                    if (parameters != null && e == 0 && addressingMode != IMMEDIATE) {
                        if (Tables.SYMTAB.get(parameters) != null) {
                            hexDisp = calculateDisp(PC, targetAddress);
                        } else if (operands != null && addressingMode != IMMEDIATE)
                            hexDisp = calculateOperands();
                    }
                    else if (parameters != null && e == 1) {
                        hexDisp = "0" + calculateDisp(PC, targetAddress);
                    }
                }

                //Check the op to see if there are any special cases
                opCheck(mnemonic);

                //Get the third bit of the object code.  This is xbpe.
                xbpe = x*8 + b*4 + p*2 + e;
                String mid;
                if (mnemonic.equals("CLEAR"))
                    mid = Tables.REGISTERNUMS.get(parameters);
                else if (mnemonic.equals("TIXR"))
                    mid = Tables.REGISTERNUMS.get(parameters);
                else
                    mid = Utils.toHex(xbpe, 1);

                //set the object code
                if (mnemonic.equals("BYTE"))
                    objCode = constructVar(mnemonic);
                else if (Tables.NO_OBJ.get(mnemonic) != null)
                    objCode = null;
                else
                    objCode = constructCode(hexOP, mid, hexDisp, mnemonic);

                writeLine(address, label, mnemonic, parameters, objCode);


            }
        }
    Utils.closeFile(scan);
        Utils.closeFormat(form);
    }

    //Separates out the parameters into individual operands.  They are either separated by an arithmetic operator
    //or by a comma.
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

    //Converts each symbol in the operands array into their corresponding address if they are found in the SYMTAB
    private static String[] symbolToAddress(String[] operands) {
        for (int i = 0; i < operands.length; i++) {
            if (Tables.SYMTAB.get(operands[i]) != null)
                operands[i] = Tables.SYMTAB.get(operands[i]);
        }
        return operands;

    }

    //Reads a line and separates it out into address, label, opcode, and parameter values
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

    //Takes the various values and writes them out as a line in the object code file
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

    //constructs the different sections of the object code into a single code based on their type
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

    //Calculates the disp address based on the current and target addresses
    private static String calculateDisp(String current, String target) {
        String disp;
        int d;

        if (e == 1)
            disp = target;
        else {
            int c = Integer.parseInt(current, 16);
            int t = Integer.parseInt(target, 16);
            if (c > t)
                d = 4096 - (c - t);
            else
                d = t - c;
            disp = Utils.toHex(d, 3);
        }
        return disp;
    }

    //Calculates the PC jump based on the current opcode.
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
                int digits = params.substring(2, params.length()-1).length();
                pcInt += digits/2;
            }
            else if (params.startsWith("C")) {
                int digits = params.substring(2, params.length()-1).length();
                pcInt += digits;
            }

        }
        else if (op.equals("CSECT")) {
            pcInt = 0;
        }
        else if (op.equals("BASE") || op.equals("LTORG") || op.equals("EXTDEF") || op.equals("EXTREF")) {
            //don't increment the PC
        }

        pcInt = pcInt + addInt;
        return Utils.toHex(pcInt, 4);

    }

    //Preps the front 2 digits of the object code
    private static void prepFront() {
        if (opcode != null) {
            if (opcode.startsWith("+")) {
                e = 1;
                p = 0;
                b = 0;
                opcode = opcode.substring(1);
            }
            if (operands != null) {
                if (operands.length == 2) {
                    if (operands[1].equals("X")) {
                        x = 1;
                        BUFFER = operands[0];
                    }
                }
            }
            //B4
            if (opcode.equals("CLEAR")) {
                opcode = Tables.OPTAB.get(opcode);
                decimalOP = Integer.parseInt(opcode, 16);
                hexOP = Utils.toHex(decimalOP, 2);
            }
            else if (opcode.equals("WORD"))
                hexOP = "0";
            else if (Tables.OPTAB.get(opcode) != null) {
                opcode = Tables.OPTAB.get(opcode);
                decimalOP = Integer.parseInt(opcode, 16);
                decimalOP += addressingMode;
                hexOP = Utils.toHex(decimalOP, 2);
            }

        }

    }

    //performs special functions if the parameters are immediate or indirect addressing, and then separates the operands
    //and converts them to their corresponding addresses.
    private static void getOperands() {
        if (parameters != null) {
            if(parameters.startsWith("#")) {
                addressingMode = IMMEDIATE;
                parameters = parameters.substring(1);
                if (Tables.SYMTAB.get(parameters) != null)
                    parameters = Tables.SYMTAB.get(parameters);
                decimalDisp = Integer.parseInt(parameters);
                hexDisp = Utils.toHex(decimalDisp, 3);
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

    //calculates the resulting operand value if they involve an arithmetic operator.
    private static String calculateOperands() {
        int result = -1;

        for (int i = 0; i < operands.length; i++) {
            if (Tables.SYMTAB.get(operands[i]) != null)
                operands[i] = Tables.SYMTAB.get(operands[i]);
        }
        if (operands.length >= 3) {
            if (operands[1].equals("-")) {
                result = Integer.parseInt(operands[0], 16) - Integer.parseInt(operands[0], 16);
                p = 0;
            }
            else if (operands[1].equals("+")) {
                result = Integer.parseInt(operands[0], 16) + Integer.parseInt(operands[0], 16);
            }
        }
        return Utils.toHex(result, 4);

    }

    //Calculates the target address that will be used for extended, pc, or base addressing
    private static String getTargetAddress() {
        String ta = "";
        if (parameters != null) {
            if (operands[0].equals(BUFFER)) {
                ta = startAddress;
            }
            else
                ta = Tables.SYMTAB.get(parameters);
        }
        return ta;
    }

    //Constructs the object code for BYTE
    private static String constructVar(String m) {
        String obj = "";
        String digits = parameters.substring(2, m.length());
        obj = digits;
        return obj;
    }

    //Check the op and see if there are any special cases
    private static void opCheck(String m) {
        if (m.equals("TIXR")) {
            hexOP = "b8";
        }
        else if (m.equals("RSUB")) {
            p = 0;
            hexDisp = "000";
        }

    }


}
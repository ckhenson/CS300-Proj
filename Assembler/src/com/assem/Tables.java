package com.assem;

import java.util.Hashtable;

/**
 * Created by efetsko on 3/14/2016.
 */
public class Tables {

    public static int testInt = 5;

    //SYMTAB hash table.
    public static Hashtable<String, String> SYMTAB = new Hashtable<String, String>();

    //OPTAB hash table.  It holds all the Mnemonic to Opcode conversion values.
    public static Hashtable<String, String> OPTAB = new Hashtable<String, String>() {{
        put("ADD", "18");
        put("ADDF", "58");
        put("ADDR", "90");
        put("AND", "40");
        put("CLEAR", "B4");
        put("COMP", "28");
        put("COMPF", "88");
        put("COMPR", "A0");
        put("DIV", "24");
        put("DIVF", "64");
        put("DIVR", "9C");
        put("FIX", "C4");
        put("FLOAT", "C0");
        put("HIO", "F4");
        put("J", "3C");
        put("JEQ", "30");
        put("JGT", "34");
        put("JLT", "38");
        put("JSUB", "48");
        put("LDA", "00");
        put("LDB", "68");
        put("LDCH", "50");
        put("LDF", "70");
        put("LDL", "08");
        put("LDS", "6C");
        put("LDT", "74");
        put("LDX", "04");
        put("LPS", "D0");
        put("MULF", "60");
        put("MULR", "98");
        put("NORM", "C8");
        put("OR", "44");
        put("RD", "D8");
        put("RMO", "AC");
        put("RSUB", "4C");
        put("SHIFTL", "A4");
        put("SHIFTR", "A8");
        put("SIO", "F0");
        put("SSK", "EC");
        put("STA", "0C");
        put("STB", "78");
        put("STCH", "54");
        put("STF", "80");
        put("STI", "D4");
        put("STL", "14");
        put("STS", "7C");
        put("STSW", "E8");
        put("STT", "84");
        put("STX", "10");
        put("SUB", "1C");
        put("SUBF", "5C");
        put("SUBR", "94");
        put("SVC", "B0");
        put("TD", "E0");
        put("TIO", "F8");
        put("TIX", "2C");
        put("TIXR", "B8");
        put("WD", "DC");
    }};

    //OPCODE FORM hash table.  It holds all the Opcode FORMAT values.
    public static Hashtable<String, String> FORMAT = new Hashtable<String, String>() {{
        put("FIX", "1");
        put("FLOAT", "1");
        put("HIO", "1");
        put("NORM", "1");
        put("SIO", "1");
        put("TIO", "1");
        put("ADDR", "2");
        put("CLEAR", "2");
        put("COMPR", "2");
        put("DIVR", "2");
        put("MULR", "2");
        put("RMO", "2");
        put("SHIFTL", "2");
        put("SHIFTR", "2");
        put("SUBR", "2");
        put("SVC", "2");
        put("TIXR", "2");
    }};

    //Register hash table.  It holds all the Register values.
    public static Hashtable<String, String> REG = new Hashtable<String, String>() {{
        put("A", "0000");
        put("X", "0001");
        put("L", "0010");
        put("B", "0011");
        put("S", "0100");
        put("T", "0101");
        put("F", "0110");
        put("PC", "1000");
        put("SW", "1001");
    }};

    //Table mnemonic variables used in SIC/XE
    public static Hashtable<String, String> ARCH = new Hashtable<String, String>() {
        {
            put("ADDF", "1");
            put("ADDR", "1");
            put("CLEAR", "1");
            put("COMPF", "1");
            put("COMPR", "1");
            put("DIVF", "1");
            put("DIVR", "1");
            put("FIX", "1");
            put("FLOAT", "1");
            put("HIO", "1");
            put("LDB", "1");
            put("LDF", "1");
            put("LDS", "1");
            put("LDT", "1");
            put("LPS", "1");
            put("MULF", "1");
            put("MULR", "1");
            put("NORM", "1");
            put("RMO", "1");
            put("SHIFTL", "1");
            put("SHIFTR", "1");
            put("SIO", "1");
            put("SSK", "1");
            put("STB", "1");
            put("STF", "1");
            put("STI", "1");
            put("STS", "1");
            put("STT", "1");
            put("SUBF", "1");
            put("SUBR", "1");
            put("SVC", "1");
            put("TIO", "1");
            put("TIXR", "1");


        }};

    //these opcodes have no corresponding object code
    public static Hashtable<String, String> NOOBJ = new Hashtable<String, String>() {
        {
            put("START", "1");
            put("EXTDEF", "1");
            put("EXTREF", "1");
            put("BASE", "1");
            put("LTORG", "1");
            put("RESW", "1");
            put("RESB", "1");
            put("EQU", "1");
            put("CSECT", "1");
            put("END", "1");
        }};

    //these opcodes have no corresponding object code
    public static Hashtable<String, String> REGISTERNUMS = new Hashtable<String, String>() {
        {
            put("A", "00");
            put("X", "10");
            put("L", "20");
            put("B", "30");
            put("S", "40");
            put("T", "50");
            put("F", "60");
            put("PC", "80");
            put("SW", "90");
        }};

}
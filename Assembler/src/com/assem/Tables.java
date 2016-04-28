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
    public static Hashtable<String, String> OPFORM = new Hashtable<String, String>() {{
	put("FIX", "1");
	put("FLOAT", "1");
	put("HIO", "1");
	put("NORM", "1");
	put("SIO", "1");
	put("TIO", "1");
	put("ADDR", "2");
	put("CLEAR", "2");
	put("ADDR", "2");
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
	
	// Hast table for Data, helps ensure that "opcode" is not identified as data.
   public static Hashtable<String, String> LABEL = new Hashtable<String, String>() {{
    	put("RESW", "1");
    	put("RESB", "1");
    	put("BYTE", "1");
    	put("WORD", "1");
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
	// accounts for spaces in the symbols
	put(" A", "0000");
	put(" X", "0001");
	put(" L", "0010");
	put(" B", "0011");
	put(" S", "0100");
	put(" T", "0101");
	put(" F", "0110");
	put(" PC", "1000");
	put(" SW", "1001");
    }};

	
}

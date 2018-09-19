package com.grassrootsmethodologies.android.basiccalculator;


public class Token
{
	public static final int DIGIT_0 = 0;
	public static final int DIGIT_1 = 1;
	public static final int DIGIT_2 = 2;
	public static final int DIGIT_3 = 3;
	public static final int DIGIT_4 = 4;
	public static final int DIGIT_5 = 5;
	public static final int DIGIT_6 = 6;
	public static final int DIGIT_7 = 7;
	public static final int DIGIT_8 = 8;
	public static final int DIGIT_9 = 9;
	public static final int DECIMAL_POINT = 10;
	public static final int ADD = 11;
	public static final int SUBTRACT = 12;
	public static final int MULTIPLY = 13;
	public static final int DIVIDE = 14;
	public static final int LEFT_PARENTHESIS = 15;
	public static final int RIGHT_PARENTHESIS = 16;
	public static final int SCIENTIFIC_EXPONENT = 17;
	public static final int EXPONENTIATE = 18;
	public static final int NUMBER = 0xFF;
	public static final int END = 0xFE;
	public static final int MISMATCHED_PARENTHESES_ERROR = 0xFD;
	public static final int OVERFLOW_ERROR = 0xFC;
	public static final int DOMAIN_ERROR = 0xFB;
	public static final int DIVISION_BY_ZERO_ERROR = 0xFA;
	public static final int SYNTAX_ERROR = 0xF9;
	public static final int FAULTY_APPLICATION_ERROR = 0xF8;

	public static boolean isDigit(int token) {
		return (sTokenTypeTable[token] == TYPE_DIGIT);
	}

	private static final int TYPE_DIGIT = 0;
	private static final int TYPE_NON_DIGIT = 1;
	private static final int[] sTokenTypeTable = { 
		TYPE_DIGIT // DIGIT_0
		, TYPE_DIGIT // DIGIT 1
		, TYPE_DIGIT // DIGIT 2
		, TYPE_DIGIT // DIGIT 3
		, TYPE_DIGIT // DIGIT 4
		, TYPE_DIGIT // DIGIT 5
		, TYPE_DIGIT // DIGIT 6
		, TYPE_DIGIT // DIGIT 7
		, TYPE_DIGIT // DIGIT 8
		, TYPE_DIGIT // DIGIT 9
		, TYPE_NON_DIGIT // DECIMAL_POINT
		, TYPE_NON_DIGIT // ADD
		, TYPE_NON_DIGIT // SUBTRACT
		, TYPE_NON_DIGIT // MULTIPLY
		, TYPE_NON_DIGIT // DIVIDE
		, TYPE_NON_DIGIT // LEFT_PARENTHESIS
		, TYPE_NON_DIGIT // RIGHT_PARENTHESIS
		, TYPE_NON_DIGIT // SCIENTIFIC_EXPONENT
		, TYPE_NON_DIGIT // EXPONENTIATE
	};

	private Token() {
	}
}

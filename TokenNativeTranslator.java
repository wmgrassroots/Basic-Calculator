package com.grassrootsmethodologies.android.basiccalculator;


import java.util.HashMap;

import android.util.SparseArray;


public class TokenNativeTranslator
{
	public static TokenNativeTranslator getInstance() {
		if (sInstance == null) {
			sInstance = new TokenNativeTranslator();
		}
		return sInstance;
	}

	public String stringValue(int token) {
		return mStringValues.get(token);
	}

	public int tokenValue(String stringValue) {
		return mTokenValues.get(stringValue).intValue();
	}

	private static final String DIGIT_0_NATIVE_STRING = "0";
	private static final String DIGIT_1_NATIVE_STRING = "1";
	private static final String DIGIT_2_NATIVE_STRING = "2";
	private static final String DIGIT_3_NATIVE_STRING = "3";
	private static final String DIGIT_4_NATIVE_STRING = "4";
	private static final String DIGIT_5_NATIVE_STRING = "5";
	private static final String DIGIT_6_NATIVE_STRING = "6";
	private static final String DIGIT_7_NATIVE_STRING = "7";
	private static final String DIGIT_8_NATIVE_STRING = "8";
	private static final String DIGIT_9_NATIVE_STRING = "9";
	private static final String DECIMAL_POINT_NATIVE_STRING = ".";
	private static final String ADD_NATIVE_STRING = "+";
	private static final String SUBTRACT_NATIVE_STRING = "-";
	private static final String SCIENTIFIC_EXPONENT_NATIVE_STRING = "E";
	private static TokenNativeTranslator sInstance;
	private SparseArray<String> mStringValues;
	private HashMap<String, Integer> mTokenValues;

	private TokenNativeTranslator() {
		initializeStringValues();
		initializeTokenValues();
	}

	private void initializeStringValues() {
		mStringValues = new SparseArray<String>();
		mStringValues.append(Token.DIGIT_0, DIGIT_0_NATIVE_STRING);
		mStringValues.append(Token.DIGIT_1, DIGIT_1_NATIVE_STRING);
		mStringValues.append(Token.DIGIT_2, DIGIT_2_NATIVE_STRING);
		mStringValues.append(Token.DIGIT_3, DIGIT_3_NATIVE_STRING);
		mStringValues.append(Token.DIGIT_4, DIGIT_4_NATIVE_STRING);
		mStringValues.append(Token.DIGIT_5, DIGIT_5_NATIVE_STRING);
		mStringValues.append(Token.DIGIT_6, DIGIT_6_NATIVE_STRING);
		mStringValues.append(Token.DIGIT_7, DIGIT_7_NATIVE_STRING);
		mStringValues.append(Token.DIGIT_8, DIGIT_8_NATIVE_STRING);
		mStringValues.append(Token.DIGIT_9, DIGIT_9_NATIVE_STRING);
		mStringValues.append(Token.DECIMAL_POINT, DECIMAL_POINT_NATIVE_STRING);
		mStringValues.append(Token.ADD, ADD_NATIVE_STRING);
		mStringValues.append(Token.SUBTRACT, SUBTRACT_NATIVE_STRING);
		mStringValues.append(Token.SCIENTIFIC_EXPONENT, SCIENTIFIC_EXPONENT_NATIVE_STRING);
	}

	private void initializeTokenValues() {
		mTokenValues = new HashMap<String, Integer>();
		mTokenValues.put(DIGIT_0_NATIVE_STRING, Integer.valueOf(Token.DIGIT_0));
		mTokenValues.put(DIGIT_1_NATIVE_STRING, Integer.valueOf(Token.DIGIT_1));
		mTokenValues.put(DIGIT_2_NATIVE_STRING, Integer.valueOf(Token.DIGIT_2));
		mTokenValues.put(DIGIT_3_NATIVE_STRING, Integer.valueOf(Token.DIGIT_3));
		mTokenValues.put(DIGIT_4_NATIVE_STRING, Integer.valueOf(Token.DIGIT_4));
		mTokenValues.put(DIGIT_5_NATIVE_STRING, Integer.valueOf(Token.DIGIT_5));
		mTokenValues.put(DIGIT_6_NATIVE_STRING, Integer.valueOf(Token.DIGIT_6));
		mTokenValues.put(DIGIT_7_NATIVE_STRING, Integer.valueOf(Token.DIGIT_7));
		mTokenValues.put(DIGIT_8_NATIVE_STRING, Integer.valueOf(Token.DIGIT_8));
		mTokenValues.put(DIGIT_9_NATIVE_STRING, Integer.valueOf(Token.DIGIT_9));
		mTokenValues.put(DECIMAL_POINT_NATIVE_STRING, Integer.valueOf(Token.DECIMAL_POINT));
		mTokenValues.put(ADD_NATIVE_STRING, Integer.valueOf(Token.ADD));
		mTokenValues.put(SUBTRACT_NATIVE_STRING, Integer.valueOf(Token.SUBTRACT));
		mTokenValues.put(SCIENTIFIC_EXPONENT_NATIVE_STRING, Integer.valueOf(Token.SCIENTIFIC_EXPONENT));
	}
}

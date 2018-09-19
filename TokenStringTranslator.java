package com.grassrootsmethodologies.android.basiccalculator;


import java.util.HashMap;

import android.content.Context;
import android.content.res.Resources;
import android.util.SparseArray;


public class TokenStringTranslator
{
	public static TokenStringTranslator getInstance(Context context) {
		if (sInstance == null) {
			sInstance = new TokenStringTranslator(context.getApplicationContext());
		}
		return sInstance;
	}

	public String stringValue(int tokenValue) {
		return mStringValues.get(tokenValue);
	}

	public int tokenValue(String stringValue) {
		return mTokenValues.get(stringValue).intValue();
	}

	private static TokenStringTranslator sInstance;
	private Resources mResources;
	private SparseArray<String> mStringValues;
	private HashMap<String, Integer> mTokenValues;

	private TokenStringTranslator(Context context) {
		mResources = context.getResources();
		initializeStringValues();
		initializeTokenValues();
	}

	private void initializeStringValues() {
		mStringValues = new SparseArray<String>();
		mStringValues.put(Token.DIGIT_0, getString(R.string.digit_0_text));
		mStringValues.put(Token.DIGIT_1, getString(R.string.digit_1_text));
		mStringValues.put(Token.DIGIT_2, getString(R.string.digit_2_text));
		mStringValues.put(Token.DIGIT_3, getString(R.string.digit_3_text));
		mStringValues.put(Token.DIGIT_4, getString(R.string.digit_4_text));
		mStringValues.put(Token.DIGIT_5, getString(R.string.digit_5_text));
		mStringValues.put(Token.DIGIT_6, getString(R.string.digit_6_text));
		mStringValues.put(Token.DIGIT_7, getString(R.string.digit_7_text));
		mStringValues.put(Token.DIGIT_8, getString(R.string.digit_8_text));
		mStringValues.put(Token.DIGIT_9, getString(R.string.digit_9_text));
		mStringValues.put(Token.DECIMAL_POINT, getString(R.string.decimal_point_text));
		mStringValues.put(Token.ADD, getString(R.string.add_text));
		mStringValues.put(Token.SUBTRACT, getString(R.string.subtract_text));
		mStringValues.put(Token.MULTIPLY, getString(R.string.multiply_text));
		mStringValues.put(Token.DIVIDE, getString(R.string.divide_text));
		mStringValues.put(Token.LEFT_PARENTHESIS, getString(R.string.left_parenthesis_text));
		mStringValues.put(Token.RIGHT_PARENTHESIS, getString(R.string.right_parenthesis_text));
		mStringValues.put(Token.SCIENTIFIC_EXPONENT, getString(R.string.scientific_exponent_text));
		mStringValues.put(Token.EXPONENTIATE, getString(R.string.exponentiate_text));
		mStringValues.put(Token.MISMATCHED_PARENTHESES_ERROR, getString(R.string.mismatched_parentheses_error));
		mStringValues.put(Token.OVERFLOW_ERROR, getString(R.string.overflow_error));
		mStringValues.put(Token.DOMAIN_ERROR, getString(R.string.domain_error));
		mStringValues.put(Token.DIVISION_BY_ZERO_ERROR, getString(R.string.division_by_zero_error));
		mStringValues.put(Token.SYNTAX_ERROR, getString(R.string.syntax_error));
		mStringValues.put(Token.FAULTY_APPLICATION_ERROR, getString(R.string.faulty_application_error));
	}

	private void initializeTokenValues() {
		mTokenValues = new HashMap<String, Integer>();
		mTokenValues.put(getString(R.string.digit_0_text), Integer.valueOf(Token.DIGIT_0));
		mTokenValues.put(getString(R.string.digit_1_text), Integer.valueOf(Token.DIGIT_1));
		mTokenValues.put(getString(R.string.digit_2_text), Integer.valueOf(Token.DIGIT_2));
		mTokenValues.put(getString(R.string.digit_3_text), Integer.valueOf(Token.DIGIT_3));
		mTokenValues.put(getString(R.string.digit_4_text), Integer.valueOf(Token.DIGIT_4));
		mTokenValues.put(getString(R.string.digit_5_text), Integer.valueOf(Token.DIGIT_5));
		mTokenValues.put(getString(R.string.digit_6_text), Integer.valueOf(Token.DIGIT_6));
		mTokenValues.put(getString(R.string.digit_7_text), Integer.valueOf(Token.DIGIT_7));
		mTokenValues.put(getString(R.string.digit_8_text), Integer.valueOf(Token.DIGIT_8));
		mTokenValues.put(getString(R.string.digit_9_text), Integer.valueOf(Token.DIGIT_9));
		mTokenValues.put(getString(R.string.decimal_point_text), Integer.valueOf(Token.DECIMAL_POINT));
		mTokenValues.put(getString(R.string.subtract_text), Integer.valueOf(Token.SUBTRACT));
		mTokenValues.put(getString(R.string.scientific_exponent_text), Integer.valueOf(Token.SCIENTIFIC_EXPONENT));
	}

	private String getString(int resourceId) {
		return mResources.getString(resourceId);
	}
}

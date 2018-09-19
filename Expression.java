package com.grassrootsmethodologies.android.basiccalculator;


import java.util.LinkedList;

import org.json.JSONArray;
import org.json.JSONException;

import android.content.Context;


public class Expression
{
	public Expression(Context context) {
		mContext = context.getApplicationContext();
		mTokenList = new LinkedList<Integer>();
	}

	public Expression(Context context, int[] tokens) {
		mContext = context.getApplicationContext();
		mTokenList = new LinkedList<Integer>();
		for (int i = 0; i < tokens.length; i++) {
			mTokenList.addLast(tokens[i]);
		}
	}

	public Expression(Expression source) {
		mContext = source.mContext;
		mTokenList = new LinkedList<Integer>(source.mTokenList);
	}

	public Expression(Context context, double source) {
		mContext = context.getApplicationContext();
		mTokenList = new LinkedList<Integer>();
		String sourceString = String.format(FORMAT_STRING, source);
		TokenNativeTranslator tokenNativeTranslator = TokenNativeTranslator.getInstance();
		for (int i = 0; i < sourceString.length(); i++) {
			mTokenList.addLast(tokenNativeTranslator.tokenValue(String.valueOf(sourceString.charAt(i))));
		}
	}

	public Expression(Context context, String internalTokenString) {
		mContext = context.getApplicationContext();
		mTokenList = new LinkedList<Integer>();
		try {
			JSONArray jsonArray = new JSONArray(internalTokenString);
			for (int i = 0; i < jsonArray.length(); i++) {
				int token = jsonArray.getInt(i);
				mTokenList.addLast(token);
			}
		} catch (JSONException e) {
			// Silently ignore. If it happens, the expression will be empty, and that is fine.
		}
	}

	public boolean isEmpty() {
		return mTokenList.isEmpty();
	}

	public int length() {
		return mTokenList.size();
	}

	public Expression clear() {
		mTokenList.clear();
		return this;
	}

	public int getFirstToken() {
		return mTokenList.getFirst();
	}

	public int popFirstToken() {
		int firstToken = mTokenList.getFirst();
		mTokenList.removeFirst();
		return firstToken;
	}

	public void pushFirstToken(int token) {
		mTokenList.addFirst(token);
	}

	public int getToken(int index) {
		return mTokenList.get(index);
	}

	public Expression append(int token) {
		mTokenList.addLast(token);
		return this;
	}

	public Expression append(int[] tokens) {
		for (int i = 0; i < tokens.length; i++) {
			mTokenList.addLast(tokens[i]);
		}
		return this;
	}

	public Expression append(Expression expression) {
		mTokenList.addAll(expression.mTokenList);
		return this;
	}

	public void insert(int location, int token) {
		mTokenList.add(location, token);
	}

	public Expression removeFirst() {
		mTokenList.removeFirst();
		return this;
	}

	public Expression removeLast() {
		mTokenList.removeLast();
		return this;
	}

	public int indexOf(int token) {
		return mTokenList.indexOf(token);
	}

	public Expression subexpression(int start) {
		Expression result = new Expression(this);
		for (int i = 0; i < start; i++) {
			result.removeFirst();
		}
		return result;
	}

	public Expression subexpression(int start, int end) {
		Expression result = new Expression(this);
		for (int i = end; i < length(); i++) {
			result.removeLast();
		}
		for (int i = 0; i < start; i++) {
			result.removeFirst();
		}
		return result;
	}

	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		TokenStringTranslator tokenStringTranslator = TokenStringTranslator.getInstance(mContext);
		for (Integer token : mTokenList) {
			stringBuilder.append(tokenStringTranslator.stringValue(token));
		}
		return stringBuilder.toString();
	}

	public String internalStringRepresentation() {
		JSONArray jsonArray = new JSONArray();
		for (int i = 0; i < mTokenList.size(); i++) {
			int token = mTokenList.get(i);
			jsonArray.put(token);
		}
		return jsonArray.toString();
	}

	private static final String FORMAT_STRING = "%." + Integer.toString(AppParameters.PRECISION_DIGITS) + "G";
	private Context mContext;
	private LinkedList<Integer> mTokenList;
}

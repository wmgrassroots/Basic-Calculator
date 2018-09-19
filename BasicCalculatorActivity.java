package com.grassrootsmethodologies.android.basiccalculator;


import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;

import com.grassrootsmethodologies.android.widget.DisplayScroller;
import com.grassrootsmethodologies.android.widget.SizeAdaptedTextViewWithCursor;


public class BasicCalculatorActivity extends Activity
{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mPreferences = getPreferences(MODE_PRIVATE);
		mExpressionEvaluator = new ExpressionEvaluator(this);
		setContentView(R.layout.basic_calculator);
		connectUIWidgets();
		connectUIEventListeners();
		mScreenOrientation = getResources().getConfiguration().orientation;
		mCurrentExpression = new Expression(this);
	}

	@Override
	public void onPause() {
		saveState();
		super.onPause();
	}

	@Override
	public void onResume() {
		super.onResume();
		restoreState();
	}

	private static final String EMPTY_STRING = "";
	private static final String DISPLAY_HOLDS_ERROR = "DISPLAY_HOLDS_ERROR";
	private static final String DISPLAY_HOLDS_RESULT = "DISPLAY_HOLDS_RESULT";
	private static final String CURRENT_EXPRESSION = "CURRENT_EXPRESSION";
	private static final String SCROLLER_POSITION_LANDSCAPE = "SCROLLER_POSITION_LANDSCAPE";
	private static final String SCROLLER_POSITION_PORTRAIT = "SCROLLER_POSITION_PORTRAIT";
	private static final String SCROLLER_POSITION_VALID_PORTRAIT = "SCROLLER_POSITION_VALID_PORTRAIT";
	private static final String SCROLLER_POSITION_VALID_LANDSCAPE = "SCROLLER_POSITION_VALID_LANDSCAPE";
	private SizeAdaptedTextViewWithCursor mDisplay;
	private DisplayScroller mScroller;
	private ExpressionEvaluator mExpressionEvaluator;
	private SharedPreferences mPreferences;
	private int mScreenOrientation;
	private boolean mScrollStateUpdated;
	private boolean mDisplayHoldsResult;
	private boolean mDisplayHoldsError;
	private Expression mCurrentExpression;

	private class TokenAppendingListener implements View.OnClickListener
	{
		public TokenAppendingListener(Integer token, boolean clearIfDisplayHoldsResult) {
			mToken = token;
			mClearIfDisplayHoldsResult = clearIfDisplayHoldsResult;
		}

		@Override
		public void onClick(View v) {
			if (mDisplayHoldsError || (mDisplayHoldsResult && mClearIfDisplayHoldsResult)) {
				mCurrentExpression.clear();
			}
			mCurrentExpression.append(mToken);
			updateDisplayFromCurrentExpression();
			moveCursorToEnd();
			clearDisplayFlags();
			mScrollStateUpdated = true;
			invalidateScrollStateForOtherScreenOrientation();
		}

		private final Integer mToken;
		private final boolean mClearIfDisplayHoldsResult;
	}

	private class TokenListAppendingListener implements View.OnClickListener
	{
		public TokenListAppendingListener(int[] tokens, boolean clearIfDisplayHoldsResult) {
			mTokens = new int[tokens.length];
			for (int i = 0; i < tokens.length; i++) {
				mTokens[i] = tokens[i];
			}
			mClearIfDisplayHoldsResult = clearIfDisplayHoldsResult;
		}

		@Override
		public void onClick(View v) {
			if (mDisplayHoldsError || (mDisplayHoldsResult && mClearIfDisplayHoldsResult)) {
				mCurrentExpression.clear();
			}
			mCurrentExpression.append(mTokens);
			updateDisplayFromCurrentExpression();
			moveCursorToEnd();
			clearDisplayFlags();
			mScrollStateUpdated = true;
			invalidateScrollStateForOtherScreenOrientation();
		}

		private final int[] mTokens;
		private final boolean mClearIfDisplayHoldsResult;
	}

	private class ClearClickListener implements View.OnClickListener
	{
		@Override
		public void onClick(View v) {
			mCurrentExpression.clear();
			updateDisplayFromCurrentExpression();
			moveCursorToEnd();
			clearDisplayFlags();
			mScrollStateUpdated = true;
			invalidateScrollStateForOtherScreenOrientation();
		}
	}

	private class BackspaceClickListener implements View.OnClickListener
	{
		@Override
		public void onClick(View v) {
			if (mCurrentExpression.isEmpty()) {
				return;
			}
			if (mDisplayHoldsError) {
				mCurrentExpression.clear();
			} else {
				mCurrentExpression.removeLast();
			}
			updateDisplayFromCurrentExpression();
			moveCursorToEnd();
			clearDisplayFlags();
			mScrollStateUpdated = true;
			invalidateScrollStateForOtherScreenOrientation();
		}
	}

	private class EqualsClickListener implements View.OnClickListener
	{
		@Override
		public void onClick(View v) {
			if (mDisplayHoldsError || mDisplayHoldsResult || mCurrentExpression.isEmpty()) {
				return;
			}
			ExpressionEvaluator.EvaluationResult result = mExpressionEvaluator.evaluate(mCurrentExpression);
			mDisplayHoldsError = result.isError();
			mDisplayHoldsResult = !mDisplayHoldsError;
			mCurrentExpression = result.getResultExpression();
			updateDisplayFromCurrentExpression();
			if (mDisplayHoldsError) {
				moveCursorToBeginning();
			} else {
				moveCursorToEnd();
			}
			mScroller.scrollToBeginning();
			mScrollStateUpdated = true;
			invalidateScrollStateForOtherScreenOrientation();
		}
	}

	private void connectUIWidgets() {
		mDisplay = (SizeAdaptedTextViewWithCursor) findViewById(R.id.display);
		mScroller = (DisplayScroller) findViewById(R.id.displayScroller);
	}

	private void connectUIEventListeners() {
		mScroller.setScrollEventListener(new DisplayScroller.ScrollEventListener() {
			@Override
			public void onScrollEvent() {
				mScrollStateUpdated = true;
			}
		});
		findViewById(R.id.clearButton).setOnClickListener(
			new ClearClickListener());
		findViewById(R.id.exponentiateButton).setOnClickListener(
			new TokenAppendingListener(Token.EXPONENTIATE, false));
		findViewById(R.id.squareButton).setOnClickListener(
			new TokenListAppendingListener(new int[] { Token.EXPONENTIATE, Token.DIGIT_2 }, false));
		findViewById(R.id.backspaceButton).setOnClickListener(
			new BackspaceClickListener());
		findViewById(R.id.scientificExponentButton).setOnClickListener(
			new TokenAppendingListener(Token.SCIENTIFIC_EXPONENT, true));
		findViewById(R.id.leftParenthesisButton).setOnClickListener(
			new TokenAppendingListener(Token.LEFT_PARENTHESIS, true));
		findViewById(R.id.rightParenthesisButton).setOnClickListener(
			new TokenAppendingListener(Token.RIGHT_PARENTHESIS, true));
		findViewById(R.id.divideButton).setOnClickListener(
			new TokenAppendingListener(Token.DIVIDE, false));
		findViewById(R.id.digit7Button).setOnClickListener(
			new TokenAppendingListener(Token.DIGIT_7, true));
		findViewById(R.id.digit8Button).setOnClickListener(
			new TokenAppendingListener(Token.DIGIT_8, true));
		findViewById(R.id.digit9Button).setOnClickListener(
			new TokenAppendingListener(Token.DIGIT_9, true));
		findViewById(R.id.multiplyButton).setOnClickListener(
			new TokenAppendingListener(Token.MULTIPLY, false));
		findViewById(R.id.digit4Button).setOnClickListener(
			new TokenAppendingListener(Token.DIGIT_4, true));
		findViewById(R.id.digit5Button).setOnClickListener(
			new TokenAppendingListener(Token.DIGIT_5, true));
		findViewById(R.id.digit6Button).setOnClickListener(
			new TokenAppendingListener(Token.DIGIT_6, true));
		findViewById(R.id.subtractButton).setOnClickListener(
			new TokenAppendingListener(Token.SUBTRACT, false));
		findViewById(R.id.digit1Button).setOnClickListener(
			new TokenAppendingListener(Token.DIGIT_1, true));
		findViewById(R.id.digit2Button).setOnClickListener(
			new TokenAppendingListener(Token.DIGIT_2, true));
		findViewById(R.id.digit3Button).setOnClickListener(
			new TokenAppendingListener(Token.DIGIT_3, true));
		findViewById(R.id.addButton).setOnClickListener(
			new TokenAppendingListener(Token.ADD, false));
		findViewById(R.id.digit0Button).setOnClickListener(
			new TokenAppendingListener(Token.DIGIT_0, true));
		findViewById(R.id.decimalPointButton).setOnClickListener(
			new TokenAppendingListener(Token.DECIMAL_POINT, true));
		findViewById(R.id.negativeButton).setOnClickListener(
			new TokenAppendingListener(Token.SUBTRACT, true));
		findViewById(R.id.equalsButton).setOnClickListener(
			new EqualsClickListener());
	}

	private void saveState() {
		saveCurrentExpressionState();
		saveScrollState();
	}

	private void restoreState() {
		restoreCurrentExpressionState();
		restoreScrollState();
	}

	private void saveCurrentExpressionState() {
		mPreferences.edit()
			.putString(CURRENT_EXPRESSION, mCurrentExpression.internalStringRepresentation())
			.putBoolean(DISPLAY_HOLDS_RESULT, mDisplayHoldsResult)
			.putBoolean(DISPLAY_HOLDS_ERROR, mDisplayHoldsError)
			.commit();
	}

	private void restoreCurrentExpressionState() {
		mCurrentExpression = new Expression(this, mPreferences.getString(CURRENT_EXPRESSION, EMPTY_STRING));
		mDisplayHoldsResult = mPreferences.getBoolean(DISPLAY_HOLDS_RESULT, false);
		mDisplayHoldsError = mPreferences.getBoolean(DISPLAY_HOLDS_ERROR, false);
		updateDisplayFromCurrentExpression();
		if (mDisplayHoldsError) {
			moveCursorToBeginning();
		} else {
			moveCursorToEnd();
		}
	}

	private void saveScrollState() {
		if (!mScrollStateUpdated) {
			return;
		}
		final int scrollPosition = mScroller.getLastVisibleScrollPosition();
		String positionKey = null;
		String validityKey = null;
		switch (mScreenOrientation) {
		case Configuration.ORIENTATION_PORTRAIT:
			positionKey = SCROLLER_POSITION_PORTRAIT;
			validityKey = SCROLLER_POSITION_VALID_PORTRAIT;
			break;
		case Configuration.ORIENTATION_LANDSCAPE:
			positionKey = SCROLLER_POSITION_LANDSCAPE;
			validityKey = SCROLLER_POSITION_VALID_LANDSCAPE;
			break;
		}
		mPreferences.edit()
			.putInt(positionKey, scrollPosition)
			.putBoolean(validityKey, true)
			.commit();
		mScrollStateUpdated = false;
	}

	private void restoreScrollState() {
		String positionKey = null;
		String validityKey = null;
		switch (mScreenOrientation) {
		case Configuration.ORIENTATION_PORTRAIT:
			positionKey = SCROLLER_POSITION_PORTRAIT;
			validityKey = SCROLLER_POSITION_VALID_PORTRAIT;
			break;
		case Configuration.ORIENTATION_LANDSCAPE:
			positionKey = SCROLLER_POSITION_LANDSCAPE;
			validityKey = SCROLLER_POSITION_VALID_LANDSCAPE;
			break;
		}
		if (mPreferences.getBoolean(validityKey, false)) {
			mScroller.scrollTo(mPreferences.getInt(positionKey, 0));
		} else if (mDisplayHoldsError) {
			mScroller.scrollToBeginning();
		} else {
			mScroller.scrollToEnd();
		}
		mScrollStateUpdated = false;
	}

	private void invalidateScrollStateForOtherScreenOrientation() {
		String validityKey = null;
		switch (mScreenOrientation) {
		case Configuration.ORIENTATION_PORTRAIT:
			validityKey = SCROLLER_POSITION_VALID_LANDSCAPE;
			break;
		case Configuration.ORIENTATION_LANDSCAPE:
			validityKey = SCROLLER_POSITION_VALID_PORTRAIT;
			break;
		}
		mPreferences.edit().putBoolean(validityKey, false).commit();
	}

	private void updateDisplayFromCurrentExpression() {
		mDisplay.setText(mCurrentExpression.toString());
	}

	private void moveCursorToBeginning() {
		mDisplay.setSelection(0);
		mScroller.scrollToBeginning();
	}

	private void moveCursorToEnd() {
		mDisplay.setSelection(mDisplay.getText().length());
		mScroller.scrollToEnd();
	}

	private void clearDisplayFlags() {
		mDisplayHoldsResult = false;
		mDisplayHoldsError = false;
	}
}

package com.grassrootsmethodologies.android.basiccalculator;


import android.content.Context;


public class ExpressionEvaluator
{
	public static class EvaluationResult
	{
		public EvaluationResult(Expression result, boolean isError) {
			this.result = result;
			error = isError;
		}

		public Expression getResultExpression() {
			return result;
		}

		public boolean isError() {
			return error;
		}

		private final Expression result;
		private final boolean error;
	}

	public ExpressionEvaluator(Context context) {
		mContext = context.getApplicationContext();
	}

	public EvaluationResult evaluate(Expression source) {
		mExpressionSource = new Expression(source);
		if (hasMismatchedParentheses(mExpressionSource)) {
			Expression mismatchedParenthesesErrorExpression = new Expression(mContext);
			mismatchedParenthesesErrorExpression.append(Token.MISMATCHED_PARENTHESES_ERROR);
			return new EvaluationResult(mismatchedParenthesesErrorExpression, true);
		}
		mExpressionSource = makeImpliedMultiplicationsExplicit(mExpressionSource);
		try {
			final double result = expression(true);
			if (Double.isInfinite(result)) {
				Expression overflowErrorExpression = new Expression(mContext);
				overflowErrorExpression.append(Token.OVERFLOW_ERROR);
				return new EvaluationResult(overflowErrorExpression, true);
			}
			if (Double.isNaN(result)) {
				Expression domainErrorExpression = new Expression(mContext);
				domainErrorExpression.append(Token.DOMAIN_ERROR);
				return new EvaluationResult(domainErrorExpression, true);
			}
			Expression resultExpression = trimTrailingZeros(new Expression(mContext, result));
			return new EvaluationResult(resultExpression, false);
		} catch (final DivisionByZeroException e) {
			Expression divisionByZeroErrorExpression = new Expression(mContext);
			divisionByZeroErrorExpression.append(Token.DIVISION_BY_ZERO_ERROR);
			return new EvaluationResult(divisionByZeroErrorExpression, true);
		} catch (final DomainException e) {
			Expression domainErrorExpression = new Expression(mContext);
			domainErrorExpression.append(Token.DOMAIN_ERROR);
			return new EvaluationResult(domainErrorExpression, true);
		} catch (final SyntaxException e) {
			Expression syntaxErrorExpression = new Expression(mContext);
			syntaxErrorExpression.append(Token.SYNTAX_ERROR);
			return new EvaluationResult(syntaxErrorExpression, true);
		} catch (final ProgramLogicException e) {
			Expression faultyApplicationErrorExpression = new Expression(mContext);
			faultyApplicationErrorExpression.append(Token.FAULTY_APPLICATION_ERROR);
			return new EvaluationResult(faultyApplicationErrorExpression, true);
		}
	}

	private Expression mExpressionSource;
	private int mCurrentToken;
	private double mNumberValue;
	private Context mContext;

	@SuppressWarnings("serial")
	private class DivisionByZeroException extends Exception
	{
	}

	@SuppressWarnings("serial")
	private class DomainException extends Exception
	{
	}

	@SuppressWarnings("serial")
	private class SyntaxException extends Exception
	{
	}

	@SuppressWarnings("serial")
	private class ProgramLogicException extends Exception
	{
	}

	private boolean hasMismatchedParentheses(Expression expression) {
		Expression workingExpression = new Expression(expression);
		int numNestingLevels = 0;
		while (!workingExpression.isEmpty()) {
			int token = workingExpression.popFirstToken();
			if (token == Token.LEFT_PARENTHESIS) {
				++numNestingLevels;
			} else if (token == Token.RIGHT_PARENTHESIS) {
				--numNestingLevels;
				if (numNestingLevels < 0) {
					return true;
				}
			}
		}
		if (numNestingLevels != 0) {
			return true;
		}
		return false;
	}

	private Expression makeImpliedMultiplicationsExplicit(Expression source) {
		Expression result = new Expression(source);
		for (int i = 0; i < result.length() - 1; i++) {
			int ti = result.getToken(i);
			int tip1 = result.getToken(i + 1);
			if ((((ti == Token.RIGHT_PARENTHESIS) || (ti == Token.DECIMAL_POINT) || Token.isDigit(ti)) && (tip1 == Token.LEFT_PARENTHESIS))
				|| ((ti == Token.RIGHT_PARENTHESIS) && ((tip1 == Token.DECIMAL_POINT) || Token.isDigit(tip1)))) {
				result.insert(i + 1, Token.MULTIPLY);
			}
		}
		return result;
	}

	private Expression trimTrailingZeros(Expression source) {
		Expression ordinaryNumberPortion;
		Expression scientificNotationExponentPortion;
		final int indexOfScientificExponentToken = source.indexOf(Token.SCIENTIFIC_EXPONENT);
		final boolean resultIsInScientificNotation = (indexOfScientificExponentToken != -1);
		if (resultIsInScientificNotation) {
			ordinaryNumberPortion = source.subexpression(0, indexOfScientificExponentToken);
			scientificNotationExponentPortion = source.subexpression(indexOfScientificExponentToken);
		} else {
			ordinaryNumberPortion = new Expression(source);
			scientificNotationExponentPortion = new Expression(mContext);
		}
		final int indexOfDecimalPoint = ordinaryNumberPortion.indexOf(Token.DECIMAL_POINT);
		if (indexOfDecimalPoint == -1) {
			return source;
		}
		for (int i = ordinaryNumberPortion.length() - 1; i >= indexOfDecimalPoint; --i) {
			if (ordinaryNumberPortion.getToken(i) == Token.DIGIT_0) {
				ordinaryNumberPortion = ordinaryNumberPortion.subexpression(0, i);
			} else {
				// All done removing zeros. Trim trailing decimal point if it
				// is there; it is no longer needed.
				if (ordinaryNumberPortion.getToken(i) == Token.DECIMAL_POINT) {
					ordinaryNumberPortion = ordinaryNumberPortion.subexpression(0, i);
				}
				break;
			}
		}
		Expression result = new Expression(ordinaryNumberPortion);
		result.append(scientificNotationExponentPortion);
		return result;
	}

	private double expression(boolean getToken) throws DivisionByZeroException, DomainException, SyntaxException,
		ProgramLogicException {
		double left = term(getToken);
		for (;;) {
			switch (mCurrentToken) {
			case Token.ADD:
				left += term(true);
				break;
			case Token.SUBTRACT:
				left -= term(true);
				break;
			case Token.LEFT_PARENTHESIS:
				// Properly placed parentheses are dealt with at a lower level.
				throw new SyntaxException();
			default:
				return left;
			}
		}
	}

	private double term(boolean getToken) throws DivisionByZeroException, DomainException, SyntaxException, ProgramLogicException {
		double left = factor(getToken);
		for (;;) {
			switch (mCurrentToken) {
			case Token.MULTIPLY:
				left *= factor(true);
				break;
			case Token.DIVIDE:
				final double d = factor(true);
				if (d != 0) {
					left /= d;
					break;
				}
				throw new DivisionByZeroException();
			case Token.LEFT_PARENTHESIS:
				// Properly placed parentheses are dealt with at a lower level.
				throw new SyntaxException();
			default:
				return left;
			}
		}
	}

	private double factor(boolean getToken) throws DivisionByZeroException, DomainException, SyntaxException,
		ProgramLogicException {
		double left = primitive(getToken);
		for (;;) {
			switch (mCurrentToken) {
			case Token.EXPONENTIATE:
				final double right = primitive(true);
				if ((left == 0) && (right == 0)) {
					throw new DomainException();
				}
				left = Math.pow(left, right);
				break;
			case Token.LEFT_PARENTHESIS:
				// Properly placed parentheses are dealt with at a lower level.
				throw new SyntaxException();
			default:
				return left;
			}
		}
	}

	private double primitive(boolean getToken) throws DivisionByZeroException, DomainException, SyntaxException,
		ProgramLogicException {
		if (getToken) {
			getToken();
		}
		switch (mCurrentToken) {
		case Token.NUMBER:
			final double result = mNumberValue;
			getToken();
			return result;
		case Token.ADD:
			return factor(true);
		case Token.SUBTRACT:
			return -factor(true);
		case Token.LEFT_PARENTHESIS:
			// Evaluate subexpression
			final double subExpression = expression(true);
			// Current token should now be the matching right parenthesis.
			if (mCurrentToken != Token.RIGHT_PARENTHESIS) {
				throw new ProgramLogicException();
			}
			// Eat the matching right parenthesis.
			getToken();
			return subExpression;
		case Token.RIGHT_PARENTHESIS:
			// Right parenthesis should have been eaten in evaluation of
			// subexpressions.
			throw new SyntaxException();
		default:
			throw new SyntaxException();
		}
	}

	private void getToken() throws SyntaxException {
		if (mExpressionSource.length() == 0) {
			mCurrentToken = Token.END;
			return;
		}
		// Consume a token from the expression source...
		int token = mExpressionSource.popFirstToken();
		// ...and use it.
		switch (token) {
		case Token.ADD:
		case Token.SUBTRACT:
		case Token.MULTIPLY:
		case Token.DIVIDE:
		case Token.LEFT_PARENTHESIS:
		case Token.RIGHT_PARENTHESIS:
		case Token.EXPONENTIATE:
			mCurrentToken = token;
			return;
		}
		if (Token.isDigit(token) || token == Token.DECIMAL_POINT) {
			// Put the digit or decimal point back.
			mExpressionSource.pushFirstToken(token);
			// Determine the length of the string containing the number.
			int index = 0;
			boolean hasDecimalPointAlready = false;
			boolean hasScientificSeparatorAlready = false;
			int t;
			do {
				t = mExpressionSource.getToken(index);
				if (t == Token.DECIMAL_POINT) {
					if (hasDecimalPointAlready) {
						throw new SyntaxException();
					}
					hasDecimalPointAlready = true;
					++index;
				} else if (t == Token.SCIENTIFIC_EXPONENT) {
					if (hasScientificSeparatorAlready) {
						throw new SyntaxException();
					}
					hasScientificSeparatorAlready = true;
					++index;
					if (index == mExpressionSource.length()) {
						throw new SyntaxException();
					}
					t = mExpressionSource.getToken(index);
					if (!((t == Token.SUBTRACT) || (t == Token.ADD) || (Token.isDigit(t)))) {
						throw new SyntaxException();
					}
					if ((t == Token.SUBTRACT) || (t == Token.ADD)) {
						++index;
						if (index == mExpressionSource.length()) {
							throw new SyntaxException();
						}
						t = mExpressionSource.getToken(index);
					}
					if (index == mExpressionSource.length()) {
						throw new SyntaxException();
					}
					t = mExpressionSource.getToken(index);
					if (!Token.isDigit(t)) {
						throw new SyntaxException();
					}
				} else if (Token.isDigit(t)) {
					++index;
				}
			} while ((index < mExpressionSource.length())
				&& ((Token.isDigit(t)) || (t == Token.DECIMAL_POINT) || (t == Token.SCIENTIFIC_EXPONENT)));
			// Parse substring into double precision value and store.
			try {
				StringBuilder nativeNumberStringBuilder = new StringBuilder();
				for (int i = 0; i < index; i++) {
					nativeNumberStringBuilder.append(TokenNativeTranslator.getInstance().stringValue(
						mExpressionSource.popFirstToken()));
				}
				mNumberValue = Double.parseDouble(nativeNumberStringBuilder.toString());
			} catch (final NumberFormatException e) {
				throw new SyntaxException();
			}
			mCurrentToken = Token.NUMBER;
			return;
		} else {
			throw new SyntaxException();
		}
	}
}

/*
 * Licensed Materials - Property of IBM
 * 5725-B69 5655-Y17 5655-Y31 5724-X98 5724-Y15 5655-V82 
 * Copyright IBM Corp. 1987, 2018. All Rights Reserved.
 *
 * Note to U.S. Government Users Restricted Rights: 
 * Use, duplication or disclosure restricted by GSA ADP Schedule 
 * Contract with IBM Corp.
 */

package sample;

import static sample.MessageCode.SAMPLE_ERROR_INVALID_RULESET_PATH;
import static sample.MessageCode.SAMPLE_ERROR_MISSING_RULESET_PATH;
import static sample.MessageCode.SAMPLE_RULESET_PATH;
import static sample.MessageCode.SAMPLE_AMOUNT_OF_THE_LOAN;

import ilog.rules.res.model.IlrFormatException;
import ilog.rules.res.model.IlrPath;


public class SampleClient {

	private static final MessageFormatter formatter = new MessageFormatter();

	/**
	 * @param args
	 */
	public static void main(String... arguments) throws Exception {
		SampleClient sample = new SampleClient();
		try {
			String rulesetPathAsParameter  = null;
			String ruleAppAsParameter = null;
			String loanAmountAsParameter = null;
			for (int i = 0; i < arguments.length - 1; i++) {
				if (arguments[i].equals("-rulesetPath")) {
					rulesetPathAsParameter = arguments[++i];
				}
				if (arguments[i].equals("-loanAmount")) {
					loanAmountAsParameter = arguments[++i];
				}
			}
			if (sample.checkUsage(rulesetPathAsParameter, loanAmountAsParameter, ruleAppAsParameter)){
				IlrPath rulesetPath = sample.getRulesetPath(rulesetPathAsParameter);
				RESJSEExecution execution = new RESJSEExecution();
				if(loanAmountAsParameter != null) {
					int value = Integer.parseInt(loanAmountAsParameter);
					execution.executeRulesetWithAmount(rulesetPath, value);
				}
				else {
					execution.executeRuleset(rulesetPath);
				}
			}
		} catch (IllegalArgumentException exception) {
			System.err.println(exception.getMessage());
			sample.exitWithUsageMessage();
		} catch (Throwable exception) {
			System.err.println(exception.getMessage());
			System.exit(2);
		}
	}

	/**
	 * 
	 * @param rulesetPathArgumentAsString
	 * @return an IlrPath constructed from provided parameter if valid
	 * @throws IllegalArgumentException
	 */
	private IlrPath getRulesetPath(String rulesetPathArgumentAsString) throws IllegalArgumentException {
		if (rulesetPathArgumentAsString == null) {
			String errorMessage = getMessage(SAMPLE_ERROR_MISSING_RULESET_PATH, getMessage(SAMPLE_RULESET_PATH));
			throw new IllegalArgumentException(errorMessage);
		}
		try {
			return IlrPath.parsePath(rulesetPathArgumentAsString);
		} catch (IlrFormatException exception) {
			String errorMessage = getMessage(SAMPLE_ERROR_INVALID_RULESET_PATH, rulesetPathArgumentAsString);
			throw new IllegalArgumentException(errorMessage);
		}
	}

	/**
	 * 
	 * @param key
	 * @param arguments
	 * @return a message from translated messages
	 */
	private String getMessage(String key, Object... arguments) {
		return formatter.getMessage(key, arguments);
	}

	/**
	 * Exit JVM and prints usage 
	 */
	private void exitWithUsageMessage() {
		String rulesetPath = getMessage(SAMPLE_RULESET_PATH);
		String amountOfTheLoan = getMessage(SAMPLE_AMOUNT_OF_THE_LOAN);
		String commandLineSyntax = SampleClient.class.getName() + " [-loanAmount <"+amountOfTheLoan+">] -rulesetPath <" + rulesetPath + ">";
		System.out.println("Required argument 'rulesetPath' is missing, see usage:");
		System.out.println(commandLineSyntax);
		System.exit(1);
	}

	/**
	 * 
	 * @param rulesetPath
	 * @param loanAmount
	 * @param ruleApp
	 * @return true only if rulesetPath parameter is not null
	 */
	private boolean checkUsage(String rulesetPath, String loanAmount,
			String ruleApp) {
		boolean checked = false;
		if (rulesetPath != null) checked = true;
		if (!checked) exitWithUsageMessage();
		return checked;
	}
}

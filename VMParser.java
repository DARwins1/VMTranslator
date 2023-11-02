// Dylan Reed
// Project #7/8
// 3650.04
// 10/30/2023

import java.util.Scanner;

public class VMParser {
	private Scanner lineReader;
	private String currentCommand;

	public VMParser(Scanner fileReader) {
		lineReader = fileReader;
	}

	public boolean hasMoreCommands() {
		return lineReader.hasNextLine();
	}

	public void advance() {
		// Get the next non-comment, non-blank line
		do {
			currentCommand = lineReader.nextLine();

			if (currentCommand.indexOf("/") >= 0) {
				// If there's a comment on this line, try to prune it off
				currentCommand = currentCommand.substring(0, currentCommand.indexOf("/"));
			}
			currentCommand = currentCommand.trim();
		} while (currentCommand.length() == 0 && lineReader.hasNextLine());
	}

	// ARITHMETIC
	// PUSH, POP
	// LABEL
	// GOTO
	// IF
	// FUNCTION
	// RETURN
	// CALL
	public String commandType() {
		if (currentCommand.contains("push")) {
			return "PUSH";
		} else if (currentCommand.contains("pop")) {
			return "POP";
		} else if (currentCommand.contains("label")) {
			return "LABEL";
		} else if (currentCommand.contains("if")) {
			return "IF";
		} else if (currentCommand.contains("goto")) {
			return "GOTO";
		} else if (currentCommand.contains("function")) {
			return "FUNCTION";
		} else if (currentCommand.contains("return")) {
			return "RETURN";
		} else if (currentCommand.contains("call")) {
			return "CALL";
		} else {
			return "ARITHMETIC";
		}
	}

	public String arg1() {
		if (commandType().equals("ARITHMETIC")) {
			return currentCommand;
		} else {
			String substring = currentCommand.substring(currentCommand.indexOf(' ') + 1);
			int endIndex = substring.indexOf(' ');
			if (endIndex > 0) {
				return substring.substring(0, endIndex);
			} else {
				return substring;
			}
		}
	}

	public String arg2() {
		String substring = currentCommand.substring(currentCommand.indexOf(' ') + 1);
		return substring.substring(substring.indexOf(' ') + 1);
	}
}

// Dylan Reed
// Project #7/8
// 3650.04
// 10/30/2023

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.util.Scanner;

public class VMTranslator {

	public static void main(String[] args) {
		String userInput;
		if (args.length != 0) {
			userInput = args[0];
		} else {
			// Get input from the user
			Scanner keyboardReader = new Scanner(System.in);
			System.out.print("Name of VM file or folder: ");
			userInput = keyboardReader.nextLine();
			keyboardReader.close();
		}

		File target = new File(System.getProperty("user.dir") + "\\" + userInput);
		File[] files;

		if (target.isFile()) {
			// User gave a single file
			files = new File[] { target };
		} else {
			// User gave a folder
			// Get every file that ends with ".vm"
			files = target.listFiles(new FilenameFilter() {
				public boolean accept(File dir, String name) {
					return name.endsWith(".vm");
				}
			});
		}

		if (files == null) {
			// User-specified file(s) could not be found
			System.out.println("Error: '" + userInput + "' could not be found!");
			System.out.println();
			System.exit(0);
		}

		// Create a writer
		String outputName = userInput;
		if (userInput.indexOf('.') > 0) {
			outputName = userInput.substring(0, userInput.indexOf('.'));
		}
		VMCodeWriter writer = new VMCodeWriter(outputName);
		writer.writeInit();

		// Loop through each file...
		for (int i = 0; i < files.length; i++) {
			try {
				// Create a parser for this file
				Scanner fileReader = new Scanner(files[i]);
				VMParser parser = new VMParser(fileReader);
				writer.setFileName(files[i].getName());

				// Now we can start translating to assembly code
				while (parser.hasMoreCommands()) {
					parser.advance(); // Get the next command

					// Evaluate the command based on type
					switch (parser.commandType()) {
					case "ARITHMETIC":
						writer.writeArithmetic(parser.arg1());
						break;
					case "PUSH":
					case "POP":
						writer.writePushPop(parser.commandType(), parser.arg1(), Integer.parseInt(parser.arg2()));
						break;
					case "LABEL":
						writer.writeLabel(parser.arg1());
						break;
					case "GOTO":
						writer.writeGoto(parser.arg1());
						break;
					case "IF":
						writer.writeIf(parser.arg1());
						break;
					case "FUNCTION":
						writer.writeFunction(parser.arg1(), Integer.parseInt(parser.arg2()));
						break;
					case "CALL":
						writer.writeCall(parser.arg1(), Integer.parseInt(parser.arg2()));
						break;
					case "RETURN":
						writer.writeReturn();
						break;
					}
				}
				System.out.println("File '" + files[i].getName() + "' has been translated!");

				fileReader.close();
			} catch (FileNotFoundException e) {
				// User-specified file could not be found
				System.out.println("Error: '" + userInput + "' could not be found!");
				System.out.println();
				System.exit(0);
			}
		}
		writer.close();
	}
}

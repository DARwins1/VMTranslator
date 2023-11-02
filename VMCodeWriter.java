// Dylan Reed
// Project #7/8
// 3650.04
// 10/30/2023

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class VMCodeWriter {
	private PrintWriter fileWriter;
	private int accumulator;
	private String fileName;

	public VMCodeWriter(String outputName) {
		accumulator = 0;
		try {
			// NOTE: If a file with this name exists, it will be overwritten!
			fileWriter = new PrintWriter(new FileWriter(outputName + ".asm"));
		} catch (IOException e) {
			System.out.println("Error: '" + outputName + ".asm' could not be written!");
		}
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public void writeInit() {
		// Write stuff at the start of the file..
		// Initialize the stack pointer to Mem[256]
		fileWriter.println("@256");
		fileWriter.println("D=A");
		fileWriter.println("@SP");
		fileWriter.println("M=D");
		// Call the Sys.init function
		writeCall("Sys.init", 0);
	}

	public void writeLabel(String label) {
		fileWriter.println("(" + label + ")");
	}

	public void writeGoto(String label) {
		fileWriter.println("@" + label);
		fileWriter.println("0;JMP");
	}

	public void writeIf(String label) {
		fileWriter.println("@SP");
		fileWriter.println("A=M-1");
		fileWriter.println("D=M"); // Get the value at the top of the stack
		fileWriter.println("@SP");
		fileWriter.println("M=M-1"); // Decrement @SP
		fileWriter.println("@" + label);
		fileWriter.println("D;JNE"); // Jump to the label if D != 0
	}

	public void writeCall(String functionName, int numArgs) {
		// Save the return address
		fileWriter.println("@" + functionName + "-return" + accumulator);
		fileWriter.println("D=A"); // Get the return address
		fileWriter.println("@SP");
		fileWriter.println("A=M");
		fileWriter.println("M=D"); // Store the return address onto the stack
		fileWriter.println("@SP");
		fileWriter.println("M=M+1"); // Increment @SP
		// Save @LCL
		fileWriter.println("@LCL");
		fileWriter.println("D=M"); // Get @LCL
		fileWriter.println("@SP");
		fileWriter.println("A=M");
		fileWriter.println("M=D"); // Store @LCL onto the stack
		fileWriter.println("@SP");
		fileWriter.println("M=M+1"); // Increment @SP
		// Save @ARG
		fileWriter.println("@ARG");
		fileWriter.println("D=M"); // Get @ARG
		fileWriter.println("@SP");
		fileWriter.println("A=M");
		fileWriter.println("M=D"); // Store @ARG onto the stack
		fileWriter.println("@SP");
		fileWriter.println("M=M+1"); // Increment @SP
		// Save @THIS and @THAT
		writePushPop("PUSH", "pointer", 0);
		writePushPop("PUSH", "pointer", 1);
		// Set @ARG
		fileWriter.println("@" + (numArgs + 5));
		fileWriter.println("D=A"); // Get the argument offset
		fileWriter.println("@SP");
		fileWriter.println("A=M-D");
		fileWriter.println("D=A"); // Get the new @ARG position
		fileWriter.println("@ARG");
		fileWriter.println("M=D"); // Set @ARG to point to the new position
		// Set @LCL
		fileWriter.println("@SP");
		fileWriter.println("D=M");
		fileWriter.println("@LCL");
		fileWriter.println("M=D"); // Set @LCL to point to the current stack position
		// Go to the function
		writeGoto(functionName);
		// Set the return address
		writeLabel(functionName + "-return" + accumulator);
		accumulator++;
	}

	public void writeReturn() {
		// Store the value of @LCL in @R13 (essentially the FRAME variable)
		fileWriter.println("@LCL");
		fileWriter.println("D=M"); // Get the value of @LCL
		fileWriter.println("@R13");
		fileWriter.println("M=D"); // Store in @R13
		// Store the value of the data at FRAME - 5 in @R14 (essentially the RET variable/return address)
		fileWriter.println("@5");
		fileWriter.println("D=A"); // Get the value of 5
		fileWriter.println("@R13");
		fileWriter.println("A=M-D"); // Go to FRAME - 5
		fileWriter.println("D=M"); // Get the value at FRAME - 5
		fileWriter.println("@R14");
		fileWriter.println("M=D"); // Store the value in @R14
		// Pop the top of the stack and store it in @ARG
		fileWriter.println("@SP");
		fileWriter.println("A=M-1");
		fileWriter.println("D=M"); // Get return value
		fileWriter.println("@ARG");
		fileWriter.println("A=M");
		fileWriter.println("M=D"); // Store return value
		// We don't need to decrement @SP here since the next block will set it to a new value anyway
		// Set @SP to point over the new top of the stack
		fileWriter.println("@ARG");
		fileWriter.println("D=M+1"); // Get the new stack position
		fileWriter.println("@SP");
		fileWriter.println("M=D"); // Set @SP to the new top of the stack
		// Restore @THAT
		fileWriter.println("@R13");
		fileWriter.println("D=M-1"); // Get the value of FRAME - 1
		fileWriter.println("A=D"); // Go to FRAME - 1
		fileWriter.println("D=M"); // Get the data at FRAME - 1
		fileWriter.println("@THAT");
		fileWriter.println("M=D"); // Set @THAT to the value saved at FRAME - 1
		// Restore @THIS
		fileWriter.println("@R2");
		fileWriter.println("D=A"); // Get the value of 2
		fileWriter.println("@R13");
		fileWriter.println("D=M-D"); // Get the value of FRAME - 2
		fileWriter.println("A=D"); // Go to FRAME - 2
		fileWriter.println("D=M"); // Get the data at FRAME - 2
		fileWriter.println("@THIS");
		fileWriter.println("M=D"); // Set @THIS to the value saved at FRAME - 2
		// Restore @ARG
		fileWriter.println("@R3");
		fileWriter.println("D=A"); // Get the value of 3
		fileWriter.println("@R13");
		fileWriter.println("D=M-D"); // Get the value of FRAME - 3
		fileWriter.println("A=D"); // Go to FRAME - 3
		fileWriter.println("D=M"); // Get the data at FRAME - 3
		fileWriter.println("@ARG");
		fileWriter.println("M=D"); // Set @ARG to the value saved at FRAME - 3
		// Restore @LCL
		fileWriter.println("@R4");
		fileWriter.println("D=A"); // Get the value of 4
		fileWriter.println("@R13");
		fileWriter.println("D=M-D"); // Get the value of FRAME - 4
		fileWriter.println("A=D"); // Go to FRAME - 4
		fileWriter.println("D=M"); // Get the data at FRAME - 4
		fileWriter.println("@LCL");
		fileWriter.println("M=D"); // Set @LCL to the value saved at FRAME - 4
		// Go to the return address
		fileWriter.println("@R14");
		fileWriter.println("A=M"); // Get the value of RET from @R14
		fileWriter.println("0;JMP");
	}

	public void writeFunction(String functionName, int numLocals) {
		writeLabel(functionName);
		for (int i = 0; i < numLocals; i++) {
			writePushPop("PUSH", "constant", 0);
		}
	}

	public void writeArithmetic(String command) {

		// Pop the topmost value off the stack and put it in @R14
		fileWriter.println("@SP");
		fileWriter.println("M=M-1"); // Decrement @SP
		fileWriter.println("A=M");
		fileWriter.println("D=M"); // Get the value
		fileWriter.println("@R14");
		fileWriter.println("M=D"); // Store in @R14

		if (!command.equals("neg") && !command.equals("not")) { // Binary operations
			// Pop the topmost value again and put it in @R13
			fileWriter.println("@SP");
			fileWriter.println("M=M-1"); // Decrement @SP again
			fileWriter.println("A=M");
			fileWriter.println("D=M"); // Get the second value
			fileWriter.println("@R13");
			fileWriter.println("M=D"); // Store in @R13

			// Go to @R14 (the value of @R13 is already in D)
			fileWriter.println("@R14");
		}

		// Compute a value (stored in the D Register)
		switch (command) {
		case "add":
			fileWriter.println("D=D+M");
			break;
		case "sub":
			fileWriter.println("D=D-M");
			break;
		case "neg":
			fileWriter.println("D=-M");
			break;
		case "eq":
			fileWriter.println("D=D-M");
			fileWriter.println("@EQ" + accumulator);
			fileWriter.println("D;JEQ"); // Jump if equal
			fileWriter.println("D=0"); // Set to 0 (false)
			fileWriter.println("@COMPEND" + accumulator);
			fileWriter.println("0;JMP"); // Jump to the end of the comparison
			fileWriter.println("(EQ" + accumulator + ")");
			fileWriter.println("D=-1"); // Set to -1 (true)
			fileWriter.println("(COMPEND" + accumulator + ")");
			accumulator++;
			break;
		case "gt":
			fileWriter.println("D=D-M");
			fileWriter.println("@GT" + accumulator);
			fileWriter.println("D;JGT"); // Jump if D > M
			fileWriter.println("D=0"); // Set to 0 (false)
			fileWriter.println("@COMPEND" + accumulator);
			fileWriter.println("0;JMP"); // Jump to the end of the comparison
			fileWriter.println("(GT" + accumulator + ")");
			fileWriter.println("D=-1"); // Set to -1 (true)
			fileWriter.println("(COMPEND" + accumulator + ")");
			accumulator++;
			break;
		case "lt":
			fileWriter.println("D=D-M");
			fileWriter.println("@LT" + accumulator);
			fileWriter.println("D;JLT"); // Jump if D < M
			fileWriter.println("D=0"); // Set to 0 (false)
			fileWriter.println("@COMPEND" + accumulator);
			fileWriter.println("0;JMP"); // Jump to the end of the comparison
			fileWriter.println("(LT" + accumulator + ")");
			fileWriter.println("D=-1"); // Set to -1 (true)
			fileWriter.println("(COMPEND" + accumulator + ")");
			accumulator++;
			break;
		case "and":
			fileWriter.println("D=D&M");
			break;
		case "or":
			fileWriter.println("D=D|M");
			break;
		case "not":
			fileWriter.println("D=!M");
			break;
		}

		// Push the value in the D register to the stack
		fileWriter.println("@SP");
		fileWriter.println("A=M");
		fileWriter.println("M=D"); // Store in stack
		fileWriter.println("@SP");
		fileWriter.println("M=M+1"); // Increment @SP
	}

	public void writePushPop(String commandType, String segment, int index) {
		if (commandType.equals("PUSH")) {
			// Get the value we want to push...
			switch (segment) {
			case "constant":
				fileWriter.println("@" + index);
				fileWriter.println("D=A");
				break;
			case "local":
				fileWriter.println("@" + index);
				fileWriter.println("D=A"); // Get the index offset
				fileWriter.println("@LCL");
				fileWriter.println("A=D+M"); // Go to LCL + index
				fileWriter.println("D=M");
				break;
			case "argument":
				fileWriter.println("@" + index);
				fileWriter.println("D=A"); // Get the index offset
				fileWriter.println("@ARG");
				fileWriter.println("A=D+M"); // Go to ARG + index
				fileWriter.println("D=M");
				break;
			case "this":
				fileWriter.println("@" + index);
				fileWriter.println("D=A"); // Get the index offset
				fileWriter.println("@THIS");
				fileWriter.println("A=D+M"); // Go to THIS + index
				fileWriter.println("D=M");
				break;
			case "that":
				fileWriter.println("@" + index);
				fileWriter.println("D=A"); // Get the index offset
				fileWriter.println("@THAT");
				fileWriter.println("A=D+M"); // Go to THAT + index
				fileWriter.println("D=M");
				break;
			case "temp":
				fileWriter.println("@R" + (5 + index));
				fileWriter.println("D=M");
				break;
			case "pointer":
				fileWriter.println("@R" + (3 + index));
				fileWriter.println("D=M");
				break;
			case "static":
				fileWriter.println("@" + fileName + "." + index);
				fileWriter.println("D=M");
				break;
			}

			// Push the value onto the stack...
			// Go to the the stack pointer
			fileWriter.println("@SP");
			fileWriter.println("A=M");
			// Set the top stack value
			fileWriter.println("M=D");

			// And increment the stack pointer...
			fileWriter.println("@SP");
			fileWriter.println("M=M+1");
		} else { // POP
			// Get the value from the stack and store it in R13
			fileWriter.println("@SP");
			fileWriter.println("A=M-1");
			fileWriter.println("D=M"); // Get the value
			fileWriter.println("@R13");
			fileWriter.println("M=D"); // Store the value
			fileWriter.println("@SP");
			fileWriter.println("M=M-1"); // Decrement @SP

			// Find the location to store the value and store it in R14
			switch (segment) {
			case "local":
				fileWriter.println("@" + index);
				fileWriter.println("D=A"); // Get the index offset
				fileWriter.println("@LCL");
				fileWriter.println("D=D+M"); // Get LCL + index
				fileWriter.println("@R14");
				fileWriter.println("M=D"); // Store LCL + index
				break;
			case "argument":
				fileWriter.println("@" + index);
				fileWriter.println("D=A"); // Get the index offset
				fileWriter.println("@ARG");
				fileWriter.println("D=D+M"); // Get ARG + index
				fileWriter.println("@R14");
				fileWriter.println("M=D"); // Store ARG + index
				break;
			case "this":
				fileWriter.println("@" + index);
				fileWriter.println("D=A"); // Get the index offset
				fileWriter.println("@THIS");
				fileWriter.println("D=D+M"); // Get THIS + index
				fileWriter.println("@R14");
				fileWriter.println("M=D"); // Store THIS + index
				break;
			case "that":
				fileWriter.println("@" + index);
				fileWriter.println("D=A"); // Get the index offset
				fileWriter.println("@THAT");
				fileWriter.println("D=D+M"); // Get THAT + index
				fileWriter.println("@R14");
				fileWriter.println("M=D"); // Store THAT + index
				break;
			case "temp":
				fileWriter.println("@R" + (5 + index));
				fileWriter.println("D=A"); // Get the temp index
				fileWriter.println("@R14");
				fileWriter.println("M=D"); // Store the temp index
				break;
			case "pointer":
				fileWriter.println("@R" + (3 + index));
				fileWriter.println("D=A"); // Get the index of THIS or THAT
				fileWriter.println("@R14");
				fileWriter.println("M=D"); // Store the index
				break;
			case "static":
				fileWriter.println("@" + fileName + "." + index);
				fileWriter.println("D=A"); // Get the location of the static value
				fileWriter.println("@R14");
				fileWriter.println("M=D"); // Store the index
				break;
			}

			// Store the popped value in the designated location
			fileWriter.println("@R13");
			fileWriter.println("D=M"); // Get the value
			fileWriter.println("@R14");
			fileWriter.println("A=M"); // Go to the location
			fileWriter.println("M=D"); // Store the value
		}

	}

	public void close() {
		fileWriter.close();
	}
}

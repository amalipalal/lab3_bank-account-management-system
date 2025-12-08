package utils;

import java.util.Scanner;
import java.util.function.Function;

/**
 * Utility class for reading validated input from the console.
 * Supports integers, doubles, non-empty strings with custom validation, and yes/no questions.
 */
public class InputReader {

    private final Scanner scanner;

    public InputReader(Scanner scanner) {
        this.scanner = scanner;
    }

    /**
     * Reads an integer value from the console within a specified range.
     * Keeps prompting the user until a valid value is entered.
     *
     * @param prompt The message displayed to the user.
     * @param min The minimum acceptable value.
     * @param max The maximum acceptable value.
     * @return A valid integer input within [min, max].
     */
    public int readInt(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt + ": ");

            String input = this.scanner.nextLine();

            try {
                int value = Integer.parseInt(input);
                if(value < min || value > max) {
                    System.out.println("Please enter a number between " + min + " and " + max + ".");
                    continue;
                }
                return value;
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number.");
            }
        }
    }

    /**
     * Reads a double value from the console above a specified minimum.
     * Keeps prompting the user until a valid value is entered.
     *
     * @param prompt The message displayed to the user.
     * @param min The minimum acceptable value.
     * @return A valid double input greater than or equal to min.
     */
    public double readDouble(String prompt, double min) {
        while (true) {
            System.out.print(prompt + ": ");

            String input = this.scanner.nextLine();

            try {
                double value = Double.parseDouble(input);
                if(value < min) {
                    System.out.println("Please enter a number more than " + min + ".");
                    continue;
                }
                return value;
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number.");
            }

        }
    }

    /**
     * Reads a non-empty string from the console, validating it using the provided function.
     * Keeps prompting until a valid string is entered.
     *
     * @param prompt The message displayed to the user.
     * @param validator A function that returns null if input is valid, or an error message otherwise.
     * @return A valid string input according to the validator.
     */
    public String readNonEmptyString(String prompt, Function<String, String> validator) {
        while (true) {
            System.out.print(prompt + ": ");
            String input = this.scanner.nextLine().trim();

            String errorMessage = validator.apply(input);

            // The validator returns null if the input is valid
            // so this is the case where there is no error message
            if(errorMessage == null) {
                return input;
            }

            System.out.println(errorMessage);
        }
    }

    /**
     * Reads a yes/no input from the console.
     * Accepts "Y" or "N" (case-insensitive). Prompts until a valid input is given.
     *
     * @param prompt The message displayed to the user.
     * @return true if user enters "Y", false if user enters "N".
     */
    public boolean readYesOrNo(String prompt) {
        while (true) {
            System.out.print(prompt + ": ");

            String input = this.scanner.nextLine();

            switch (input.toLowerCase()) {
                case "y":
                    return true;
                case "n":
                    return false;
                default:
                    System.out.println("Invalid input: Please enter Y or N.");
            }
        }
    }
}

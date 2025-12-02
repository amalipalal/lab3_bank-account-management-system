package utils;

import java.util.Scanner;
import java.util.function.Function;

public class InputReader {

    private final Scanner scanner;

    public InputReader(Scanner scanner) {
        this.scanner = scanner;
    }

    /// Read integer values from console as strings to be converted
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

    ///  Read float and double values from console but as strings to be converted
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

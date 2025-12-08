package utils;

/**
 * Utility class for validating user input such as names, phone numbers, account numbers, and addresses.
 * Each method returns a string containing an error message if the input is invalid, or null if valid.
 */
public class ValidationUtil {

    /**
     * Validates a name. Must be at least 2 characters and contain only letters and spaces.
     *
     * @param name The name to validate.
     * @return An error message if invalid, otherwise null.
     */
    public static String validateName(String name) {
        if (name == null || name.isEmpty()) return "Name cannot be empty.";
        if (!name.matches("^[A-Za-z ]{2,}$")) return "Name must contain only letters and spaces, and be at least 2 characters long.";
        return null;
    }

    /**
     * Validates a Ghanaian phone number. Must start with +233 followed by 9 digits.
     *
     * @param phone The phone number to validate.
     * @return An error message if invalid, otherwise null.
     */
    public static String validatePhoneNumber(String phone) {
        if (phone == null || phone.isEmpty()) return "Phone number cannot be empty.";
        if (!phone.startsWith("+233")) return "Phone number must start with +233.";
        if (!phone.matches("^\\+233\\d{9}$")) return "Phone number must have 9 digits after +233.";
        return null;
    }

    /**
     * Validates an account number. Must follow the format 'ACC' + 3 digits (e.g., ACC123).
     *
     * @param number The account number to validate.
     * @return An error message if invalid, otherwise null.
     */
    public static String validateAccountNumber(String number) {
        if (number == null || number.isEmpty()) return "Account number cannot be empty.";
        if (!number.matches("^ACC\\d{3}$")) return "Account number must start with 'ACC' followed by 3 digits (e.g., ACC123).";
        return null;
    }

    /**
     * Validates a postal address. Must be 5–100 characters and can include letters, numbers,
     * spaces, commas, periods, apostrophes, and hyphens.
     *
     * @param address The address to validate.
     * @return An error message if invalid, otherwise null.
     */
    public static String validateAddress(String address) {
        if (address == null || address.isEmpty()) return "Address cannot be empty.";
        if (!address.matches("^[A-Za-z0-9\\s,.'\\-]{5,100}$")) return "Address must be 5–100 characters and can include letters, numbers, spaces, commas, periods, apostrophes, and hyphens.";
        return null;
    }
}

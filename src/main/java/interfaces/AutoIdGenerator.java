package interfaces;

/**
 * Provides a contract for generating unique IDs and retrieving ID-related information.
 * Implementations may generate IDs for accounts, customers, transactions, etc.
 */
public interface AutoIdGenerator {

    /**
     * Generates a new unique ID.
     *
     * @return A newly generated unique ID as a {@link String}.
     */
    String generateId();

    /**
     * Returns the current counter value used by the generator.
     * Typically, represents how many IDs have been generated so far.
     *
     * @return The current ID counter.
     */
    int getCounter();

    /**
     * Extracts the internal index or numeric portion from a given ID string.
     *
     * @param idNumber The ID string to extract the index from.
     * @return The numeric value inside the ID.
     */
    int extractIndex(String idNumber);

    /**
     * Ensures the count of the number of ids that have been generated
     * are up to date.
     *
     * @param count of the of ids generated on startup
     */
    void setIdCounter(int count);
}

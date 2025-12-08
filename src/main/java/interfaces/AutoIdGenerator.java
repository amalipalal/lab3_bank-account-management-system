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
     * Useful for mapping IDs to array indices or storage positions.
     *
     * @param idNumber The ID string to extract the index from.
     * @return The numeric index extracted from the ID.
     */
    int extractIndex(String idNumber);
}

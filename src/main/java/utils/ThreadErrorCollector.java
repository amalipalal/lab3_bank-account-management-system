package utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A thread-safe collector for capturing and displaying errors produced by
 * concurrent transaction tasks. Errors are stored in a synchronized list and
 * can be retrieved, displayed, cleared, or counted for reporting purposes.
 */
public class ThreadErrorCollector {
    private int PREVIOUS_ERROR_COUNT = 0;

    private final List<String> errors = Collections.synchronizedList(new ArrayList<>());

    /**
     * Records an error reported by a worker thread.
     * <p>
     * Each call represents a failure in an individual task and
     * contributes to the batch error count.
     * </p>
     *
     * @param msg the error message describing the failure
     */
    public void addError(String msg) {
        errors.add(msg);
        PREVIOUS_ERROR_COUNT += 1;
    }

    /**
     * Indicates whether any errors occurred during
     * the current batch.
     *
     * @return true if at least one error was collected
     */
    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    /**
     * Displays all captured errors to the user as
     * a grouped report.
     * <p>
     * This is typically called after all concurrent task
     * have finished
     * </p>
     */
    public void showErrors() {
        errors.forEach(DisplayUtil::displayNotice);
    }

    /**
     * Resets the batch error count while leaving the
     * collected messages intact.
     * <p>
     * Useful when the calling code wants to record a fresh
     * batch of errors.
     * </p>
     */
    public void resetCount() {
        PREVIOUS_ERROR_COUNT = 0;
    }

    /**
     * Clears all collected error messages from
     * the current batch.
     */
    public void clearErrors() {
        this.errors.clear();
    }

    public int getPREVIOUS_ERROR_COUNT() {
        return PREVIOUS_ERROR_COUNT;
    }

    public List<String> getErrors() {
        return List.copyOf(errors);
    }
}

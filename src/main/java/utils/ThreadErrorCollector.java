package utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ThreadErrorCollector {
        private int PREVIOUS_ERROR_COUNT = 0;

        private final List<String> errors = Collections.synchronizedList(new ArrayList<>());

        public void addError(String msg) {
            errors.add(msg);
            PREVIOUS_ERROR_COUNT += 1;
        }

        public List<String> getErrors() {
            return List.copyOf(errors);
        }

        public boolean hasErrors() {
            return !errors.isEmpty();
        }

        public void showErrors() {
            errors.forEach(DisplayUtil::displayNotice);
        }

        public void resetCount() {
            PREVIOUS_ERROR_COUNT = 0;
        }

        public int getPREVIOUS_ERROR_COUNT() {
            return PREVIOUS_ERROR_COUNT;
        }

        public void clearErrors() {
            this.errors.clear();
        }
}

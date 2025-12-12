package utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ThreadErrorCollector {
        private final List<String> errors = Collections.synchronizedList(new ArrayList<>());

        public void addError(String msg) {
            errors.add(msg);
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
}

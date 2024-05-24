package common;

import java.io.Serializable;

public class MessageProtocol {
    public static final String INDEX = "INDEX";
    public static final String SEARCH = "SEARCH";
    public static final String RESULT = "RESULT";

    public static String createIndexMessage(String word, String documentPath) {
        return INDEX + " " + word + " " + documentPath;
    }

    public static String createSearchMessage(String term) {
        return SEARCH + " " + term;
    }

    public static String createResultMessage(String word, String documentPath, int frequency) {
        return RESULT + " " + word + " " + documentPath + " " + frequency;
    }

    public static class IndexMessage implements Serializable {
        private final String word;
        private final String documentPath;

        public IndexMessage(String word, String documentPath) {
            this.word = word;
            this.documentPath = documentPath;
        }

        public String getWord() {
            return word;
        }

        public String getDocumentPath() {
            return documentPath;
        }
    }

    public static class SearchMessage implements Serializable {
        private final String term;

        public SearchMessage(String term) {
            this.term = term;
        }

        public String getTerm() {
            return term;
        }
    }

    public static class ResultMessage implements Serializable {
        private final String word;
        private final String documentPath;
        private final int frequency;

        public ResultMessage(String word, String documentPath, int frequency) {
            this.word = word;
            this.documentPath = documentPath;
            this.frequency = frequency;
        }

        public String getWord() {
            return word;
        }

        public String getDocumentPath() {
            return documentPath;
        }

        public int getFrequency() {
            return frequency;
        }
    }
}

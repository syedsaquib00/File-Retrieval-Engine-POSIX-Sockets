package common;


public class SearchResult {
    private String fileName;
    private int occurrences;

    public SearchResult(String fileName, int occurrences) {
        this.fileName = fileName;
        this.occurrences = occurrences;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getOccurrences() {
        return occurrences;
    }

    public void setOccurrences(int occurrences) {
        this.occurrences = occurrences;
    }

    @Override
    public String toString() {
        return "SearchResult{" +
                "fileName='" + fileName + '\'' +
                ", occurrences=" + occurrences +
                '}';
    }
}
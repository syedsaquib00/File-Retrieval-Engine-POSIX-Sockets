package client;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import common.IndexStore;
import common.MessageProtocol;
import common.SearchResult;

public class ClientProcessingEngine {
    private ClientConnection connection;
    private int numThreads=4;
    private IndexStore indexStore=new IndexStore();
    
    

    public void connect(String serverIp, int port) {
        try {
            connection = new ClientConnection(serverIp, port);
            System.out.println("Connected to server " + serverIp + " on port " + port);
        } catch (IOException e) {
            System.err.println("Failed to connect to server: " + e.getMessage());
        }
    }

    public void quit() {
        if (connection != null) {
            connection.close();
            System.out.println("Disconnected from server.");
        }
    }
    
    
    public void index(String datasetPath) {
    	
    	if (connection == null) {
          System.out.println("Not connected to server.");
          return;
         }
    	
    	
    	long startTime = System.currentTimeMillis();
    	
    	// Create thread pool
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);

        // Traverse dataset and submit indexing tasks to executor
        indexDataset(new File(datasetPath), executor);

        //wall time
        long endTime = System.currentTimeMillis();
        long elapsedTime = endTime - startTime;
        
        //Dataset size
        double datasetSizeMB = getTotalDatasetSizeMB(datasetPath);
    	double result1 = Double.parseDouble(String.format("%.4f", datasetSizeMB));
        
    	//Throughput
        double throughputMBs = datasetSizeMB / (elapsedTime / 1000.0); // Convert to seconds
        double result = Double.parseDouble(String.format("%.4f", throughputMBs));
        
        //Print the values
        System.out.println("Size of the dataset: " + result1 + " MB");
        System.out.println("Wall time for indexing: " + elapsedTime + " milliseconds");
        System.out.println("Throughput in MB/s: "+result+" MB/s");
        
        // Shutdown executor after all tasks are complete
        executor.shutdown();

    }
    
    private void indexDataset(File datasetDir, ExecutorService executor) {
        // Check if datasetDir is a directory
        if (!datasetDir.isDirectory()) {
            System.err.println("Invalid dataset path: " + datasetDir);
            return;
        }

        // Traverse dataset and submit indexing tasks to executor
        File[] files = datasetDir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    // Recursively index files in subfolders
                    indexDataset(file, executor);
                } else {
                    // Submit indexing task to executor
                    executor.submit(new IndexingTask(file));
                }
            }
        }
    }
    

    public List<SearchResult> search(String query) {
    	
      if (connection == null) {
      System.out.println("Not connected to server.");
      return null;
  }

        long startTime = System.currentTimeMillis();
        List<String> terms = parseQuery(query);
        Map<File, Integer> fileOccurrences = new HashMap<>();

        // Initialize a set to store files containing all query terms
        Set<File> filesContainingAllTerms = new HashSet<>();

        for (String term : terms) {
            // Lookup occurrences of the term in the index
            Map<File, Integer> termOccurrences = indexStore.lookup(term);
            for (Map.Entry<File, Integer> entry : termOccurrences.entrySet()) {
                File file = entry.getKey();
                // Increment the frequency of occurrences for the file
                int frequency = entry.getValue();
                int currentFrequency = fileOccurrences.getOrDefault(file, 0);
                fileOccurrences.put(file, currentFrequency + frequency);
                // Check if the file contains all query terms
                if (!filesContainingAllTerms.contains(file)) {
                    boolean allTermsPresent = true;
                    for (String queryTerm : terms) {
                        if (!indexStore.lookup(queryTerm).containsKey(file)) {
                            allTermsPresent = false;
                            break;
                        }
                    }
                    // If all terms are present, add the file to the set
                    if (allTermsPresent) {
                        filesContainingAllTerms.add(file);
                    }
                }
            }
        }

        // Sort files by the total occurrences of query terms
        List<File> sortedFiles = new ArrayList<>(filesContainingAllTerms);
        sortedFiles.sort((file1, file2) -> fileOccurrences.get(file2).compareTo(fileOccurrences.get(file1)));

        // Create SearchResult objects for the top files
        List<SearchResult> searchResults = new ArrayList<>();
        for (File file : sortedFiles.subList(0, Math.min(sortedFiles.size(), 10))) {
            int occurrences = fileOccurrences.get(file);
            searchResults.add(new SearchResult(file.getName(), occurrences));
        }

        long endTime = System.currentTimeMillis();
        long elapsedTime = endTime - startTime;
        System.out.println("Wall time for search: " + elapsedTime + " milliseconds");
        return searchResults;
    }



    private List<String> parseQuery(String query) {
        String[] terms = query.split("\\s+AND\\s+");
        return Arrays.asList(terms);
    }

    private class IndexingTask implements Runnable {
        private File file;

        public IndexingTask(File file) {
            this.file = file;
        }

        @Override
        public void run() {
            indexFile(file);
        }
    }

    private void indexFile(File file) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] words = line.split("\\s+");
                for (String word : words) {
                    String term = word.replaceAll("[^a-zA-Z0-9]", "").toLowerCase();
                    int currentFrequency = indexStore.lookup(term).getOrDefault(file, 0);
                    indexStore.update(term, file, currentFrequency + 1);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private static double getTotalDatasetSizeMB(String datasetPath) {
        File datasetDir = new File(datasetPath);
        return getTotalSizeInMB(datasetDir);
    }

    private static double getTotalSizeInMB(File file) {
        if (file.isFile()) {
            return (double) file.length() / (1024 * 1024);
        }

        double totalSize = 0.0;
        File[] files = file.listFiles();
        if (files != null) {
            for (File subFile : files) {
                totalSize += getTotalSizeInMB(subFile);
            }
        }
        return totalSize;
    }

    
    
    
    
    
    


//    public void index(String folderPath) {
//        if (connection == null) {
//            System.out.println("Not connected to server.");
//            return;
//        }
//
//        File folder = new File(folderPath);
//        if (!folder.isDirectory()) {
//            System.out.println("Invalid folder path.");
//            return;
//        }
//
//        File[] files = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".txt"));
//        if (files != null) {
//            for (File file : files) {
//                try {
//                    List<String> lines = java.nio.file.Files.readAllLines(file.toPath());
//                    for (String line : lines) {
//                        String[] words = line.split("\\W+");
//                        for (String word : words) {
//                            if (!word.isEmpty()) {
//                                connection.sendIndexMessage(word.toLowerCase(), file.getAbsolutePath());
//                            }
//                        }
//                    }
//                    System.out.println("Indexed file: " + file.getAbsolutePath());
//                } catch (IOException e) {
//                    System.err.println("Failed to read file: " + file.getAbsolutePath());
//                }
//            }
//        }
//    }
//
//    public void search(String query) {
//        if (connection == null) {
//            System.out.println("Not connected to server.");
//            return;
//        }
//
//        String[] terms = query.split("\\s+AND\\s+");
//        for (String term : terms) {
//            connection.sendSearchMessage(term.toLowerCase());
//        }
//
//        List<String> results = connection.receiveSearchResults();
//        System.out.println("Search results:");
//        for (String result : results) {
//            System.out.println(result);
//        }
//    }

//public void search(String query) {
//    if (connection == null) {
//        System.out.println("Not connected to server.");
//        return;
//    }
//
//    // Send search message
//    try {
//        connection.sendIndexMessage(query);
//    } catch (IOException e) {
//        System.err.println("Error sending search message: " + e.getMessage());
//        return;
//    }
//
//    // Receive search results
//    List<SearchResult> results;
//    try {
//        results = connection.receiveSearchResults();
//    } catch (IOException | ClassNotFoundException e) {
//        System.err.println("Error receiving search results: " + e.getMessage());
//        return;
//    }
//
//    // Print search results
//    System.out.println("Search results:");
//    for (SearchResult result : results) {
//        System.out.println(result);
//    }
//}


}

package com.flink.demo.predictor;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class WordPredictor implements Serializable {

    // Data structure holding bi-grams as the data arrives.
    // Guava's Table interface is ideal for this use case it's similar to map of maps.
    // K1 and K2 forms the bi-grams and the Value tracks the number of occurrences/ probability
    private final Table<String, String, Integer> biGramWordTable;

    // State to track current word. When the new word arrives, currentWord and newWord forms
    // the bi-gram pair. The currentWord shall then be updated.
    private String currentWord;

    public WordPredictor() {
        this.biGramWordTable = HashBasedTable.create();
        this.currentWord = "";
    }

    public List<String> predict(final String newWord) {
        // Case#1 - the first word of the stream has arrived
        if (this.currentWord.isEmpty()) {
            this.currentWord = newWord;
            return Collections.emptyList();
        }

        List<String> predictions = Collections.emptyList();
        // Search table for entries with newWord as primary key
        Map<String, Integer> pairs = this.biGramWordTable.row(newWord);
        if (!pairs.isEmpty()) {
            // Sort the collection with descending order of number of occurrences / probability value key
            predictions = this.getPredictions(pairs);
        }

        // Add the newly formed bi-gram [currentWord, newWord] to table model
        this.addToTable(this.currentWord, newWord);

        // Update currentWord
        this.currentWord = newWord;

        return predictions;
    }

    private void addToTable(final String key1, final String key2) {
        try {
            // 1. Check if model already contains the pair - [key1, key2]
            int occurrences = this.biGramWordTable.get(key1, key2);

            // 2. Model already contains the pair [key1, key2]
            // 3. Increment number of occurrences and update table model
            occurrences = occurrences + 1;
            this.biGramWordTable.put(key1, key2, occurrences);
        } catch (NullPointerException ex) {
            // [key1, key2] is the first entry in the table
            this.biGramWordTable.put(key1, key2, 1);
        }
    }

    private List<String> getPredictions(final Map<String, Integer> pairs) {
        // Sort descending order of occurrences of value
        Map<String, Integer> sortedPairs = pairs
                .entrySet()
                .stream()
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (e1, e2) -> e2, LinkedHashMap::new));

        // Collect top five predictions from the above sorted map based on probability of occurrences
        return sortedPairs
                .keySet()
                .stream()
                .limit(5)
                .collect(Collectors.toList());
    }

}
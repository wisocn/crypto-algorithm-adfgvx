/*
 *	Adfgvx.java
 *
 *	17.10.2016
 *
 * Copyright 2014 SRC d.o.o., Trzaska cesta 116,
 * 1001 Ljubljana, Slovenia. All Rights Reserved.
 */
package io.algorithm.crypto.chiper;

import java.util.*;
import java.util.logging.Logger;

/**
 * ADFGVX alghorithm encrypting
 * and decrypting process
 *
 * @author vidvisocnik
 * @since 17.10.2016
 */
public class Adfgvx {

    /**
     * Logger definition
     */
    private static final Logger logger = Logger.getLogger(Adfgvx.class.getName());

    /**
     * Columns for matrix for ADFGVX chiper
     */
    private final char[] columns = {'A', 'D', 'F', 'G', 'V', 'X'};

    /**
     * Chiper matrix with English alphabeth
     */
    private final char[][] chiperMatrix = {
            {'n', 'a', '1', 'c', '3', 'h'},
            {'8', 't', 'b', '2', 'o', 'm'},
            {'e', '5', 'y', 'r', 'p', 'd'},
            {'4', 'f', '6', 'g', '7', 'i'},
            {'9', 'j', '0', 'k', 'l', 'q'},
            {'s', 'u', 'v', 'w', 'x', 'z'}
    };

    /**
     * Default chiper key.
     */
    private char[] chiperKey = {'P', 'R', 'I', 'V', 'A', 'C', 'Y'};

    /**
     * Replacement map where the key is character of english alphabeth
     * and value is a replacement for that character.
     */
    private Map<Character, Replacement> replacementMap = new HashMap<>();

    /**
     * Replacement map where the key is replaced character combination
     * and value is a character of english alphabeth.
     */
    private Map<Replacement, Character> inverseReplacementMap = new HashMap<>();

    /**
     * Default constructor
     */
    public Adfgvx() {
        prepareReplacementMaps();
    }

    /**
     * Constructor with posibility of key overriding
     *
     * @param key overided key
     */
    public Adfgvx(String key) {

        if (key == null || key.isEmpty()) {
            final String errorDescription = "Please provide valid key";
            throw new IllegalArgumentException(errorDescription);
        }

        prepareReplacementMaps();
        this.chiperKey = key.toCharArray();
    }

    /**
     * Encrypting String that is being passed into method.
     *
     * @param unencrypted un-encrypted textual representation.
     * @return ADFGVX encrypted String
     */
    public String encrypt(String unencrypted) {

        char[] sanitizedInput = sanitizeInput(unencrypted).trim().toLowerCase().toCharArray();

        logger.fine("Chipering text : " + new String(sanitizedInput));
        logger.fine("Length of text : " + sanitizedInput.length);

        StringBuilder chiperTextBuilder = new StringBuilder();
        for (int i = 0; i < sanitizedInput.length; i++) {
            Replacement replacement = replacementMap.get(sanitizedInput[i]);
            logger.fine("Replacment for character : "
                    + sanitizedInput[i] + " is : "
                    + String.valueOf(replacement.getColumn()) + String.valueOf(replacement.getLine()));
            chiperTextBuilder.append(replacement.getColumn());
            chiperTextBuilder.append(replacement.getLine());
        }

        String replacementUnorderedString = chiperTextBuilder.toString();
        if (isShorterThanChiperKey(replacementUnorderedString)) {
            replacementUnorderedString = appendToChiperLength(replacementUnorderedString);
        }

        logger.fine(">> replaced characters size := " + replacementUnorderedString.length());

        char[][] equalLengthCharacterMatrix = splitStrings(replacementUnorderedString);

        TreeMap<Character, char[]> alphabeticalsorting = new TreeMap<>();
        for (int j = 0; j < equalLengthCharacterMatrix[0].length; j++) {
            char[] transponed = new char[equalLengthCharacterMatrix.length];
            for (int i = 0; i < equalLengthCharacterMatrix.length; i++) {
                transponed[i] = equalLengthCharacterMatrix[i][j];
            }
            alphabeticalsorting.put(chiperKey[j], transponed);
        }

        chiperTextBuilder = new StringBuilder();
        for (Map.Entry<Character, char[]> characterEntry : alphabeticalsorting.entrySet()) {
            char[] array = characterEntry.getValue();
            for (char c : array) {
                if (c != '\0') {
                    chiperTextBuilder.append(String.valueOf(c));
                }
            }
            chiperTextBuilder.append(" ");
        }

        return chiperTextBuilder.toString();
    }

    /**
     * Spliting chiper text into key length
     * array which are grouped into matrix.
     *
     * @param chiperText contatinated string
     * @return
     */
    private char[][] splitStrings(String chiperText) {

        int totalSubstrings = (int) Math.ceil((double) chiperText.length() / this.chiperKey.length);
        char[][] characterMatrix = new char[totalSubstrings][this.chiperKey.length];

        int index = 0;
        for (int i = 0; i < chiperText.length(); i = i + this.chiperKey.length) {
            String keyLengthColumnString = chiperText.substring(i, Math.min(i + this.chiperKey.length, chiperText.length()));
            if (isShorterThanChiperKey(keyLengthColumnString)) {
                logger.fine(">> keyLengthString shorter than key - appending");
                keyLengthColumnString = appendToChiperLength(keyLengthColumnString);
            }

            characterMatrix[index++] = keyLengthColumnString.toCharArray();
        }

        return characterMatrix;
    }

    /**
     * Appends NUL Ascii value to a string that is
     * shorter and key length.
     *
     * @param shorterString shorter String than key
     * @return Appended string with length of key.
     */
    private String appendToChiperLength(String shorterString) {

        for (int i = shorterString.length(); i < chiperKey.length; i++) {
            shorterString = shorterString.concat("\0");
        }

        return shorterString;
    }

    /**
     * Checks if chiper text is shorter than key length.
     *
     * @param chiperText chiperText
     * @return If chiper text is shorter
     * than key return true otherwise false.
     */
    private boolean isShorterThanChiperKey(String chiperText) {
        return chiperText.length() > 0 && chiperText.length() < chiperKey.length;
    }

    /**
     * Sanitze input text by removing special
     * characters {: , . -} and UTF8-BOM
     *
     * @param unencrypted unencrypted text
     * @return sanitized String
     */
    private String sanitizeInput(String unencrypted) {
        unencrypted = unencrypted.replaceAll("\\s", "");
        unencrypted = unencrypted.replaceAll("\\:", "");
        unencrypted = unencrypted.replaceAll("\\,", "");
        unencrypted = unencrypted.replaceAll("\\.", "");
        unencrypted = unencrypted.replaceAll("\\-", "");
        unencrypted = unencrypted.replaceAll("\"", "");
        unencrypted = unencrypted.replaceAll("\uFEFF", ""); // odstrani UTF8-BOM
        return unencrypted;
    }

    /**
     * Decryption method of ADFGVX encryption
     *
     * @param encrypted ADFGVX encrypted String
     * @return decrypted String
     */
    public String decrypt(String encrypted) {

        logger.fine(">> decryption of : " + encrypted);
        logger.fine(">> with key : " + String.valueOf(this.chiperKey));
        // split input at spaces
        String[] splitedString = encrypted.split("\\s");

        // creating temporary sorted key.
        char[] temporaryKey = chiperKey.clone();
        Arrays.sort(temporaryKey);

        Map<Character, char[]> characterSortedMap = new HashMap<>();
        for (int i = 0; i < splitedString.length; i++) {
            characterSortedMap.put(temporaryKey[i], splitedString[i].toCharArray());
        }

        LinkedHashMap<Character, char[]> keyBasedSortingMap = new LinkedHashMap<>();
        for (int i = 0; i < chiperKey.length; i++) {
            char[] characterArray = characterSortedMap.get(chiperKey[i]);
            keyBasedSortingMap.put(chiperKey[i], characterArray);
        }

        char[][] matrix = new char[getLongestArray(characterSortedMap)][chiperKey.length];

        int index = 0;
        for (Map.Entry<Character, char[]> keyBasedEntrySet : keyBasedSortingMap.entrySet()) {
            char[] chiperedArray = keyBasedEntrySet.getValue();

            if (chiperedArray == null) {
                continue;
            }

            for (int j = 0; j < chiperedArray.length; j++) {
                matrix[j][index] = chiperedArray[j];
            }

            index += 1;
        } // end for

        StringBuilder dechiperedTextBuilder = new StringBuilder();
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                char crka = matrix[i][j];
                if (crka != '\0') {
                    dechiperedTextBuilder.append(matrix[i][j]);
                }
            }
        }

        logger.fine(dechiperedTextBuilder.toString());
        // dolzina kljucev horizontalno je vedno sodo število.
        StringBuilder dechiperedText = new StringBuilder();
        for (int i = 0; i < dechiperedTextBuilder.toString().length(); i += 2) {
            char[] crki = dechiperedTextBuilder.substring(i, i + 2).toCharArray();
            Replacement replacement = new Replacement(crki[0], crki[1]);
            dechiperedText.append(inverseReplacementMap.get(replacement));
        }

        return dechiperedText.toString();
    }

    /**
     * Stream whole map and find
     * the longest array (by length) in map
     *
     * @param characterSortedMap
     * @return the length of longest
     */
    private int getLongestArray(Map<Character, char[]> characterSortedMap) {
        return characterSortedMap
                .entrySet()
                .stream()
                .max((mapEntry, mapEntryComparing)
                        -> mapEntry.getValue().length > mapEntryComparing.getValue().length ? 1 : -1)
                .get().getValue().length;
    }


    /**
     * Preparing map {@link Adfgvx#replacementMap}
     * and {@link Adfgvx#inverseReplacementMap}
     * for replacement purposes.
     */
    private void prepareReplacementMaps() {
        for (int i = 0; i < chiperMatrix.length; i++) {
            for (int j = 0; j < chiperMatrix[i].length; j++) {
                Replacement replacementForCharater = new Replacement(columns[i], columns[j]);
                this.replacementMap.put(chiperMatrix[i][j], replacementForCharater);
                this.inverseReplacementMap.put(replacementForCharater, chiperMatrix[i][j]);
            }
        }
    }
}

/**
 * Replacemnt class for easier structure
 * and character replacement.
 */
class Replacement {
    Character column;
    Character line;

    public Replacement(Character kolona, Character vrstica) {
        this.column = kolona;
        this.line = vrstica;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Replacement that = (Replacement) o;

        if (column != null ? !column.equals(that.column) : that.column != null) return false;
        return line != null ? line.equals(that.line) : that.line == null;
    }

    @Override
    public int hashCode() {
        int result = column != null ? column.hashCode() : 0;
        result = 31 * result + (line != null ? line.hashCode() : 0);
        return result;
    }

    public char getColumn() {
        return column;
    }

    public char getLine() {
        return line;
    }
}

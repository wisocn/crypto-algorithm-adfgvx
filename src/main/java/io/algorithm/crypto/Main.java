package io.algorithm.crypto;

import io.algorithm.crypto.cipher.Adfgvx;
import io.algorithm.crypto.logger.CustomLogFormat;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 * Main class for ADFGVX cipher showcase.
 * <p>
 * <p>
 * In cryptography, the ADFGVX cipher was a field cipher used by the German Army
 * on the Western Front during World War I. ADFGVX was in fact an extension of an earlier cipher called ADFGX.
 * Invented by Lieutenant[1] Fritz Nebel (1891–1977)[2] and introduced in March 1918,
 * the cipher was a fractionating transposition cipher which combined a modified Polybius square
 * with a single columnar transposition.
 * <p>
 * The cipher is named after the six possible letters used in the ciphertext: A, D, F, G, V and X.
 * Letters were chosen deliberately because they are very different from one another in the Morse code.
 * That reduced the possibility of operator error.
 * <p>
 * Nebel designed the cipher to provide an army on the move with encryption that
 * was more convenient than trench codes but was still secure.
 * <p>
 * In fact, the German believed the ADFGVX cipher was unbreakable
 * source: https://en.wikipedia.org/wiki/ADFGVX_cipher
 * <p>
 * Java implementation of ADFGVX cipher
 *
 * @author vidvisocnik
 * @created 30.08.2018
 */
public class Main {

    /**
     * Adfgvx instance of cipher
     */
    private static Adfgvx adfgvx = new Adfgvx();

    /**
     * Logger definition
     */
    private static final Logger logger = Logger.getLogger(Main.class.getName());

    /**
     * General logger configuration
     */
    static {
        Logger rootLogger = LogManager.getLogManager().getLogger("");
        rootLogger.setLevel(Level.FINE);
        for (Handler h : rootLogger.getHandlers()) {
            h.setLevel(Level.FINE);
            h.setFormatter(new CustomLogFormat());
        }
        logger.setLevel(Level.FINE);
    }

    /**
     * Ignition method.
     * @param args {0} text to encrypt
     */
    public static void main(String args[]) {

        String textToEncypt = (args == null || args.length == 0)
                ? "attack at 12am" : args[0];

        String encryptedText = adfgvx.encrypt(textToEncypt);
        logger.fine("encrpyted text : " + encryptedText);
        String decrypted = adfgvx.decrypt(encryptedText);
        logger.fine("Decrypted: " + decrypted);
    }

}

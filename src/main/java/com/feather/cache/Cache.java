package com.feather.cache;

import com.alex.io.OutputStream;
import com.alex.store.Store;
import com.alex.util.whirlpool.Whirlpool;
import com.feather.Settings;
import com.feather.utils.Logger;
import com.feather.utils.Utils;

import java.io.IOException;

public final class Cache {

    private static final int WHIRLPOOL_HASH_SIZE = 64;
    private static final int SIGNATURE_HEADER_SIZE = 65;

    public static Store store;

    private Cache() {
        // Private constructor to prevent instantiation
    }

    /**
     * Initializes the cache store with the configured cache path.
     *
     * @throws IOException if the store cannot be initialized
     */
    public static void init() throws IOException {
        try {
            store = new Store(Settings.CACHE_PATH);
            Logger.log("Cache", "Cache store initialized successfully");
        } catch (IOException e) {
            Logger.log("Cache", "Failed to initialize cache store");
            throw e;
        }
    }

    /**
     * Gets the initialized store instance.
     *
     * @return the store instance
     * @throws IllegalStateException if the cache hasn't been initialized
     */
    public static Store getStore() {
        if (store == null) {
            throw new IllegalStateException("Cache not initialized. Call init() first.");
        }
        return store;
    }

    /**
     * Generates a UKEYS file containing index information with cryptographic signature.
     *
     * @return byte array representing the UKEYS file
     * @throws IllegalStateException if the cache hasn't been initialized
     */
    public static byte[] generateUkeysFile() {
        if (store == null) {
            throw new IllegalStateException("Cache not initialized. Call init() first.");
        }

        try {
            byte[] indexData = buildIndexData();
            byte[] signature = createSignature(indexData);
            return combineDataWithSignature(indexData, signature);

        } catch (Exception e) {
            Logger.log("Cache", "Failed to generate UKEYS file");
            throw new RuntimeException("Failed to generate UKEYS file", e);
        }
    }

    /**
     * Builds the index data portion of the UKEYS file.
     */
    private static byte[] buildIndexData() {
        OutputStream stream = new OutputStream();
        var indexes = store.getIndexes();

        stream.writeByte(indexes.length);

        for (int i = 0; i < indexes.length; i++) {
            writeIndexEntry(stream, indexes[i]);
        }

        return extractBytes(stream);
    }

    /**
     * Writes a single index entry to the output stream.
     */
    private static void writeIndexEntry(OutputStream stream, Object index) {
        if (index == null) {
            writeNullIndexEntry(stream);
        } else {
            writeValidIndexEntry(stream, index);
        }
    }

    /**
     * Writes a null index entry with default values.
     */
    private static void writeNullIndexEntry(OutputStream stream) {
        stream.writeInt(0);
        stream.writeInt(0);
        stream.writeBytes(new byte[WHIRLPOOL_HASH_SIZE]);
    }

    /**
     * Writes a valid index entry with actual data.
     */
    private static void writeValidIndexEntry(OutputStream stream, Object index) {
        // Using reflection-like calls - adjust based on actual Index class API
        try {
            int crc = (Integer) index.getClass().getMethod("getCRC").invoke(index);
            Object table = index.getClass().getMethod("getTable").invoke(index);
            int revision = (Integer) table.getClass().getMethod("getRevision").invoke(table);
            byte[] whirlpool = (byte[]) index.getClass().getMethod("getWhirlpool").invoke(index);

            stream.writeInt(crc);
            stream.writeInt(revision);
            stream.writeBytes(whirlpool);

        } catch (Exception e) {
            Logger.log("Cache", "ERROR - Failed to write index entry, writing null entry instead");
            writeNullIndexEntry(stream);
        }
    }

    /**
     * Creates a cryptographic signature for the given data.
     */
    private static byte[] createSignature(byte[] data) {
        OutputStream hashStream = new OutputStream(SIGNATURE_HEADER_SIZE);

        // Add signature header
        hashStream.writeByte(0);

        // Generate and add Whirlpool hash
        byte[] hash = Whirlpool.getHash(data, 0, data.length);
        hashStream.writeBytes(hash);

        // Extract hash data
        byte[] hashData = extractBytes(hashStream);

        // Apply RSA encryption to the hash
        return Utils.cryptRSA(
                hashData,
                Settings.GRAB_SERVER_PRIVATE_EXPONENT,
                Settings.GRAB_SERVER_MODULUS
        );
    }

    /**
     * Combines the index data with its signature.
     */
    private static byte[] combineDataWithSignature(byte[] indexData, byte[] signature) {
        OutputStream stream = new OutputStream();
        stream.writeBytes(indexData);
        stream.writeBytes(signature);
        return extractBytes(stream);
    }

    /**
     * Extracts bytes from an OutputStream, resetting the offset in the process.
     */
    private static byte[] extractBytes(OutputStream stream) {
        byte[] result = new byte[stream.getOffset()];
        stream.setOffset(0);
        stream.getBytes(result, 0, result.length);
        return result;
    }
}
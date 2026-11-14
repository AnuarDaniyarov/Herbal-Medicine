package kz.iitu.medicineplatform.search;

import com.pgvector.PGvector;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

/**
 * Generates fixed-length vector embeddings from text using a hash-based approach.
 * This is a lightweight alternative to neural embeddings that works without external ML models.
 * Produces 384-dimensional vectors suitable for pgvector cosine similarity search.
 *
 * The algorithm uses multiple hash functions to project words into a high-dimensional space,
 * then aggregates them (similar to the "word hashing" technique used in lightweight embedding systems).
 */
@Service
public class EmbeddingService {

    static final int DIMENSIONS = 384;
    private static final int[] PRIMES = {31, 37, 41, 43, 47, 53, 59, 61, 67, 71, 73, 79, 83, 89, 97, 101};
    private static final long FNV_OFFSET = 0xCBF29CE484222325L;
    private static final long FNV_PRIME = 0x100000001B3L;

    /**
     * Generates a 384-dimensional embedding vector from the given text.
     */
    public float[] embed(String text) {
        if (text == null || text.isBlank()) {
            return new float[DIMENSIONS];
        }

        float[] vector = new float[DIMENSIONS];
        String normalized = text.toLowerCase().trim();

        // Split into tokens
        String[] tokens = normalized.split("[^\\p{L}\\p{N}]+");

        for (String token : tokens) {
            if (token.length() < 2) continue;
            float[] tokenVector = hashToken(token);
            for (int i = 0; i < DIMENSIONS; i++) {
                vector[i] += tokenVector[i];
            }
        }

        // L2 normalize
        return normalize(vector);
    }

    /**
     * Hashes a single token into a sparse vector using multiple hash functions.
     */
    private float[] hashToken(String token) {
        float[] vector = new float[DIMENSIONS];
        byte[] bytes = token.getBytes(StandardCharsets.UTF_8);
        long hash = hashFNV(bytes);

        for (int i = 0; i < PRIMES.length; i++) {
            int prime = PRIMES[i];
            long h = hash * prime + i;
            // Map to dimension index
            int dim = (int) (Math.abs(h) % DIMENSIONS);
            // Use sine to get a value in [-1, 1]
            vector[dim] = (float) Math.sin(h * 0.1);
        }

        return vector;
    }

    /**
     * FNV-1a hash function (64-bit).
     */
    private long hashFNV(byte[] data) {
        long hash = FNV_OFFSET;
        for (byte b : data) {
            hash ^= (b & 0xFF);
            hash *= FNV_PRIME;
        }
        return hash;
    }

    /**
     * L2-normalizes a vector to unit length.
     */
    private float[] normalize(float[] vector) {
        double magnitude = 0;
        for (float v : vector) {
            magnitude += v * v;
        }
        magnitude = Math.sqrt(magnitude);
        if (magnitude == 0) return vector;

        float[] normalized = new float[DIMENSIONS];
        for (int i = 0; i < DIMENSIONS; i++) {
            normalized[i] = (float) (vector[i] / magnitude);
        }
        return normalized;
    }

    /**
     * Converts a float array to a PGvector instance for database operations.
     */
    public PGvector toPgVector(float[] embedding) {
        return new PGvector(embedding);
    }
}

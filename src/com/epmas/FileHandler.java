package com.epmas;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

/**
 * CHAPTER 6: Files & Streams (Data Archiving & Persistence)
 * This utility class implements five distinct standard I/O storage methodologies:
 * 1. File metadata interrogation.
 * 2. Text character formatting (CSV via PrintWriter/Scanner).
 * 3. Raw binary primitive pipes (DataOutputStream/DataInputStream).
 * 4. Full Object Serialization (ObjectOutputStream/ObjectInputStream).
 * 5. Memory-buffered performance utilities (BufferedReader/BufferedWriter).
 */
public class FileHandler {

    /**
     * CHAPTER 6.1: The Metadata Label
     * Uses the File utility class to inspect/validate target directories,
     * create file targets if missing, and output metadata properties.
     */
    public static void printFileMetadata(String filePath) {
        System.out.println("\n--- CHAPTER 6.1: FILE METADATA SYSTEM CHECK ---");
        File file = new File(filePath);
        
        try {
            // Verify if directory structure exists, create if missing
            File parentDir = file.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                boolean dirCreated = parentDir.mkdirs();
                System.out.println("[Metadata] Parent directories created: " + dirCreated);
            }

            // Create target file if missing
            if (!file.exists()) {
                boolean fileCreated = file.createNewFile();
                System.out.println("[Metadata] Target file created: " + fileCreated);
            }

            // Extract and print system metadata properties
            System.out.println("[Metadata] Absolute Path: " + file.getAbsolutePath());
            System.out.println("[Metadata] File Size: " + file.length() + " bytes");
            System.out.println("[Metadata] Readable: " + file.canRead());
            System.out.println("[Metadata] Writable: " + file.canWrite());
            System.out.println("[Metadata] Last Modified: " + new Date(file.lastModified()));

        } catch (IOException e) {
            System.err.println("[Metadata Error] Failed to inspect metadata: " + e.getMessage());
        }
    }

    /**
     * CHAPTER 6.2: Text Character Streams
     * Deploys PrintWriter to format records cleanly as human-readable CSV text outputs.
     */
    public static void writePortfolioCSV(String filePath, List<Asset> assets) {
        System.out.println("\n--- CHAPTER 6.2: WRITING CSV CHARACTER STREAMS ---");
        
        // CHAPTER 5: try-with-resources auto-closes resource to ensure proper resource cleanup
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            // Write CSV Headers
            writer.println("TYPE,SYMBOL,NAME,PRICE,QUANTITY,SPECIAL_FIELD");
            
            for (Asset asset : assets) {
                if (asset instanceof CryptoAsset) {
                    CryptoAsset crypto = (CryptoAsset) asset;
                    writer.println(String.format("CRYPTO,%s,%s,%.4f,%.6f,%s", 
                            crypto.getSymbol(), crypto.getName(), crypto.getPrice(), crypto.getQuantity(), crypto.getBlockchainNetwork()));
                } else {
                    writer.println(String.format("BASE,%s,%s,%.4f,%.6f,N/A", 
                            asset.getSymbol(), asset.getName(), asset.getPrice(), asset.getQuantity()));
                }
            }
            System.out.println("[CSV Writer] Successfully exported " + assets.size() + " assets to: " + filePath);
        } catch (IOException e) {
            System.err.println("[CSV Writer Error] Failed to write CSV file: " + e.getMessage());
        }
    }

    /**
     * CHAPTER 6.2: Text Character Streams (Input tap)
     * Deploys Scanner arrays to scan and parse human-readable CSV records back into models.
     */
    public static List<Asset> readPortfolioCSV(String filePath) {
        System.out.println("\n--- CHAPTER 6.2: READING CSV CHARACTER STREAMS ---");
        List<Asset> assets = new ArrayList<>();
        
        try (Scanner scanner = new Scanner(new File(filePath))) {
            // Skip Header row if exists
            if (scanner.hasNextLine()) {
                scanner.nextLine();
            }

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.trim().isEmpty()) continue;

                String[] tokens = line.split(",");
                if (tokens.length < 6) continue;

                String type = tokens[0];
                String symbol = tokens[1];
                String name = tokens[2];
                double price = Double.parseDouble(tokens[3]);
                double quantity = Double.parseDouble(tokens[4]);
                String special = tokens[5];

                if ("CRYPTO".equalsIgnoreCase(type)) {
                    assets.add(new CryptoAsset(symbol, name, price, quantity, special));
                } else {
                    assets.add(new Asset(symbol, name, price, quantity));
                }
            }
            System.out.println("[CSV Reader] Hydrated " + assets.size() + " assets from CSV.");
        } catch (FileNotFoundException e) {
            System.err.println("[CSV Reader Error] Target CSV file not found: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("[CSV Reader Error] Parsing error: " + e.getMessage());
        }
        return assets;
    }

    /**
     * CHAPTER 6.3: Binary Primitives Streams
     * Deploys raw byte-level stream pipes (DataOutputStream) to write primitives directly as bytes.
     */
    public static void writePortfolioBinary(String filePath, List<Asset> assets) {
        System.out.println("\n--- CHAPTER 6.3: WRITING BINARY PRIMITIVE STREAMS ---");
        
        try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(filePath))) {
            dos.writeInt(assets.size()); // Write total entries count as binary int
            
            for (Asset asset : assets) {
                // Write structural primitives directly as performance bytes
                dos.writeUTF(asset.getSymbol());
                dos.writeUTF(asset.getName());
                dos.writeDouble(asset.getPrice());
                dos.writeDouble(asset.getQuantity());
                
                boolean isCrypto = asset instanceof CryptoAsset;
                dos.writeBoolean(isCrypto);
                if (isCrypto) {
                    dos.writeUTF(((CryptoAsset) asset).getBlockchainNetwork());
                }
            }
            System.out.println("[Binary Writer] Wrote raw binary structures representing portfolio assets.");
        } catch (IOException e) {
            System.err.println("[Binary Writer Error] Writing error: " + e.getMessage());
        }
    }

    /**
     * CHAPTER 6.3: Binary Primitives Streams
     * Deploys raw byte-level stream pipes (DataInputStream) to read binary structures.
     */
    public static List<Asset> readPortfolioBinary(String filePath) {
        System.out.println("\n--- CHAPTER 6.3: READING BINARY PRIMITIVE STREAMS ---");
        List<Asset> assets = new ArrayList<>();
        
        try (DataInputStream dis = new DataInputStream(new FileInputStream(filePath))) {
            int size = dis.readInt();
            for (int i = 0; i < size; i++) {
                String symbol = dis.readUTF();
                String name = dis.readUTF();
                double price = dis.readDouble();
                double quantity = dis.readDouble();
                boolean isCrypto = dis.readBoolean();
                
                if (isCrypto) {
                    String network = dis.readUTF();
                    assets.add(new CryptoAsset(symbol, name, price, quantity, network));
                } else {
                    assets.add(new Asset(symbol, name, price, quantity));
                }
            }
            System.out.println("[Binary Reader] Successfully parsed " + assets.size() + " records from binary bytes.");
        } catch (IOException e) {
            System.err.println("[Binary Reader Error] Reading error: " + e.getMessage());
        }
        return assets;
    }

    /**
     * CHAPTER 6.4: Object Serialization
     * Freezes the memory object graph into a serialized stream using ObjectOutputStream.
     */
    public static void serializePortfolio(String filePath, List<Asset> assets) {
        System.out.println("\n--- CHAPTER 6.4: OBJECT SERIALIZATION (FREEZING OBJECTS) ---");
        
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            oos.writeObject(assets); // Serialize the complete list hierarchy directly
            System.out.println("[Serializer] Portfolio frozen and archived to: " + filePath);
        } catch (IOException e) {
            System.err.println("[Serializer Error] Serialization failed: " + e.getMessage());
        }
    }

    /**
     * CHAPTER 6.4: Object Serialization
     * Hydrates the serialized stream back into dynamic Java memory objects using ObjectInputStream.
     */
    @SuppressWarnings("unchecked")
    public static List<Asset> deserializePortfolio(String filePath) {
        System.out.println("\n--- CHAPTER 6.4: OBJECT DESERIALIZATION (HYDRATING OBJECTS) ---");
        List<Asset> assets = new ArrayList<>();
        
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath))) {
            assets = (List<Asset>) ois.readObject();
            System.out.println("[Deserializer] Hydrated " + assets.size() + " active objects back into memory heap.");
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("[Deserializer Error] Deserialization failed: " + e.getMessage());
        }
        return assets;
    }

    /**
     * CHAPTER 6.5: Buffering Performance
     * Wraps baseline readers/writers inside BufferedReader/BufferedWriter layers.
     * Implements a fast block-chunk copying utility representing an archive backup routine.
     */
    public static void backupFileBuffered(String sourcePath, String destPath) {
        System.out.println("\n--- CHAPTER 6.5: MEMORY BUFFERING PERFORMANCE BACKUP ROUTINE ---");
        
        File source = new File(sourcePath);
        File dest = new File(destPath);
        
        if (!source.exists()) {
            System.out.println("[Buffering] Source file for backup does not exist: " + sourcePath);
            return;
        }

        // CHAPTER 5 & 6: Nested buffered stream handling with auto-closing
        try (
            BufferedReader reader = new BufferedReader(new FileReader(source));
            BufferedWriter writer = new BufferedWriter(new FileWriter(dest))
        ) {
            String line;
            int lineCount = 0;
            // Process character operations in block chunks inside memory buffers
            while ((line = reader.readLine()) != null) {
                writer.write(line);
                writer.newLine();
                lineCount++;
            }
            // Flush buffered buffer explicitly to target persistence layer
            writer.flush();
            System.out.println("[Buffering Backup] Completed copying " + lineCount + " lines via high-speed memory buffers.");
            System.out.println("[Buffering Backup] Backup destination verified: " + dest.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("[Buffering Backup Error] Copy failed: " + e.getMessage());
        }
    }
}

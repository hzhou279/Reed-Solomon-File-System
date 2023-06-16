package RSFS;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import RSFS.ReedSolomon.ReedSolomon;

// read data from a file and calculate its parity
public class Encoder {

    private static final int BLOCK_SIZE = 4; // number of bytes in one block
    private static final int DATA_SHARD_COUNT = 4; // number of data disks in RSFS
    private static final int PARITY_SHARD_COUNT = 2; // number of parity disks in RSFS
    private static final int TOTAL_SHARD_COUNT = DATA_SHARD_COUNT + PARITY_SHARD_COUNT; // total number of disks in RSFS
    private static final int FILE_SIZE_MULTIPLE = DATA_SHARD_COUNT * BLOCK_SIZE;
    private static final ReedSolomon REED_SOLOMON = ReedSolomon.create(DATA_SHARD_COUNT, PARITY_SHARD_COUNT);

    private String filePath;
    private byte[] fileData;
    private byte[] paddedFileData;
    private byte[][] shards;
    private int fileSize;
    private String[] diskPaths;

    public Encoder(String filePath, String[] diskPaths) throws IOException {
        this.filePath = filePath;
        fileData = Files.readAllBytes(Path.of(filePath));
        fileSize = fileData.length;
        this.diskPaths = diskPaths;
        // encode();
    }

    public void store() {
        for (int i = 0; i < TOTAL_SHARD_COUNT; i++) {
            try (FileOutputStream fos = new FileOutputStream(diskPaths[i])) {
                fos.write(shards[i]); // Write the byte data to the file
                System.out.println("Byte data stored in disk successfully.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void encode()  {
        paddedFileData = pad(fileData);
        shards = splitFileToShards(paddedFileData);
        byte[] mergedFileData = mergeShardsToFile(shards);
        if (!Arrays.equals(paddedFileData, mergedFileData)) System.out.println("split and merge failed.");
        REED_SOLOMON.encodeParity(shards, 0, shards[0].length);
    }

    private byte[][] splitFileToShards(byte[] fileData) {
        byte[][] shards = new byte[TOTAL_SHARD_COUNT][fileData.length / DATA_SHARD_COUNT];
        int blockCnt = fileData.length / BLOCK_SIZE;
        for (int blockIdx = 0; blockIdx < blockCnt; blockIdx++) {
            int byteIdxInFile = blockIdx * BLOCK_SIZE;
            int shardIdx = blockIdx % DATA_SHARD_COUNT;
            int byteIdxInShard = blockIdx / BLOCK_SIZE * BLOCK_SIZE;
            for (int i = 0; i < BLOCK_SIZE; i++, byteIdxInFile++, byteIdxInShard++)
                shards[shardIdx][byteIdxInShard] = fileData[byteIdxInFile];
        }
        return shards;
    }

    private byte[] mergeShardsToFile(byte[][] shards) {
        byte[] fileData = new byte[shards[0].length * DATA_SHARD_COUNT];
        int blockCnt = fileData.length / BLOCK_SIZE;
        for (int blockIdx = 0; blockIdx < blockCnt; blockIdx++) {
            int byteIdxInFile = blockIdx * BLOCK_SIZE;
            int shardIdx = blockIdx % DATA_SHARD_COUNT;
            int byteIdxInShard = blockIdx / BLOCK_SIZE * BLOCK_SIZE;
            for (int i = 0; i < BLOCK_SIZE; i++, byteIdxInFile++, byteIdxInShard++)
                fileData[byteIdxInFile] = shards[shardIdx][byteIdxInShard];
        }
        return fileData;
    }

    private byte[] pad(byte[] fileData) {
        if (fileData.length % FILE_SIZE_MULTIPLE == 0) return fileData;
        int paddedfileSize = fileData.length / FILE_SIZE_MULTIPLE * FILE_SIZE_MULTIPLE + FILE_SIZE_MULTIPLE;
        byte[] paddedFileData = new byte[paddedfileSize];
        Arrays.fill(paddedFileData, (byte) 0);
        int startIndex = 0; // Starting index in destinationArray where sourceArray will be copied
        int length = fileData.length; // Number of elements to be copied
        System.arraycopy(fileData, 0, paddedFileData, startIndex, length);
        return paddedFileData;
    }

    public String getFilePath() {
        return filePath;
    }

    public byte[] getFileData() {
        return fileData;
    }

    public byte[] getPaddedFileData() {
        return paddedFileData;
    }

    public byte[][] getShards() {
        return shards;
    }

    public int getFileSize() {
        return fileSize;
    }
}

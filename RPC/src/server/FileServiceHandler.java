package server;

import org.apache.thrift.TException;
import util.*;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileServiceHandler implements FileStore.Iface {

    private Map<String, RFileMetadata> fileListMap;

    public FileServiceHandler() {
        fileListMap = new HashMap<>();
    }

    @Override
    public List<RFileMetadata> listOwnedFiles(String user) throws SystemException, TException {
        List<RFileMetadata> ownedFiles = new ArrayList<RFileMetadata>();
        RFileMetadata rFileMetadata;
        // Get list of files from current directory
        for (Object obj : fileListMap.entrySet()) {
            Map.Entry pair = (Map.Entry) obj;
            rFileMetadata = (RFileMetadata) pair.getValue();
            if (rFileMetadata.getOwner().equalsIgnoreCase(user)) {
                ownedFiles.add(rFileMetadata);
            }
        }

        if (ownedFiles.isEmpty()) {
            throw new SystemException().setMessage("User " + user + " does not exist.");
        }

        return ownedFiles;
    }


    @Override
    public StatusReport writeFile(RFile rFile) throws SystemException, TException {
        StatusReport statusReport = new StatusReport();
        String fileName = rFile.getMeta().getFilename().concat("|").concat(rFile.getMeta().getOwner());
        File file = new File(fileName);
        RFileMetadata rFileMetadata = null;
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file.getName())));
            if (fileListMap.containsKey(fileName)) {
                rFileMetadata = fileListMap.get(fileName);
                modifyMetaData(rFileMetadata, rFile.getContent());
                bw.write(rFile.getContent());
                statusReport.setStatus(Status.SUCCESSFUL);
            } else {
                rFileMetadata = new RFileMetadata();
                createMetadata(rFileMetadata, rFile);
                bw.write(rFile.getContent());
                fileListMap.put(fileName, rFileMetadata);
                statusReport.setStatus(Status.SUCCESSFUL);
            }
        } catch (IOException e) {
            statusReport.setStatus(Status.FAILED);
            throw new SystemException().setMessage("Error while writing to file " + rFileMetadata.getFilename());
        } catch (NoSuchAlgorithmException e) {
            statusReport.setStatus(Status.FAILED);
            throw new SystemException().setMessage("Error while generating MD5 hash for file " + rFileMetadata.getFilename());
        } finally {
            try {
                bw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return statusReport;
    }


    @Override
    public RFile readFile(String filename, String owner) throws SystemException, TException {
        String requestedFile = filename.concat("|").concat(owner);
        File file = new File(requestedFile);
        RFile rFile = new RFile();
        StringBuilder contents;
        RFileMetadata metadata;

        if (fileListMap.containsKey(requestedFile)) {
            try {
                metadata = fileListMap.get(requestedFile);
                contents = getFileContent(file);
                rFile.setContent(contents.toString());
                rFile.setMeta(metadata);
            } catch (IOException e) {
                throw new SystemException().setMessage("Error in reading file " + requestedFile);
            }
        } else {
            throw new SystemException().setMessage("Requested file does not exist on server.");
        }
        return rFile;
    }

    private void modifyMetaData(RFileMetadata rFileMetadata, String fileContents) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        rFileMetadata.setUpdated(System.currentTimeMillis());
        rFileMetadata.setVersion(rFileMetadata.getVersion() + 1);
        rFileMetadata.setContentLength(fileContents.length());
        rFileMetadata.setContentHash(generateHash(fileContents));
    }

    private void createMetadata(RFileMetadata rFileMetadata, RFile rFile) throws IOException, NoSuchAlgorithmException {
        rFileMetadata.setFilename(rFile.getMeta().getFilename());
        rFileMetadata.setCreated(System.currentTimeMillis());
        rFileMetadata.setUpdated(System.currentTimeMillis());
        rFileMetadata.setVersion(0);
        rFileMetadata.setOwner(rFile.getMeta().getOwner());
        rFileMetadata.setContentLength(rFile.getContent().length());
        rFileMetadata.setContentHash(generateHash(rFile.getContent()));
    }

    /**
     *
     * @param file
     * @return
     * @throws IOException
     */
    private StringBuilder getFileContent(File file) throws IOException {
        StringBuilder content = new StringBuilder();
        String line;
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
        while ((line = br.readLine()) != null) {
            content.append(line);
        }
//        br.close();
        return content;
    }

    private String generateHash(String content) throws NoSuchAlgorithmException, UnsupportedEncodingException {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            byte[] hashedBytes = digest.digest(content.getBytes("UTF-8"));

            return convertByteArrayToHexString(hashedBytes);
    }

    private String convertByteArrayToHexString(byte[] arrayBytes) {
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < arrayBytes.length; i++) {
            stringBuffer.append(Integer.toString((arrayBytes[i] & 0xff) + 0x100, 16).substring(1));
        }
        return stringBuffer.toString();
    }

}

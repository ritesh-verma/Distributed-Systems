package util;

import java.io.*;

public class FileProcessor {
    private BufferedReader br;
    private BufferedWriter bw;
    private static int lineNumber = 0;
    private File file;

    public FileProcessor(String fileName) throws IOException {
        file = new File(fileName);
        if (file.exists()) {
            bw = new BufferedWriter(new FileWriter(file, true));
        } else {
            file.createNewFile();
            bw = new BufferedWriter(new FileWriter(file));
        }
        br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
    }

    public String readLineFromFile(){
        String line = null;
        try {
            if(br != null)
                line = br.readLine();
            lineNumber++;
        } catch(IOException e) {
            System.out.println("Error while reading from file at line: " + lineNumber);
            e.printStackTrace();
            System.exit(1);
        }
        return line;
    }

    public void writeToFile(String line) throws IOException {
        bw.write(line);
        bw.flush();
    }
}

import java.io.*;

class FileIO {

    private InputStream inputStream;
    private OutputStream outputStream;
    private String path;

    FileIO(String path) {
        this.path = path;
    }


    public String read() throws IOException {
        inputStream = new FileInputStream(path);
        int data = inputStream.read();
        StringBuilder str = new StringBuilder();
        char content;
        while (data != -1) {
            content = (char) data;
            str.append(content);
            data = inputStream.read();
        }
        inputStream.close();
        return str.toString();
    }

    public void write(String st) throws IOException {
        outputStream = new FileOutputStream(path);
        outputStream.write(st.getBytes());
        outputStream.close();
    }
}



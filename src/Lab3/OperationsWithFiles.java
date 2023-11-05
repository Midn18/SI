package Lab3;

import java.io.FileInputStream;
import java.io.IOException;

public class OperationsWithFiles {

    public static byte[] readFileBytesDSA(String filePath) {
        try (FileInputStream fis = new FileInputStream(filePath);
        ) {
            int size = fis.available();
            byte[] result = new byte[size];
            fis.read(result);
            fis.close();
            return result;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

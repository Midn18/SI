package Lab3;

import java.math.BigInteger;

public class Main {

    public static void main(String[] args) {
        DSA dsa = new DSA();
        BigInteger[] keys = dsa.generateKeys();
        System.out.println("Chei generate:");
        System.out.println("q: " + keys[0]);
        System.out.println("p: " + keys[1]);
        System.out.println("h: " + keys[2]);
        System.out.println("g: " + keys[3]);
        System.out.println("a: " + keys[4]);
        System.out.println("b: " + keys[5]);

        String filePath = "src/Lab3/docs/SampleToSign.txt";
        byte[] fileBytes = OperationsWithFiles.readFileBytesDSA(filePath);

        BigInteger[] signature = dsa.sign(fileBytes);
        System.out.println("Semnătură generată:");
        System.out.println("r: " + signature[0]);
        System.out.println("s: " + signature[1]);

        // Verificare semnătură
        boolean verificationResult = dsa.verify(fileBytes, signature[0].toString(), signature[1].toString());
        if (verificationResult) {
            System.out.println("Semnătura este validă.");
        } else {
            System.out.println("Semnătura nu este validă.");
        }
    }
}

package Lab1;

import java.io.*;
import java.nio.charset.*;
import java.util.*;

public class DESImplemetation {

    private static DESAlgorithm des;
    private static String textFromFile;

    public static void main(String[] args) {
        System.out.println("\n**** DES Algorithm Implementation ****");
        Scanner input;
        int digit;
        String key, plainText, cipherText;

        while (true) {
            print();
            input = new Scanner(System.in);

            try {
                digit = input.nextInt();
            } catch (Exception ex) {
                digit = 0;
            }

            switch (digit) {
                case 1: {
                    if (readDataFromAFile("C:\\Default\\1.Univer\\SI\\labs\\SecurityLabs\\src\\Lab1\\key.txt")) {
                        key = textFromFile;

                        if (readDataFromAFile("C:\\Default\\1.Univer\\SI\\labs\\SecurityLabs\\src\\Lab1\\input.txt")) {
                            plainText = textFromFile;
                            des_encrypt(plainText, key);
                        }
                    }
                    break;
                }
                case 2: {
                    if (readDataFromAFile("C:\\Default\\1.Univer\\SI\\labs\\SecurityLabs\\src\\Lab1\\key.txt")) {
                        key = textFromFile;
                        if (readDataFromAFile("output.txt")) {
                            cipherText = textFromFile;
                            ;
                            des_decrypt(cipherText, key);
                        }
                    }
                    break;
                }

                default: {
                    if (digit == 3) {
                        break;
                    }
                    System.out.println("Please type correct digit...");
                }
            }

            if (digit == 3) {
                break;
            }
        }

    }

    //this method do required padding for des algorithm
    public static String doPadding(String inputText) {
        if (inputText.length() % 8 != 0) {

            int paddingLength = 8 - inputText.length() % 8;
            for (int i = 0; i < paddingLength; i++) {
                inputText = inputText.concat(" ");
            }
        } else {
            return inputText;
        }
        return inputText;
    }

    //this method perform Electronic Code Book mode in DES Implementation
    public static String[] doECB(String plainText) {
        int start = 0, end = 8;
        int noOfBlock = plainText.length() / 8;
        String temp;
        String[] textArray = new String[noOfBlock];

        for (int i = 0; i < noOfBlock; i++) {
            temp = plainText.substring(start, end);
            textArray[i] = temp;
            start = end;
            end = end + 8;
        }
        return textArray;
    }

    //this method read data from input file
    public static boolean readDataFromAFile(String fileName) {

        StringBuffer bf = new StringBuffer();
        try {
            File file = new File(fileName);
            int fileLenght = (int) file.length();
            if (fileLenght == 0) {
                System.out.println("no file or text is found in " + fileName);
                return false;
            }

            byte[] buffer = new byte[fileLenght];
            FileInputStream fis = new FileInputStream(fileName);

            int nRead;
            while ((nRead = fis.read(buffer)) != -1) {
                bf.append(new String(buffer, Charset.forName("utf-8")));
            }

            fis.close();

        } catch (IOException ex) {
            System.out.println(fileName + " file is not found");
            return false;
        }

        textFromFile = new String(bf);
        return true;
    }

    //this method write data into output file
    public static void writeDataInAFile(String text, String fileName) {

        byte[] buffer = text.getBytes(Charset.forName("utf-8"));

        try {
            FileOutputStream fos = new FileOutputStream(fileName);
            fos.write(buffer);
            fos.close();
        } catch (IOException ex) {
            System.out.println("File not found");
        }
    }

    //this method encrypt data using des algorithm
    public static void des_encrypt(String plainText, String key) {
        des = new DESAlgorithm(key);

        String[] plainTextArray = doECB(doPadding(plainText));
        String cipher_block;
        StringBuffer sb = new StringBuffer();

        for (int i = 0; i < plainTextArray.length; i++) {
            cipher_block = des.encrypt(plainTextArray[i]);
            sb.append(cipher_block);
        }

        String cipherText = new String(sb);
        writeDataInAFile(cipherText, "output.txt");
        System.out.println("This cipher text is written in output.txt file");
    }


    //this method decrypt data using des algorithm
    public static void des_decrypt(String cipherText, String key) {
        des = new DESAlgorithm(key);

        String[] cipherTextArray = doECB(doPadding(cipherText));
        String plain_block;
        StringBuffer sb = new StringBuffer();

        for (int i = 0; i < cipherTextArray.length; i++) {
            plain_block = des.decrypt(cipherTextArray[i]);
            sb.append(plain_block);
        }

        String genPlainText = new String(sb);
        writeDataInAFile(genPlainText, "genInput.txt");
        System.out.println("This generated plain text is written in genInput.txt file");
    }


    //this method print into system
    public static void print() {
        System.out.println("\nPlease ");
        System.out.println("Enter 1 for encrypting text from input.txt file");
        System.out.println("Enter 2 for decrypting text from output.txt file");
        System.out.println("Enter 3 for exit\n");
    }

}

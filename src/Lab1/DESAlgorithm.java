package Lab1;

public class DESAlgorithm {

    private int[] left = new int[32];
    private int[] right = new int[32];

    private int[] funcOut = new int[32];
    private int[] result = new int[32];

    private int[] roundKey = new int[48];
    private int[] SBoxOut = new int[32];
    KeyGenerator key;

    public DESAlgorithm(String keyWord) {
        key = new KeyGenerator(keyWord);
    }

    //this method encrypt any plaintext
    //mode 1 is for encryption
    public String encrypt(String plainText) {
        return binToString(encryptDecrypt(1, plainText));
    }

    //this method encrypt any plaintext. mode 1 for encryption 
    public String decrypt(String cipherText) {
        return binToString(encryptDecrypt(2, cipherText));  //2 for decryption
    }

    //this method encrypt or decrypt 64 bit block
    public int[] encryptDecrypt(int mode, String text) {

        int[] inputBlock = getBinaryForTextBlock(text);
        int[] initialPermOut = getInitialPermuted(inputBlock);
        doSegmentation(initialPermOut);

        int round = 1;
        while (round <= 16) {
            performOneRound(mode, round);
            round++;
        }

        swap32();
        int[] finalPermIn = getConcatenated();

        return getFinalPermuted(finalPermIn);
    }


    //this method convets text block to binary stream
    public static int[] getBinaryForTextBlock(String plainText) {
        byte[][] block = new byte[8][8];
        int[] binaryText = new int[64];

        for (int i = 0; i < 8 && i < plainText.length(); i++) {
            block[i] = getBinaryBits(plainText.charAt(i));
        }

        int index = 0;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                binaryText[index] = block[i][j];
                index++;
            }
        }
        return binaryText;
    }


    //this method converts a character to 8 byte array
    public static byte[] getBinaryBits(int ch) {
        byte[] bin = new byte[8];
        for (int i = 0; i < 8; i++) {
            bin[7 - i] = (byte) ((ch >> i) & 1);
        }
        return bin;
    }

    //this method perform each round for encryption and decryption 
    public void performOneRound(int mode, int round) {
        if (mode == 1) {
            roundKey = key.getRoundKeyForEncryption(round);
        } else if (mode == 2) {
            roundKey = key.getRoundKeyForDecryption(round);
        }

        funcOut = doDESFunction(right, roundKey);
        result = getXOR32Bit(left, funcOut);

        for (int i = 0; i < 32; i++) {
            left[i] = right[i];
            right[i] = result[i];
        }
    }

    //this method do initial permutation
    public int[] getInitialPermuted(int[] permIn) {
        int[] permOut = new int[64];
        int[] storeNum = AllData.getInitialPermutationTable();
        int temp;
        int i = 0;
        int loop = 0;
        int check = 0;
        while (permIn.length != check) {
            temp = storeNum[i];
            if (temp == loop) {
                permOut[check] = permIn[loop - 1];
                loop = 0;
                check++;
                i++;
            }
            loop++;
        }
        return permOut;
    }

    //this method divide 64 bit block to two 32 bit block. 
    public void doSegmentation(int[] permOut) {
        int index = 0;
        for (int i = 0; i < 32; i++) {
            left[i] = permOut[i];
        }

        for (int i = 32; i < 64; i++) {
            right[index] = permOut[i];
            index++;
        }
    }

    //this method performs des function
    public int[] doDESFunction(int[] rightIn, int[] roundKey) {

        int[] rightOut = doExpansion(rightIn);
        int[] sboxIn = getXOR48Bit(rightOut, roundKey);
        doSubstitution(sboxIn);

        return doPermutation(SBoxOut);
    }

    /**
     * this method converts 32 bit right half to 48 bit using expansion table
     *
     * @param rightIn
     * @return 48 bit right half
     */
    public int[] doExpansion(int[] rightIn) {
        int[] storeNum = AllData.getExpansionTable();
        int[] rightOut = new int[48];
        int temp;
        int i = 0;
        int loop = 0;
        int check = 0;

        while (check != 48) {
            temp = storeNum[i];
            if (temp == loop) {
                rightOut[check] = rightIn[loop - 1];
                loop = 0;
                check++;
                i++;
            }
            loop++;
        }
        return rightOut;
    }

    //exclusive or between rightOut and key
    public int[] getXOR48Bit(int side1[], int[] side2) {
        int index = 0;
        int[] result = new int[48];

        for (int i = 0; i < side1.length; i++) {
            if (side1[i] == side2[i]) {
                result[index] = 0;
            } else {
                result[index] = 1;
            }
            index++;
        }
        return result;
    }

    /**
     * this method perform substitution. it make 48 bit XOROut to 32 bit
     * SBoxOut
     *
     * @param XOROut
     * @return SBoxOut
     */
    public void doSubstitution(int[] XOROut) {
        int[] temp = new int[6];
        int count = 0;
        int choice = 0;
        int i;
        while (count != 48) {
            for (i = 0; i < 6; i++) {
                temp[i] = XOROut[i + count];
            }

            int num = getOutputFromSBox(choice, getSBoxRow(temp), getSBoxColumn(temp));
            make32bit(choice, num);

            choice++;
            count += 6;
        }

        Reverse(SBoxOut);
    }

    //this method do another straight permutation to s boxes output.
    public int[] doPermutation(int[] SBoxOut) {
        int[] funcOut = new int[32];
        int[] storeNum = AllData.getStraightPermutationTable();
        int temp;
        int i = 0;
        int loop = 0;
        int check = 0;
        while (check != 32) {
            temp = storeNum[i];
            if (temp == loop) {
                funcOut[check] = SBoxOut[loop - 1];
                loop = 0;
                check++;
                i++;
            }
            loop++;
        }
        return funcOut;
    }

    //this method calculates decimal row number for each s box
    public int getSBoxRow(int[] num) {
        return 2 * num[0] + 1 * num[5];
    }

    //this method calculates decimal column number for each s box
    public int getSBoxColumn(int[] num) {
        return 8 * num[1] + 4 * num[2] + 2 * num[3] + 1 * num[4];
    }

    //this method gives the decimal output from each s boxes
    public int getOutputFromSBox(int choice, int row, int col) {
        int num = 0;
        switch (choice) {
            case 0:
                num = AllData.SBOX1[row][col];
                break;

            case 1:
                num = AllData.SBOX2[row][col];
                break;

            case 2:
                num = AllData.SBOX3[row][col];
                break;

            case 3:
                num = AllData.SBOX4[row][col];
                break;

            case 4:
                num = AllData.SBOX5[row][col];
                break;

            case 5:
                num = AllData.SBOX6[row][col];
                break;

            case 6:
                num = AllData.SBOX7[row][col];
                break;

            case 7:
                num = AllData.SBOX8[row][col];
                break;
        }
        return num;
    }

    //this method combine all s boxes output to make 32 bit
    public void make32bit(int index, int num) {
        int num1, num2, num3;
        num1 = num;

        for (int i = 0; i < 4; i++) {
            num2 = num1 % 2;
            num3 = num1 / 2;
            num1 = num3;
            SBoxOut[(index * 4) + i] = num2;
        }
    }

    //this method reverse the bit sequence 
    public void Reverse(int[] num) {
        int count = 0;
        int fix = 3;
        int temp1, temp2;
        while (count != 32) {
            for (int i = 0; i < 2; i++) {
                temp1 = num[count + i];
                num[count + i] = num[fix - (count + i)];
                num[fix - (count + i)] = temp1;
            }
            fix += 8;
            count += 4;
        }
    }

    //this method XOR left half 32 bit to functionOutput 32 bit 
    public int[] getXOR32Bit(int[] side1, int[] side2) {
        int index = 0;
        int[] result = new int[32];

        for (int i = 0; i < side1.length; i++) {
            if (side1[i] == side2[i]) {
                result[index] = 0;
            } else {
                result[index] = 1;
            }
            index++;
        }
        return result;
    }

    //this method swap right and left half for each feistal round
    public void swap32() {
        int temp;
        for (int i = 0; i < 32; i++) {
            temp = left[i];
            left[i] = right[i];
            right[i] = temp;
        }
    }


    //this method finally concated left and right half
    public int[] getConcatenated() {
        int index = 32;
        int[] result = new int[64];
        for (int i = 0; i < 32; i++) {
            result[i] = left[i];
        }

        for (int i = 0; i < 32; i++) {
            result[index] = right[i];
            index++;
        }
        return result;
    }

    //this method do final permutation 
    public int[] getFinalPermuted(int[] permIn) {
        int[] permOut = new int[64];
        int[] storeNum = AllData.getFinalPermutationTable();
        int temp = 0;
        int i = 0;
        int loop = 0;
        int check = 0;

        while (permIn.length != check) {
            temp = storeNum[i];
            if (temp == loop) {
                permOut[check] = permIn[loop - 1];
                loop = 0;
                check++;
                i++;
            }
            loop++;
        }
        return permOut;
    }

    //this method converts binary to array to String
    public static String binToString(int[] array) {
        StringBuffer sb = new StringBuffer();
        StringBuilder output = new StringBuilder();

        byte[] byteArray = new byte[4];
        int value, index = 0;

        for (int j = 0; j < array.length; j = j + 4) {
            for (int i = 0; i <= 3; i++) {
                byteArray[i] = (byte) array[index + i];
            }
            index = index + 4;

            int decimal = byteArray[0] * 8 + byteArray[1] * 4 + byteArray[2] * 2 + byteArray[3] * 1;
            sb.append(Integer.toString(decimal, 16));
        }

        String hex = new String(sb);

        for (int i = 0; i < hex.length(); i += 2) {
            String str = hex.substring(i, i + 2);
            output.append((char) Integer.parseInt(str, 16));
        }
        return new String(output);
    }
}

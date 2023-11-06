package Lab3;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;
import lombok.Data;

@Data
public class DSA {

    int keyLength;
    MessageDigest md;

    BigInteger q, p, h, g, x, y, ps1, k, r, s, hash, w, u1, u2, v;

    Random random = new Random(1);

    public BigInteger[] generateKeys() {
        int randomLength = (int) ((Math.random() * 512) + 512);
        while (randomLength % 64 != 0) {
            randomLength++;
            if (randomLength > 1024 || randomLength <= 512) {
                randomLength = (int) ((Math.random() * 512) + 512);
            }
        }
        keyLength = randomLength;
        q = BigInteger.probablePrime(160, random);
        BigInteger temp1, temp2;
        do {
            temp1 = BigInteger.probablePrime(keyLength, random);
            temp2 = temp1.subtract(BigInteger.ONE);
            temp1 = temp1.subtract(temp2.remainder(q));
        } while (!temp1.isProbablePrime(5));
        p = temp1;
        ps1 = p.subtract(BigInteger.ONE);
        BigInteger temp3;
        do {
            h = new BigInteger(keyLength - 1, random);
            temp3 = h.modPow(ps1.divide(q), p);
        } while (temp3.equals(BigInteger.ONE));
        g = temp3;
        do {
            x = new BigInteger(159, random);
        } while (x.compareTo(q) > 0 && x.compareTo(BigInteger.ZERO) != 1);
        y = g.modPow(x, p);
        return new BigInteger[]{q, p, h, g, x, y};
    }

    public BigInteger[] sign(byte[] fileBytes) {
        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        md.update(fileBytes);
        BigInteger hash = new BigInteger(1, md.digest());

        do {
            k = new BigInteger(158, new Random());
            r = g.modPow(k, p).mod(q);
            s = BigInteger.ZERO;
        }
        while (r.equals(BigInteger.ZERO));
        while (s.equals(BigInteger.ZERO)) {
            s = k.modInverse(q).multiply(hash.add(x.multiply(r))).mod(q);
            k = new BigInteger(158, new Random());
        }

        return new BigInteger[]{r, s};
    }

    public boolean verify(byte[] bytes, String rS, String sS) {
        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        md.update(bytes);
        hash = new BigInteger(1, md.digest());
        r = new BigInteger(rS);
        s = new BigInteger(sS);

        w = s.modInverse(q);
        u1 = hash.multiply(w).mod(q);
        u2 = r.multiply(w).mod(q);
        v = g.modPow(u1, p).multiply(y.modPow(u2, p)).mod(p).mod(q);
        return v.equals(r);
    }
}

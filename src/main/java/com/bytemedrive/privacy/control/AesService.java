package com.bytemedrive.privacy.control;

import jakarta.enterprise.context.ApplicationScoped;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;


@ApplicationScoped
public class AesService {

    private static final String ENCRYPT_ALGO = "AES/GCM/NoPadding";

    private static final int TAG_LENGTH_BIT = 128; // must be one of {128, 120, 112, 104, 96}

    private static final int IV_LENGTH_BYTE = 12;

    private SecretKey getAESKeyFromPassword(char[] password, byte[] salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(password, salt, 65536, 256);
        SecretKey secret = new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");
        return secret;
    }

    public byte[] decryptWithPassword(byte[] bytes, char[] password, byte[] salt) {
        try {
            return decryptWithKey(bytes, getAESKeyFromPassword(password, salt));
        } catch (Exception e) {
            throw new SecurityException(e);
        }
    }

    public byte[] decryptWithKey(byte[] input, SecretKey secretKey) {
        try {
            // get back the iv and salt from the cipher text
            ByteBuffer bb = ByteBuffer.wrap(input);

            byte[] iv = new byte[IV_LENGTH_BYTE];
            bb.get(iv);
            byte[] cipherText = new byte[bb.remaining()];
            bb.get(cipherText);

            Cipher cipher = Cipher.getInstance(ENCRYPT_ALGO);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, new GCMParameterSpec(TAG_LENGTH_BIT, iv));
            return cipher.doFinal(cipherText);
        } catch (Exception e) {
            throw new SecurityException("Cannot AES encrypt", e);
        }
    }

    public void decryptFile(InputStream inputStream, OutputStream outputStream, SecretKey secretKey) {
        try {
            byte[] iv = new byte[IV_LENGTH_BYTE];
            inputStream.read(iv);
            Cipher cipher = Cipher.getInstance(ENCRYPT_ALGO);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, new GCMParameterSpec(TAG_LENGTH_BIT, iv));

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                byte[] output = cipher.update(buffer, 0, bytesRead);
                if (output != null) {
                    outputStream.write(output);
                }
            }

            byte[] finalOutput = cipher.doFinal();
            if (finalOutput != null) {
                outputStream.write(finalOutput);
            }
        } catch (Exception e) {
            throw new SecurityException("Cannot AES encrypt", e);
        }
    }
}

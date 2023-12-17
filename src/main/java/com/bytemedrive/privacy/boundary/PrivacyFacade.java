package com.bytemedrive.privacy.boundary;


import com.bytemedrive.privacy.control.AesService;
import com.bytemedrive.privacy.control.ShaService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import javax.crypto.SecretKey;
import java.io.InputStream;
import java.io.OutputStream;


@ApplicationScoped
public class PrivacyFacade {

    @Inject
    ShaService shaService;

    @Inject
    AesService aesService;

    public String hashSha3(String input) {
        return shaService.hashSha3(input);
    }

    public byte[] decryptWithPassword(byte[] bytes, char[] password, byte[] salt) {
        return aesService.decryptWithPassword(bytes, password, salt);
    }

    public byte[] decryptWithKey(byte[] bytes, SecretKey secretKey) {
        return aesService.decryptWithKey(bytes, secretKey);
    }

    public void decryptFile(InputStream inputStream, OutputStream outputStream, SecretKey secretKey) {
        aesService.decryptFile(inputStream, outputStream, secretKey);
    }
}

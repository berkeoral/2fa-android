/*
* Modified this code
* https://gist.github.com/josias1991/3bf4ca59777f7dedcaf41a495d96d984
*
* Uses 128 bit AES for encryption
* Unlike original code doesnt create new encryption key for every encryption
* encryptData returns string instead byte array
 */

package com.group11.blg439e.a2phase_auth;

import javax.crypto.*;
import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;

import android.util.Base64;

class Encryptor {
    private static final String TRANSFORMATION = "AES/GCM/NoPadding";
    private static final String ANDROID_KEY_STORE = "AndroidKeyStore";
    private KeyStore keyStore;

    private byte[] iv;

    Encryptor() throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException {
        keyStore = KeyStore.getInstance(ANDROID_KEY_STORE);
        keyStore.load(null);
    }

    String encryptText(final String alias, final String textToEncrypt)
            throws UnrecoverableEntryException
            , NoSuchAlgorithmException
            , KeyStoreException
            , NoSuchProviderException
            , NoSuchPaddingException
            , InvalidKeyException
            , IOException
            , InvalidAlgorithmParameterException
            , SignatureException
            , BadPaddingException
            , IllegalBlockSizeException {
        final Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, ((KeyStore.SecretKeyEntry) keyStore.getEntry(alias, null)).getSecretKey());
        iv = cipher.getIV();
        return Base64.encodeToString(cipher.doFinal(textToEncrypt.getBytes("UTF-8")), Base64.DEFAULT);
    }

    byte[] getIv() {
        return iv;
    }
}

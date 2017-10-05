/*
*Modified this code
*https://gist.github.com/josias1991/3bf4ca59777f7dedcaf41a495d96d984
 */

package com.group11.blg439e.a2phase_auth;

import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.support.annotation.NonNull;

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
/*
    @NonNull
    private SecretKey getSecretKey(final String alias) throws NoSuchAlgorithmException,
            NoSuchProviderException, InvalidAlgorithmParameterException {
        final KeyGenerator keyGenerator = KeyGenerator
                .getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEY_STORE);

        keyGenerator.init(new KeyGenParameterSpec.Builder(alias,
                KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT | KeyProperties.PURPOSE_SIGN)
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setUserAuthenticationRequired(false)
                .build());
        return keyGenerator.generateKey();
    }
*/
    byte[] getIv() {
        return iv;
    }
}

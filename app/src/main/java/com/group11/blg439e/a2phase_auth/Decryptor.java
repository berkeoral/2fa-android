
package com.group11.blg439e.a2phase_auth;

import javax.crypto.*;
import javax.crypto.spec.GCMParameterSpec;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.*;
import java.security.cert.CertificateException;
import android.util.Base64;

class Decryptor {
    private static final String TRANSFORMATION = "AES/GCM/NoPadding";
    private static final String ANDROID_KEY_STORE = "AndroidKeyStore";

    private KeyStore keyStore;

    Decryptor()
            throws KeyStoreException
            , CertificateException
            , NoSuchAlgorithmException
            , IOException {
        keyStore = KeyStore.getInstance(ANDROID_KEY_STORE);
        keyStore.load(null);
    }

    String decryptData(final String alias, String encryptedText, final byte[] encryptionIv)
            throws NoSuchPaddingException
            , NoSuchAlgorithmException
            , BadPaddingException
            , IllegalBlockSizeException
            , UnsupportedEncodingException
            , UnrecoverableEntryException
            , KeyStoreException
            , InvalidAlgorithmParameterException
            , InvalidKeyException {
        byte[] encryptedData = Base64.decode(encryptedText, Base64.DEFAULT);
        final Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        final GCMParameterSpec spec = new GCMParameterSpec(128, encryptionIv);
        cipher.init(Cipher.DECRYPT_MODE
                , ((KeyStore.SecretKeyEntry) keyStore.getEntry(alias, null)).getSecretKey()
                , spec);
        return new String(cipher.doFinal(encryptedData), "UTF-8");
    }
}

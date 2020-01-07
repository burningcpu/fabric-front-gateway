package com.webank.fabric.front.commons.utils;

import com.webank.fabric.front.commons.Utils;
import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.hyperledger.fabric.gateway.Wallet;
import org.hyperledger.fabric.gateway.impl.WalletIdentity;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.security.PrivateKey;


public class GateWayUtils {
    /**
     * get privateKey.
     */
    public static PrivateKey getPrivateKey(String keyFile) throws IOException {
        String privateKeyString = Utils.extractFileString(keyFile);

        if (StringUtils.isNotBlank(privateKeyString)) {
            PrivateKey privateKey = getPrivateKeyFromString(privateKeyString);

            return privateKey;
        }
        return null;
    }

    private static PrivateKey getPrivateKeyFromString(String data) throws IOException {
        final Reader pemReader = new StringReader(data);

        final PrivateKeyInfo pemPair;
        try (PEMParser pemParser = new PEMParser(pemReader)) {
            pemPair = (PrivateKeyInfo) pemParser.readObject();
        }

        return new JcaPEMKeyConverter().getPrivateKey(pemPair);
    }

    /**
     * get wallet.
     */
    public static Wallet getWallet(String mspId, String userName, String certFile, String keyFile) throws IOException {
        Wallet wallet = Wallet.createInMemoryWallet();
        String signedCert = Utils.extractFileString(certFile);
        PrivateKey privateKey = GateWayUtils.getPrivateKey(keyFile);
        Wallet.Identity identity = new WalletIdentity(mspId, signedCert, privateKey);
        wallet.put(userName, identity);
        return wallet;
    }
}

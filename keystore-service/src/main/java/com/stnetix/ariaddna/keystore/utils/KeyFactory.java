package com.stnetix.ariaddna.keystore.utils;


import com.stnetix.ariaddna.commonutils.logger.AriaddnaLogger;
import com.stnetix.ariaddna.keystore.exceptions.KeyStoreException;
import sun.security.x509.X509CertImpl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.KeyStore;

/**
 * Created by alexkotov on 20.04.17.
 */

public class KeyFactory {


    public KeyFactory(IPersistHelper persistHelper, CertFactory certFactory){
        this.persistHelper = persistHelper;
        this.certFactory = certFactory;
        pass = this.persistHelper.getPassword();
    }

    private IPersistHelper persistHelper;
    private CertFactory certFactory;
    private static final String KEYSTORE_PATH;
    private char[] pass;
    private static final AriaddnaLogger LOGGER;
    private static final String KEYSTORE_FORMAT;

    static {

        KEYSTORE_PATH = "ariaddna.keystore";
        LOGGER = AriaddnaLogger.getLogger(KeyFactory.class);
        KEYSTORE_FORMAT = "JKS";
    }

    public File getNewKeyStore() throws KeyStoreException {
        try  {
            return generateKeyStoreByName(KEYSTORE_PATH);
        } catch (Exception e) {
            LOGGER.error("KeyStore object is not create. Exception: ",e);
            throw new KeyStoreException("Caused by: ",e);
        }
    }

    public void storeCertToKeyStore(File certFile, File keyStoreFile) throws KeyStoreException {
        try {
            X509CertImpl cert = (X509CertImpl) certFactory.getCertByFile(certFile);
            String alias = certFactory.getCertSubjectName(cert);
            LOGGER.info("Certificate with filename {} has Subject name {}", certFile.getAbsolutePath(), alias);
            FileInputStream fis = new FileInputStream(keyStoreFile);
            KeyStore keyStore = KeyStore.getInstance(KEYSTORE_FORMAT);
            keyStore.load(fis, pass);
            LOGGER.info("KeyStore load successful");
            fis.close();

            keyStore.setCertificateEntry(alias, cert);
            FileOutputStream fos = new FileOutputStream(keyStoreFile);
            keyStore.store(fos, pass);
            LOGGER.info("Certificate with filename {} stored in keyStore with filename {}", certFile.getAbsolutePath(), keyStoreFile.getAbsolutePath());
            fos.close();

        } catch (Exception e) {
            LOGGER.error("Exception: ", e);
            throw new KeyStoreException("Caused by: ", e);
        }
    }

    public boolean isCertContainsInKeyStore(File certFile, File keyStoreFile) throws KeyStoreException {
        try (FileInputStream fis = new FileInputStream(keyStoreFile)) {
            X509CertImpl cert = (X509CertImpl) certFactory.getCertByFile(certFile);
            String alias = certFactory.getCertSubjectName(cert);
            KeyStore keyStore = KeyStore.getInstance(KEYSTORE_FORMAT);
            keyStore.load(fis, pass);
            LOGGER.info("Certificate with filename {} "+(keyStore.containsAlias(alias)?"contain":"not contain")+" in keystore with filename {}", certFile.getAbsolutePath(), keyStoreFile.getAbsolutePath());
            return keyStore.containsAlias(alias);

        } catch (Exception e) {
            LOGGER.error("Exception: ", e);
            throw new KeyStoreException("Caused by: ", e);
        }
    }

    public File getCertByAlias(String alias, File keyStoreFile) throws KeyStoreException {
        try {
            FileInputStream fis = new FileInputStream(keyStoreFile);
            KeyStore keyStore = KeyStore.getInstance(KEYSTORE_FORMAT);
            keyStore.load(fis, pass);
            LOGGER.info("KeyStore {} loaded successful.", keyStoreFile.getAbsolutePath());
            fis.close();

            X509CertImpl cert = (X509CertImpl) keyStore.getCertificate(alias);
            File certFile = new File(alias+".cer");
            FileOutputStream fos = new FileOutputStream(certFile);
            fos.write(cert.getEncoded());
            LOGGER.info("Certificate {} loaded successful.", certFile.getAbsolutePath());
            fos.close();
            return certFile;
        } catch (Exception e) {
            LOGGER.error("Exception: ", e);
            throw new KeyStoreException("Caused by: ", e);
        }
    }

    public void removeCertFromKeyStore(File certFile, File keyStoreFile) throws KeyStoreException {
        try {
            X509CertImpl cert = (X509CertImpl) certFactory.getCertByFile(certFile);
            String alias = certFactory.getCertSubjectName(cert);

            FileInputStream fis = new FileInputStream(keyStoreFile);
            KeyStore keyStore = KeyStore.getInstance(KEYSTORE_FORMAT);
            keyStore.load(fis, pass);
            fis.close();

            keyStore.deleteEntry(alias);

            FileOutputStream fos = new FileOutputStream(keyStoreFile);
            keyStore.store(fos, pass);
            LOGGER.info("Certificate with filename {} deleted from keyStore with filename {}", certFile.getAbsolutePath(), keyStoreFile.getAbsolutePath());
            fos.close();
            persistHelper.deleteCertificate(alias);

        } catch (Exception e) {
            LOGGER.error("Exception: ", e);
            throw new KeyStoreException("Caused by: ", e);
        }
    }

    public void setCertDisable(File certFile) throws KeyStoreException {
        X509CertImpl cert = (X509CertImpl) certFactory.getCertByFile(certFile);
        String alias = certFactory.getCertSubjectName(cert);
        persistHelper.setCertificateDisable(alias);
    }

    private File generateKeyStoreByName(String name) throws KeyStoreException {
        KeyStore keyStore = null;
        try (FileOutputStream fos = new FileOutputStream(name)) {
            keyStore = KeyStore.getInstance(KEYSTORE_FORMAT);
            keyStore.load(null, pass);
            keyStore.store(fos, pass);
            File keyStoreFile = new File(name);
            LOGGER.info("KeyStore was create with file name {}", keyStoreFile.getAbsolutePath());
            return keyStoreFile;
        } catch (Exception e) {
            LOGGER.error("KeyStore object is not create. Exception: ",e);
            throw new KeyStoreException("Caused by: ",e);
        }

    }


}

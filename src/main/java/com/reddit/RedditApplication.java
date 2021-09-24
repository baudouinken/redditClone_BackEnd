package com.reddit;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.Provider;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Enumeration;

import javax.annotation.PostConstruct;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.RFC4519Style;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableAsync;

import com.reddit.config.SwaggerConfig;

@SpringBootApplication
@EnableAsync
@Import(SwaggerConfig.class)
public class RedditApplication implements CommandLineRunner {

  public static void main(String[] args) {
    SpringApplication.run(RedditApplication.class, args);
  }
  
  private KeyStore keyStore;

  @Override
  public void run(String... args) throws Exception {
    // TODO Auto-generated method stub

    boolean showSubject = true;
    boolean showIssuer = true;
    boolean showStartDate = true;
    boolean showEndDate = true;
    boolean showPubKey = true;

        KeyStore keyStore1 = null;
          keyStore1 = loadKeystoreFile("ehighway_client1.p12");
          //keyStore1.load(null, null);
          //keyStore1.store(new FileOutputStream("ehighway_client1.p12"), "^q=|GEkm,'AqWfmY|z$n".toCharArray());
    
        X509Certificate x509 = null;
        Enumeration<String> aliases = keyStore1.aliases();
        
        System.out.println(keyStore1.aliases().hasMoreElements());
        
        while (aliases.hasMoreElements()) {
          String alias = aliases.nextElement();
    
          Certificate cert = keyStore1.getCertificate(alias);
          if (cert != null) {
            if ("X.509".equals(cert.getType())) {
              x509 = (X509Certificate) cert;
              if (showSubject) {
                System.out.println("Subject: " + x509.getSubjectX500Principal().toString());
              }
              if (showIssuer) {
                System.out.println("Issuer: " + x509.getIssuerX500Principal().toString());
              }
              if (showStartDate) {
                System.out.println("Start Date: " + x509.getNotBefore().toString());
              }
              if (showEndDate) {
                System.out.println("End Date: " + x509.getNotAfter().toString());
              }
              if (showPubKey) {
                PublicKey key = x509.getPublicKey();
                System.out.println(key.toString());
              }
            } else {
              System.out.println("Unrecognized certificate type '" + cert.getType() + "'");
            }
          }
        }

    
  }

  @PostConstruct
  public void init() throws Exception {       
    
      Provider provider = new BouncyCastleProvider(); 
      
      Security.addProvider(provider);
           
  }
  
  private KeyStore loadKeystoreFile(String storePath) throws Exception {
    Provider provider = new BouncyCastleProvider(); 
    
    Security.addProvider(provider);
    try (InputStream instream = new BufferedInputStream(new FileInputStream(storePath))) {
      keyStore = KeyStore.getInstance("PKCS12");
      keyStore.load(instream, "^q=|GEkm,'AqWfmY|z$n".toCharArray());
      return keyStore;
    }
  }

  private void initializeKeystore(Path keystoreFile, Provider provider) throws Exception {
 
    char[]    passwd    = "^q=|GEkm,'AqWfmY|z$n".toCharArray();
    KeyPair   keypair   = createKeyPair();
    
    KeyStore  keystore  = KeyStore.getInstance("PKCS12");
    keystore.load(null, passwd); // looks weird but is necessary so the store gets initialized
    
    X509Certificate cert = createCertificate(keypair, provider, true);
    keystore.setKeyEntry("ehighway_client1", keypair.getPrivate(), passwd, new Certificate[] {cert});
    
    try (OutputStream outstream = new BufferedOutputStream(Files.newOutputStream(keystoreFile))) {
      keystore.store(outstream, passwd);
    }
      
  }

  private KeyPair createKeyPair() throws Exception {
    KeyPairGenerator keypairGen = KeyPairGenerator.getInstance("RSA");
    keypairGen.initialize(512, new SecureRandom());
    return keypairGen.generateKeyPair();
  }

  private X509Certificate createCertificate(KeyPair keypair, Provider provider, boolean isSelfSigned) throws Exception {
    
    CertConfig      certCfg       = new CertConfig("test", "test", "test", "test", "test");
    X500NameBuilder builder       = createStdBuilder(certCfg);

    LocalDateTime   begin         = LocalDate.now().atStartOfDay(); 
    LocalDateTime   end           = begin.plus(10, ChronoUnit.YEARS);
    
    ContentSigner   contentSigner  = new JcaContentSignerBuilder("SHA256WITHRSA").build(keypair.getPrivate());
    X509v3CertificateBuilder certGen = new JcaX509v3CertificateBuilder(
      new X500Name("cn=" + certCfg.getIssuer()),
      BigInteger.valueOf(1),
      toDate(begin),
      toDate(end),
      builder.build(),
      keypair.getPublic()
    );

    X509Certificate cert = new JcaX509CertificateConverter().setProvider(provider).getCertificate(certGen.build(contentSigner));

    if (isSelfSigned) {
      cert.verify(keypair.getPublic());
      cert.verify(cert.getPublicKey());
    }

    CertificateFactory certFactory = CertificateFactory.getInstance("X.509", provider);
    return (X509Certificate) certFactory.generateCertificate(new ByteArrayInputStream(cert.getEncoded()));
    
  }

  private Date toDate(LocalDateTime ldt) {
    return new Date(ldt.toInstant(ZoneOffset.UTC).toEpochMilli());
  }

  private static X500NameBuilder createStdBuilder(CertConfig certCfg) {
    X500NameBuilder result = new X500NameBuilder(RFC4519Style.INSTANCE);
    result.addRDN(RFC4519Style.c, certCfg.getCountry());
    result.addRDN(RFC4519Style.o, certCfg.getOrganisation());
    result.addRDN(RFC4519Style.l, certCfg.getLocation());
    result.addRDN(RFC4519Style.st, certCfg.getState());
    return result;
  }

}

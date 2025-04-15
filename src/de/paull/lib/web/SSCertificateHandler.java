package de.paull.lib.web;

import de.paull.lib.files.Reader;
import de.paull.lib.util.Table;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.URL;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;

/**
 * A class which generates an SSLContext for WebServer and Socket of an JKS certificate
 */
public class SSCertificateHandler {

    private static SSLContext sslContext;
    private static X509Certificate certificate;

    private final String pw;
    private final InputStream stream;

    /**
     * Initializes the SSlContext with certificate
     * @param stream stream for the SSLCertificate File, use Reader to get Stream
     * @param pw password for the SSL certificate
     */
    public SSCertificateHandler(InputStream stream, String pw) {
        this.stream = stream;
        this.pw = pw;
        try {
            init();
            String table = Table.convert(new String[][] {
                    {"CN", certificate.getSubjectX500Principal().getName().substring(3)},
                    {"Expires at", new SimpleDateFormat("z, dd.MM.yyyy - HH:mm").format(certificate.getNotAfter())},
                    {"Expires in", (certificate.getNotAfter().getTime() - System.currentTimeMillis()) / 86400000 + " days"}
            }, "Certificate loaded...");
            System.out.println(table);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("UNABLE TO VERIFY CERTIFICATE");
        }
        checkIp();
    }

    /**
     * Initializes the SSlContext with certificate
     * @param path path for the SSLCertificate File
     * @param pw password for the SSL certificate
     */
    public SSCertificateHandler(String path, String pw) throws IOException {
        this(Reader.readToStream(path), pw);
    }

    /**
     * @return the configured SSLContext
     */
    public static SSLContext getSSLContext() {
        return sslContext;
    }

    private void init() throws NoSuchAlgorithmException, KeyStoreException, IOException, CertificateException, UnrecoverableKeyException, KeyManagementException {
        sslContext = SSLContext.getInstance("TLS");

        // initialise the keystore
        char[] password = pw.toCharArray();
        KeyStore ks = KeyStore.getInstance("JKS");
        ks.load(stream, password);

        // setup the key manager factory
        KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
        kmf.init(ks, password);

        // setup the trust manager factory
        TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
        tmf.init(ks);

        // setup the HTTPS context and parameters
        sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);

        // The certificate
        certificate = (X509Certificate) ks.getCertificate("myssl");
    }

    private void checkIp() {
        String domain = "";
        String dnsDomain = "";
        try {
            assert certificate != null;
            domain = certificate.getSubjectX500Principal().getName().substring(5).trim();
            InetAddress ip = InetAddress.getByName(new URL("https://" + domain).getHost());
            dnsDomain = ip.getHostAddress();
        } catch (Exception e) {
            e.printStackTrace();
        }
        String currentIP = getMyIp();
        boolean same = currentIP.equals(dnsDomain);

        System.out.println(Table.convert(new String[][] {
                {"Domain", domain},
                {"Current IP", currentIP},
                {"DNS-A-Record", dnsDomain},
                {"Same Ip", same + ""}
        }, "Current DNS / IPv4"));

        if (!same)
            System.err.println("WARNING:\nDNS-A-Record points to a different ip");
    }

    private static String getMyIp() {
        // https://stackoverflow.com/questions/2939218/getting-the-external-ip-address-in-java
        String ip = "";
        try {
            URL whatismyip = new URL("http://checkip.amazonaws.com");
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    whatismyip.openStream()));
            ip = in.readLine(); // you get the IP as a String
        } catch(IOException e) {
            ip = "";
        }
        return ip;
    }
}

# DECOIT RT-Connector #

The DECOIT RT-Connector is a library that allows to access most parts of the Request Tracker (RT) REST API.

## Usage ##

Follow the steps below to start using this library in your project. Currently the library is not available via Maven Central und thus must be compiled and installed locally.

### Preparation ###

1. Maven 3 is required to build this project, install if it is not available yet.
2. Java 7 or greater is required for this library to work
3. If you are planning to use HTTPS for connection to RT and RT uses a self-signed certificate, you have to import that to your Java TrustStore. See below for details.

### Creating and importing a self-signed certificate ###

Java requires a self-signed certificate to have the Server Alias Name (SAN) Extension set. For default Apache HTTP Server installations this extension is missing. To create a usable certificate you may use the Java 7+ Keytool. Follow these steps to create a new certificate and import it to your TrustStore. You need openssl to be installed for this to work.

1. Create the new certificate with SAN extension set (replace ALIAS, PASSWORD and SERVER-IP for their respective values).
`keytool -genkey -keyalg RSA -alias ALIAS -keystore tmp_keystore.jks -storepass PASSWORD -validity 360 -keysize 2048 -ext san=ip:SERVER-IP`
2. Convert the KeyStore to a PKCS12 container. The Java KeyStore password and PKCS12 container password must match!
`keytool -importkeystore -srckeystore tmp_keystore.jks -destkeystore tmp_pkcs12.p12 -deststoretype PKCS12`
3. Extract the certificate from the container and convert it to .pem format.
`openssl pkcs12 -in mypkcs12pkcs12.p12 -out my_cert.pem -nodes`
4. Copy my_cert.pem to the /etc/ssl/certs directory on your server and configure Apache HTTP Server to use that certificate for HTTPS connections. The SSLCertificateKeyFile directive may be removed since the private key is contained inside the .pem file.
`SSLCertificateFile /etc/ssl/certs/my_cert.pem`
5. On your machine that will run the RT Connector library type the following (replace SERVER-IP with Apache server IP address)
`openssl s_client -connect SERVER-IP:443 2>&1`
6. Look for the certificate fingerprint and copy it, including the BEGIN CERTIFICATE and END CERTIFICATE lines, to a plain text file rt_cert.txt.
7. Import that certifcate into the TrustStore that your local software will be using (replace ALIAS and KEY-STORE-FILE with their respective values).
`keytool -import -alias "ALIAS" -file rt_cert.txt -keystore KEY-STORE-FILE`

### Installation and usage ###

If you plan to use the RT Connector inside a Maven project, just type the following inside the local copy of this repository and add the dependency to your project.

```
lang bash

mvn install

```

```
lang maven

<dependency>
    <groupId>de.decoit</groupId>
    <artifactId>rt-connector</artifactId>
    <version>0.1</version>
</dependency>

```

When not using the library inside a maven project do the following and add the JAR file from the target directory to your class path. Make sure to download and add all dependencies as well, the library does not package them into its JAR file.

```
lang bash

mvn package

```

keytool -genkeypair \
  -alias testkey \
  -keyalg RSA \
  -keysize 2048 \
  -validity 365 \
  -keystore testkeystore.jks \
  -storepass changeit \
  -keypass changeit \
  -dname "CN=localhost, OU=Test, O=Company, L=City, ST=State, C=Country" \
  -ext "SAN=dns:localhost"

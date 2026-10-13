set -e
BT=/opt/android-sdk/build-tools/35.0.0; AJ=/opt/android-sdk/platforms/android-34/android.jar
rm -rf out && mkdir -p out/gen out/cls out/dex
$BT/aapt2 compile --dir res -o out/res.zip
$BT/aapt2 link -I $AJ --manifest AndroidManifest.xml -o out/base.apk --java out/gen out/res.zip --min-sdk-version 24 --target-sdk-version 34 --version-code 3 --version-name 1.2
javac -source 8 -target 8 -nowarn -bootclasspath $AJ -cp $AJ -d out/cls $(find src out/gen -name '*.java') 2>&1 | grep -v warning || true
$BT/d8 --min-api 24 --release --lib $AJ --output out/dex $(find out/cls -name '*.class')
cp out/base.apk out/u.apk && (cd out/dex && zip -q ../u.apk classes.dex)
$BT/zipalign -f -p 4 out/u.apk out/a.apk
[ -f nemito.jks ] || keytool -genkeypair -keystore nemito.jks -storepass nemito123 -keypass nemito123 -alias nemito -keyalg RSA -keysize 2048 -validity 10000 -dname "CN=Nemito,O=Oli,C=HU" >/dev/null 2>&1
$BT/apksigner sign --ks nemito.jks --ks-pass pass:nemito123 --out Nemito.apk out/a.apk
$BT/apksigner verify --verbose Nemito.apk | head -5
$BT/aapt2 dump badging Nemito.apk | head -4

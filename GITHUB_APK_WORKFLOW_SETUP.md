# GitHub Actions से APK बनाने का आसान तरीका (How to Add GitHub Actions Workflow)

AI Studio द्वारा GitHub पर `.github/workflows/` फ़ाइल पुश करने पर GitHub सुरक्षा कारणों से ("Insufficient permissions to push workflow files") रोक लगाता है। 

इसे हल करने के लिए, कोड GitHub पर पुश हो जाने के बाद, नीचे दिए गए 2 आसान स्टेप्स से आप GitHub वेबसाइट पर सीधे वर्कफ़्लो जोड़ सकते हैं:

---

### स्टेप 1: GitHub पर नई फ़ाइल बनाएं
1. अपने ब्राउज़र में अपनी GitHub रिपॉजिटरी खोलें।
2. ऊपर **"Add file"** बटन पर क्लिक करें -> **"Create new file"** चुनें।
3. फ़ाइल के नाम (Name your file) में यह लिखें:
   ```text
   .github/workflows/build-apk.yml
   ```
   *(जैसे ही आप `.github/` लिखेंगे, GitHub अपने आप फ़ोल्डर बना देगा)*

---

### स्टेप 2: नीचे दिया गया कोड पेस्ट करके Commit करें

फ़ाइल के अंदर यह पूरा कोड पेस्ट करें:

```yaml
name: Build Android APK - RUDRA STUDY LIFE PRO

on:
  push:
    branches: [ "main", "master" ]
  pull_request:
    branches: [ "main", "master" ]
  workflow_dispatch:

jobs:
  build:
    name: Build & Export APK
    runs-on: ubuntu-latest

    steps:
      - name: Checkout Repository
        uses: actions/checkout@v4

      - name: Set up JDK 17
        uses: actions/setup-java@v4
        with:
          distribution: 'temurin'
          java-version: '17'

      - name: Setup Gradle
        uses: gradle/actions/setup-gradle@v3

      - name: Grant Execute Permission for Gradlew
        run: chmod +x gradlew

      - name: Ensure .env exists
        run: |
          if [ ! -f .env ]; then
            cp .env.example .env
          fi

      - name: Run Unit Tests
        run: ./gradlew testDebugUnitTest --stacktrace

      - name: Build Debug APK
        run: ./gradlew assembleDebug --stacktrace

      - name: Upload Debug APK Artifact
        uses: actions/upload-artifact@v4
        with:
          name: RUDRA-STUDY-LIFE-PRO-debug-apk
          path: app/build/outputs/apk/debug/app-debug.apk
          retention-days: 14
```

4. नीचे हरे बटन **"Commit changes..."** पर क्लिक करें।

---

### स्टेप 3: APK डाउनलोड करें
1. GitHub रिपॉजिटरी के **"Actions"** टैब पर जाएं।
2. **"Build Android APK - RUDRA STUDY LIFE PRO"** रन होगा (1-2 मिनट)।
3. रन पूरा होने के बाद, उसके अंदर **Artifacts** सेक्शन से **`RUDRA-STUDY-LIFE-PRO-debug-apk`** डाउनलोड करें और अनज़िप करके अपने फ़ोन में इंस्टॉल करें!

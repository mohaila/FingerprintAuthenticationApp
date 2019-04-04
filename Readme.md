# Fingerprint authentication app
Use phone fingerprint scanner to authenticate the user.
The fingerprint API is for Android 6.0+ (minimum build API 23)
- Enable screen lock
- Add a fingerprint
- for the emulator: use settings - security - fingerprint and register a fingerprint with the command:
adb -e emu finger touch 2222 (or any id you want)
- for the emulator, to emulate a touch:
 adb -e emu finger touch 2222
 - to emulate a wrong fingerprint:
 adb -e emu finger touch 11
 - add an action to onAuthenticationSucceeded to start another activity
 ## Screenshots
 ![No finger registered screen](/images/00.png)
 ![Authentication success screen](/images/01.png)
 ![Authentication failure screen](/images/02.png)

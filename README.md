# TikTakChat

Android chat app prototype built with Jetpack Compose and the Backdrop Liquid Glass library.

## Current prototype

- Swipe or tap between Status, Calls, Chats, and Profile.
- Liquid Glass bottom tabs and a separate circular search control.
- Morphing page actions and full-screen global search with a progressive Liquid Glass blur.
- Chat, status, call-history, and profile-settings screens use sample data.
- The TikTakChat logo is included as a local asset; the app background is plain black.

The chat screen currently keeps sample messages locally. A deployed Pterodactyl backend, account system, and real-time message persistence still need server configuration.

## Build

With Android SDK 37 and Java configured:

```sh
ANDROID_HOME="$HOME/Android/sdk" ANDROID_SDK_ROOT="$HOME/Android/sdk" ./gradlew assembleDebug
```

The debug APK is written to `app/build/outputs/apk/debug/app-debug.apk`.

Liquid Glass components are adapted from [Kyant0/AndroidLiquidGlass](https://github.com/Kyant0/AndroidLiquidGlass) under Apache License 2.0. The license is included in `licenses/AndroidLiquidGlass-LICENSE`.
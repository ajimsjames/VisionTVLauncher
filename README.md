# Vision TV Launcher 📺✨

[![Android TV](https://img.shields.io/badge/Platform-Android%20TV%20%7C%20Google%20TV-blue.svg?style=flat-square&logo=android)](https://github.com/ajimsjames/VisionTVLauncher)
[![License](https://img.shields.io/badge/License-Apache%202.0-green.svg?style=flat-square)](LICENSE)
[![GitHub Release](https://img.shields.io/github/v/release/ajimsjames/VisionTVLauncher?style=flat-square)](https://github.com/ajimsjames/VisionTVLauncher/releases)
[![Build Status](https://img.shields.io/badge/Build-Passing-brightgreen.svg?style=flat-square)]()

A sleek, modern **Frosted Glass / Apple tvOS inspired** custom launcher crafted specifically for **Android TV, Google TV, and Sony BRAVIA** displays.

---

## 🌟 Key Features

- **Frosted Glass Monochrome Palette**: Crisp translucent cards, 3D D-Pad focus zoom, and white outline highlights.
- **Dynamic 4K Nature Backdrops**: Auto-transitions across bundled high-resolution 4K nature and forest wallpapers with smooth cross-fading.
- **Live Animated Weather Telemetry**: Real-time regional telemetry displaying current conditions, feels-like temperature, humidity, and wind stats with custom vector weather animations.
- **Hardware TIS HDMI Switching**: Direct hardware tuning integration into **MediaTek TV Input Service (TIS)**:
  - `HDMI 1`: Cable / DTH (`HW2`)
  - `HDMI 2`: Gaming Console / PlayStation 5 (`HW3`)
- **Seamless Home Button Redirect**: Built-in `HomeWatcherService` accessibility interceptor that keeps your TV anchored to Vision TV Launcher when pressing the remote's `HOME` key.
- **Revamped 5-Column App Drawer**: Clean overlay grid displaying all installed applications with intuitive D-Pad navigation.
- **Direct SmartTube 4K HDR Launch**: Out-of-the-box support for **SmartTube** as the primary TV YouTube player.

---

## 🚀 Installation & Deployment

### Quick Install via ADB
```bash
adb connect <TV_IP_ADDRESS>:5555
adb install -r VisionTVLauncher-v1.0.apk
```

### Build from Source
```bash
git clone https://github.com/ajimsjames/VisionTVLauncher.git
cd VisionTVLauncher
./gradlew assembleDebug
```

---

## 🛠️ Remote Control Navigation

| Remote Button | Action |
|---|---|
| **D-Pad (Up/Down/Left/Right)** | Focus navigation with smooth 3D elevation |
| **Center / OK** | Launch highlighted application or HDMI input |
| **Back** | Dismiss All Apps drawer or return to top shelf |
| **Home** | Anchors instantly to Vision TV Launcher |

---

## 📜 License
Apache License 2.0. Built for community customization.

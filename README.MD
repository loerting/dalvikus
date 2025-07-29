# Dalvikus

[![GitHub release](https://img.shields.io/github/v/release/loerting/dalvikus?style=flat-square)](https://github.com/loerting/dalvikus/releases)  
[![License: GPL v3](https://img.shields.io/badge/License-GPLv3-blue.svg?style=flat-square)](https://www.gnu.org/licenses/gpl-3.0.html)  
[![Kotlin](https://img.shields.io/badge/Kotlin-Compose%20Multiplatform-7F52FF?style=flat-square&logo=kotlin)](https://kotlinlang.org)

---

## What is Dalvikus?

**Dalvikus** is a modern Android reverse engineering and modification toolkit built with [Compose Multiplatform](https://www.jetbrains.com/lp/compose-multiplatform/).  
Designed for developers and researchers who want to inspect, edit, and rebuild Android apps seamlessly.

> [!Ethical reminder]
> With great power comes great responsibility. Dalvikus is intended **ONLY** for ethical purposes.

---

## Features

- Open APK and DEX files, allowing direct editing of DEX files inside APKs **without unpacking**
- Rich smali language editor with **syntax highlighting**
- Assist popup for **code completion** while typing
- Light and dark themes for comfortable editing
- Support for **multiple decompilers** to analyze bytecode
- Integrated app signing utility supporting **version 1 to version 4 signatures** for re-signing modified APKs
- Built-in **ADB run bridge** to deploy and start apps on connected devices directly
- Powerful search tools: **tree view, string constants, method and field references**

---

## Getting Started

### Requirements

- JDK 11 or higher
- Supported OS: Windows, Linux, macOS

### Installation

Download the latest release from [Releases](https://github.com/loerting/dalvikus/releases) and follow the instructions for your platform.

Or build from source:

```bash
git clone https://github.com/loerting/dalvikus.git
cd dalvikus
./gradlew :composeApp:run
```
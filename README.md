# Modpack Installer

Modpack Installer is a Java-based application designed to simplify the process of downloading, organizing, and setting up Minecraft modpacks from CurseForge. It features both a graphical user interface (GUI) and a command-line interface (CLI), making it versatile for different user environments. The application is particularly useful for server administrators who want to quickly set up a modded Minecraft server.

## Table of Contents

- [Features](#features)
- [Requirements](#requirements)
- [Setup Instructions](#setup-instructions)
  - [1. Clone the Repository](#1-clone-the-repository)
  - [2. Set Up the Project Structure](#2-set-up-the-project-structure)
  - [3. Compile the Application](#3-compile-the-application)
  - [4. Run the Application](#4-run-the-application)
  - [5. Optional: Package as a JAR](#5-optional-package-as-a-jar)
- [Configuration](#configuration)
  - [CurseForge API Key](#curseforge-api-key)
  - [Themes](#themes)
- [Usage](#usage)
  - [GUI Mode](#gui-mode)
  - [CLI Mode](#cli-mode)
- [Contributions](#contributions)
- [License](#license)
- [Acknowledgements](#acknowledgements)

## Features

- **GUI and CLI Support:** 
  - The application can run in both graphical and command-line modes, allowing users to choose their preferred interface.
  
- **CurseForge API Integration:** 
  - Fetch and display modpacks from specific authors using the CurseForge API. Select and download specific versions of modpacks.

- **Customizable Installation:**
  - Automatically organize downloaded files, remove client-side mods, and set up a Minecraft server with ease.

- **Theme Support:** 
  - Switch between light and dark themes in the GUI to match your environment or preference.

## Requirements

- **Java Development Kit (JDK) 8 or higher:** 
  - Required to compile and run the Java application.

- **CurseForge API Key:** 
  - Needed to access modpack data from the CurseForge API.

## Setup Instructions

### 1. Clone the Repository

First, clone the repository to your local machine:

```bash
git clone https://github.com/your-username/modpack-installer.git
cd modpack-installer

# Helm charts
- [Setup](#setup)
  - [Download Helm binary](#download-helm-binary)
  - [Install Helm](#install-helm)
  - [Verify installation](#v-installation)
## Setup
### Download Helm binary
1. Go to the official [Helm releases page](https://github.com/helm/helm/releases)
2. Download the **Windows AMD64 .zip** file for the latest version (e.g., `helm-v3.14.0-windows-amd64.zip`).
### Install Helm
* **Unzip the file** (you’ll get a folder like `windows-amd64`).
* Copy the `helm.exe` file to a location in your system's `PATH`, such as:
  * `C:\Program Files\Helm` (create it if it doesn't exist)
* Add this folder to your system’s PATH variable:
  * Open Start → search *Environment Variables* → Edit the System Environment Variables
  * Click **Environment Variables**
  * Under **System variables**, find `Path`, click **Edit**, and add: `C:\Program Files\Helm`
### Verify installation
- Close and reopen your terminal (PowerShell or Command Prompt), then run:
  ```bash
  helm version
  ```
- You should see output like:
  ```bash
  version.BuildInfo{Version:"v3.14.0", ...}
  ```
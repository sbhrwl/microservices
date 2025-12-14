# Helm charts
- [Setup](#setup)
  - [Download Helm binary](#download-helm-binary)
  - [Install Helm](#install-helm)
  - [Verify installation](#v-installation)
- [Create Helm chart structure](#create-helm-chart-structure)
- [Clean up the default templates](#clean-up-the-default-templates)
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
## Create Helm chart structure
- Generate the basic `Helm chart directory`. 
- Run this in your terminal:
  ```
  helm create orchestrate-sensor-services
  ```
- This creates a directory called [**orchestrate-sensor-services**](orchestrate-sensor-services) with default templates and values.
<img src="images/directorystructure.jpg">

## Clean up the default templates
- Helm’s `create` command generates a bunch of example templates we don’t need. Let’s simplify.
- Go to the `templates` folder:
  ```
  cd orchestrate-sensor-services/templates
  ```
- Delete all the default templates *except* `_helpers.tpl`.
  - *(Use **`del`** if you’re in Command Prompt on Windows instead of Git Bash or PowerShell)*
  ```bash
  del deployment.yaml service.yaml hpa.yaml ingress.yaml serviceaccount.yaml tests\test-connection.yaml
  del tests\test-connection.yaml & rmdir tests & del NOTES.txt

  rm deployment.yaml service.yaml hpa.yaml ingress.yaml serviceaccount.yaml tests/test-connection.yaml
  ```
- You should only have this file left:
  ```
  _helpers.tpl
  ```
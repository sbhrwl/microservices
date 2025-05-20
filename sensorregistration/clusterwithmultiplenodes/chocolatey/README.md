# Install Chocolatey on Windows
- Open PowerShell as Administrator
  - Press Windows key, search for PowerShell
  - Right-click → Run as administrator
- Run this command:
```
Set-ExecutionPolicy Bypass -Scope Process -Force; [System.Net.ServicePointManager]::SecurityProtocol = [System.Net.ServicePointManager]::SecurityProtocol -bor 3072; iex ((New-Object System.Net.WebClient).DownloadString('https://community.chocolatey.org/install.ps1'))
```
- Wait for installation to complete
It will show success message like:
  - Chocolatey vX.X.X installed
- Verify installation: `choco --version`


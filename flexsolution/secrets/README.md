# Configure Hashicorp vault to persist secrets in Cloud SQL
- [Prerequisites](#rrerequisites)
- [Vault installation](#vault-installation)
- [Configure Cloud SQL for Vault](#configure-cloud-sql-for-vault)
- [Configure Vault to use Cloud SQL](#configure-vault-to-use-cloud-sql)
- [Configure Vault for production](#configure-vault-for-production)
- [Application accessing secrets from vault](#application-accessing-secrets-from-vault)
  - [Fetching secrets via API](#fetching-secrets-via-api)
- [Example python code to fetch secrets](#example-python-code-to-fetch-secrets)
- [Automatic secret rotation](#automatic-secret-rotation)
- [Key benefits](#key-benefits)
- [Conclusion](#conclusion)
## Prerequisites
* Vault requires persistent storage to keep encrypted secrets. 
  * Instead of using a local filesystem or an in-memory backend, we will configure **Cloud SQL as Vault’s backend**.
* You have a running Cloud SQL instance and VM1 in your service project.
* You have the necessary IAM permissions to create resources and configure services in both your service and host projects.
* You have SSH access to VM1.
* Next steps
  * install HashiCorp Vault on VM1 and configure it to persist secrets in Cloud SQL
## Vault installation
* Install HashiCorp Vault on VM1:
  * SSH into VM1:
    * Use gcloud compute ssh vm1 --zone=<your-vm1-zone> --project=<your-service-project> to connect to VM1.
  * Install Vault:
    * Download the Vault binary from the HashiCorp website (or use a package manager if available for your OS).
    * Example for Linux (using wget and unzip):
      * `wget https://releases.hashicorp.com/vault/<vault-version>/vault_<vault-version>_linux_amd64.zip`
      * `unzip vault_<vault-version>_linux_amd64.zip`
      * `sudo mv vault /usr/local/bin/`
    * Replace <vault-version> with the desired Vault version.
    * Verify the installation
      * `vault --version`
 ## Configure Cloud SQL for Vault
* Create a Vault Database and User:
  * Connect to your Cloud SQL instance using gcloud sql connect <cloud-sql-instance-name> --user=root --project=<your-service-project>.
  * Create a dedicated database for Vault:
    * `CREATE DATABASE vault;`
  * Create a user for Vault with appropriate permissions:
    * `CREATE USER 'vaultuser'@'%' IDENTIFIED BY '<your-vault-user-password>';`
    * `GRANT ALL PRIVILEGES ON vault.* TO 'vaultuser'@'%';`
    * `FLUSH PRIVILEGES;`
  * Replace `<your-vault-user-password>` with a strong password.
## Configure Vault to use Cloud SQL
* Create a Vault Configuration File:
  * Create a file (e.g., vault.hcl) with the following configuration:
```
storage "postgresql" {
  address = "<cloud-sql-instance-private-ip>:5432"
  username = "vaultuser"
  password = "<your-vault-user-password>"
  database = "vault"
  table = "vault_kv_store"
}

listener "tcp" {
  address = "0.0.0.0:8200"
  tls_disable = 1 #Disable TLS for initial setup. Enable in production.
}

disable_mlock = true
```
* Replace:
  * `<cloud-sql-instance-private-ip>` with the private IP address of your Cloud SQL instance.
  * `<your-vault-user-password>` with the password you set for the vaultuser.
* Start Vault:
  * Run Vault with the configuration file:
  * `vault server -config=vault.hcl`
* Initialize and **Unseal Vault**:
  * Open a new SSH session to VM1.
  * Initialize Vault:
    * Run: `vault operator init`
    * This will generate:
      * `Unseal Keys`
        * Store these securely.
        * You'll need them to `unseal Vault after restarts`.
      * `Initial Root Token`
        * Store this securely
        * You'll need it to `authenticate to Vault`.
   * `Unseal Vault`:
     * Run: `vault operator unseal <unseal-key-1>`
       * Repeat for each unseal key
  * `Authenticate to Vault`:
    * Set the `VAULT_ADDR` environment variable:
      * `export VAULT_ADDR='http://127.0.0.1:8200'`
    * Authenticate using the root token:
      * `vault login <root-token>`
## Configure Vault for production
* Enable TLS:
  * Generate `TLS certificates` and configure Vault to use them.
  * Update the `listener block` in your `vault.hcl` file.
* Enable mlock:
  * Remove `disable_mlock = true` from your vault.hcl file.
* Secure Storage:
  * Store `unseal keys` and the `root token` securely (e.g., using a secrets management service).
* Firewall Rules:
  * Ensure that only authorized systems can access Vault on `port 8200`.
* Service Account:
  * Create a dedicated `service account` for Vault and `grant` it the necessary Cloud SQL `permissions`.
* Test Vault Functionality:
  * Create a secret: `vault kv put secret/my-secret my-value=test`
  * Read the secret: `vault kv get secret/my-secret`

## Application accessing secrets from vault
- Your application retrieves the credentials dynamically using Vault’s API.
### Fetching secrets via API
```sh
vault kv get secret/db-creds
```

## Example python code to fetch secrets
Your application uses **Vault’s API** to retrieve the secrets before connecting to the database.

```python
import hvac  # HashiCorp Vault API Client
import psycopg2  # PostgreSQL DB Connection

# Vault authentication
vault_client = hvac.Client(url="http://vault-server:8200", token="your-vault-token")

# Retrieve database credentials
db_secrets = vault_client.secrets.kv.v2.read_secret_version(path="db-creds")["data"]["data"]
db_user = db_secrets["username"]
db_password = db_secrets["password"]

# Connect to Cloud SQL (PostgreSQL)
conn = psycopg2.connect(
    dbname="your_db",
    user=db_user,
    password=db_password,
    host="your-cloudsql-instance",
    port="5432"
)

print("Connected to Cloud SQL securely!")
```

## Automatic secret rotation
- You configure **Vault’s dynamic secrets** feature to issue temporary credentials instead of storing static ones.

```sh
vault secrets enable database

vault write database/config/mydb \
    plugin_name=postgresql-database-plugin \
    allowed_roles="my-app-role" \
    connection_url="postgresql://{{username}}:{{password}}@cloudsql-instance-ip:5432/your_db" \
    username="admin" \
    password="admin_password"

vault write database/roles/my-app-role \
    db_name="mydb" \
    creation_statements="CREATE ROLE \"{{name}}\" WITH LOGIN PASSWORD '{{password}}' VALID UNTIL '{{expiration}}';" \
    default_ttl="1h" \
    max_ttl="24h"
```

- Now, instead of storing passwords permanently, your app gets a **temporary database user** from Vault:

```sh
vault read database/creds/my-app-role
```

## Key benefits
✅ **Security:** No hardcoded secrets in the application code.  
✅ **Scalability:** Cloud SQL stores Vault's encrypted data, allowing multi-instance deployments.  
✅ **Automatic Rotation:** Vault can rotate secrets periodically, reducing security risks.  
✅ **Auditing:** All secret access can be logged for compliance.  

## Conclusion
- By using **HashiCorp Vault** for secrets management and **Google Cloud SQL** for persistence, you ensure that secrets are **securely stored, easily accessible, and dynamically managed**.
- This approach is ideal for modern cloud-native applications that require **strong security and compliance**.
 * Access Control: Use Vault's policies and authentication methods to control access to secrets.
 * Firewall Rules: Restrict access to Vault's port (8200).
 * Least Privilege: Grant Vault only the necessary permissions to Cloud SQL.
 * Audit Logs: Enable audit logs to track Vault activity.

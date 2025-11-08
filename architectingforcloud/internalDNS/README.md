# Configure web app url to use Internal DNS and certificates signed with public root CA 
- [Scenario](#scenario)
- [Key components](#key-components)
- [Configure an internal load balancer](#configure-an-internal-load-balancer)
- [Obtain a public root certificate](#obtain-a-public-root-certificate)
- [Configure google cloud private DNS](#configure-google-cloud-private-dns)
- [Configure web application on application server for HTTPS](#configure-web-application-on-application-server-for-https)
- [Configure firewall rules](#configure-firewall-rules)
- [Island browser configuration](#island-browser-configuration)
- [Access flow](#access-flow)
- [Advantages](#advantages)
- [Important considerations](#important-considerations)
- [Comparison with other approaches](#comparison-with-other-approaches)
- [Where to install certificates](#where-to-install-certificates)
  - [TLS termination at the internal Load balancer](#tls-termination-at-the-internal-load-balancer)
  - [TLS termination on the VMs](#tls-termination-on-the-vms)
  - [Key considerations](#key-considerations)
## Scenario
- The **Web application** on application server is exposed with a **DNS name only accessible within the VPN tunnel** (i.e., private/internal DNS).
  - `private DNS zone`/`app.gfc.landisgyr.com`
- External users access it via **Island Browser**, which routes traffic securely through the **VPN tunnel**.  
- **SSL/TLS encryption is required** to secure traffic between users and the application.  
- Instead of using **self-signed certificates**, which require manual trust configuration, a **publicly trusted certificate** is used.
  - Certificates are issued by a **Public Certificate Authority (CA)** (e.g., `DigiCert`, `GlobalSign`, `Let's Encrypt`)
## Key components
* **Web Application:** Web application running on application server.
* **Load Balancer (Recommended):** An internal load balancer to distribute traffic to application server and provide a `stable endpoint`.
* **Internal DNS:** A managed DNS service within GCP environment.
* **Public Root Certificate:** A certificate from a trusted Certificate Authority (CA).
* **VPN Tunnel:** The existing VPN tunnel between the Island browser and host project.
## Configure an internal load balancer
* **Load balancer is recommended**
* Go to the `Load Balancing page` in the Google Cloud Console for **`service project`**.
* Create an internal load balancer.
* **Backend Configuration:**
  * Add application server as a backend.
  * Configure health checks to ensure the application is running.
* **Frontend Configuration:**
  * **Reserve** an `internal IP address` for the load balancer.
  * Select the shared VPC network and subnet.
* This load balancer will provide a `stable internal IP address` for the application.
## Obtain a public root certificate
* Purchase or obtain a certificate from a reputable CA (e.g., Let's Encrypt, DigiCert, Sectigo).
* Ensure the `certificate covers the domain name` we will use for our application (e.g., `app.internal.example.com`/`app.gfc.landisgyr.com`).
* We will need
  * `Certificate`
  * `Private key`
  * `CA certificate chain`
## Configure google cloud private DNS
* Go to the `Cloud DNS` page in the Google cloud console for your **host project**.
* Create a **private DNS zone**.
  * Name: `internal.example.com` (or our chosen internal domain).
    * `gfc.landisgyr.com`
  * DNS name: `internal.example.com.`
  * Network: Select `shared VPC network`.
* Create an **A record** within the zone:
  * Name: `app.internal.example.com.`
    * `app.gfc.landisgyr.com` 
  * Type: A
  * TTL: (Choose a suitable TTL)
  * Data: The `internal IP address of internal load balancer` (or `application server's internal IP` if not using load balancer).
## Configure web application on application server for HTTPS
* Install below on **internal load balancer** (or on **application server** itself).
  * Certificate
  * Private key and 
  * CA certificate chain 
* Configure your application to use `HTTPS` with the installed certificate.
* Ensure the application is listening on port `443`.
## Configure firewall rules
* Go to the Firewall Rules page in the Google Cloud Console for **host project**.
* Create a firewall rule to allow HTTPS traffic (port 443) from the `**VPN tunnel's internal IP range**` to the `internal load balancer's IP address (or application server's IP`).
    * Name: allow-vpn-to-application server-https
    * Network: shared VPC network
    * Direction: ingress
    * `Source IP ranges`: `VPN tunnel internal IP range`
    * Target: internal load balancer IP (or `application server's IP`)
    * Protocol/ports: tcp/443
## Island browser configuration
* Ensure the Island browser is configured to use the DNS servers provided by your GCP VPC network.
  * This is usually handled automatically by the VPN tunnel.
## Access flow
* When the user types `https://app.internal.example.com`/`https://app.gfc.landisgyr.com` in their browser
* The browser will:
  * **Resolve** the `DNS name` to the `internal IP address` of the load balancer (or application server).
  * Establish an `HTTPS` connection to the load balancer (or application server).
  * The load balancer (or application server) will `present the certificate` signed by the public root CA.
  * Because the certificate is signed by a public CA, the browser will trust it.
  * The application will then function as expected.
## Advantages
* **Security:** HTTPS provides encrypted communication.
* **Trust:** Using a public root certificate ensures browser trust.
* **Internal DNS:** Provides a manageable and scalable DNS solution.
* **VPN Tunnel:** Securely connects the "Island browser" to your GCP network.
* **Load Balancer (Optional but Recommended):** Provides high availability and scalability.
## Important considerations
* **Certificate Management:** Implement a process for renewing your SSL/TLS certificates.
* **Security Hardening:** Secure your application server application and operating system.
* **Monitoring and Logging:** Monitor the application, load balancer, and VPN tunnel for performance and security.
* **Testing:** Thoroughly test the setup from the "Island browser" to ensure everything is working correctly.
* **DNS Resolution:** ensure the island browser is actually using your google cloud's DNS servers.

## Comparison with other approaches

| Approach                      | Internal DNS + Public CA | Internal DNS + Private CA | Public DNS + Public CA |
|--------------------------------|--------------------------|---------------------------|-------------------------|
| **DNS Resolution Scope**       | Private (Internal)       | Private (Internal)        | Public (Internet)       |
| **Certificate Trust**          | Trusted by all browsers | Requires manual CA trust  | Trusted by all browsers |
| **Security Risk**              | Low (VPN-protected)      | Low (VPN-protected)       | Higher (Exposed to public) |
| **Ease of Deployment**         | Easy                     | Complex (CA management)   | Easy |
| **Use Case**                   | Secure internal apps with external users | Internal corporate apps | Public-facing applications |

## Where to install certificates
- When your application running on a VM is behind an internal load balancer (ILB), the certificate installation location depends on **how you want to handle `TLS/SSL` termination**.
### TLS termination at the internal Load balancer
* **Recommended for most cases**
* **Where to install certificates:**
  * Install the SSL/TLS certificate on the **internal load balancer itself**.
* **How it works:**
    * The ILB receives the client's HTTPS request.
    * The ILB decrypts the traffic using the installed certificate.
    * The ILB then forwards the decrypted traffic to your backend VMs.
    * Communication between the ILB and the VMs can be either HTTP or HTTPS (if you want end-to-end encryption).
* **Advantages:**
    * `Centralized certificate management`
    * `Offloads SSL/TLS processing from your VMs, improving their performance`.
    * Simpler configuration on the VMs.
    * Enhanced security by managing the certificates in one location.
* **Considerations:**
    * You'll need to ensure the backend VMs are configured to accept traffic from the ILB.
    * If you require end to end encryption, you will need to install certificates on the backend servers as well.

### TLS termination on the VMs
* **End-to-End TLS encryption**
* **Where to install certificates:**
  * Install the SSL/TLS certificate on **each of your backend VMs**.
* **How it works:**
    * The ILB forwards the HTTPS request to the backend VMs without decrypting it.
    * The backend VMs decrypt the traffic using their own installed certificates.
* **Advantages:**
    * Provides end-to-end encryption, ensuring that traffic is encrypted throughout its journey.
* **Disadvantages:**
    * `Increased complexity` in certificate management, as you need to manage certificates on each VM.
    * `Higher CPU load on the VMs`, as they are responsible for SSL/TLS processing.
    * `More difficult to manage`.
* **When to use:**
    * When you have strict security requirements that mandate end-to-end encryption.
    * When you need to perform specific TLS-related actions on the backend VMs.
## Key considerations
* **Certificate Type:**
  * Use a certificate that matches the domain name or IP address used to access your application.
* **Certificate Authority (CA):**
  * Obtain your certificate from a trusted CA or use a self-signed certificate for testing purposes.
* **Certificate Management:**
  * Implement a process for managing certificate renewals and revocations.
* **Internal vs. External certificates:**
  * If the clients accessing your application are within the same internal network, you might consider using internal certificates issued by your own CA.
  * If the clients are external, you'll need certificates from a public CA.
* **Load balancer configuration:**
  * Configure your ILB to use the appropriate SSL/TLS settings, such as the certificate, protocol versions, and cipher suites.
* **Health checks:**
  * Configure health checks on the ILB to ensure that only healthy VMs receive traffic.

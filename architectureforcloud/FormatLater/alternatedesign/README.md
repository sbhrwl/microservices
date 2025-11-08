# Flex solution
- [Subnet allocation per node](#subnet-allocation-per-node)
  - [When to move to one subnet](#when-to-move-to-one-subnet)
  - [When to keep separate subnets](#when-to-keep-separate-subnets)
  - [Recommendation](#recommendation)
- [Communication](#communication)
  - [Terminal server communication](#terminal-server-communication)
  - [Inter node communication](#inter-node-communication)
  - [External communication](#external-communication)
- [GitLab and public cloud registry](#gitLab-and-public-cloud-registry)
- [Key takeaways](#key-takeaways)
- [Setting up Island browser for application access](#setting-up-island-browser-for-application-access)
  - [Deploy Island gateway in landing zone](#deploy-island-gateway-in-landing-zone)
  - [Secure tunnel for application access over island browser](#secure-tunnel-for-application-access-over-island-browser)
  - [Access flow for web application](#access-flow-for-web-application)
- [Setting up Island for support team to access Terminal Server](#setting-up-island-for-support-team-to-access-terminal-server)
  - [RDP gateway in landing zone](#rdp-gateway-in-landing-zone)
  - [Secure tunnel for RDP access over island browser](#secure-tunnel-for-rdp-access-over-island-browser)
  - [Access flow for support team](#access-flow-for-support-team)
## Subnet allocation per node
- Each **GFC node** is placed in a **separate subnet**, which improves security and network isolation.
  - **Node 1 (Infrastructure/Management)** → `10.0.1.0/28`
  - **Node 2 (GFC Application)** → `10.0.2.0/28`
  - **Node 3 (TSM/Other Services)** → `10.0.3.0/28`
  - **Node 4 (Terminal Server for Admin/Testing)** → `10.0.4.0/28`

### When to move to one subnet
- If **ip conservation** is a major concern, especially in a vpc with a limited address space.  
- If **instance-level firewall rules** are strictly defined and managed properly.  
- If the team prefers **simpler network management** with fewer subnets.  
### When to keep separate subnets
- If **strict network isolation** is a requirement (e.g., regulatory compliance, reducing blast radius).  
- If **scaling** the network in the future might require additional segmentation.  
- If different **routing and firewall rules per service** are needed (e.g., terminal server should never talk to infrastructure).  
### Recommendation
- If ip space is tight and instance-level firewalls can achieve the same security goals, consolidating into one subnet is a valid choice. however, ensure:  
  - firewall rules are **well-documented and strictly enforced**.  
  - no unintended cross-communication happens.  
  - future scaling or segmentation needs are considered.  

## Communication
### Terminal server communication
- **The terminal server is used for troubleshooting, support, and test operations.**  
- It interacts with other nodes using different TCP ports:
  - **Talks to Node 1 (Infrastructure/Management):**  
    - `TCP 22` → SSH (Secure Shell access for remote admin)  
    - `TCP 443` → HTTPS (Web-based secure access)  
    - `TCP 29000` → Configuration Manager communication  
  - **Talks to Node 2 (GFC Application):**  
    - `TCP 22` → SSH  
    - `TCP 2405` → IEC104 test connections  
    - `TCP 29000` → Configuration Manager  
  - **Talks to Node 3 (TSM):**  
    - `TCP 443` → HTTPS  
    - `TCP 22` → SSH  
### Inter node communication
- Each node has **specific communication rules** to interact with other nodes:
- **Node 2 (GFC App) → Node 3 (TSM)**
  - `TCP 443` → Secure web communication  
  - `TCP 9004, 9005, 9006` → Kafka or internal messaging services  

- **Node 2 (GFC App) → Node 1 (Infrastructure)**
  - `TCP 443` → Secure web communication  
  - `TCP 29301, 29343` → Logging & messaging  

- **Node 1 (Infrastructure) → Node 2 (GFC App)**
  - `TCP 443` → Secure web communication  
  - `TCP 27700` → Spring Gateway (Microservices API Gateway)  
  - `TCP 15673` → WebStomp messaging  

- **Node 1 (Infrastructure) → Node 3 (TSM)**
  - `TCP 443` → Secure web communication  
### External communication
- **IEC104 Client → Node 2 (GFC App)**
  - `TCP 2404` → IEC 60870-5-104 protocol (SCADA communication)  
- **Node 2 (GFC App) → HES (Head-End System)**
  - `TCP 64646` → AMQP over TLS for secure message transfer  

## GitLab and public cloud registry
- **Kisters Cloud Registry is publicly accessible** but requires **organization credentials** to access artifacts securely.  
- **GitLab CI/CD pipeline** will be **configured to pull artifacts** from the Kisters Cloud Registry and **deploy them onto the GFC service project**.  
- This ensures **controlled and automated deployment** while maintaining security.

## Key takeaways
✅ **Each GFC node is in a separate subnet**, ensuring **network segmentation** and **isolation**.  
✅ **Terminal Server (Node 4) serves as an access point** for management and testing, connecting securely to all other nodes.  
✅ **Nodes communicate using specific TCP ports**, ensuring proper isolation and security.  
✅ **External communication follows secure protocols**, especially for SCADA (IEC104) and HES.  
✅ **Kisters Cloud Registry & GitLab CI/CD integration** enables secure and controlled deployments.

## Setting up Island browser for application access
- Users from customer organization to access the GFC application using **Island browser**
### Deploy Island gateway in landing zone 
- Deploy **`Island Gateway`** in Landing Zone (LZ)
  - Deploy Island Gateway VMs in a dedicated security VPC.
  - Configure **HTTPS termination with an external load balancer**.
  - Set up **firewall rules** to allow only Island browser traffic from customer organization.
### Secure tunnel for application access over island browser
-  Establish a **`secure tunnel`**
  - The **`Island browser`** should connect to the landing zone via a `secure tunnel` using a `public root certificate`.
  - Ensure only authorized users from the customer organization can access the tunnel.
- Ensure GFC is accessible only within the VPN.
  - Configure **internal DNS resolution** to resolve https://gfc.example.com over VPN.
  - Restrict **direct internet access** to GFC from external users.
### Access flow for web application
- User Access Request (**from Island browser**)
  - The **user** launches the **Island browser**.  
  - They enter the **GFC application URL** (e.g., `https://gfc.example.com`).  
  - The request is sent via **Island gateways deployed within the Landing Zone (LZ)**.
- Request routing through **Landing zone**  
  - The request **reaches the Landing Zone (DMZ)**, acting as a **secure entry point**.  
  - The **DMZ forwards the request via a VPN tunnel** to the **GFC application**, ensuring encrypted communication.  
  - The **GFC application is exposed via a private DNS name**, resolvable only within the VPN tunnel.
- GFC Application Presents **SSO Login**  
  - The **GFC application detects the external user account** and presents an **SSO login button**.  
  - The user **clicks the SSO button**, triggering authentication via **Enterprise ID (Azure AD)**.  
- User **Authenticates via SSO**  
  - The **Identity Provider (IdP)** (Enterprise ID / Azure AD) prompts the user to enter their **external credentials**.  
  - The user **enters their credentials**.  
  - The IdP **validates the credentials** and, upon success, issues an **access token**.  
- **Authorization and role mapping**  
  - The **GFC application retrieves user attributes** from the **IdP token**.  
  - It extracts **security group membership** from Enterprise ID.  
  - The **security groups are mapped to GFC application roles** for access control.  
- User **gains access to GFC application**  
  - The **GFC application grants access** based on the **mapped role**.  
  - The user **successfully accesses the application**, with permissions aligned to their **assigned security group**.  
## Setting up Island for support team to access Terminal Server
- The support team (inside the same organization) needs **RDP access** to the **Terminal Server (TS) in GCP** via **Island browser**.  
### RDP gateway in landing zone
- To allow `Remote Desktop` access
- **Required for:** Securely accessing the **Windows Terminal Server** via **RDP over Island.**  
- **Setup:**  
  - Deploy an **RDP Gateway** in **LZ** to handle remote desktop connections.  
  - Enable **Azure AD-based authentication** for access control.  
  - Configure **CheckPoint firewall rules** to restrict access to only support users.  
  - Use an **Internal Load Balancer** (or VPN) to route RDP traffic securely.  

### Secure tunnel for RDP access over island browser
- **Required for:** Ensuring a secure connection between **Island browser** and the **Terminal Server**.  
- **Setup:**  
  - If the Terminal Server is in a **private network**, create a **VPN or SSH tunnel** in LZ.  
  - Use **Cloud Identity-Aware Proxy (IAP) or Bastion Host** if needed for additional security.  

### Access flow for support team
- **User Initiates RDP Connection via Island Browser**
  - A **support team member** opens **Island browser**.  
  - They enter the **RDP Gateway URL** (e.g., `rdp-gateway.example.com`).  
  - The request is sent to the **Landing Zone (LZ) in GCP**.
  - **Island Browser (Support Team) → RDP Gateway (LZ)** 
- Traffic Reaches the **Landing Zone (LZ)**
  - The request first **hits the External HTTPS Load Balancer (if web-based RDP Gateway is used)**.  
  - If a direct **RDP client is allowed in Island browser**, traffic is routed to the **RDP Gateway** inside LZ.  
  - The RDP Gateway requires authentication before allowing access.
  - **Landing Zone RDP Gateway → Keycloak (Authentication via Azure AD)**
- Authentication via **Keycloak and Azure AD**
  - The user is **redirected to Keycloak** (hosted in LZ) for authentication.  
  - Keycloak verifies the credentials via **Azure AD/Enterprise ID**.  
  - If authentication is successful, **a session token is issued**, and the user is granted access to the RDP Gateway.
  - **Authenticated Session → RDP Gateway → Terminal Server (TS)**
- Secure **RDP Connection Established**
  - The **RDP Gateway** forwards the request to the **Windows Terminal Server (TS)** inside GCP.  
  - The **Terminal Server validates the user’s credentials** (Active Directory or local accounts).  
  - If authentication is successful, an **RDP session is established**.
  - **TS Validates User Credentials → Establishes RDP Session**
- User **Accesses Windows Desktop on Terminal Server**
  - The **Windows Desktop environment** opens inside the **Island browser** via RDP.  
  - The support team can now **troubleshoot the GFC application**.  
  - Any commands or configurations are executed **inside the Terminal Server**, isolated from the local machine.
  - **User Troubleshoots GFC from Windows Desktop on Terminal Server**
- Secure **Exit and Session Termination**
  - When the user logs out, the **RDP session is closed**.  
  - The **RDP Gateway logs the session for auditing and compliance**.  
  - Firewall and security policies in **Landing Zone (Checkpoint)** ensure that only authorized users accessed TS.
  - **Session Ends → Secure Exit & Logs Stored in Landing Zone**

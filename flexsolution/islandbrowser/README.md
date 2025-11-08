# Accessing services running in service project via Island browser
- [VPN tunnel between Island browser and host project](#vpn-tunnel-between-island-browser-and-host-project)
- [Steps to configure access](#steps-to-configure-access)
  - [Verify the VPN tunnel](#verify-the-vpn-tunnel)
  - [Create firewall rules in the Host project](#create-firewall-rules-in-the-host-project)
    - [For infrastructure server](#for-infrastructure-server)
    - [For web application](#for-web-application)
    - [For tsm server](#for-tsm-server)
  - [Apply network tags to the VMs](#apply-network-tags-to-the-vms)
- [Connect using internal IP addresses](#connect-using-internal-ip-addresses)
- [Advantages of this setup](#advantages-of-this-setup)
- [Notes](#notes)
## VPN tunnel between Island browser and host project
* Security: Traffic is encrypted within the VPN tunnel, providing a secure connection.
* Private IP Addresses: You can use the VMs' internal IP addresses, eliminating the need for public IP addresses and reducing exposure to the internet.
* Simplified Firewall Rules: You can create simpler firewall rules based on the VPN tunnel's internal IP range.
## Steps to configure access
### Verify the VPN tunnel
* Ensure the VPN tunnel between the "Island browser" network and your host project is active and functioning correctly.
* Confirm the internal IP address range assigned to the "Island browser" network within the VPN tunnel. This is crucial for configuring firewall rules.
### Create firewall rules in the Host project
 * Go to the Firewall Rules page in the Google Cloud Console for your host project.
 * Create firewall rules to allow traffic from the VPN tunnel's internal IP range to your VMs.
#### For infrastructure server
* Name: allow-vpn-to-infrastructure-server
* `Network`: Your Shared VPC network `shared-vpc-network`.
* Direction of traffic: Ingress.
* Action on match: Allow.
* `Targets`: Specified target tags (e.g., `infrastructure-server-tag`) or target service account.
* Source filter: IP ranges.
* `Source IP ranges`: Enter the `internal IP address range of the VPN tunnel` (the **Island browser** network).
* `Protocols and ports`:
 * TCP: 22, 443, 29000
* Click "Create."
#### For web application
* Name: allow-vpn-to-web-application
* `Network`: Your Shared VPC network `shared-vpc-network`.
* Direction of traffic: Ingress.
* Action on match: Allow.
* `Targets`: Specified target tags (e.g., `web-application-tag`) or target service account.
* Source filter: IP ranges.
* `Source IP ranges`: Enter the `internal IP address range of the VPN tunnel` (the **Island browser** network)
* `Protocols and ports`:
 * TCP: 22, 2405, 29000
* Click "Create."
#### For tsm server
* Name: allow-vpn-to-tsm-server
* `Network`: Your Shared VPC network `shared-vpc-network`.
* Direction of traffic: Ingress.
* Action on match: Allow.
* `Targets`: Specified target tags (e.g., `tsm-server-tag`) or target service account.
* Source filter: IP ranges.
* `Source IP ranges`: Enter the `internal IP address range of the VPN tunnel` (the **Island browser** network)
* `Protocols and ports`:
 * TCP: 22, 443
* Click "Create."
### Apply network tags to the VMs
* Go to the Compute Engine page in the Google Cloud Console for your service project.
* Edit each VM (infrastructure server, web application, tsm server).
* In the "Network tags" section, add the tags you used in the firewall rules (e.g., infrastructure-server-tag, web-application-tag, tsm-server-tag).
* Save the changes.
## Connect using internal IP addresses
* From the "Island browser," use the `VMs' internal IP addresses` to connect.
* Use the appropriate `ports` (22, 443, 29000, 2405) as specified in your requirements.
## Advantages of this setup
* `Enhanced security`: The VPN tunnel provides a secure, encrypted connection.
* `Simplified addressing`: You can use internal IP addresses, reducing the need for public IPs and simplifying routing.
* `Centralized control`: Firewall rules are managed in the host project, providing centralized control over network access.
## Notes
* **Routing**
  * Ensure that routing is properly configured within the VPN tunnel and your GCP network to allow traffic to flow between the "Island browser" network and your VMs.
* **VPN tunnel stability**
  * Monitor the VPN tunnel's stability and performance to ensure reliable connectivity.
* **Security best practices**
  * Continue to follow security best practices, such as regularly reviewing firewall rules and limiting access to only necessary resources.
* **Testing**
  * Thoroughly test the connectivity from the `Island browser` to each VM on the specified ports after configuring the firewall rules.

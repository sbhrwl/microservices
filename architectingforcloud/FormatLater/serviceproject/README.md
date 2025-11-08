# Service project setup
- [Steps to create the service project](#steps-to-create-the-service-project)
  - [Enable shared VPC on the Host project](#enable-shared-vpc-on-the-host-project)
  - [Create the Service project](#create-the-service-project)
  - [Attach the Service project to the Host project](#attach-the-service-project-to-the-host-project)
- [Creating subnets](#creating-subnets)
  - [Example scenario](#example-scenario)
  - [Summary](#summary)
- [Create VMs in the Service project](#create-vms-in-the-service-project)
- [Create the Cloud SQL instance](#create-the-cloud-sql-instance)
  - [Private Service Access for Cloud SQL](#private-service-access-for-cloud-sql)
  - [Configure authorized networks](#configure-authorized-networks)
- [Configure firewall rules](#configure-firewall-rules)
  - [Allow traffic between VMs](#allow-traffic-between-vms)
  - [Allow traffic from infrastructure server to Cloud SQL](#allow-traffic-from-infrastructure-server-to-cloud-sql)
  - [Consider security best practices](#consider-security-best-practices)
  - [Configure VM communication](#configure-vm-communication)
- [Important considerations](#important-considerations)
## Steps to create the service project
### Enable shared VPC on the Host project
* Go to the VPC network page in the Google Cloud Console for your host project.
* Select VPC network.
* Click on "Enable Shared VPC".
* Choose the service project(s) you want to attach to this host project. 
  * In this case, select your **new service project**.
### Create the Service project
* If you haven't already, create the service project in the Google Cloud Console.
### Attach the Service project to the Host project
  * This step is usually done during the [Shared VPC enablement process](#enable-shared-vpc-on-the-host-project). 
  * Ensure your service project is attached to the host project.
## Creating subnets
* Host Project manages subnets
  * With Shared VPC, the subnets are created and managed within the host project.
  * The service project doesn't create its own subnets.
  * It uses the subnets defined in the host project.
* Single subnet for simplicity
  * For your current setup with three VMs and a Cloud SQL instance, having all these resources reside within a single subnet is generally sufficient and simplifies management.
* Region and Zone
  * Subnets are regional resources, meaning they span multiple zones within a region.
  * You'll need to choose a region for your Shared VPC and create subnets within that region in your host project.
  * Your VMs and Cloud SQL instance in the service project will then be deployed within that region.
* IP address ranges
  * Carefully plan the IP address range for the subnet.
  * It should be large enough to accommodate all your resources (VMs, Cloud SQL, and any future resources you might add).
  * Consider using a private IP address range (e.g., 10.0.0.0/8, 172.16.0.0/12, or 192.168.0.0/16).
* Private Service Access for Cloud SQL
  * Cloud SQL needs a dedicated IP range within your Shared VPC network through Private Service Access.
  * This range should not overlap with the subnet range used by your VMs. 
  * This configuration is done in the host project as well.
### Example scenario
- Let's assume your host project is named `host-project` and your service project is `service-project`.
* In the Host project:
  * You create a VPC network (e.g., `shared-vpc-network`).
  * Within this VPC network, you create a subnet in a specific region (e.g., us-central1) named `vm-subnet`.
    * Example IP range for vm-subnet: `10.0.1.0/24`.
  * You configure `Private Service Access` for Cloud SQL. 
    * This involves reserving a `private IP range` (e.g., 10.0.2.0/24) that doesn't overlap with vm-subnet.
* In the Service project:
  * **Attach** `service-project` to `host-project` for Shared VPC.
  * When creating VMs in service-project, select:
    * Network: `shared-vpc-network` (from the host project)
    * Subnet: `vm-subnet` (from the host project)
 * When creating `Cloud SQL instance` in service-project, connect it to the `shared-vpc-network` and it will utilize the IP range reserved for `Private Service Access` (configured in the host project).

### Summary
- The subnet itself is defined and managed in the host project. 
- Your service project then uses this subnet to host its resources. 
- A single subnet is likely sufficient for your current needs, and careful planning of IP address ranges is crucial to avoid conflicts and ensure smooth operation.

## Create VMs in the Service project
* Go to the Compute Engine page in the Google Cloud Console for your service project.
* Click "Create Instance".
* Network Configuration:
  * Select the VPC network from your host project.
  * Choose the specific subnet from your host project that you want to use for this VM.
  * Assign an internal IP address (or let GCP assign one).
  * Configure external IP address as needed (for initial setup or if the VMs need to be accessible from the internet, but in your case, it seems like they primarily communicate internally).
  * Configure other VM settings (machine type, image, etc.) as required.
* Repeat this process for all three VMs (infrastructure server, web application, tsm server), ensuring they are in the same subnet for internal communication.
## Create the Cloud SQL instance
* Go to the Cloud SQL page in the Google Cloud Console for your service project.
* Click "Create Instance".
* Choose your database engine (e.g., PostgreSQL, MySQL).
* Configure the instance settings (name, region, zone, machine type, storage).
### Private Service Access for Cloud SQL
* Cloud SQL needs a dedicated IP range within your Shared VPC network through Private Service Access.
  * Ensure you have configured `Private Service Access (PSA)` for Cloud SQL in the `host project`.
  * This allows Cloud SQL to have an `internal IP address range` within your shared VPC network, enabling `secure and private communication with your VMs`.
* This range should not overlap with the subnet range used by your VMs.
* When creating your Cloud SQL instance in `service-project`, you'll connect it to the `shared-vpc-network` and it will utilize the IP range reserved for Private Service Access` (configured in the host project).
### Configure authorized networks
* Select the VPC network from your host project.
* Choose the same subnet that your VMs are using.
* **`Configure authorized networks`** if you need to restrict access to the Cloud SQL instance.
  * Although with Shared VPC and internal IPs, you might not need to explicitly authorize the entire subnet if firewall rules are in place
* Click "Create".
## Configure firewall rules
* Go to the Firewall rules page in the Google Cloud Console for your `host project` (since the firewall rules are associated with the network, which is in the host project).
### Allow traffic between VMs
* Create a firewall rule that allows traffic between the `internal IP ranges` of the subnet where your VMs reside.
  * Specify the source as the subnet's IP range.
  * Specify the destination as the subnet's IP range.
  * Allow the necessary protocols and ports (e.g., TCP, UDP, ICMP) for communication between your VMs.
### Allow traffic from infrastructure server to Cloud SQL
* Create a firewall rule that allows traffic from the `internal IP of infrastructure server` (or the subnet range) to the `internal IP of Cloud SQL private service access range` (or `Cloud SQL instance`).
  * Specify the source as the `IP of infrastructure server` (or the subnet)
  * Port as `5432 for Postgresql` or 3306 for MySQL.
### Consider security best practices
* Implement the `principle of least privilege` when creating firewall rules.
* Only allow the necessary traffic.
### Configure VM communication
* Once the VMs are created and the firewall rules are in place, you can configure the applications running on the VMs to communicate with each other using their `internal IP addresses`.
* Infrastructure server will connect to Cloud SQL using the `Cloud SQL instance's private IP address`
  * Make sure private service access is configured for Cloud SQL.
* Infrastructure server will communicate with Web application and TSM server using their internal IPs.
* Web application will communicate with Infrastructure server and TSM server using their internal IPs.
## Important considerations
* Private Service Access for Cloud SQL
  * Ensure you have configured `Private Service Access (PSA)` for Cloud SQL in the host project.
  * This allows Cloud SQL to have an internal IP address within your shared VPC network, enabling secure and private communication with your VMs.
* Security Groups/Tags
  * Consider using network tags or security groups to manage firewall rules more effectively, especially as your environment grows.
* Monitoring and Logging
  * Set up monitoring and logging for your VMs and Cloud SQL instance to track performance and identify potential issues.
* IAM Permissions
  * Ensure the necessary IAM permissions are granted to the service project to use resources in the host project.
* Testing
  * Thoroughly test the communication between your VMs and between infrastructure server and Cloud SQL after setting up the environment.

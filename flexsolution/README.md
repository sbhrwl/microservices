## Concepts
- **Host project aka Landing zone**
  - `Network admin`, enables to centrally manage VPCs, firewall rules, and **IAM settings** across networking projects
- **Networking project**
  - Consists of VPC, subnets and firewall rules.
  - `Shared VPC` is enabled on the networking project.
- **Service project**
  - Service project is linked to networking project.
  - Hosts the application workloads such as VMs and Cloud SQL.
* **Shared VPC**: This allows your service project to `use the subnets` defined in your networking project. 
* [Creating service project](serviceproject/README.md)
  * [Configure Hashicorp vault to persist secrets in Cloud SQL](secrets/README.md)
  * [Accessing services running in service project via Island browser](islandbrowser/README.md)
* [Configure web app url to use Internal DNS and certificates signed with public root CA](internalDNS/README.md)
* [Accessing web application via Island browser](useraccesstoapplication/README.md)
* [Deployment process](deployment/README.md)
* [Alternate design](alternatedesign/README.md)

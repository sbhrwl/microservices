| Email                                                                                                                                               | Status  | Name                                   | Description                                                                                  | OAuth 2 Client ID     |
| --------------------------------------------------------------------------------------------------------------------------------------------------- | ------- | -------------------------------------- | -------------------------------------------------------------------------------------------- | --------------------- |
| [gke-wi-secrets-01@cpet-d-smoc-c01-srv-aah-01.iam.gserviceaccount.com](mailto:gke-wi-secrets-01@cpet-d-smoc-c01-srv-aah-01.iam.gserviceaccount.com) | Enabled | gke-wi-secrets-01                      | Service account for accessing and managing secrets via Workload Identity                     | 107299971315134328771 |
| [gke-wi-c02@cpet-d-smoc-c01-srv-aah-01.iam.gserviceaccount.com](mailto:gke-wi-c02@cpet-d-smoc-c01-srv-aah-01.iam.gserviceaccount.com)               | Enabled | gke-wi-c02                             | Service account for GKE workloads in cluster C02                                             | 104306191782293523933 |
| [gke-wi-c01@cpet-d-smoc-c01-srv-aah-01.iam.gserviceaccount.com](mailto:gke-wi-c01@cpet-d-smoc-c01-srv-aah-01.iam.gserviceaccount.com)               | Enabled | gke-wi-c01                             | Service account for GKE workloads in cluster C01                                             | 118421224762473195244 |
| [gke-wi-c00@cpet-d-smoc-c01-srv-aah-01.iam.gserviceaccount.com](mailto:gke-wi-c00@cpet-d-smoc-c01-srv-aah-01.iam.gserviceaccount.com)               | Enabled | gke-wi-c00                             | Service account for GKE workloads in cluster C00                                             | 111279704760092950176 |
| [gke-wi-acs-sa-01@cpet-d-smoc-c01-srv-aah-01.iam.gserviceaccount.com](mailto:gke-wi-acs-sa-01@cpet-d-smoc-c01-srv-aah-01.iam.gserviceaccount.com)   | Enabled | gke-wi-acs-sa-01                       | Service account for Anthos Config Sync to manage cluster configuration via Workload Identity | 105799287988728200537 |
| [gke-wi-acs-01@cpet-d-smoc-c01-srv-aah-01.iam.gserviceaccount.com](mailto:gke-wi-acs-01@cpet-d-smoc-c01-srv-aah-01.iam.gserviceaccount.com)         | Enabled | gke-wi-acs-01                          | Service account for Anthos Config Sync in GKE                                                | 107617799084051669738 |
| [gke-node-01@cpet-d-smoc-c01-srv-aah-01.iam.gserviceaccount.com](mailto:gke-node-01@cpet-d-smoc-c01-srv-aah-01.iam.gserviceaccount.com)             | Enabled | gke-node-01                            | Node service account used by GKE nodes for cluster operations                                | 103101227181124887005 |
| [828752755010-compute@developer.gserviceaccount.com](mailto:828752755010-compute@developer.gserviceaccount.com)                                     | Enabled | Compute Engine default service account | Default Compute Engine service account for GKE and other Google Cloud services               | 116872117692202629477 |


---
# Microservices
- [Motivation](motivation/README.md)
- [Designing a system](https://github.com/sbhrwl/system_design/blob/main/projects/design/README.md)
- [Registering sensors](sensorregistration/README.md)
- [Sending commands to sensors](commandorchestration/README.md)
- [Analytics on sensor data](analytics/README.md)
- [Saving sensor data via gRPC](gRPC/README.md)
- [Hub to Sensor](hubToSensor/README.md)
<details>
  <summary>prompt</summary>

**Prompt:**
* I’ll provide you with a text, your task is to convert it into a **concise Markdown summary** suitable for documentation or technical reference.
* Follow these rules carefully:
  * Treat the content as **technical**, assuming you’re a **microservices expert**.
  * **Remove duplicate or redundant text**, keeping only essential technical information.
  * Convert all sections into **clean bullet-point summaries** and feel free to add new sections
  * Ensure the content follows a **logical flow** and build a story line (problem → motivation → evolution → solution → implementation → takeaway).
  * Apply the following Markdown formatting standards:
    * **Headings**
      * Only the **first word** of each heading starts with a capital letter; the rest are lowercase.
      * No **numbering** in headings (e.g., no “1.”, “2.”, etc.).
      * Use concise, topic-focused section titles.
    * **Index**
      * Always include an **index section** at the top of the document.
      * Use **GitHub-style internal links** for navigation: all lowercase, spaces replaced by hyphens, punctuation removed.
      * Example: `- [System design principles](#system-design-principles)`
    * **Spacing**
      * **No blank lines** between bullet points or sub-points.
      * **No blank lines** between major sections (e.g., between headings).
      * Avoid excessive vertical spacing for compact readability.
    * **Bullet points**
      * Use `-` for all bullets and sub-bullets (consistent indentation).
      * Main bullets introduce core concepts; sub-bullets provide explanations or examples.
      * Limit nesting to **two levels** for clarity and readability.
    * **Style**
      * Maintain a **professional, minimal, and presentation-friendly** tone.
      * Use **bold** for emphasis (key terms or section names).
      * Use **inline code formatting** (`like this`) for technical keywords, parameters, and code-level references.
      * Avoid horizontal dividers (`---`) between sections.
      * Keep sentences short and purposeful.
    * **Developer takeaways**
      * Add a dedicated **“Developer takeaways”** section at the end when relevant.
      * Summarize key implementation principles or design lessons in concise bullet points.
      * Use action-oriented phrasing (e.g., “Apply”, “Use”, “Avoid”, “Ensure”).
      * Keep tone instructive and directly applicable to engineering decisions.
      * Add **Visuals (Mermaid UML diagrams)**
      * Include **Mermaid diagrams** to represent flows, architectures, or dependencies.
      * Keep diagrams **minimal and readable** (avoid visual clutter).
      * Follow top to bottom, vertical layout.
      * Ensure arrows and node labels match the textual explanation.
      * Place diagrams **after** the relevant descriptive section.
</details>






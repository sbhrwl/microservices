# Microservices
- [Motivation](motivation/README.md)
- [Designing a system](https://github.com/sbhrwl/system_design/blob/main/projects/design/README.md)
- [Registering sensors](sensorregistration/README.md)
- [Sending commands to sensors](commandorchestration/README.md)
- [Analytics on sensor data](analytics/README.md)
- [Saving sensor data via gRPC](gRPC/README.md)
- [Hub to Sensor](hubToSensor/README.md)
- [App](app/README.md)
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







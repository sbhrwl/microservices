### Case 1: Active EMT (Direct Connection)
*You shoulder the entire regulatory burden, hosting the BSI-compliant security infrastructure yourself.*

```mermaid
graph TD
    subgraph WAN [Wide Area Network - External]
        A["Your Analytics Cloud<br/>(Registered EMT)"] -->|1. Initiates TLS Session via HSM| B[Smart Meter PKI]
    end

    subgraph Boundaries [Edge Security]
        C["Smart Meter Gateway<br/>(SMGW)"]
    end

    subgraph HAN [Home Area Network - Local]
        D["CLS Comm Adapter<br/>(Terminates TLS Proxy)"] -->|3. Local Control Protocol| E["Technical Facility<br/>(e.g., Heat Pump / Inverter)"]
    end

    A -->|2. Authenticated Connection| C
    C -->|Secure TLS Proxy Channel| D

    classDef external fill:#f9f,stroke:#333,stroke-width:2px;
    classDef edge fill:#bbf,stroke:#333,stroke-width:2px;
    classDef local fill:#bfb,stroke:#333,stroke-width:2px;
    class A external; class C edge; class D,E local;

```

### Case 2: Partner / Wholesale EMT (Indirect Connection)
*You bypass the regulatory nightmare by piggybacking on an established partner’s infrastructure via a standard Web API.*

```mermaid
graph TD
    subgraph YourDomain [Your Infrastructure]
        A["Your Analytics App"] 
    end

    subgraph PartnerDomain [Partner WAN Domain]
        B["Certified Partner Cloud<br/>(Wholesale EMT / GWA)"]
    end

    subgraph LocalGrid [Local Infrastructure]
        C["Smart Meter Gateway<br/>(SMGW)"] -->|3. Proxy Channel| D["CLS Adapter (HAN)"]
        D -->|4. Local Command| E["Technical Facility"]
    end

    A -->|1. Standard REST/JSON API| B
    B -->|2. Secure BSI Tunnel| C

    classDef mine fill:#ffcccb,stroke:#333,stroke-width:2px;
    classDef partner fill:#ffe57f,stroke:#333,stroke-width:2px;
    classDef grid fill:#e0e0e0,stroke:#333,stroke-width:1px;
    class A mine; class B partner; class C,D,E grid;

```

### Case 3: Edge / CLS Software Approach
*You don't fight your way in. Your analytics code lives right inside the HAN on the physical CLS device, using the SMGW purely to dial out.*

```mermaid
graph TD
    subgraph WAN [External Cloud]
        A["Your Cloud Backend"]
    end

    subgraph EdgeDevice [Physical CLS Hardware inside HAN]
        B[["Your Analytics App<br/>(Local Edge Instance)"]]
        C["CLS Comm Adapter"]
    end

    subgraph LocalEnv [HAN Environment]
        D["Technical Facility"]
    end

    subgraph Gateway [Border]
        E["Smart Meter Gateway<br/>(SMGW)"]
    end

    B -->|1. Internal Local Read| C
    C -->|2. Pulls Data| D
    B -->|3. Outbound Data Pushes| E
    E -->|4. Compressed Analytics| A

    classDef edgeApp fill:#b2dfdb,stroke:#004d40,stroke-width:3px;
    classDef hardware fill:#e0f2f1,stroke:#004d40,stroke-width:1px;
    class B edgeApp; class C hardware;

```

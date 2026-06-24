# EPMAS: Enterprise Portfolio Management & Archiving System

EPMAS is a fully functional, modular Java console application built to showcase advanced Java Object-Oriented Programming (OOP) and Data Programming paradigms. 

Rather than a generic student tracker, EPMAS implements a modern **FinTech asset registry and archive manager** that processes traditional equities and digital/cryptocurrency securities.

---

## 📂 Project Structure

All source files are located under the `com.epmas` package:

```text
EPMAS/
├── src/
│   └── com/
│       └── epmas/
│           ├── Asset.java                      # Chapter 1 & 2 (Base Model, Encapsulation, Static tracking)
│           ├── CryptoAsset.java                # Chapter 3 & 4 (Inheritance, Subclass Specialization)
│           ├── InvalidAssetException.java      # Chapter 5 (Custom Checked Exception)
│           ├── FileHandler.java                # Chapter 6 (I/O Streams: CSV, Binary, Serialization, Buffering)
│           ├── DatabaseHandler.java            # Chapter 7 (JDBC relational queries with Mock Fallback)
│           ├── PortfolioManager.java           # Chapter 4 (Upcasting, Polymorphic Report, Introspection)
│           └── Main.java                       # Integration test orchestrator
├── README.md                                   # Project guide
└── data/                                       # (Auto-generated) Persistence storage directory
    ├── portfolio.csv                           # Human-readable character stream data
    ├── portfolio_backup.csv                    # Performance-buffered copy
    ├── portfolio.dat                           # Primitive binary byte stream
    └── portfolio.ser                           # Serialized object state
```

---

## 🛠️ Compliance with OOP Chapters

EPMAS comprehensively integrates all 7 core Java paradigms:

1. **Chapter 1: OOP & Object Modeling**: Domain concepts are separated into discrete, self-contained objects (`Asset`, `CryptoAsset`) to enforce strict modularity and platform independence.
2. **Chapter 2: Inside Objects & Classes**: 
   - Uses `private` fields with public getters/setters (Encapsulation).
   - Chains default initializers using overloaded `this()` constructor routing.
   - Monitors live runtime objects via `totalObjectsCreated` static fields and static method trackers.
3. **Chapter 3: Inheritance**: Extends `Asset` to specialize into `CryptoAsset` (IS-A hierarchy) using `super(args)` to route initialization parameters up the class tree.
4. **Chapter 4: Polymorphism**: 
   - Stores upcasted objects inside a single `List<Asset>` collection.
   - Leverages **Dynamic Method Dispatch** by overriding `.toString()` so that the JVM resolves child behaviors automatically.
   - Uses `instanceof` (Type Introspection) for downcasting safely during reporting.
5. **Chapter 5: Exception Handling**: Guards value boundaries using `InvalidAssetException` (custom checked exception). Ensures resource recovery using robust `try-catch-finally` structures.
6. **Chapter 6: Files & Streams**: Includes 5 data serialization pipelines:
   - File metadata inspections.
   - CSV text streams using `PrintWriter` and `Scanner`.
   - Structural primitive binary files using `DataOutputStream` and `DataInputStream`.
   - Direct object graph serialization (`Serializable`) using `ObjectOutputStream` / `ObjectInputStream`.
   - Chunk performance buffering using `BufferedReader` and `BufferedWriter`.
7. **Chapter 7: Database Programming**: Implements the 4-step JDBC workflow using parameterized `PreparedStatement` models to mitigate SQL injection. Features a robust automatic fallback to an in-memory SQL simulation ledger if database drivers or engines are offline.

---

## 🚀 How to Compile and Run

### Step 1: Compile the Project
Open your terminal in the `EPMAS` directory and compile all Java classes:
```powershell
javac -d bin src/com/epmas/*.java
```

### Step 2: Run the Orchestrator
Execute the compiled program to run the full verification pipeline:
```powershell
java -cp bin com.epmas.Main
```

### Output Verification
Running the system will execute tests for object initialization tracking, custom checked exceptions, polymorphism execution, CSV text creation, raw binary dumps, object serialization, and JDBC database ledger commands (or simulated fallback database ledger).

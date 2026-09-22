# WSDL2JAVA
- [Gradle wsdl2java task](#gradle-wsdl2java-task)
- [Flow](#flow)
- [Summary](#summary)
## Gradle wsdl2java task
- `C:\Git\gfc-app\data-hub-api\build.gradle`
```gradle
wsdl2java {
  wsdlsToGenerate = [
    [
      "-b", "$projectDir/src/main/resources/xjb/bindings.xjc",
      "-verbose",
      "$projectDir/src/main/resources/test/Messages_test.wsdl"
    ]
  ]
}
```
## Flow
- Read `Messages_test.wsdl`
- Follow all `xs:import` and `xs:include` references.
- Invoke JAXB (XJC) to generate Java classes for all schema types.
- Apply your JAXB customizations from `bindings.xjc`
- Write the generated classes to: `build/generated/sources/wsdl`
```text
Messages_test.wsdl
Imports XSDs
JAXB (XJC)
Generates Java classes
build/generated/sources/wsdl
```
- The binding file is applied here: `"-b", "$projectDir/src/main/resources/xjb/bindings.xjc"`
- So your `LocalDate` and `ZonedDateTime` mappings will be used during generation.
## Summary
| Item | Purpose |
| --- | --- |
| `Messages_test.wsdl` | Entry point |
| Imported XSDs | Define the XML structure |
| `bindings.xjc` | Customize Java type mappings |
| `wsdl2java` | Runs JAXB/XJC and generates Java classes |
| `build/generated/sources/wsdl` | Output directory for generated classes |

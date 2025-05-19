## [IO operations](lectures/1.FileHandling.pdf)
- I/O (Input and Output) is used to process the input and produce the output.
- Java uses the concept of a `stream` to make I/O operation fast. 
- The `java.io` package contains all the classes required for input and output operations.
- We can perform file handling in Java by `Java I/O API`
- The `InputStream` class of the java.io package is an abstract superclass that represents an input stream of bytes.
- `InputStream` is an abstract class, it is not useful by itself. 
  - However, its subclasses can be used to read data.
- The `OutputStream` class of the java.io package is an abstract superclass that represents an output stream of bytes.
- `OutputStream` is an abstract class, it is not useful by itself. 
  - However, its subclasses can be used to write data.

- [File demo](exercises/FileDemo.java)
- [Overriding toString method](exercises/OverridingToStringMethod.java)
- [File writer](exercises/FileWriterDemo.java), [Buffered writer](exercises/BufferedWriterDemo.java), [Print writer](exercises/PrintWriterDemo.java) 
- [File reader](FileReaderDemo.java), [Buffered reader](exercises/BufferedReaderDemo.java) 

## [Serialization Deserialization](lectures/2.Serialization.pdf)
- The process of saving (or) writing state of an object to a file is called **Serialization**
- but strictly speaking it is the process of converting an object from java supported form to either network supported form (or) file supported form.
- By using `FileOutputStream` and `ObjectOutputStream` classes we can achieve serialization process.
  - `writeObject(Object obj)`
- The process of reading state of an object from a file is called **DeSerialization**
- but strictly speaking it is the process of converting an object from file supported form (or) network supported form to java supported form.
- By using `FileInputStream` and `ObjectInputStream` classes we can achieve DeSerialization.
  - `readObject()`
- [`Serialization DeSerialization demo`](exercises/SerializationDeSerializationDemo.java)
- [`Transient` keyword](exercises/SelectiveSerializationDemo.java)
- [Read JSON](exercises/ReadJSON.java)
- [Questions](lectures/3.FilehandlingQuestions.pdf)

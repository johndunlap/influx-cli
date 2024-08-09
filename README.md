# influx-cli
Influx CLI provides a flexible way to bind command line arguments to Java objects. Annotations are used to map command line interface to your data structure. The result is very concise code which reduces the boilerplate which is required to parse and validate command line arguments.

## Why should you use this?
* Library not a framework
* No dependencies
* Permissive licensing
* Flexible
* Easy to use
* Free

## Getting started
### Maven
```xml
<dependency>
    <groupId>org.voidzero</groupId>
    <artifactId>influx-cli</artifactId>
    <version>0.13.0</version>
</dependency>
```
### Example

```java
import org.voidzero.influx.cli.annotation.Command;
import org.voidzero.influx.cli.annotation.Arg;
import org.voidzero.influx.cli.InfluxCli;

@Command(
        openingText = "This is the opening text",
        closingText = "This is the closing text."
)
public class Example {
    @Arg(code = 'f')
    private String firstName;

    @Arg(code = 'l')
    private String lastName;

    public static void main(String[] args) {
        Example example = (Example) new InfluxCli().bindOrExit(Example.class, args);
        System.out.printf("Hello, %s %s!\n", example.firstName, example.lastName);
    }
}
```

The following is output when **--help** is passed into the above program:
```text
This is the opening text
  -f  --first-name  Accepts a string value
  -l  --last-name   Accepts a string value
This is the closing text.
```

The following is output when **--first-name John --last-name Dunlap** is passed into the above program:
```text
Hello, John Dunlap!
```

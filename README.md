# orchalang

## orcha language

* [documentation](http://www.orchalang.com/)
* [reference implementation](https://github.com/orchaland/orchalang/tree/master/orchalang)

Auto configuration: 

the default auto configuration is independant from any specific implementation for:
* pre-processing
* lexical analysis
* syntax analysis
* semantic analysis
* post-processing
* [auto configuration factories](https://github.com/orchaland/orchalang/blob/master/orchalang/src/main/resources/META-INF/spring.factories)
* [auto configuration class](https://github.com/orchaland/orchalang/blob/master/orchalang/src/main/java/orcha/lang/compiler/SpringIntegrationAutoConfiguration.java)

the lexical analysis needs another implementation because of the specific syntax of a when instruction. For example the 'a=0 and b=0' syntax comes from the Spring Expression Language:

````java
when "app terminates with 'a=0 and b=0'"
````

The default auto configuration of the when instruction is implemented there: https://github.com/orchaland/orchalang/blob/master/orchalang-spring-integration-implementation/src/main/kotlin/orcha/lang/compiler/referenceimpl/springIntegration/WhenInstructionForSpringIntegration.kt

The default auto configuration of the send instruction is implemented there: https://github.com/orchaland/orchalang/blob/master/orchalang-spring-integration-implementation/src/main/kotlin/orcha/lang/compiler/referenceimpl/springIntegration/SendInstructionForSpringIntegration.kt 
## Spring integration implementation

* [reference implementation](https://github.com/orchaland/orchalang/tree/master/orchalang-spring-integration-implementation)

Auto configuration: 

the default auto configuration offers:
* link edition
* output code generation
* [auto configuration factories](https://github.com/orchaland/orchalang/blob/master/orchalang-spring-integration-implementation/src/main/ressources/META-INF/spring.factories)
* [auto configuration class 1](https://github.com/orchaland/orchalang/blob/master/orchalang-spring-integration-implementation/src/main/kotlin/orcha/lang/compiler/referenceimpl/springIntegration/DefaultAutoConfiguration.kt)
* [auto configuration class 2](https://github.com/orchaland/orchalang/blob/master/orchalang-spring-integration-implementation/src/main/kotlin/orcha/lang/compiler/referenceimpl/springIntegration/SpringIntegrationAutoConfiguration.kt)

## How to use this project

````shell script
git clone https://github.com/orchaland/orchalang
cd orchalang
````

Open the project inside Intellij

Run the main program: https://github.com/orchaland/orchalang/blob/master/orchalang-orchacompiler/src/main/kotlin/orcha/lang/compiler/DemoApplication.kt

## The program to be compiled

The program to be compiled is: https://github.com/orchaland/orchalang/blob/master/src/main/orcha/source/orchaCompiler.orcha

The related configuration: https://github.com/orchaland/orchalang/blob/master/orchalang-orchacompiler/src/main/kotlin/orcha/lang/compiler/OrchaCompilerConfiguration.kt

## The configuration of the main program

https://github.com/orchaland/orchalang/blob/master/orchalang-orchacompiler/src/main/kotlin/orcha/lang/compiler/OrchaCompilerConfig.kt

The OrchaCompiler component: https://github.com/orchaland/orchalang/blob/master/orchalang/src/main/kotlin/orcha/lang/compiler/OrchaCompiler.kt

The output generation to SpringIntegration Java DSL: https://github.com/orchaland/orchalang/blob/master/orchalang-spring-integration-implementation/src/main/kotlin/orcha/lang/compiler/referenceimpl/springIntegration/OutputGenerationToSpringIntegrationJavaDSL.kt

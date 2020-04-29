# orcha language

## list of metadata

* Title: [title syntax](https://github.com/orchaland/orchalang/blob/master/orchalang/src/main/java/orcha/lang/compiler/syntax/TitleInstruction.java)

* [metadata usage](https://github.com/orchaland/orchalang/blob/master/orchalang/src/test/java/orcha/lang/compiler/referenceimpl/MetadataSyntaxAnalysisTest.java)

## list of instructions

* Receive: [receive syntax](https://github.com/orchaland/orchalang/blob/master/orchalang/src/main/java/orcha/lang/compiler/syntax/ReceiveInstruction.java)
* Compure: [compute syntax](https://github.com/orchaland/orchalang/blob/master/orchalang/src/main/java/orcha/lang/compiler/syntax/ComputeInstruction.java)
* [instructions usage](https://github.com/orchaland/orchalang/blob/master/orchalang/src/test/java/orcha/lang/compiler/referenceimpl/InstructionsSyntaxAnalysisTest.java)


## compiler

The compiler transpile an Orcha program into a adjacency graph of [integration patterns](https://www.enterpriseintegrationpatterns.com/):
* An Orcha compute instruction for instance if transpiled into a [service activator](https://www.enterpriseintegrationpatterns.com/patterns/messaging/MessagingAdapter.html)
* An Orcha when instruction is transpiled into an [aggregator](https://www.enterpriseintegrationpatterns.com/patterns/messaging/Aggregator.html)
* [Reference implementation](https://github.com/orchaland/orchalang/blob/master/orchalang/src/main/java/orcha/lang/compiler/OrchaCompiler.java)

Before launching, the adjacency graph of integration patterns needs to be transpiled into an executable version.
This [reference implentation](https://github.com/orchaland/orchalang/tree/master/orchalang-spring-integration-implementation) uses [Spring Integration](https://spring.io/projects/spring-integration) for the target of the executable version.

## compilation stages and auto configuration

the default auto configuration is independant from any specific implementation for:

* [preprocessing](https://github.com/orchaland/orchalang/blob/master/orchalang/src/main/java/orcha/lang/compiler/referenceimpl/PreprocessingImpl.java)
* [lexical analysis](https://github.com/orchaland/orchalang/blob/master/orchalang/src/main/java/orcha/lang/compiler/referenceimpl/LexicalAnalysisImpl.java)
* [syntax analysis](https://github.com/orchaland/orchalang/blob/master/orchalang/src/main/java/orcha/lang/compiler/referenceimpl/SyntaxAnalysisImpl.java)
* [semantic analysis](https://github.com/orchaland/orchalang/blob/master/orchalang/src/main/java/orcha/lang/compiler/referenceimpl/SemanticAnalysisImpl.java)
* [post-processing](https://github.com/orchaland/orchalang/blob/master/orchalang/src/main/java/orcha/lang/compiler/referenceimpl/PostprocessingImpl.java)

* [preprocessing usage](https://github.com/orchaland/orchalang/blob/master/orchalang/src/test/java/orcha/lang/compiler/referenceimpl/PreprocessingTest.java)

* [auto configuration factories](https://github.com/orchaland/orchalang/blob/master/orchalang/src/main/resources/META-INF/spring.factories)
* [auto configuration class](https://github.com/orchaland/orchalang/blob/master/orchalang/src/main/java/orcha/lang/compiler/SpringIntegrationAutoConfiguration.java)

the lexical analysis needs another implementation because of the specific syntax of a when instruction. For example the 'a=0 and b=0' syntax comes from the Spring Expression Language:

````java
when "app terminates with 'a=0 and b=0'"
````

The default auto configuration of the when instruction is implemented there: https://github.com/orchaland/orchalang/blob/master/orchalang-spring-integration-implementation/src/main/kotlin/orcha/lang/compiler/referenceimpl/springIntegration/WhenInstructionForSpringIntegration.kt

The default auto configuration of the send instruction is implemented there: https://github.com/orchaland/orchalang/blob/master/orchalang-spring-integration-implementation/src/main/kotlin/orcha/lang/compiler/referenceimpl/springIntegration/SendInstructionForSpringIntegration.kt 

Compailation stages depends on sub implementation: 
* [link edition](https://github.com/orchaland/orchalang/blob/master/orchalang-spring-integration-implementation/src/main/kotlin/orcha/lang/compiler/referenceimpl/springIntegration/LinkEditorImpl.kt)
* [output code generation](https://github.com/orchaland/orchalang/blob/master/orchalang-spring-integration-implementation/src/main/kotlin/orcha/lang/compiler/referenceimpl/springIntegration/OutputGenerationToSpringIntegrationJavaDSL.kt)

## Customizing the instructions

Each instruction can be easily customized by inheritance is sub projects.
For instance the [when instruction](https://github.com/orchaland/orchalang/blob/master/orchalang/src/main/java/orcha/lang/compiler/syntax/WhenInstruction.java) from this project is [adapted to the Spring integration sub project](https://github.com/orchaland/orchalang/blob/master/orchalang-spring-integration-implementation/src/main/java/orcha/lang/compiler/referenceimpl/springIntegration/WhenInstructionForSpringIntegration.java).
This adaptation can be reached automatically by the current implementation if:
* a [factory](https://github.com/orchaland/orchalang/blob/master/orchalang-spring-integration-implementation/src/main/java/orcha/lang/compiler/referenceimpl/springIntegration/WhenInstructionFactory.java) is provided.
* a bean with the name whenInstruction is defined into a [configuration file](https://github.com/orchaland/orchalang/blob/master/orchalang-spring-integration-implementation/src/main/java/orcha/lang/compiler/referenceimpl/springIntegration/SpringIntegrationAutoConfiguration.java).





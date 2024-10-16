# ♕ BYU CS 240 Chess

This project demonstrates mastery of proper software design, client/server architecture, networking using HTTP and WebSocket, database persistence, unit testing, serialization, and security.

## 10k Architecture Overview

The application implements a multiplayer chess server and a command line chess client.

[![Sequence Diagram](10k-architecture.png)](https://sequencediagram.org/index.html#initialData=C4S2BsFMAIGEAtIGckCh0AcCGAnUBjEbAO2DnBElIEZVs8RCSzYKrgAmO3AorU6AGVIOAG4jUAEyzAsAIyxIYAERnzFkdKgrFIuaKlaUa0ALQA+ISPE4AXNABWAexDFoAcywBbTcLEizS1VZBSVbbVc9HGgnADNYiN19QzZSDkCrfztHFzdPH1Q-Gwzg9TDEqJj4iuSjdmoMopF7LywAaxgvJ3FC6wCLaFLQyHCdSriEseSm6NMBurT7AFcMaWAYOSdcSRTjTka+7NaO6C6emZK1YdHI-Qma6N6ss3nU4Gpl1ZkNrZwdhfeByy9hwyBA7mIT2KAyGGhuSWi9wuc0sAI49nyMG6ElQQA)

## Modules

The application has three modules.

- **Client**: The command line program used to play a game of chess over the network.
- **Server**: The command line program that listens for network requests from the client and manages users and games.
- **Shared**: Code that is used by both the client and the server. This includes the rules of chess and tracking the state of a game.

## Starter Code

As you create your chess application you will move through specific phases of development. This starts with implementing the moves of chess and finishes with sending game moves over the network between your client and server. You will start each phase by copying course provided [starter-code](starter-code/) for that phase into the source code of the project. Do not copy a phases' starter code before you are ready to begin work on that phase.

## IntelliJ Support

Open the project directory in IntelliJ in order to develop, run, and debug your code using an IDE.

## Maven Support

You can use the following commands to build, test, package, and run your code.

| Command                    | Description                                     |
| -------------------------- | ----------------------------------------------- |
| `mvn compile`              | Builds the code                                 |
| `mvn package`              | Run the tests and build an Uber jar file        |
| `mvn package -DskipTests`  | Build an Uber jar file                          |
| `mvn install`              | Installs the packages into the local repository |
| `mvn test`                 | Run all the tests                               |
| `mvn -pl shared test`      | Run all the shared tests                        |
| `mvn -pl client exec:java` | Build and run the client `Main`                 |
| `mvn -pl server exec:java` | Build and run the server `Main`                 |

These commands are configured by the `pom.xml` (Project Object Model) files. There is a POM file in the root of the project, and one in each of the modules. The root POM defines any global dependencies and references the module POM files.

## Running the program using Java

Once you have compiled your project into an uber jar, you can execute it with the following command.

```sh
java -jar client/target/client-jar-with-dependencies.jar

♕ 240 Chess Client: chess.ChessPiece@7852e922
```

Phase 2 Link: https://sequencediagram.org/index.html?presentationMode=readOnly#initialData=IYYwLg9gTgBAwgGwJYFMB2YBQAHYUxIhK4YwDKKUAbpTngUSWDABLBoAmCtu+hx7ZhWqEUdPo0EwAIsDDAAgiBAoAzqswc5wAEbBVKGBx2ZM6MFACeq3ETQBzGAAYAdADZM9qBACu2GADEaMBUljAASij2SKoWckgQaIEA7gAWSGBiiKikALQAfOSUNFAAXDAA2gAKAPJkACoAujAA9D4GUAA6aADeAETtlMEAtih9pX0wfQA0U7jqydAc45MzUyjDwEgIK1MAvpjCJTAFrOxclOX9g1AjYxNTs33zqotQyw9rfRtbO58HbE43FgpyOonKUCiMUyUAAFJForFKJEAI4+NRgACUh2KohOhVk8iUKnU5XsKDAAFUOrCbndsYTFMo1Kp8UYdOUyABRAAyXLg9RgdOAoxgADNvMMhR1MIziSyTqDcSpymgfAgEDiRCo2XLmaSYCBIXIUNTKLSOndZi83hxZj9tgztPL1GzjOUAJIAOW54UFwtG1v0ryW9s22xg3vqNWltDBOtOepJqnKRpQJoUPjAqQtQxFKCdRP1rNO7sjPq5ftjt3zs2AWdS9QgAGt0OXozB69nZc7i4rCvGUOUu42W+gtVQ8blToCLmUIlCkVBIqp1VhZ8D+0VqJcYNdLfnJuVVk8R03W2gj1N9hPKFvsuZygAmJxObr7vOjK8nqZnseXmBjxvdAOFMLxfH8AJoHYckYB5CBoiSAI0gyLJkHMNkjkuao6iaVoDHUBI0HfAMUCDBYlgOLCoBnc5gSuUjyJDd4AToyglW1IcYAQBCkDQWEeMQ1F0VibFB0TXtk3KEokDFSwzThUjCyZZM3Q5cheX5f0D0DGAbSWGAADFwhqABZaseyLVTpwHZUuIUxkJ1EAokxZVNjUyTNs1zGtRmUl0S0KMtvV9bTPzIzsG3PNsoxjEdLJUhUbO3ScVUi7NorQJyE1ooFd3goS1DXTANzvZLqKuAYdLGQDry+P8L2-G9qPvdCMGfV93yq8KmvqqL-16g4zE4MDvD8QJkHsVIwC8FA2wK3xmGQ9JMkwB88lsnd5wqaRNPqLlmhaAjVCI7oGvHFrcrnYd+ovEq2JBTbUq4wTFthBas2EjExLs3VJLcmAZLkryc3OtB-L7Ut1MhCxUBodLUkZcVJSMbQ9AMBKAq3QcIQpHxbhgEGkeywwJKsgGOBQbhPIbWEwYh6ygvU3a+X2oyTPMkcYAAdRYSsuQRzKAF4wZJrdSvnD6wBXYqJex4pdx6KiFZBU51rADq316A4QNGiDAmwPjmzg6EYAAcXzVlltQta2uYDiSnKCoza5PCWnsfMztui6VbZCWboy-97ryx6UvBbjoQt0ZVHeyPLa+0SxbJxKDSBywQbp73wcxyGmfKcJXfCD0uQANQF4yzIRnm+YLwX-xFrOc+sh3w5h-GkiJ7QYCTgl-oNckwCjtRYQZpK840vkBRgAAqdnK490Ym6Slu0ud-MeWhRok6u+iTdiIfVBlhB1we+WtquKYF7UcZKn6K+PWkG+AEYnwAZgAFieFDMgUu4Vi+HQCBQDNl-oeCYXwr5enzOAvYMBGjKy2q1HIGsYAvi1j0S+lsb4VDvvmB+z836fymN-U01V-5PEAcA0BX5wFPEgdAvofRYHwOGqBTwY1IJAJoJwO8cAPKGCHikFaaFkGYRVk7WoDRDpXy9oHC8756GjAQccHeis+hg12BA-MUCaGrFYiHMRnF3LpkyEPWEfCTEoCHgnLEPcZB9xTIDSgsl0603pkvV0UN86F2LmXOenMGzV35nXC8Dc5Hjlcp4leXE24E07vIbug4-rkwNGmE0ZjFEFg8YFdknoKxVkye2GMV9sln2emSPB0ht6FH9vAfh1iirH2DnOMpaj76P1qno28qtCjq01l1dpg1WF63GgESa01LBUx4skGAAApCAfFzb5kCJQkAzZbaiIdruaolI3YyOCFnd82AgGTKgHACAPEoCzHacosqNSHqVQ0Z8J4xzgCnPOZczRdDKmaP0S06J5QABWCy0BmPmXxBpaJvp2MiY4tOGd3GwrUpyTSU8RxIwrgE7MQTa5gzCaOO6SLyp2QDojbQMKHFkgpBkypo9PHj25JPQUV8MUcxgFfHFAt2lC3aaU4lRj2X5kckkgFhpUgoDWR6MU9RgAXlhCy7Q1oTmUA+dAH6nFkkp0cX4LQpj8zysqbMRiellVnIuWqvl49KRVGkAoNmHLuSClee881UAhakU5YK0YD8eWVOqWcEO5RwWgvzEfE+Bj+WOz3Lcnp8A7b9O1sM9h+sxlICmmAQB6I43plgMAbAxzCDxESEIm26tDFRp2ntA6rRjDdL9g8w03A8CMnRmIOWoqQBNqgC2-QKBzFdp7QYGx6rnqaoCqmLtCkY50pyWWFmrty5spuBSlJjjO05oPiPS1uSZBVv8V6sQIre6ronTmkG07t1zr3ZihG-ramIBzYOlAYbmmbkjYrGNSDHyoM6om3WQA
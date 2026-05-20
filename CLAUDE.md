# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build and Test Commands
This project is configured as an Eclipse project (see `.project`, `.classpath`). There is no Maven or Gradle build system in the root.

- **Running Tests**: Tests are located in `src/local/sailpointTesting/`. Use a JUnit runner to execute tests such as `Local_UnitTestingTest.java`.
- **Compiled Classes**: Compiled output is located in the `bin/` directory.

## Architecture and Structure
The codebase is designed to unit test SailPoint IdentityIQ (IIQ) rules and snippets in a simulated environment.

- **Rule Definitions**: SailPoint rules are defined as XML files in the `Rule/` directory. These XMLs contain BeanShell code in the `<Source>` section.
- **Simulation Framework**: 
    - `src/sailpoint/services/standard/junit/`: Contains the core simulation logic.
    - `Console.java`: Re-implements key IdentityIQ methods to execute rules and tasks within a JUnit context.
    - `SailPointJUnitTestHelper`: Provides the base class for tests to manage the simulated SailPoint connection.
- **Testing Logic**: `src/local/sailpointTesting/Local_UnitTestingTest.java` demonstrates how to load a rule by name from the simulation and execute it with specific arguments to verify the return value.
- **Utility Snippets**: `src/local/sailpointTesting/IIQSnippets.java` contains common SailPoint operational patterns (e.g., account aggregation) for testing.

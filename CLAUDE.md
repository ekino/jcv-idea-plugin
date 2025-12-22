# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is an IntelliJ IDEA plugin that provides language support for JCV (JSON Content Validation) validators embedded within JSON files. JCV allows validation of JSON content using a pattern syntax like `{#validator_id:param1;param2#}` injected into JSON string values.

The plugin supports:
- Official JCV Core validators (e.g., `uuid`, `contains`, `date_time_format`)
- JCV-DB validators (e.g., `mongo_id`, `json_object`)
- Custom project-specific validators defined in `.jcvdefinitions.json`

## Build Commands

### Development Tasks
```bash
# Build the plugin
./gradlew build

# Clean and build
./gradlew clean build

# Run tests
./gradlew test

# Run a single test class
./gradlew test --tests "com.ekino.oss.jcv.idea.plugin.language.completion.JcvCodeInsightTest"

# Run a specific test method
./gradlew test --tests "com.ekino.oss.jcv.idea.plugin.language.completion.JcvCodeInsightTest.testValidatorCompletion"

# Run tests with coverage
./gradlew test koverXmlReport

# Run the plugin in a sandboxed IDE instance
./gradlew runIde

# Run UI tests
./gradlew runIdeForUiTests

# Verify plugin compatibility with multiple IDE versions
./gradlew verifyPlugin

# Generate coverage report
./gradlew koverXmlReport

# Run Qodana code quality checks
./gradlew qodana

# List all available tasks
./gradlew tasks
```

### Release Tasks
```bash
# Patch changelog for release
./gradlew patchChangelog

# Publish plugin to JetBrains Marketplace (requires PUBLISH_TOKEN env var)
./gradlew publishPlugin
```

### Code Generation
The project uses Grammar-Kit and JFlex to generate parser and lexer code:
- Grammar: `src/main/kotlin/com/ekino/oss/jcv/idea/plugin/language/Jcv.bnf`
- Lexer: `src/main/kotlin/com/ekino/oss/jcv/idea/plugin/language/Jcv.flex`
- Generated code: `src/main/gen/`

To regenerate parser/lexer after modifications:
1. Open the `.bnf` or `.flex` file in IntelliJ IDEA
2. Use Ctrl+Shift+G (Cmd+Shift+G on Mac) or right-click → "Generate Parser Code" / "Generate JFlex Lexer"
3. The generated files will be placed in `src/main/gen/`

## Architecture

### Language Injection System

The plugin works by **injecting the JCV language into JSON string values**. The core flow is:

1. **JcvLanguageInjector** (`language/psi/JcvLanguageInjector.kt`) detects JSON string values matching `{#.*` pattern
2. IntelliJ treats the injected portion as JCV language, enabling all IDE features
3. **JcvLexer** (generated from `Jcv.flex`) tokenizes the JCV syntax
4. **JcvParser** (generated from `Jcv.bnf`) creates PSI tree structure

### Validator Definition System

Validators come from three sources, managed by **JcvValidatorRegistry** (`definition/JcvValidatorRegistry.kt`):

#### 1. Official Built-in Validators
- **JcvCoreValidatorDefinition** (`definition/impl/JcvCoreValidatorDefinition.kt`): ~20 hardcoded JCV Core validators
- **JcvDbValidatorDefinition** (`definition/impl/JcvDbValidatorDefinition.kt`): 3 hardcoded JCV-DB validators
- Each includes parameter definitions, descriptions, suggested values, and documentation links

#### 2. Project Custom Validators
- Defined in `.jcvdefinitions.json` at project root
- Loaded by **ValidatorDescriptionsResolver** (`definition/custom/ValidatorDescriptionsResolver.kt`)
- Cached in **JcvDefinitionsCache** (`service/JcvDefinitionsCache.kt`)
- Can be auto-created via inspection quick-fixes

#### 3. Library Detection
- **JcvLibraryCache** (`service/JcvLibraryCache.kt`) detects JCV libraries per module
- Matches dependency patterns like `com.ekino.oss.jcv:jcv-core`
- Features are module-aware: completions/inspections only show validators available in current module

### Caching Services

Two parallel caching systems refresh on project changes:

- **JcvDefinitionsCache**: Caches custom validators from `.jcvdefinitions.json` per project
- **JcvLibraryCache**: Caches detected libraries per module

Both are initialized by **PostActivityStartupListener** (`listener/PostActivityStartupListener.kt`) and refreshed by **ProjectChangesListener** (`listener/ProjectChangesListener.kt`).

### Key Extension Points

| Feature | Implementation | Purpose |
|---------|---------------|---------|
| **Completion** | `language/completion/JcvCompletionContributor.kt` | Autocomplete validator IDs and parameter values |
| **Inspections** | `language/inspection/*` | 6 inspections: unknown validator, missing library, required/unexpected/empty parameters, whitespace |
| **Intentions** | `language/intention/*` | Suggest JCV replacements for raw JSON values |
| **Documentation** | `language/documentation/JcvValidatorDocumentationProvider.kt` | Hover docs with parameter descriptions and external links |
| **Line Markers** | `definition/custom/JcvDefinitionLineMarkerProvider.kt` | Navigate from validator usage to `.jcvdefinitions.json` |

### Replacement Suggestion System

**SuggestionReplacementUtil** (`language/intention/SuggestionReplacementUtil.kt`) analyzes JSON values and suggests matching JCV validators:

- **UUID pattern** → `{#uuid#}`
- **MongoDB ObjectId** → `{#mongo_id#}`
- **ISO date strings** → `{#date_time_format:iso_format_name#}`
- **URLs** → `{#url#}`, `{#templated_url#}`, etc.
- **Text** → `{#contains:...#}`, `{#regex:...#}`, etc.
- **Types** → `{#string_type#}`, `{#number_type#}`, `{#boolean_type#}`, etc.

Supports multi-caret replacement with intersection of matching validators.

## Important Implementation Details

### Module-Aware Design

All features respect module boundaries. When checking validator availability:
```kotlin
// Use this pattern from ValidatorOriginUtil.kt
validator.existsInModule(module)
```

This ensures validators requiring specific libraries are only shown/validated in modules that have those dependencies.

### PSI Utilities

**JcvPsiImplUtil** (`language/psi/impl/JcvPsiImplUtil.kt`) provides core PSI helper functions:
- `getValidatorId()`: Extract validator ID from PSI element
- `getIndexedParameters()`: Get parameters with their position indices
- `getSeparator()`, `getParameterValue()`: Navigate PSI structure

**JcvElementFactory** (`language/psi/JcvElementFactory.kt`) creates new PSI elements from text for refactoring.

### Grammar Syntax

JCV pattern format: `{#validator_id:param1;param2;param3#}`

- Validator ID between `{#` and `:`
- Parameters separated by `;`
- Closing with `#}`
- No whitespace allowed (enforced by inspection)

### Custom Validator Definition Format

`.jcvdefinitions.json` structure:
```json
{
  "validators": [
    {
      "id": "validator_name",
      "parameters": [
        {
          "description": "Parameter description",
          "required": true,
          "suggested_values": ["option1", "option2"]
        }
      ]
    }
  ]
}
```

Parsed using Jackson with snake_case naming strategy.

### Inspection Quick-Fix Pattern

When adding new inspections that create quick-fixes, follow the pattern in **JcvUnknownValidatorInspection**:
1. Detect problem in validator element
2. Register `ProblemDescriptor` with quick-fix
3. Quick-fix modifies `.jcvdefinitions.json` or applies code transformation
4. Cache automatically refreshes via file listener

## Testing

Tests are organized by feature area:
- **Parsing**: `language/parsing/JcvParsingTest.kt` - Tests parser/lexer with `.txt` test data
- **Completion**: `language/completion/JcvCodeInsight*Test.kt` - Tests autocomplete functionality
- **Inspections**: `language/inspection/JcvInspections*Test.kt` - Tests code inspections/warnings
- **Intentions**: `language/intention/JcvReplacementSuggestions*Test.kt` - Tests replacement suggestions

All tests extend **JcvBasePlatformTestCase** (`language/JcvBasePlatformTestCase.kt`) which:
- Sets up IntelliJ test fixtures
- Provides helper methods for test setup
- Configures the test environment with mock libraries

Test data structure:
```
src/test/
├── kotlin/         # Test code
└── testData/       # Test data files
    └── parsing/    # Parser test cases (.txt files)
```

When writing tests, use the helper utility methods in `language/Utils.kt` for common operations.

## Platform Version

- Target platform: IntelliJ IDEA Community Edition (IC)
- Current version: 2024.3.6
- Minimum build: 243
- JVM toolchain: Java 21
- Requires bundled plugin: `com.intellij.modules.json`

## Key Files to Know

- `build.gradle.kts`: Build configuration with IntelliJ Platform Gradle Plugin
- `gradle.properties`: Plugin metadata and platform version
- `src/main/resources/META-INF/plugin.xml`: Plugin descriptor registering all extensions
- `src/main/kotlin/com/ekino/oss/jcv/idea/plugin/language/Jcv.bnf`: Parser grammar
- `src/main/kotlin/com/ekino/oss/jcv/idea/plugin/language/Jcv.flex`: Lexer specification
- `src/main/resources/messages/JcvBundle.properties`: I18n message bundle

## Validator Origins

The **JcvValidatorOrigin** sealed interface has two implementations:

- **LibraryOrigin**: Validators from JCV libraries (Core, DB)
  - Has `artifactId`, `groupId`, `dependencyPattern()`
  - Implements `DocumentationUriProvider` for external docs

- **ProjectOrigin**: Custom validators from `.jcvdefinitions.json`
  - References the virtual file containing definitions
  - No external documentation links

## Quick Reference for Common Tasks

### Adding a New Built-in Validator

1. Add the validator definition to `JcvCoreValidatorDefinition.kt` or `JcvDbValidatorDefinition.kt`:
```kotlin
JcvValidatorDefinitionImpl(
    id = "my_validator",
    origin = JCV_CORE_ORIGIN,  // or JCV_DB_*_ORIGIN
    parameters = listOf(
        ParameterDefinitionImpl(description = "Parameter description", required = true)
    )
)
```

2. Update tests in `JcvInspectionsTest.kt` and `JcvCodeInsightTest.kt`

### Adding a New Inspection

1. Create a new class extending `JcvInspectionBase` in `language/inspection/`
2. Override `buildVisitor()` to detect the issue
3. Register in `plugin.xml` under `<localInspection>`
4. Add message key to `JcvBundle.properties`
5. Write tests extending `JcvBasePlatformTestCase`

### Modifying the Parser Grammar

1. Edit `Jcv.bnf` file
2. Regenerate parser (Ctrl+Shift+G in IntelliJ)
3. Update `JcvPsiImplUtil.kt` if new methods are needed
4. Update tests in `JcvParsingTest.kt`

## Debugging Tips

### Common Issues

1. **Validator not recognized**: Check if the validator is properly registered in:
   - `JcvCoreValidatorDefinition` or `JcvDbValidatorDefinition` for built-in validators
   - `.jcvdefinitions.json` for custom validators
   - Ensure the required library is in the module dependencies

2. **Language injection not working**: Verify that:
   - The JSON string starts with `{#` pattern
   - The `JcvLanguageInjector` is properly registered in `plugin.xml`
   - JSON language support is enabled (requires `com.intellij.modules.json` dependency)

3. **Cache not updating**: Force refresh by:
   - Modifying and saving `.jcvdefinitions.json`
   - Rebuilding the project
   - Checking `ProjectChangesListener` is registered

### Running in Debug Mode

To debug the plugin:
1. Set breakpoints in your code
2. Run `./gradlew runIde` with debug configuration
3. A sandboxed IDE instance will open with the plugin loaded
4. Perform actions in the sandbox IDE to trigger your breakpoints

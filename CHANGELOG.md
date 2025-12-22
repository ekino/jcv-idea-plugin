# JCV Changelog

## [Unreleased]

### Changed

- **IntelliJ Platform Plugin Template** – Upgraded IntelliJ Platform Plugin Template to [`v2.4.0`](https://github.com/JetBrains/intellij-platform-plugin-template/releases/tag/v2.4.0).
- **Dependencies** – Updated multiple dependencies:
  - IntelliJ Platform Gradle Plugin: `2.7.0` → `2.10.5`
  - Gradle Changelog Plugin: `2.3.0` → `2.5.0`
  - Kotlin: `2.2.0` → `2.2.21`
  - Kover: `0.9.1` → `0.9.3`
  - Qodana: `2025.1.1` → `2025.2.2`
- **Build tooling** – Upgraded Gradle Wrapper to `9.2.1` (major version upgrade from 8.x).
- **GitHub Actions** – Updated all action versions to v5 (from v4):
  - `actions/checkout@v5`
  - `actions/setup-java@v5`
  - `actions/upload-artifact@v5`
  - `gradle/actions/setup-gradle@v5`
  - Updated Qodana action to `v2025.2.2`

### Fixed

- **Deprecated API** – Replaced deprecated `ActionUtil.performActionDumbAwareWithCallbacks` with `ActionUtil.invokeAction` in `JcvReplacementIntention`.

### Known Issues

- Two deprecation warnings remain (to be addressed in future releases):
  - Jackson's `PropertyNamingStrategy.SNAKE_CASE` is deprecated
  - `ActionUtil.invokeAction` overload used is deprecated (newer signature recommended)

## [3.0.4] - 2025-07-29

### Changed

- **IntelliJ Platform Plugin Template** – Upgraded IntelliJ Platform Plugin Template to [`v2.2.0`](https://github.com/JetBrains/intellij-platform-plugin-template/releases/tag/v2.2.0).<br>
  ⚠️ `master` branch has to be renamed to `main` to follow the same GitHub actions rules and help futur upgrades.

### Fixed

- **Intention definition** - Add missing `JSON` language to JCV replacement intention configuration.
- **API usage** - Remove "override-only" api usage and deprecated ones.
- **File type** - Replaced object by class

## [3.0.3] - 2025-07-24

### Changed

- **Build system** – Migrated the Gradle build to the latest official IntelliJ Platform Plugin Template configuration.
- **Build tooling** – Upgraded Gradle Wrapper to `8.14.3`.

### Removed

- Deleted a “not so important test” that failed due to mismatched PsiFile/Document handling.

### Fixed

- **IntelliJ API** – Replaced deprecated `PsiElement#getStartOffset / #getEndOffset` helpers with the recommended API in several inspections (`JcvUnexpectedParameterInspection`, `JcvWhiteSpacesInspection`, `JcvEmptyParameterInspection`).
- **Java 21 compatibility** – Updated test data: `DateTimeFormatter.ISO_INSTANT` now parses `2011-12-03T10:15:30+01:00` correctly on Java 21 (was failing on Java 11).
- **IntelliJ 2022.3+** – Added explicit overrides for `AnAction#getActionUpdateThread` on existing actions to comply with the new threading contract.
- **Action system** – Avoided direct use of template presentations when constructing `AnActionEvent` to prevent Template presentations must not be used directly runtime errors.
- **File handling** – Replaced deprecated `PathKt.exists(Path)` calls with `File#exists` to restore binary-compatibility.
- **Action group pop-up** – Stopped overriding the final `ActionGroup.isPopup();` migrated to the new pop-up API to avoid potential `VerifyError`.

## [3.0.2]

### Fixed

- Replacement suggestions popup appears now on IntelliJ Platform 2021.2

## [3.0.1]

### Fixed

- Remove "untilBuild" IntelliJ version from plugin description to extends compatibility with recent versions

## [3.0.0]

### Changed

- Re-implemented as a true injectable language

### Added

- Validator documentation
- Settings (Color Scheme, Inspections, Intentions)
- Code Insight only for detect validators in the project classpath #16
- Support of custom validator definitions

## [2.0.1]

### Fixed

- Add missing `number_type` validator to JCV Core definitions #17

## [2.0.0]

### Added

- Syntax highlighting
- Validator auto-completion
- Replacement suggestions

### Removed

- No more JCV live templates

## [1.0.0]

### Added

- Initial release

[Unreleased]: https://github.com/ekino/jcv-idea-plugin/compare/v3.0.4...HEAD
[3.0.4]: https://github.com/ekino/jcv-idea-plugin/compare/v3.0.3...v3.0.4
[3.0.3]: https://github.com/ekino/jcv-idea-plugin/compare/v3.0.2...v3.0.3
[3.0.2]: https://github.com/ekino/jcv-idea-plugin/compare/v3.0.1...v3.0.2
[3.0.1]: https://github.com/ekino/jcv-idea-plugin/compare/v3.0.0...v3.0.1
[3.0.0]: https://github.com/ekino/jcv-idea-plugin/compare/2.0.1...v3.0.0
[2.0.1]: https://github.com/ekino/jcv-idea-plugin/compare/2.0.0...2.0.1
[2.0.0]: https://github.com/ekino/jcv-idea-plugin/compare/1.0.0...1.0.0
[1.0.0]: https://github.com/ekino/jcv-idea-plugin/compare/a70d7c59e66af964b488b484250e9ade19bfdc31...1.0.0

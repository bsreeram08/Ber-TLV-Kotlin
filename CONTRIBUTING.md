# Contributing to TLV Library

Thank you for your interest in contributing to the TLV Library! This document provides guidelines for contributing to this project.

## Code of Conduct

By participating in this project, you agree to maintain a respectful and inclusive environment for all contributors.

## Getting Started

1. Fork the repository
2. Clone your fork: `git clone https://github.com/YOUR_USERNAME/tlv-library-kotlin.git`
3. Create a new branch: `git checkout -b feature/your-feature-name`

## Development Setup

### Prerequisites
- Java 21 or higher
- Kotlin 1.9.0 or higher

### Building the Project
```bash
./gradlew build
```

### Running Tests
```bash
./gradlew test
```

## Making Changes

### Code Style
- Follow Kotlin coding conventions
- Use meaningful variable and function names
- Add KDoc comments for public APIs
- Keep functions focused and concise

### Testing
- Write unit tests for new functionality
- Ensure all existing tests pass
- Aim for high test coverage

### Commit Messages
Use clear and descriptive commit messages:
```
feat: add support for multi-byte tag parsing
fix: correct length calculation in parser
docs: update README with new examples
test: add tests for edge cases in BerTlvBuilder
```

## Submitting Changes

1. Ensure your code follows the project's style guidelines
2. Add or update tests as needed
3. Update documentation if you're changing APIs
4. Run the full test suite: `./gradlew test`
5. Commit your changes with a clear message
6. Push to your fork: `git push origin feature/your-feature-name`
7. Create a Pull Request

## Pull Request Guidelines

- Provide a clear description of the changes
- Reference any related issues
- Include screenshots or examples if applicable
- Be responsive to feedback and requests for changes

## Reporting Issues

When reporting bugs or suggesting enhancements:
- Use a clear and descriptive title
- Provide steps to reproduce the issue
- Include relevant code samples
- Specify your environment (Java version, OS, etc.)

## Questions?

If you have questions about contributing, feel free to:
- Open an issue for discussion
- Contact the maintainers

Thank you for contributing!
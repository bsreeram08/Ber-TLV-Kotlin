# TLV Library for Kotlin/Java

A Kotlin library for parsing and building BER TLV (Basic Encoding Rules Tag-Length-Value) data structures, commonly used in payment processing and EMV applications.

![Build Status](https://github.com/bsreeram08/Ber-TLV-Kotlin/workflows/CI/badge.svg)
![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)
![Kotlin](https://img.shields.io/badge/kotlin-1.9.0-blue.svg)
![Java](https://img.shields.io/badge/java-21-orange.svg)

## Features

- Parse BER TLV data from byte arrays
- Build TLV structures programmatically
- Support for both primitive and constructed TLV elements
- Comprehensive search and navigation capabilities
- Hexadecimal utility functions
- Extensive logging support for debugging

## Installation
NOTE : Only available as a jar

### Gradle (Kotlin DSL) (Not uploaded yet)
```kotlin
dependencies {
    implementation("com.sreeram.tlv:tlv-library:1.0.0")
}
```

### Gradle (Groovy) (Not uploaded yet)
```groovy
dependencies {
    implementation 'com.sreeram.tlv:tlv-library:1.0.0'
}
```

### Maven (Not uploaded yet)
```xml
<dependency>
    <groupId>com.sreeram.tlv</groupId>
    <artifactId>tlv-library</artifactId>
    <version>1.0.0</version>
</dependency>
```

### Direct JAR
Download the latest JAR from [Releases](../../releases) and add it to your project's classpath.

## Quick Start

### Parsing TLV Data

```kotlin
import com.sreeram.tlv.*

// Parse from hex string
val hexData = "500456495341"
val bytes = HexUtil.parseHex(hexData)

val parser = BerTlvParser()
val tlvs = parser.parse(bytes)

// Find specific tags
val applicationLabel = tlvs.find(BerTag(0x50))
println("Application Label: ${applicationLabel?.textValue}") // "VISA"
```

### Building TLV Data

```kotlin
import com.sreeram.tlv.*

val builder = BerTlvBuilder()
    .addText(BerTag(0x50), "VISA")
    .addHex(BerTag(0x9F, 0x02), "000000001000")
    .addAmount(BerTag(0x9F, 0x02), BigDecimal("10.00"))

val tlv = builder.buildTlv()
val bytes = builder.buildArray()
```

### Working with Tags

```kotlin
// Create tags
val tag1 = BerTag(0x50)                    // Single byte tag
val tag2 = BerTag(0x9F, 0x02)              // Multi-byte tag
val tag3 = BerTag("9F02")                  // From hex string

// Search operations
val found = tlvs.find(BerTag(0x50))         // Find first occurrence
val allFound = tlvs.findAll(BerTag(0x50))   // Find all occurrences

// Navigate constructed TLVs
if (tlv.isConstructed) {
    val children = tlv.values
    children.forEach { child ->
        println("Child tag: ${child.tag}")
    }
}
```

## API Reference

### Core Classes

#### `BerTlv`
Represents a single TLV element.

**Properties:**
- `tag: BerTag` - The tag
- `isPrimitive: Boolean` - Whether this is a primitive element
- `isConstructed: Boolean` - Whether this contains other TLV elements
- `hexValue: String` - Value as hex string (primitive only)
- `textValue: String` - Value as ASCII text (primitive only)
- `bytesValue: ByteArray` - Raw byte value (primitive only)
- `intValue: Int` - Value as integer (primitive only)
- `values: List<BerTlv>` - Child elements (constructed only)

**Methods:**
- `find(tag: BerTag): BerTlv?` - Find first child with tag
- `findAll(tag: BerTag): List<BerTlv>` - Find all children with tag

#### `BerTlvs`
Container for multiple TLV elements.

**Properties:**
- `list: List<BerTlv>` - All TLV elements

**Methods:**
- `find(tag: BerTag): BerTlv?` - Find first element with tag
- `findAll(tag: BerTag): List<BerTlv>` - Find all elements with tag

#### `BerTlvParser`
Parses byte arrays into TLV structures.

**Methods:**
- `parse(buf: ByteArray): BerTlvs` - Parse multiple TLVs
- `parseConstructed(buf: ByteArray): BerTlv` - Parse single constructed TLV

#### `BerTlvBuilder`
Builds TLV structures programmatically.

**Methods:**
- `addText(tag: BerTag, text: String): BerTlvBuilder`
- `addHex(tag: BerTag, hex: String): BerTlvBuilder`
- `addBytes(tag: BerTag, bytes: ByteArray): BerTlvBuilder`
- `addAmount(tag: BerTag, amount: BigDecimal): BerTlvBuilder`
- `addDate(tag: BerTag, date: Date): BerTlvBuilder`
- `addTime(tag: BerTag, date: Date): BerTlvBuilder`
- `buildTlv(): BerTlv`
- `buildArray(): ByteArray`

#### `HexUtil`
Utility functions for hexadecimal operations.

**Methods:**
- `parseHex(hex: String): ByteArray` - Convert hex string to bytes
- `toHexString(bytes: ByteArray): String` - Convert bytes to hex string

## Requirements

- Kotlin 1.9.0 or higher
- Java 21 or higher

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

This project is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details.

## Acknowledgments

Based on the original Java implementation from PaynetEasy's TLV library, converted to Kotlin with improvements and modernizations.
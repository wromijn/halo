# simple-hal

## What?

A Java library to produce API output in the [HAL](http://stateless.co/hal_specification.html) format. It lets you create representation objects that serialize with Jackson.

## Why?

#### No magic
An API is a contract and it should be easy to verify and maintain this contact. I wanted a library with as little magic as possible. No automatic behaviour with limited and obscure overrides.

#### No duplicate code
Many libraries make it hard or impossible to re-use parts of the output generation code. I want to be able to define both a ProductSummary and a Product representation and not duplicate the code for the common fields.

## Installation

#### Maven

```xml
<repositories>
	<repository>
	    <id>jitpack.io</id>
	    <url>https://jitpack.io</url>
	</repository>
</repositories>

...

<dependency>
	<groupId>com.github.wromijn</groupId>
	<artifactId>simple-hal</artifactId>
	<version>0.0.3-RELEASE</version>
</dependency>
```

#### Gradle
```gradle
allprojects {
	repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}

...

dependencies {
        implementation 'com.github.wromijn:simple-hal:0.0.3-RELEASE'
}
```

## Usage

#### Creating HalRepresentation object

```java
HalRepresentation representation = new HalRepresentation();
```

Use Jackson to serialize the representation.

#### Adding properties
```java
representation.addString("property_name", "String value")
              .addInteger("integer_field", 1)
	      .addNumber("number_field", 1F)
	      .addBoolean("boolean_field", true);
```

Serializes to

```json
{
	"property_name": "String value",
	"integer_field": 1,
	"number_field": 1.0,
	"boolean_field": true
}
```

Like JSON-Schema, simple-hal makes a clear distinction between numeric fields that can contain floating point values and numeric fields that can not. To emphasize this distinction, "integer" type fields are output without a fractional part and "number" type fields are always output as floating point values (1 is output as 1.0). This way, an API user can tell even without referring to a schema that a floating point data type is needed.

## Adding links

```java
// simple link
representation.addLink("self", "http://api.example.com/item/1")

// link with properties
representation.addLink("self", new Link("http://api.example.com/item/1").setTitle("title"))

// or set the href with a method call
representation.addLink("self", new Link().setHref("http://api.example.com/item/1").setTitle("title"))

// list of links
representation.addLinkList("curies", Arrays.asList(new Link("/link1").setName("ns")))
```

## Embedding objects

Embedding is always done by embedding another HalRepresentation object. For example:

```java
private HalRepresentation createChildRepresentation(Child c) {
	...
}

// simple embedding
representation.addEmbedded("hal:child", createChildRepresentation(child))

// embedding a stream of objects - you could embed a list instead, but this is quicker
representation.addEmbeddedList("hal:children", children.stream().map(this::createChildRepresentation))
```

## Inlining objects

Inlining is like embedding, but the object is added to the properties instead of the `"_embedded"` section. Inline objects can only have properties: `_links` and `_embedded` sections are removed when they exist.

#### Embedding
```json
{
	"foo": "bar",
	"_embedded": {
		"hal:child": {
			"key": "value"
		}
	}
}
```

#### Inlining
```json
{
	"foo": "bar",
	"child": {
		"key": "value"
	}
}
```

#### Example code:
```java
private HalRepresentation createChildRepresentation(Child c) {
	...
}

// simple inlining
representation.addInline("child", createChildRepresentation(child))

// inlining a stream of objects - you could inline a list instead, but this is quicker
representation.addInlineList("children", children.stream().map(this::createChildRepresentation))
```

## Extras

#### Adding whole objects as a property
Provided that the object is serializable with Jackson, you can add a whole object as a property:
```java
addObject(String name, Object o);
```
This is a nice option to have if you want to add an Array or schemaless data as a property.

#### Creating a representation from an object
If you're working with legacy code or just want to add links to an existing model, you can upgrade it to a HalRepresentation.
This will serialize your object to a Jackson JsonNode and copy the properties to a new HalRepresentation.

```java
HalRepresentation.fromObject(Object o);
HalRepresentation.fromObject(Object o, ObjectMapper om);
```

[![Build Status](https://travis-ci.org/wromijn/simple-hal.svg?branch=master)](https://travis-ci.org/wromijn/simple-hal)
[![codecov](https://codecov.io/gh/wromijn/simple-hal/branch/master/graph/badge.svg)](https://codecov.io/gh/wromijn/simple-hal)
[![Maintainability](https://api.codeclimate.com/v1/badges/e384ffe146c10612337e/maintainability)](https://codeclimate.com/github/wromijn/simple-hal/maintainability)
[![](https://jitpack.io/v/wromijn/simple-hal.svg)](https://jitpack.io/#wromijn/simple-hal)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

# Terminal Text Buffer

A Java implementation of a terminal text buffer. This data structure simulates the internal mechanics of a terminal emulator, managing a grid of character cells, text styling, cursor movements, and scrollback history.

## Features

* **Grid-based Text Management**: Splits the buffer into two logical components:
  * **Screen**: The actively editable area constrained to specific dimensions (e.g., 80x24).
  * **Scrollback**: A preserved, read-only history of lines that have scrolled off the top of the screen.
* **Automatic Wrapping & Reflow**: Seamlessly overwrites content at the cursor, automatically wraps text to the next line when reaching the right edge, and pushes overflow to the history buffer. Supports dynamic screen resizing with content reflow.
  * Content knows whether it was wrapped or not so it can be 'unwrapped' in case of another resizing.
* **Wider Characters Support**: Fully handles Unicode characters occupying 2 grid cells (e.g., Emojis, CJK ideographs) ensuring proper cell alignment and cursor jumping.
* **Memory-Optimized Data Model**: Avoids heavy per-cell objects by utilizing primitive parallel arrays for the grid, minimizing memory footprint and Garbage Collector overhead. The `TextAttributes` record acts purely as a global brush state and an on-the-fly DTO.

## Architecture & Memory Model

To ensure high performance and low memory footprint, there is no individual `Cell` object instantiated for every coordinate.

Instead, the buffer is built using the pattern. Each `Row` stores its data in parallel primitive arrays (`int[]` for Unicode characters, `byte[]` for 16 standard ANSI colors, and `boolean[]` for styles like bold, italic, underline). This entirely eliminates the overhead of Java object headers and references per cell, making the buffer highly cache-friendly.

Visual styling is encapsulated in the `TextAttributes` Java record. It serves only as the global "brush" state in the `TerminalBuffer` and as a lightweight Data Transfer Object (DTO) generated on-the-fly when querying cell properties, maintaining immutability without polluting the core grid memory.

## Build & Test

This project is built using Gradle. To compile the code and run the comprehensive suite of unit tests, use the provided Gradle wrapper from your terminal:

```bash
# Run all unit tests
./gradlew test

# Build the entire project
./gradlew build
```

## API Usage

The main interaction point is the `TerminalBuffer` class. Below are the key operations supported by the buffer.

### Initialising Buffer

```java
// Initialize an 79x24 terminal with a 1000-line scrollback history
TerminalBuffer buffer = new TerminalBuffer(79, 24, 1000);
TerminalBuffer defaultBuffer = new TerminalBuffer(); // default values are 80, 24, 1000
```

### Cursor Movement

The buffer maintains a cursor position that dictates where the next characters will be written. The cursor is automatically clamped to the screen boundaries, meaning it will never crash by moving out of bounds.

```java
// Set cursor to a specific row and column (0-indexed)
buffer.setCursorPosition(5, 10); 

// Move cursor by n cells relative to its current position
buffer.moveCursorRight(2);
buffer.moveCursorLeft(1);
buffer.moveCursorDown(3);
buffer.moveCursorUp(1);

// Retrieve current cursor position
Cursor.Position pos = buffer.getCursorPosition();
System.out.println("Cursor is at Row: " + pos.row() + ", Col: " + pos.col());
```

### Editing

Operations that modify the buffer take the current cursor position and text attributes into account.

```java
// Set text attributes for subsequent writes (e.g., foreground, background, bold, italic, underline)
TextAttributes attrs = new TextAttributes((byte)1, (byte)2, true, false, false);
buffer.setAttributes(attrs);

// Resizing buffer with all its content
buffer.resize(72,28); // buffer will rewrite all its content to new screen with given dimensions

// Insert text (automatically handles wrapping and wide characters)
buffer.insertText("Hello, Terminal! 🚀");

// Write a single character (using its Unicode codepoint)
buffer.writeCharacter('A');

// Fill the current line with a specific character
buffer.fillLine('-');

// Insert an empty line at the bottom of the screen (pushes top row to scrollback)
buffer.insertEmptyLine();

// Clear operations
buffer.clearScreen();
buffer.clearScreenAndScrollback();
```

### Content Access
The buffer allows extracting individual characters, entire lines, or the full state of the screen and scrollback as strings.
```java
// Get the entire visible screen content as a single string
String screenText = buffer.getScreenContent();

// Get the entire scrollback history + screen content
String fullText = buffer.getScreenAndScrollbackContent();

// Accessing specific lines
String topScreenLine = buffer.getLineContentScreen(0);
String oldestScrollbackLine = buffer.getLineContentScrollback(0);
String absoluteLine = buffer.getLineAbsolute(5); // Treats scrollback + screen as one continuous array

// Accessing individual characters (codePoints)
int screenChar = buffer.getCharacterAtScreen(0, 0);
int scrollbackChar = buffer.getCharacterAtScrollback(0, 0);
int absolute = buffer.getCharacterAbsolute(0, 0); // Treats scrollback + screen as one continuous array

// Accessing text attributes at a specific location
TextAttributes cellAttrs = buffer.getScreenAttributesAt(0, 0);
```

### Acknowledgements
* `WcWidth` implementation: The logic used to calculate the physical grid width of Unicode characters (identifying 0, 1, or 2-cell wide characters) is a Java port of the excellent C library [`wcwidth9`](https://github.com/joshuarubin/wcwidth9.git) by Joshua Rubin.
* **Project context**: This project was developed as a test assignment for JetBrains.
# Assignment 4: Concurrency

**Course:** Advanced Software Paradigms, CSCI 6221  
**Semester:** Fall 2026  
**University:** ADA University  
**Deadline:** November 3, 2026  
**Assignment:** A4. Concurrency

## Overview

This repository contains my solution and technical notes for **Assignment 4: Concurrency** in the Advanced Software Paradigms course.

The Java application loads an image, divides it into square regions, calculates the average red, green, and blue (RGB) values for each region, and replaces each region with its average color.

The image is displayed during processing so that the changes can be observed progressively.

The application supports two processing modes:

* **Single threaded mode (`S`)** processes the square regions sequentially.
* **Multithreaded mode (`M`)** divides the image into bands of square rows and processes those bands using multiple worker threads.

The processed image is saved as `result.jpg` in the current working directory.

## Features

* Loads an image from a path provided through command line arguments.
* Validates the number and values of command line arguments.
* Supports single threaded (`S`) and multithreaded (`M`) processing.
* Calculates the average RGB color of each square region.
* Handles partial square regions at the image edges.
* Displays processing progress in a Swing window.
* Scales the displayed preview to fit the available screen space when necessary.
* Keeps the original image dimensions for processing and output.
* Saves the processed image as `result.jpg`.
* Uses Java standard library APIs for image processing, concurrency, and the graphical interface.

## Repository Structure

The project contains five Java source files inside the `src` directory. All five files use the default package.

```text
A4.Concurrency/
│
├── src/
│   ├── Main.java
│   ├── ImageAverager.java
│   ├── ImageViewer.java
│   ├── ProgramArguments.java
│   └── ProcessingMode.java
│
├── .gitignore
└── README.md
```

The `recources` directory is intended for local input images and is excluded from Git tracking. You can use your own image file when running the application.

### Source File Responsibilities

| File | Responsibility |
|---|---|
| `Main.java` | Entry point. Parses arguments, loads the image, creates the viewer and averager, starts the selected processing mode, and saves the result. |
| `ProgramArguments.java` | Parses and validates the image path, square size, and processing mode. |
| `ProcessingMode.java` | Defines the supported processing modes, `S` and `M`. |
| `ImageViewer.java` | Displays the image in a Swing window and scales only the displayed preview when necessary. |
| `ImageAverager.java` | Calculates average RGB values, updates square regions, and manages single threaded or multithreaded processing. |

## Programming Languages and Tools

* **Language:** Java
* **JDK:** Developed and tested with Zulu OpenJDK 26.0.1.
* **IDE:** Visual Studio Code.
* **Version control:** Git and GitHub.
* **Graphical interface:** Java Swing and AWT.
* **Image input and output:** `javax.imageio.ImageIO`.
* **Concurrency:** Java threads, `CountDownLatch`, and `AtomicReference`.

### Dependencies

There are no third party dependencies. No external libraries are required.

The application uses classes included in the Java standard library, including:

* `java.awt` and `java.awt.image`
* `javax.swing`
* `javax.imageio`
* `java.io`
* `java.util`
* `java.util.concurrent`

A JDK is required to compile the source files. A graphical desktop environment is required to display the Swing image window.

## How to Run the Application

### 1. Requirements

Before running the application, make sure that:

* Java JDK is installed.
* The Java compiler (`javac`) and Java launcher (`java`) are available in the terminal.
* You have an image file supported by Java ImageIO, such as JPG or PNG.
* You are using a graphical desktop environment.

You can check your Java installation with:

```bash
java -version
javac -version
```

### 2. Open the Project Directory

Open a terminal in the project root directory, the directory containing `src` and `README.md`.

### 3. Add an Input Image

Place an image in the local `recources` directory, or use an image path elsewhere on your computer.

For example:

```text
recources/large.jpg
```

The image files are excluded from Git tracking. Each user must provide an image locally to run the application.

### 4. Compile the Source Files

From the project root directory, create an output directory:

```bash
mkdir out
```

Compile all Java source files:

```bash
javac -d out src/Main.java src/ProcessingMode.java src/ProgramArguments.java src/ImageViewer.java src/ImageAverager.java
```

If compilation succeeds, the compiled `.class` files will be placed in the `out` directory.

### 5. Run in Single Threaded Mode

```bash
java -cp out Main recources/large.jpg 20 S
```

In this mode, one thread processes the square regions sequentially.

### 6. Run in Multithreaded Mode

```bash
java -cp out Main recources/large.jpg 20 M
```

In this mode, the image is divided into bands of square rows. Worker threads process separate bands concurrently.

The number of worker threads is based on the available processors and the number of square rows.

### 7. Command Line Arguments

The application expects exactly three arguments:

```text
java Main <image> <squareSize> <S/M>
```

| Argument | Description | Example |
|---|---|---|
| `image` | Path to the input image | `recources/large.jpg` |
| `squareSize` | Positive integer defining the width and height of each square region | `20` |
| `S/M` | Processing mode. `S` means single threaded and `M` means multithreaded. | `M` |

The processing mode is case insensitive. Both `s` and `S` select single threaded processing.

### 8. Output

After successful processing, the application writes:

```text
result.jpg
```

The output is saved in the current working directory, which should be the project root when following the instructions above.

If a file with that name already exists, it is overwritten.

## Processing Details

### Average RGB Color

For each square region, the program reads the red, green, and blue components of every pixel.

It calculates the arithmetic mean of each component and fills the region with the resulting RGB color.

The rightmost and bottommost regions may be smaller than the requested square size when the image dimensions are not exact multiples of that size. These partial regions are processed using their actual dimensions.

### Single Threaded Mode

In `S` mode, one thread processes the square regions sequentially.

The preview is refreshed as processing progresses.

### Multithreaded Mode

In `M` mode, the image is divided into bands of square rows. Worker threads process separate bands.

A `CountDownLatch` is used to wait for worker completion. An `AtomicReference` is used to record worker errors so that they can be checked by the main processing flow.

### Preview Scaling

When an image is larger than the available display area, `ImageViewer` scales the displayed preview to fit the screen.

The original `BufferedImage` dimensions are retained for image processing and output.

## Testing and Validation

Testing has been performed manually from the terminal in Visual Studio Code on Windows.

### Tests Completed

| Test | Command or setup | Expected behavior | Result |
|---|---|---|---|
| Single threaded processing | `java -cp out Main recources/large.jpg 20 S` | Image is processed and `result.jpg` is saved. | Passed |
| Multithreaded processing | `java -cp out Main recources/large.jpg 20 M` | Image is processed using multiple worker threads and `result.jpg` is saved. | Passed |
| Different square sizes in multithreaded mode | Square sizes `50` and `20` | Both sizes are processed successfully. | Passed |
| Square size larger than the image | `java -cp out Main recources/large.jpg 10000 S` | The entire image becomes one average color region. | Passed |
| Missing arguments | Run without arguments | Usage information is printed and processing does not start. | Passed |
| Non integer square size | `java -cp out Main recources/large.jpg abc S` | An integer validation message is printed. | Passed |
| Zero square size | `java -cp out Main recources/large.jpg 0 S` | A positive size validation message is printed. | Passed |
| Large image display | Run the application with a large image | The preview fits the screen while processing uses the original image dimensions. | Passed |

The tests above were performed manually. macOS testing has not yet been confirmed.


## Error Handling

The application checks for the following input problems:

* An incorrect number of command line arguments.
* A square size that is not an integer.
* A square size that is zero or negative.
* A processing mode other than `S` or `M`.
* A missing input file or a path that does not identify a file.
* A file that cannot be decoded as an image.

Worker errors in multithreaded mode are also recorded and checked after worker completion.

## Portability

The application uses Java standard library APIs and does not require third party dependencies.

It is intended to run on systems with a compatible JDK and a graphical desktop environment, including Windows and macOS.

The source code has been tested on Windows using Zulu OpenJDK 26.0.1. macOS execution has not yet been confirmed through testing.

## Development Process

The implementation was developed and checked incrementally.

1. **Initial implementation:** The core program was first written in a single `Main.java` file to establish image loading, square averaging, display, processing modes, and output behavior.
2. **Separation of responsibilities:** The code was organized into five Java files: `Main.java`, `ProcessingMode.java`, `ProgramArguments.java`, `ImageViewer.java`, and `ImageAverager.java`.
3. **Argument validation:** Checks were added for the number of arguments, integer square size, positive square size, and supported processing mode.
4. **Display handling:** The image viewer was adjusted so that Swing window creation runs on the Event Dispatch Thread (EDT). Preview scaling affects the displayed image rather than the source image.
5. **Concurrency improvements:** The multithreaded implementation was refined to handle worker completion, worker errors, interruption, and safer numeric calculations.
6. **Manual testing:** Both processing modes, selected argument errors, different square sizes, and a square size larger than the image were tested.

## AI Usage Disclaimer

This task was one of the most time consuming and challenging to handle on my own without AI assistance. Having never worked with threads before, implementing them for the first time was particularly demanding.

AI assistance was used during development to review code organization, discuss concurrency and Swing event handling paradigms, refine error handling, and design manual test cases.

## Author

**Nargiz Aghayeva**  
M.S. Student in Computer Science and Data Analytics  
ADA University | The George Washington University

**Course:** Advanced Software Paradigms, CSCI 6221  
**Semester:** Fall 2026  
**University:** ADA University  
**Deadline:** November 3, 2026  
**Assignment:** A4. Concurrency

## Overview

This repository contains my solution and technical notes for **Assignment 4: Concurrency** in the Advanced Software Paradigms course.

The Java application loads an image, divides it into square regions, calculates the average red, green, and blue (RGB) values for each region, and replaces the region with that average color. The image is displayed during processing so that the changes can be observed progressively.

The application supports two processing modes:

* **Single threaded mode (`S`)** processes the square regions sequentially.
* **Multithreaded mode (`M`)** divides the image into bands of square rows and processes those bands using multiple worker threads.

The processed image is saved as `result.jpg` in the current working directory.

## Features

* Loads an image from a path provided through command line arguments.
* Validates the number and values of command line arguments.
* Supports single threaded (`S`) and multithreaded (`M`) processing.
* Calculates the average RGB color of each square region.
* Handles square regions at the image edges when the image dimensions are not exact multiples of the square size.
* Displays processing progress in a Swing window.
* Scales the displayed preview to fit the available screen space when needed, without resizing the original image used for processing.
* Saves the processed image as `result.jpg`.
* Uses Java standard library APIs for image processing, concurrency, and the graphical interface.

## Repository Structure

All five Java source files are in the same directory and use the default package. No additional source folders or packages are required.

```text
A4/
├── Main.java
├── ProcessingMode.java
├── ProgramArguments.java
├── ImageViewer.java
├── ImageAverager.java
└── README.md
```

### Source File Responsibilities

| File | Responsibility |
| --- | --- |
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

There are **no third party dependencies** and no external libraries are required.

The application uses classes included in the Java standard library, including:

* `java.awt` and `java.awt.image`
* `javax.swing`
* `javax.imageio`
* `java.io`
* `java.util`
* `java.util.concurrent`

A JDK is required to compile the source files. A graphical desktop environment is required to display the Swing image window.

## How the Program Works

### 1. Command Line Arguments

The program expects exactly three arguments:

```text
java Main <image> <squareSize> <S/M>
```

| Argument | Description | Example |
| --- | --- | --- |
| `image` | Path or filename of the input image | `sample.jpg` |
| `squareSize` | Positive integer defining the width and height of each square region | `20` |
| `S/M` | Processing mode. `S` means single threaded, and `M` means multithreaded. | `M` |

The mode is case insensitive. For example, `s` and `S` select single threaded processing.

### 2. Compile

Open a terminal in the directory containing the five `.java` files and run:

```bash
javac Main.java ProcessingMode.java ProgramArguments.java ImageViewer.java ImageAverager.java
```

If compilation succeeds, Java class files are created in the same directory.

### 3. Run in Single Threaded Mode

Place an input image such as `sample.jpg` in the current directory, or provide a valid path to an image.

```bash
java Main sample.jpg 20 S
```

### 4. Run in Multithreaded Mode

```bash
java Main sample.jpg 20 M
```

In multithreaded mode, the program chooses the worker thread count based on the available processors and the number of square rows. The number of worker threads is the smaller of these two values.

### 5. Output

After successful processing, the application writes:

```text
result.jpg
```

The output is saved in the current working directory. If a file with that name already exists, it is overwritten.

The terminal displays a completion message, the output filename, the selected mode, and the number of worker threads used.

## Processing Details

### Average RGB Color

For each square region, the program reads the red, green, and blue components of every pixel. It calculates the arithmetic mean of each component and fills the region with the resulting RGB color.

The rightmost and bottommost regions may be smaller than the requested square size when the image dimensions are not exact multiples of that size. These partial regions are processed using their actual dimensions.

### Single Threaded Mode

In `S` mode, one thread processes the square regions sequentially. The preview is refreshed as processing progresses.

### Multithreaded Mode

In `M` mode, the image is divided into bands of square rows. Worker threads process separate bands, and a `CountDownLatch` allows the main processing thread to wait for the workers to finish. Worker errors are recorded using an `AtomicReference` so that the main processing thread can detect a worker failure.

The preview uses a separate image copy. Access to the shared source image for copying and square processing is synchronized to reduce the risk of reading pixels while they are being changed.

### Preview Scaling

When an image is larger than the available display area, `ImageViewer` scales the displayed preview to fit the screen. The original `BufferedImage` dimensions are retained for averaging and output.

## Development Process

The implementation was developed and checked incrementally.

1. **Initial implementation:** The core program was first written together in a single `Main.java` file to establish the image loading, square averaging, display, processing modes, and output behavior.
2. **Separation of responsibilities:** The code was then organized into five Java files: `Main.java`, `ProcessingMode.java`, `ProgramArguments.java`, `ImageViewer.java`, and `ImageAverager.java`.
3. **Argument validation:** Checks were added for the number of arguments, integer square size, positive square size, and supported processing mode.
4. **Display handling:** The image viewer was adjusted so that Swing window creation runs on the Event Dispatch Thread (EDT). Preview scaling affects the displayed image rather than the source image.
5. **Preview safety:** A separate preview image copy and synchronization around shared image access were added for multithreaded processing.
6. **Worker error handling:** Worker thread errors are recorded and checked after the workers finish.
7. **Manual testing:** Both processing modes, selected argument errors, and a square size larger than the image were tested.

These steps describe the development work performed so far. The test list below distinguishes confirmed checks from additional checks that can still be performed.

## Testing and Validation

Testing has been performed manually from the terminal in Visual Studio Code on Windows.

### Tests Completed

| Test | Command or setup | Expected behavior | Result |
| --- | --- | --- | --- |
| Single threaded processing | `java Main sample.jpg 20 S` | Image is processed, progress is displayed, and `result.jpg` is saved. | Passed |
| Multithreaded processing | `java Main sample.jpg 20 M` | Image is processed using multiple worker threads, and `result.jpg` is saved. | Passed |
| Different square sizes in multithreaded mode | `java Main sample.jpg 50 M` and `java Main sample.jpg 20 M` | Both sizes are processed successfully. | Passed |
| Square size larger than the image | `java Main sample.jpg 10000 S` | The entire image becomes one average color region. | Passed |
| Missing arguments | `java Main` | Usage information is printed, and processing does not start. | Passed |
| Non integer square size | `java Main sample.jpg abc S` | An integer validation message is printed. | Passed |
| Zero square size | `java Main sample.jpg 0 S` | A positive size validation message is printed. | Passed |
| Large image display | A large image was run through the application. | The displayed preview fits the screen while processing and output use the original image dimensions. | Passed |


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

The application uses Java standard library APIs and does not require third party dependencies. It is intended to run on systems with a compatible JDK and a graphical desktop environment, including Windows and macOS.

The source code has been tested on Windows using Zulu OpenJDK 26.0.1. macOS execution has not yet been confirmed through testing.


## AI Usage Disclaimer

This task was one of the most time-consuming and challenging to handle on my own without AI assistance. Having never worked with threads before, implementing them for the first time was particularly demanding. AI assistance was used during development to review code organization, discuss concurrency and Swing event-handling paradigms, refine error handling, and design manual test cases. 

## Author

**Nargiz Aghayeva**  
M.S. Student in Computer Science and Data Analytics  
ADA University | The George Washington University
"""

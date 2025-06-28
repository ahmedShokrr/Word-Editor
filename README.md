# Word Editor - Operating Systems Course Project

A Java-based word editor application demonstrating advanced multithreading concepts, spell checking, and auto-correction features. This project showcases producer-consumer patterns, thread synchronization, and concurrent programming for an Operating Systems course.

## 🚀 Features

### Core Functionality

- **Real-time Spell Checking**: Multi-threaded spell checking with visual highlighting
- **Auto-correction**: Intelligent word suggestion and correction algorithms
- **GUI Interface**: Modern Swing-based graphical user interface
- **Console Mode**: Command-line interface for demonstration purposes
- **Dictionary Management**: Thread-safe dictionary loading and word lookup

### Technical Highlights

- **Multithreading**: Demonstrates producer-consumer pattern with thread pools
- **Concurrent Data Structures**: Uses `ConcurrentHashMap` and `BlockingQueue`
- **Thread Synchronization**: Implements proper locking mechanisms
- **Asynchronous Processing**: Utilizes `CompletableFuture` for non-blocking operations
- **Custom Data Structures**: Implements custom Stack and utility classes

## 📁 Project Structure

```
Word-Editor/
├── src/
│   ├── Main.java                           # Application entry point
│   ├── dictionary.txt                      # Word dictionary file
│   ├── core/                              # Core business logic
│   │   ├── AutoCorrector.java             # Auto-correction engine
│   │   ├── DictionaryManager.java         # Thread-safe dictionary management
│   │   ├── ProcessingResult.java          # Result wrapper class
│   │   ├── SpellChecker.java              # Multi-threaded spell checker
│   │   ├── SpellCheckResult.java          # Spell check results
│   │   └── WordProcessor.java             # Main processing engine
│   ├── ui/                                # User interface components
│   │   ├── SpellCheckResultDialog.java    # Results display dialog
│   │   └── WordEditorGUI.java             # Main GUI application
│   └── utils/                             # Utility classes
│       ├── Search.java                    # Search algorithms
│       ├── SortUtils.java                 # Sorting utilities
│       └── Stack.java                     # Custom stack implementation
├── .gitignore                             # Git ignore rules
├── README.md                              # This file
└── Word-Editor.iml                        # IntelliJ IDEA module file
```

## 🛠️ Prerequisites

- **Java Development Kit (JDK)**: Version 8 or higher
- **Operating System**: Windows, macOS, or Linux
- **IDE** (Optional): IntelliJ IDEA, Eclipse, or VS Code

## 🚦 Getting Started

### 1. Clone the Repository

```bash
git clone <repository-url>
cd Word-Editor
```

### 2. Compile the Project

```bash
# Create output directory
mkdir bin

# Compile all Java files
javac -d bin -cp src src/*.java src/core/*.java src/ui/*.java src/utils/*.java
```

### 3. Run the Application

#### GUI Mode (Default)

```bash
java -cp bin;src wordeditor.Main
```

#### Console Mode

```bash
java -cp bin;src wordeditor.Main --console
```

## 🎯 Usage

### GUI Mode

1. Launch the application in GUI mode
2. Type text in the main text area
3. Watch real-time spell checking with color highlighting:
   - **Red background**: Misspelled words
   - **Green background**: Correct words
4. Right-click on misspelled words for suggestions
5. Use the auto-correct feature for automatic fixes

### Console Mode

1. Launch with `--console` argument
2. Enter text when prompted
3. View processing results including:
   - Original text
   - Processed/corrected text
   - Spelling errors and suggestions
   - Processing time statistics
4. Type `exit` to quit

## 🧵 Multithreading Architecture

### Thread Pool Management

- **Fixed Thread Pool**: 4 worker threads for parallel processing
- **Producer-Consumer Pattern**: Asynchronous text processing pipeline
- **Thread-Safe Operations**: All shared data structures use concurrent collections

### Processing Pipeline

1. **Thread 1**: Text preprocessing and cleaning
2. **Thread 2**: Spell checking and error detection
3. **Thread 3**: Auto-correction and suggestion generation
4. **Thread 4**: Post-processing and result compilation

### Synchronization Mechanisms

- **Object Locks**: Synchronized dictionary loading
- **Volatile Variables**: Thread-safe status flags
- **Blocking Queues**: Producer-consumer communication
- **CompletableFuture**: Asynchronous result handling

## 📚 Dictionary Management

### Dictionary File

- **Location**: `src/dictionary.txt`
- **Format**: One word per line, plain text
- **Sample Words**: elephant, butterfly, telescope, adventure, etc.

### Features

- **Concurrent Loading**: Dictionary loads in background thread
- **Fallback System**: Built-in word list if dictionary file unavailable
- **Thread-Safe Access**: Multiple threads can query simultaneously
- **Dynamic Updates**: Support for adding custom words at runtime

## 🔧 Configuration

### Customizing the Dictionary

1. Edit `src/dictionary.txt`
2. Add one word per line
3. Restart the application to reload

### Adjusting Thread Pool Size

```java
// In WordProcessor.java
private final ExecutorService threadPool = Executors.newFixedThreadPool(4);
```

## 🧪 Testing

### Manual Testing

1. Test with correctly spelled text
2. Test with misspelled words
3. Test with mixed correct/incorrect text
4. Test console and GUI modes
5. Test with empty/invalid input

### Performance Testing

- Monitor thread utilization
- Measure processing time for large texts
- Test concurrent spell checking operations

## 🐛 Troubleshooting

### Common Issues

**Dictionary Not Loading**

- Ensure `dictionary.txt` is in `src/` directory
- Check file permissions
- Verify classpath includes `src/`

**GUI Not Appearing**

- Check Java Swing support
- Verify display environment variables
- Try console mode as alternative

**Compilation Errors**

- Ensure JDK version compatibility
- Check all source files are present
- Verify package structure

## 🏗️ Architecture Patterns

### Design Patterns Used

- **Producer-Consumer**: Text processing pipeline
- **Observer**: GUI event handling
- **Singleton**: Dictionary manager instance
- **Factory**: Thread pool creation
- **Strategy**: Different suggestion algorithms

### SOLID Principles

- **Single Responsibility**: Each class has one primary function
- **Open/Closed**: Extensible suggestion algorithms
- **Liskov Substitution**: Interface-based design
- **Interface Segregation**: Focused interfaces
- **Dependency Inversion**: Dependency injection patterns

## 📊 Performance Characteristics

### Time Complexity

- **Dictionary Lookup**: O(1) average case
- **Spell Checking**: O(n) where n = number of words
- **Suggestion Generation**: O(m) where m = dictionary size

### Space Complexity

- **Dictionary Storage**: O(d) where d = dictionary size
- **Processing Queue**: O(q) where q = queue capacity
- **GUI Components**: O(1) fixed overhead

## 🔮 Future Enhancements

### Potential Improvements

- Language detection and multi-language support
- Grammar checking functionality
- Machine learning-based suggestions
- Cloud-based dictionary synchronization
- Plugin architecture for extensions

### Scalability Considerations

- Database-backed dictionary storage
- Distributed processing capabilities
- Microservices architecture
- RESTful API endpoints

## 👥 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests if applicable
5. Submit a pull request

## 📄 License

This project is created for educational purposes as part of an Operating Systems course. Please refer to your institution's academic policies regarding code sharing and collaboration.

## 🙏 Acknowledgments

- Operating Systems course materials
- Java Concurrency documentation
- Swing GUI framework documentation
- Open-source spell checking algorithms

---

**Course**: Operating Systems  
**Semester**: 5  
**Institution**: AIU  
**Year**: 2025

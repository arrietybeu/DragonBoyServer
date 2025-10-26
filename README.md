# DragonBoy Server - Game Server Project

![Java Version](https://img.shields.io/badge/Java-21-orange)
![Gradle](https://img.shields.io/badge/Gradle-Latest-green)
![License](https://img.shields.io/badge/License-MIT-green)

## 📖 Giới Thiệu

DragonBoy Server là một **dự án học tập** về server game MMORPG được viết bằng Java. Đây là dự án cá nhân mà tôi đã phát triển trong khoảng 1 năm để **tích lũy và thực hành** các kiến thức về lập trình Java và game server. 

> ⚠️ **Lưu ý**: Dự án này chưa hoàn chỉnh và chưa đạt mức độ production-ready. Đây là một project học tập chứng minh tôi biết và đang học lập trình Java, chưa phải mức độ chuyên nghiệp.

### Mục Đích Dự Án

- **Học tập**: Thực hành các kiến thức về Java cơ bản và nâng cao
- **Thực nghiệm**: Áp dụng các công nghệ và framework khác nhau
- **Portfolio**: Chứng minh khả năng làm việc với Java
- **Tiếp tục phát triển**: Dự án đang được cải thiện theo thời gian

### 🗄️ Database Stack
- **MySQL**: Relational database cho game data (players, accounts, inventory)
- **ScyllaDB/Cassandra**: NoSQL database cho distributed logging (log-server module)
- **Dual Database Architecture**: Tối ưu cho từng use case riêng biệt

### 📚 Những Gì Tôi Học Được Từ Dự Án Này

- **Java NIO**: Học về non-blocking I/O, `ServerSocketChannel`, `Selector` pattern
- **Database**: Thực hành với MySQL, JDBC, connection pooling (HikariCP)
- **NoSQL**: Tìm hiểu về Cassandra/ScyllaDB và DataStax driver
- **ECS Pattern**: Học về Entity-Component-System architecture
- **Thread Management**: Thread pools, concurrency, synchronization
- **Spring Boot**: Bước đầu với web framework, REST API
- **Gradle Build**: Dependency management, build automation
- **Logging**: SLF4J, Logback, Log4j
- **Maven Dependencies**: Quản lý thư viện bên thứ 3
- **Game Development**: Các khái niệm cơ bản về game loop, game state

### ⚠️ Trạng Thái Dự Án

- ✅ **Hoàn thành cơ bản**: Network layer, database layer, game loop
- 🚧 **Đang phát triển**: Game features, bug fixes, improvements
- ❌ **Chưa có**: 
  - Production deployment
  - Comprehensive testing coverage
  - Complete error handling
  - Security hardening
  - Performance optimization
  - Documentation hoàn chỉnh

> 📌 **Lưu ý quan trọng**: Đây là project học tập, code chưa đạt chuẩn production. Có nhiều điểm cần cải thiện và tôi vẫn đang trong quá trình học hỏi.

## 🏗️ Kiến Trúc Hệ Thống

Dự án được tổ chức thành 4 module chính:

### 1. **commons** - Core Commons Library
Module này chứa các component cơ bản được sử dụng xuyên suốt hệ thống:

- **Network Layer** (`nro.commons.network`):
  - `NioServer`: Server chấp nhận non-blocking I/O
  - `Dispatcher`: Xử lý các sự kiện network (read/write/accept)
  - `AConnection`: Base class cho tất cả kết nối
  - `PacketProcessor`: Xử lý packets với thread pool
  - Hỗ trợ encryption/decryption cho packet

- **Database Layer** (`nro.commons.database`):
  - `DatabaseFactory`: Quản lý connection pool với HikariCP
  - `Database`: Wrapper cho các operation database (SELECT, INSERT, UPDATE)
  - `TransactionHandler`: Xử lý transaction
  - Connection pooling với config linh hoạt

- **Services** (`nro.commons.services`):
  - `CronService`: Scheduled tasks với Quartz Scheduler
  - Runnable execution với timezone support

- **Configuration** (`nro.commons.configuration`):
  - Property-based configuration system
  - Support nhiều transformer types (Boolean, Number, Array, Collection, Enum, etc.)
  - Tự động transform từ properties file sang Java objects

- **Utilities** (`nro.commons.utils`):
  - `ThreadPoolManager`: Quản lý thread pools
  - `UncaughtExceptionHandler`: Xử lý exceptions
  - `NetworkUtils`: Utilities cho network operations
  - `SystemInfo`: System monitoring

### 2. **ecs** - Entity Component System Framework
Custom ECS framework dựa trên Artemis ODB:

- **Core Components**:
  - `World`: Core engine của ECS
  - `EntityManager`: Quản lý entities
  - `ComponentManager`: Quản lý components
  - `System`: Xử lý logic game dựa trên components

- **Systems**:
  - `IteratingSystem`: Xử lý từng entity
  - `EntityProcessingSystem`: Xử lý entity với interval
  - `IntervalSystem`: Hệ thống chạy theo interval

- **Aspects**:
  - Aspect matching system để filter entities dựa trên components
  - Entity subscriptions

- **Managers**:
  - `GroupManager`: Quản lý entity groups
  - `TagManager`: Tag management
  - `PlayerManager`: Quản lý players
  - `TeamManager`: Team management

### 3. **game-server** - Game Server Module
Module chính chứa game logic:

#### Core Systems (`nro.server.engine`)

- **GameWorld**:
  - Chạy game loop ở 60 TPS
  - Quản lý lifecycle của game world
  - Xử lý main queue cho thread-safe operations

- **Game Systems**:
  - `CombatSystem`: Xử lý combat logic
  - `MovementSystem`: Xử lý di chuyển của entities
  - `HealthSystem`: Quản lý HP/MP
  - `InventorySystem`: Quản lý inventory
  - `MapChangeSystem`: Xử lý chuyển map
  - `FashionUpdateSystem`: Cập nhật trang phục
  - `QuestSystem`: Hệ thống quest

#### Network Layer (`nro.server.network.nro`)

- **Connection Management**:
  - `NroConnection`: Game connection với states (CONNECTED, AUTHED, IN_GAME)
  - `GameConnectionFactory`: Tạo connections
  - Encryption/decryption cho packets

- **Packets**:
  - Client Packets (31 packets): Xử lý requests từ client
  - Server Packets (48 packets): Gửi data cho client
  - Packet factory với reflection
  - Packet state validation

#### Data Layer (`nro.server.data_holders`, `nro.server.dao`)

- **Data Holders**:
  - `DataManager`: Quản lý tất cả game data
  - YAML data loaders cho items, monsters, skills, etc.
  - `ItemData`, `MapData`, `SkillData`: Data repositories

- **DAO Layer**:
  - `AccountDAO`: Quản lý tài khoản
  - `PlayerDAO`: Quản lý nhân vật
  - `InventoryDAO`: Quản lý kho đồ
  - `BannedIpDAO`: Quản lý banned IPs

#### Services (`nro.server.services`)

- **Player Services**:
  - `PlayerService`: Business logic cho player
  - `PlayerEnterWorldService`: Xử lý vào game
  - `PlayerLeaveWorldService`: Xử lý rời game
  - `InventoryService`: Quản lý inventory logic

- **Game Services**:
  - `MonsterService`: Quản lý monster
  - `NpcService`: Quản lý NPC
  - `ItemService`: Xử lý items
  - `ChatService`: Chat system
  - `ChangeMapService`: Chuyển map logic
  - `AreaService`: Quản lý areas
  - `CommandService`: Admin commands

#### Models (`nro.server.model`)

- **ECS Components** (`nro.server.model.ecs.component`):
  - `HealthComponent`: HP/MP tracking
  - `PositionComponent`: Vị trí trong map
  - `StatsComponent`: Chỉ số nhân vật
  - `StateComponent`: Game state
  - `AppearanceComponent`: Ngoại hình
  - `InventoryComponent`: Inventory data
  - `SkillComponent`: Skills
  - Player-specific components, Monster-specific components

- **Game Models**:
  - `Account`: Tài khoản
  - `GameMap`: Map trong game
  - `Npc`: NPC trong game
  - Templates cho entities, items, skills, tasks

#### Configurations (`nro.server.configs`)

- Properties-based configuration
- Network configuration
- Server configuration
- Character configuration
- Thread configuration
- Shutdown configuration

### 4. **log-server** - Log Server Module
Microservice riêng biệt sử dụng Spring Boot và NoSQL database:

- **Spring Boot 3.2.0**:
  - RESTful API cho log management
  - Spring Web MVC
  - Auto-configuration
  - Dependency injection

- **ScyllaDB/Cassandra Integration**:
  - **DataStax Java Driver 4.17.0**: Native driver cho Cassandra/ScyllaDB
  - NoSQL database với high write throughput
  - Keyspace: `logs_keyspace`
  - Table: `user_logs` để lưu trữ log entries
  - Distributed logging system

- **Features**:
  - REST API endpoints để query logs
  - Log aggregation và retrieval
  - CQL (Cassandra Query Language) support
  - Log entry model với metadata

## 🛠️ Công Nghệ Sử Dụng

### Core Technologies

1. **Java 21**: Làm ngôn ngữ chính
2. **Gradle**: Build system và dependency management
3. **Lombok**: Giảm boilerplate code với annotations
4. **SLF4J + Logback**: Logging framework

### Database & Persistence

5. **MySQL** với **MySQL Connector 9.2.0**: Relational database chính
   - JDBC driver cho MySQL
   - ACID transactions
   - Prepared statements

6. **HikariCP 6.3.0**: High-performance JDBC connection pool
   - Connection pool configuration
   - Leak detection và health monitoring
   - Automatic connection cleanup
   - Configurable pool sizing

7. **ScyllaDB/Cassandra** với **DataStax Java Driver 4.17.0**: NoSQL database cho logging
   - Distributed NoSQL database
   - High write throughput cho logs
   - Sử dụng trong log-server module
   - Keyspace và table management

### Networking

8. **Java NIO** (Non-blocking I/O):
   - `ServerSocketChannel` và `SocketChannel`
   - `Selector` pattern cho multi-client support
   - ByteBuffer management
   - Encrypted packet communication

9. **Custom Network Framework**:
   - Non-blocking server architecture
   - Read/write dispatchers với thread pooling
   - Packet encryption/decryption
   - Connection state management

### Game Architecture

10. **ECS Framework** (Custom Artemis-based):
    - Entity-Component-System pattern
    - System-based game loop (60 TPS)
    - Component mappers
    - Aspect-based entity filtering
    - Manager system (Group, Tag, Player, Team)

### Scheduling & Concurrency

11. **Quartz Scheduler 2.5.0**: Cron jobs và scheduled tasks
12. **Thread Pools**:
    - Configurable thread pool management
    - Priority-based thread execution
    - Runnable statistics
    - Deadlock detection

### Data Processing

13. **Jackson**:
    - `jackson-databind 2.15.2`: JSON processing
    - `jackson-dataformat-yaml 2.15.2`: YAML parsing cho game data

14. **YAML Data Files**:
    - Game configuration data
    - Item definitions
    - Monster definitions
    - Skill definitions
    - Quest data

### Utilities & Tools

15. **Reflections 0.10.2**: Reflection API cho packet discovery và annotation processing
16. **Disruptor 4.0.0**: High-performance event processing (lock-free ring buffer)
17. **JSON Libraries**:
    - `json-simple 1.1.1`: Simple JSON processing
    - `org.json 20250107`: Advanced JSON processing
18. **Log4j 2.20.0**: Alternative logging framework (Apache Logging)

### Build & Deployment

19. **Shadow Plugin 7.1.2**: Gradle plugin để tạo executable JAR
    - Fat JAR với tất cả dependencies
    - Application packaging

### Testing

20. **JUnit Jupiter 5.8.1 / 5.10.0**: Testing framework
    - Unit testing
    - Integration testing
    - Test execution platform

### Spring Framework (Log Server)

21. **Spring Boot 3.2.0**:
    - Web starter cho REST API
    - Auto-configuration và dependency injection
    - REST controllers cho log service
    - Integration với ScyllaDB

## 📂 Cấu Trúc Project

```
DragonBoyServer/
├── commons/           # Core common library
│   └── src/main/java/nro/commons/
│       ├── configs/          # Configuration classes
│       ├── configuration/    # Property transformation system
│       ├── consts/           # Constants
│       ├── database/         # Database layer
│       ├── network/         # Network layer
│       ├── services/        # Services (Cron, etc.)
│       └── utils/           # Utilities
│
├── ecs/               # ECS framework
│   └── src/main/java/com/artemis/
│       ├── annotations/    # ECS annotations
│       ├── injection/       # Dependency injection
│       ├── link/            # Entity linking
│       ├── managers/        # Managers
│       ├── systems/         # Base systems
│       └── utils/           # ECS utilities
│
├── game-server/       # Main game server
│   └── src/main/java/nro/server/
│       ├── configs/         # Server configuration
│       ├── consts/          # Game constants
│       ├── controllers/     # Controllers
│       ├── dao/            # Data Access Objects
│       ├── data_holders/   # Game data management
│       ├── engine/          # Game engine & systems
│       ├── model/           # Game models
│       ├── network/         # Network handling
│       ├── services/        # Business logic services
│       └── utils/           # Utilities
│
├── log-server/        # Log server (Spring Boot)
│   └── src/main/java/
│
├── resources/         # Game resources
│   ├── configs/       # Configuration files
│   ├── data/          # Game data (YAML, SQL)
│   └── x1/, x2/, x3/, x4/  # Game assets
│
└── scripts/           # Utility scripts
```

## 🏛️ Kiến Trúc Microservices

Dự án sử dụng kiến trúc hybrid với core server và microservice riêng biệt:

### Core Game Server
- **Monolithic Core**: Main game server xử lý tất cả game logic
- **Layered Architecture**: Clear separation between layers
- **In-Process Services**: Services chạy trong cùng process

### Log Server (Microservice)
- **Spring Boot Application**: Standalone microservice
- **RESTful API**: `/api/logs` endpoint
- **NoSQL Storage**: ScyllaDB cho distributed logging
- **Separation of Concerns**: Log storage tách biệt khỏi game server
- **Scalability**: Có thể scale log-server độc lập

## 🔄 Game Loop & Systems

Server chạy game loop ở **60 TPS (Ticks Per Second)**:

```12:14:game-server/src/main/java/nro/server/engine/GameWorld.java
private GameWorld() {
    int ticksPerSecond = 60;
    this.tickInterval = 1000 / ticksPerSecond;
}
```

### Systems Execution Order

Các systems được chạy theo thứ tự trong mỗi tick:

1. **FashionUpdateSystem**: Cập nhật trang phục
2. **MovementSystem**: Xử lý di chuyển
3. **MapChangeSystem**: Xử lý chuyển map
4. **QuestSystem**: Xử lý quests
5. **PlayerManager**: Quản lý players
6. **CombatSystem**: Xử lý combat
7. **InventorySystem**: Quản lý inventory

## 📦 Packet System

### Packet Flow

1. **Client → Server**:
   - Client gửi packet qua TCP
   - Server read từ `ByteBuffer`
   - Decrypt nếu cần
   - Parse packet theo command
   - Route đến packet handler
   - Xử lý trong packet processor thread pool

2. **Server → Client**:
   - Server tạo packet object
   - Serialize vào `ByteBuffer`
   - Encrypt nếu cần
   - Thêm vào send queue
   - Dispatcher write khi ready

### Packet Types

- **Client Packets**: 31 packet handlers
  - Login, movement, attack, inventory operations, etc.
  
- **Server Packets**: 48 packet types
  - Server responses, notifications, game state updates

## 🎮 Game Features

### Implemented Features

- ✅ **Account System**: Login, authentication, session management
- ✅ **Player System**: Character management, stats, leveling
- ✅ **Combat System**: Attack, damage calculation, monster AI
- ✅ **Inventory System**: Item management, equipment
- ✅ **Map System**: Multiple maps, map transitions
- ✅ **NPC System**: NPCs, interactions, quests
- ✅ **Quest System**: Quest tracking và completion
- ✅ **Monster System**: Monster spawning, AI
- ✅ **Chat System**: In-game chat
- ✅ **Command System**: Admin commands
- ✅ **Periodic Save**: Auto-save player data
- ✅ **Banned IP System**: Security feature
- ✅ **Connection Security**: Encryption, flood protection

## 🚀 Build & Run

### Prerequisites

- Java 21
- MySQL Database
- Gradle

### Configuration

1. Configure database trong `resources/configs/database.properties`
2. Configure network trong `resources/configs/network.properties`
3. Configure server settings trong `resources/configs/server.properties`

### Build

```bash
# Build tất cả modules
./gradlew build

# Build shadow JAR cho game-server
./gradlew :game-server:shadowJar

# Chạy server
java -jar game-server/build/libs/game-server-1.0.0-all.jar
```

## 📊 Performance & Optimization

### Optimizations

1. **Dual Database Architecture**: 
   - MySQL (relational) cho game data với ACID transactions
   - ScyllaDB (NoSQL) cho logging với high write throughput
2. **Connection Pooling**: HikariCP với configurable pool size
3. **Thread Pool Management**: Optimized thread pools cho network I/O
4. **NIO**: Non-blocking I/O cho hàng nghìn connections
5. **ECS Architecture**: Efficient entity processing
6. **Packet Batching**: Queue-based packet sending
7. **Connection Alive Checker**: Detect và cleanup dead connections
8. **Profiling Strategy**: Monitor system performance
9. **Leak Detection**: Detect connection leaks
10. **Event-Driven**: Disruptor pattern cho lock-free event processing

### Monitoring

- Connection pool statistics
- Thread pool monitoring
- System performance profiling
- Runtime statistics

## 🔐 Security Features

- Packet encryption/decryption
- Flood protection
- Login throttle
- IP banning system
- Session validation
- Connection state management

## 📝 Development Notes

### Best Practices

1. **Thread Safety**: Sử dụng concurrent collections và locks
2. **Exception Handling**: Comprehensive error handling
3. **Logging**: Structured logging với SLF4J
4. **Configuration**: Externalized configuration
5. **Separation of Concerns**: Clear separation giữa các layers
6. **Clean Code**: Lombok để giảm boilerplate

### Code Quality

- Custom exception handling
- Resource management (try-with-resources)
- Uncaught exception handler
- Deadlock detection
- Performance monitoring

## 📚 Kiến Thức Tôi Đang Học Trong Dự Án Này

Dự án này giúp tôi thực hành và hiểu về:

1. **Java NIO**: Socket programming, Selector pattern, non-blocking I/O
2. **Concurrency**: Thread pools, thread safety, synchronization cơ bản
3. **Database**: 
   - **SQL**: JDBC, Connection pooling với HikariCP, PreparedStatement
   - **NoSQL**: Tìm hiểu về Cassandra/ScyllaDB, CQL queries
   - Học cách sử dụng cả SQL và NoSQL cho mục đích khác nhau
4. **Game Architecture**: Tìm hiểu ECS pattern, Game loop concept
5. **Software Design**: 
   - Layered architecture
   - Một số design patterns (Singleton, DAO, Factory)
6. **Spring Boot**: Học cơ bản về web framework, REST API
7. **Build Tools**: Gradle, dependency management
8. **Logging**: SLF4J, Logback - học về structured logging
9. **Configuration**: Property files, externalized configuration
10. **Serialization**: Binary protocols, JSON parsing cơ bản
11. **Thread Management**: Thread pools, executor services
12. **Reflection**: Annotation processing, dynamic class loading
13. **Testing**: JUnit cơ bản

> 📝 **Note**: Tôi đang học những concept này thông qua dự án này, chưa phải expert. Mỗi công nghệ là một cơ hội để tôi thực hành và cải thiện.

## 📚 Công Nghệ Chi Tiết

### Network Stack
- **Java NIO**: Non-blocking socket operations
- **Selector Pattern**: Efficient multi-client handling
- **ByteBuffer Management**: Direct and heap buffers
- **Packet Encryption**: Custom encryption/decryption
- **Connection Lifecycle**: Connect → Auth → InGame states

### ECS Framework
- **Entity Management**: Create, update, delete entities
- **Component Storage**: Efficient component storage
- **System Processing**: Aspect-based system execution
- **Dependency Injection**: Automatic dependency injection
- **Entity Linking**: Entity-to-entity relationships

### Database Layer

#### MySQL (Primary Database)
- **DAO Pattern**: Data Access Object pattern cho clean separation
- **Transaction Management**: ACID transactions với autocommit
- **Batch Operations**: Batch insert/update operations
- **Connection Pooling**: HikariCP integration với configurable pool size
- **SQL Injection Prevention**: PreparedStatement usage
- **JDBC**: MySQL Connector 9.2.0 với modern JDBC API

**Tables Structure**:
- `accounts`: Tài khoản player
- `players`: Nhân vật trong game
- `inventory`: Kho đồ của player
- `banned_ips`: Security và ban list

#### ScyllaDB/Cassandra (Log Database)
- **NoSQL Database**: Distributed, high-performance database
- **High Write Throughput**: Optimized cho logging workloads
- **DataStax Driver 4.17.0**: Native Cassandra driver
- **CQL Queries**: Cassandra Query Language
- **Data Model**:
  - Keyspace: `logs_keyspace`
  - Table: `user_logs` với columns:
    - `log_id` (String - primary key)
    - `timestamp` (Instant)
    - `event` (String)
    - `level` (String)
    - `message` (String)
    - `stack_trace` (String)
    - `meta` (Map<String, String>)

### Game Systems
- **Combat**: Damage calculation, state management
- **Movement**: Position tracking, pathfinding
- **Inventory**: Item management, equipment
- **Quest**: Quest tracking, progress, rewards
- **AI**: Monster AI, NPC behavior

## 💡 Đặc Điểm Kỹ Thuật

### Game Loop

- **60 TPS**: 60 ticks per second = ~16.67ms per tick
- **Thread-safe Operations**: Queue-based main thread operations
- **System Processing**: Ordered system execution
- **Entity Updates**: Batch entity processing

### Packet System

- **Binary Protocol**: Custom binary packet format
- **Command-based Routing**: Command-based packet routing
- **State-based Validation**: Validate packet by connection state
- **Encryption Support**: Optional packet encryption
- **Thread Pool Processing**: Separate thread pool cho packet processing

### Memory Management

- **Component Pooling**: Reuse component objects
- **Buffer Management**: ByteBuffer pooling
- **Connection Pooling**: Reuse database connections
- **Thread Pooling**: Reuse threads
- **Resource Cleanup**: Proper resource cleanup

## 🎯 Kết Luận

Dự án **DragonBoy Server** là một project học tập cá nhân được tôi phát triển trong khoảng **1 năm** để học và thực hành Java. Đây là hành trình tích lũy kiến thức của tôi:

- ✅ **Đã học**: Sử dụng các công nghệ như Java NIO, MySQL, Spring Boot
- 🚧 **Đang học**: Cải thiện code quality, best practices, design patterns
- ❓ **Sẽ học**: Production-ready code, comprehensive testing, better architecture

### Mục Tiêu Của Dự Án

1. **Chứng minh**: Tôi biết Java và có thể xây dựng chương trình Java
2. **Thực hành**: Áp dụng các công nghệ đã học vào project thực tế
3. **Portfolio**: Có project để showcase trong portfolio cá nhân
4. **Tiếp tục phát triển**: Dự án sẽ được cải thiện dần theo thời gian

> 💡 Dự án này **không phải production-ready** và còn nhiều điều cần cải thiện. Tôi coi đây là **khởi đầu** trong hành trình học Java của mình.

---

**Tác giả**: Arriety  
**Thời gian phát triển**: ~1 năm  
**Mục đích**: Học tập và thực hành Java  
**Trạng thái**: Đang phát triển  
**License**: MIT (see [LICENSE](LICENSE) file)  
**Language**: Vietnamese

---

## 🙏 Credits & Acknowledgments

### Third-Party Libraries & Frameworks

Dự án này sử dụng nhiều thư viện và framework mã nguồn mở từ cộng đồng. Tôi rất biết ơn các tác giả và maintainers đã tạo ra các công cụ tuyệt vời này:

#### Core Dependencies
- **Java NIO**: Official Java platform cho non-blocking I/O
- **MySQL Connector**: Official JDBC driver từ Oracle
- **HikariCP**: High-performance JDBC connection pool (Brett Wooldridge)
- **SLF4J + Logback**: Logging frameworks (QOS.ch)
- **Jackson**: JSON/YAML processing (FasterXML)

#### Game Architecture
- **Artemis ODB**: ECS framework được sử dụng trong module `ecs/`
  - Credit: Arni Arent và Adrian Papari
  - Tôi đã sử dụng framework này và một số sửa đổi nhỏ cho phù hợp với dự án
  - Original: https://github.com/junkdog/artemis-odb
  - Module `ecs/` chứa code từ Artemis ODB

#### Build & Tools
- **Gradle**: Build automation tool
- **Shadow Plugin**: Gradle plugin cho creating fat JARs (John Rengelman)
- **Lombok**: Code generation library (Project Lombok)
- **Reflections**: Reflection utilities (ronmamo)

#### Scheduling & Concurrency
- **Quartz**: Enterprise job scheduler (Terracotta Inc.)
- **Disruptor**: High-performance inter-thread messaging library (LMAX)

#### Web Framework
- **Spring Boot**: Enterprise application framework (Spring)
- **DataStax Java Driver**: Cassandra driver (DataStax)

#### Testing
- **JUnit Jupiter**: Testing framework (JUnit Team)

#### Logging
- **Log4j**: Apache logging framework

### Disclaimer

- Module `ecs/` chứa Artemis ODB framework - tôi không phải tác giả gốc của framework này
- Tôi đã thực hiện một số sửa đổi nhỏ và tích hợp vào dự án
- Tất cả credits cho Artemis ODB thuộc về các tác giả gốc: Arni Arent và Adrian Papari

### Special Thanks

- Cộng đồng open source đã tạo ra các công cụ tuyệt vời
- Tất cả contributors của các thư viện được sử dụng trong dự án này
- Mọi người đã review và góp ý cho dự án học tập của tôi

---

## 📄 License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.

**Note**: 
- This is a learning project. Code may contain bugs or incomplete features. Use at your own risk.
- The `ecs/` module contains code from Artemis ODB framework. Please refer to the original Artemis ODB license for that module.


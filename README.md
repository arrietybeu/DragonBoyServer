# DragonBoy Server

![Java Version](https://img.shields.io/badge/Java-21-orange)
![Gradle](https://img.shields.io/badge/Gradle-Latest-green)
![License](https://img.shields.io/badge/License-MIT-green)
A Java-based MMORPG game server implementing custom ECS architecture, dual database system, and non-blocking I/O networking.

## Architecture Overview

### Multi-Module Structure

```
DragonBoyServer/
├── commons/           # Core networking, database, and utilities
├── ecs/              # Entity-Component-System framework (Artemis-based)
├── game-server/      # Main game logic and systems
└── log-server/       # Microservice for distributed logging (Spring Boot)
```

### Technology Stack

**Core**
- Java 21, Gradle, Lombok
- SLF4J + Logback for logging

**Database**
- MySQL 9.2.0 + HikariCP 6.3.0 (game data, ACID transactions)
- ScyllaDB/Cassandra + DataStax Driver 4.17.0 (distributed logging)

**Networking**
- Java NIO (non-blocking I/O)
- Custom packet encryption/decryption
- Selector pattern for multi-client handling

**Game Engine**
- Custom ECS framework (Artemis ODB-based)
- 60 TPS game loop
- System-based processing

**Web & Scheduling**
- Spring Boot 3.2.0 (log-server REST API)
- Quartz Scheduler 2.5.0
- Disruptor 4.0.0 (lock-free event processing)

**Data Processing**
- Jackson (JSON/YAML parsing)
- Reflections 0.10.2 (packet discovery)

## Core Features

### Network Layer (`commons`)
- Non-blocking I/O server with `ServerSocketChannel` and `Selector`
- Thread-pooled packet processing with encryption support
- Connection pooling via HikariCP
- Scheduled tasks with Quartz

### ECS Framework (`ecs`)
- Entity-Component-System pattern
- Aspect-based entity filtering
- Component managers and dependency injection
- Group/Tag/Player/Team management

### Game Server (`game-server`)

**Systems (60 TPS)**
- `CombatSystem` - Damage calculation and combat logic
- `MovementSystem` - Position tracking and pathfinding
- `HealthSystem` - HP/MP management
- `InventorySystem` - Item and equipment handling
- `MapChangeSystem` - Map transitions
- `QuestSystem` - Quest tracking and completion
- `FashionUpdateSystem` - Appearance updates

**Network**
- 31 client packet handlers
- 48 server packet types
- State-based packet validation (CONNECTED → AUTHED → IN_GAME)
- Flood protection and connection lifecycle management

**Data Layer**
- YAML-based game data (items, monsters, skills, maps)
- DAO pattern for database operations
- Transaction handling with MySQL

**Services**
- Player lifecycle (enter/leave world)
- Monster/NPC management
- Chat system
- Admin commands
- Periodic auto-save

### Log Server (`log-server`)
- Spring Boot REST API (`/api/logs`)
- ScyllaDB integration for high-throughput logging
- Keyspace: `logs_keyspace`, Table: `user_logs`
- Independent microservice scalability

## Performance Optimizations

- Dual database architecture (SQL for ACID, NoSQL for writes)
- HikariCP connection pooling with leak detection
- Non-blocking I/O for thousands of concurrent connections
- Thread pool management with deadlock detection
- Packet batching and queue-based sending
- Component pooling and ByteBuffer reuse
- Disruptor pattern for lock-free event processing

## Security

- Packet encryption/decryption
- Flood protection and login throttling
- IP banning system
- Session validation
- Connection state management

## Build & Run

```bash
# Build all modules
./gradlew build

# Build executable JAR
./gradlew :game-server:shadowJar

# Run server
java -jar game-server/build/libs/game-server-1.0.0-all.jar
```

**Configuration Files**
- `resources/configs/database.properties` - Database settings
- `resources/configs/network.properties` - Network configuration
- `resources/configs/server.properties` - Server parameters

## Database Schema

**MySQL Tables**
- `accounts` - Player accounts
- `players` - Character data
- `inventory` - Player items
- `banned_ips` - Security blacklist

**ScyllaDB Schema**
- `user_logs` - Distributed log entries with timestamp, event, level, message, stack_trace, metadata

## Credits

This project uses several open-source libraries:

- **Artemis ODB** (Arni Arent, Adrian Papari) - ECS framework in `ecs/` module
- **HikariCP** (Brett Wooldridge) - Connection pooling
- **Jackson** (FasterXML) - JSON/YAML processing
- **Quartz** (Terracotta) - Job scheduling
- **Disruptor** (LMAX) - Inter-thread messaging
- **Spring Boot** (Spring) - Web framework
- **DataStax Driver** - Cassandra client

## License

MIT License - See [LICENSE](LICENSE) file

**Note**: The `ecs/` module contains modified Artemis ODB code. Original license applies.

---

**Development Time**: ~1 year  
**Status**: Active development  
**Purpose**: Learning project demonstrating Java server architecture
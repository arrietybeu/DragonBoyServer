# DragonBoy Server

![Java Version](https://img.shields.io/badge/Java-21-orange)
![Gradle](https://img.shields.io/badge/Gradle-Latest-green)
![License](https://img.shields.io/badge/License-MIT-green)

Java MMORPG server học tập, tập trung vào NIO, ECS, thread pool và hai lớp lưu trữ (MySQL cho dữ liệu game, ScyllaDB/Cassandra cho logging). Mục tiêu là luyện tập thiết kế server, không phải sản phẩm production. Code còn đang hoàn thiện.

## Trạng thái ngắn gọn
- Đã có: network layer NIO, game loop 60 TPS, packet system, DAO MySQL, ECS tùy biến, logging NoSQL, auto-save, bảo vệ kết nối cơ bản.
- Chưa có: quy trình deploy production, kiểm thử đầy đủ, hardening bảo mật, tối ưu hiệu năng toàn diện, tài liệu hoàn chỉnh.

## Module
- commons: NIO server, dispatcher, packet processor, encryption, HikariCP, transaction handler, cron (Quartz), cấu hình property, utilities.
- ecs: khung ECS dựa trên Artemis ODB (world, entity/component managers, aspect systems, managers cho group/tag/player/team).
- game-server: logic chính (Combat, Movement, MapChange, Quest, Inventory, FashionUpdate), GameWorld 60 TPS, network `nro.server.network.nro` với connection state, 31 client packets, 48 server packets; services cho player/monster/npc/item/chat/map/command; DAO và data loaders YAML.
- log-server: Spring Boot 3.2, REST logging, ScyllaDB/Cassandra với DataStax driver 4.17.0, keyspace `logs_keyspace`, bảng `user_logs`.

## Tính năng hiện có
- Đăng nhập, quản lý tài khoản/phiên.
- Nhân vật: chỉ số, level, ngoại hình, trang bị, kho đồ.
- Di chuyển, đổi map, khu vực.
- Chiến đấu: tính sát thương, AI quái, trạng thái.
- Nhiệm vụ, NPC tương tác, chat.
- Lệnh quản trị, auto-save, kiểm tra kết nối, cấm IP, mã hóa gói tin và chống flood cơ bản.

## Kiến trúc tóm tắt
- Game loop: 60 tick/giây, main queue bảo vệ thread-safe, thứ tự systems: FashionUpdate → Movement → MapChange → Quest → PlayerManager → Combat → Inventory.
- Packet: binary, định tuyến theo command, kiểm tra state, encrypt/decrypt, thread pool xử lý và send queue.
- Lưu trữ: MySQL (accounts, players, inventory, banned_ips) qua HikariCP; ScyllaDB/Cassandra cho log phân tán.

## Công nghệ chính
- Java 21, Gradle, Lombok, SLF4J/Logback, Log4j (tùy chọn), Jackson JSON/YAML, Reflections, Disruptor.
- Quartz cho lịch, thread pools cấu hình được.
- Shadow plugin đóng gói fat JAR.
- JUnit Jupiter 5 cho kiểm thử.

## Cấu trúc thư mục
```
commons/         core network, database, config, services, utils
ecs/             khung ECS dựa trên Artemis ODB
game-server/     logic game, network, DAO, services, models, configs
log-server/      Spring Boot log service
resources/       configs, dữ liệu YAML/SQL, assets
scripts/         tiện ích
```

## Build & chạy
Prerequisites: Java 21, MySQL, Gradle.

1) Cấu hình:
   - `resources/configs/database.properties`
   - `resources/configs/network.properties`
   - `resources/configs/server.properties`
2) Build: `./gradlew build`
3) Đóng gói game-server: `./gradlew :game-server:shadowJar`
4) Chạy: `java -jar game-server/build/libs/game-server-1.0.0-all.jar`

## Ghi chú
- Đây là dự án luyện tập, có thể còn lỗi hoặc thiếu tính năng. Không khuyến nghị dùng trực tiếp cho production.
- Module `ecs/` chứa mã từ Artemis ODB (giữ license gốc).

## License
MIT, xem `LICENSE`.

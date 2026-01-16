# BeEyes Project Guide for Agentic Coders

## Build, Test, and Lint Commands

- **Build**: `gradle build`
- **Test**: `gradle test`
- **Single test**: `gradle test --tests ClassName.methodName`
- **Single test class**: `gradle test --tests ClassName`

Uses system `gradle` (no gradlew wrapper). No automated linting tools configured.

## Tech Stack
Java 21, Spring Boot 3.5.7, Gradle (Kotlin DSL), Jimmer ORM, PostgreSQL, Redis, Kafka, InfluxDB, Sa-Token

## Project Structure
```
src/main/java/cv/beriholic/beeyes/
├── controller/      - REST endpoints (@RestController)
├── service/         - Service interfaces
├── service/impl/    - Service implementations
├── repository/      - Data access (JSqlClient)
├── models/entity/   - Jimmer entity definitions
├── models/dto/      - DTOs and request/response objects
├── exception/       - Custom exceptions
├── config/          - Spring configuration classes
├── consts/          - Constants and enums
├── utils/           - Utility classes
├── aspect/          - AOP aspects
├── filter/          - Servlet filters
└── helper/          - Helper classes
```

## Code Style Guidelines

### Imports
Group: JDK libraries, third-party, project imports (blank line separated). Use `jakarta.*` not `javax.*`. No wildcard imports.

### Naming Conventions
- **Classes**: PascalCase (`AuthController`, `UserServiceImpl`)
- **Methods**: camelCase, verb-first (`login`, `createRule`)
- **Constants**: UPPER_SNAKE_CASE (`QUERY_MACHINE_MAX_PAGE_SIZE`)
- **Entities**: PascalCase with `DO` suffix (`UserDO`, `AlertRuleDO`)
- **DTOs**: PascalCase with suffix (`AuthLoginRequest`, `AlertRuleDTO`)
- **Repositories**: PascalCase with `Repository` suffix (`UserRepository`)

### Class Structure
```java
package cv.beriholic.beeyes.<module>;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import cv.beriholic.beeyes.models.dto.RestBean;

@Slf4j
@RestController
@RequestMapping("/api/v1/<resource>")
@RequiredArgsConstructor
public class <Name>Controller {
    private final <Service> <service>;

    @<HttpMethod>
    public RestBean<ReturnType> methodName(@RequestBody RequestType request) {
        return RestBean.success(data);
    }
}
```

### Lombok & DI
Use `@Slf4j`, `@RequiredArgsConstructor` for constructor injection. Use `@Data`, `@AllArgsConstructor`, `@NoArgsConstructor` for DTOs. Prefer `@RequiredArgsConstructor`, use `@Resource` only when needed.

### Controllers
`@RestController` + `@RequestMapping("/api/v1/<resource>")`. Return `RestBean<T>`. `@RequestBody` for POST/PUT, `@PathVariable`/`@RequestParam` for GET. Check permissions: `PermissionValidateHelper.checkPermission(PermissionCode.XXX)`.

### Services
Interface in `service/`, impl in `service/impl/` with `@Service`, `@Slf4j`. Use `@Transactional` for modifications. Jimmer updates: `EntityDODraft.$.produce(entity, draft -> {...})`.

### Repositories
Extend `BaseRepository<E, T, D>`. Use JSqlClient, View/Fetcher projections, Jimmer specifications.

### Exceptions
`BizRuntimeException` for business errors, `AuthorizationException` for auth/permission errors. Both extend `AbstractBeEyesException`.

### Logging
`log.info()`, `log.warn()`, `log.error()`. Include context: `log.info("[ServiceName] operationName start, input:{}", JsonUtil.toJSONString(request))`. LogAspect handles controller logging.

### Validation
Use `ValidateHelper` static methods. Throw `IllegalArgumentException` with descriptive messages. Jakarta validation annotations on DTOs when needed.

### Constants & Utils
Constants in `consts/` package (interfaces/enums). Utilities in `utils/` package, static methods: `BcryptUtil`, `JsonUtil`, `SnowflakeIdGenerator`.

### Entities (Jimmer)
Interfaces with `@Entity`, `@Table(name)`. `@Id` with `@GeneratedValue(generatorType = SnowflakeIdGenerator.class)`. `@Key` for unique fields. `@Column(name)` for custom names. `@OneToOne`, `@OneToMany`, `@ManyToOne` for relationships. Extend `BaseDO`.

### Transactions & JSON
`@Transactional` on modifying service methods. Use SaveMode.INSERT_ONLY/UPSERT with repository.save(). Long IDs serialized as strings via JacksonConfiguration. Use `JsonUtil.toJSONString()`/`JsonUtil.parseObject()`.

### External Systems
- **InfluxDB**: MetricService for machine runtime queries
- **Kafka**: `@KafkaListener` for consuming, topics in `KafkaTopic`
- **WebSocket**: Configured in `WebSocketConfiguration` for real-time metrics

### Testing
JUnit 5 (`@Test`, `@SpringBootTest`). Test classes in `src/test/java/cv/beriholic/beeyes/`. Naming: `<Name>Tests`.

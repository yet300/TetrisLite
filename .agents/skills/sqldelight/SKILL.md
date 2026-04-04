---
name: sqldelight
description: Implement, review, and refactor SQLDelight code in this repo. Use for `.sq` or `.sqm` files, `TetrisLiteDatabase`, `SqlDriver`, `SqlSchema<QueryResult.AsyncValue<Unit>>`, `ColumnAdapter`, `DatabaseManager`, `DatabaseDriverFactory`, DAO/query changes, migrations, coroutines query flows, or SQLDelight KMP Gradle wiring.
---

# SQLDelight

Use this skill for SQLDelight work in this repository.

This repo currently uses:

- SQLDelight `2.3.1`
- the local Gradle plugin alias `libs.plugins.local.sqlDelight`
- async database generation via `generateAsync = true`
- a dedicated `core/database` module
- `expect`/`actual` driver factories per platform
- DAOs, mappers, and repositories above the generated queries

Default to the repo's current SQLDelight style, not the broadest possible SQLDelight API surface.

## Read These First

Open only the files relevant to the task.

Vendored upstream docs in this repo:

- `referance/sqldelight-master/docs/multiplatform_sqlite/index.md`
- `referance/sqldelight-master/docs/multiplatform_sqlite/foreign_keys.md`
- `referance/sqldelight-master/docs/multiplatform_sqlite/custom_projections.md`
- `referance/sqldelight-master/docs/multiplatform_sqlite/query_arguments.md`
- `referance/sqldelight-master/docs/multiplatform_sqlite/types.md`
- `referance/sqldelight-master/docs/multiplatform_sqlite/transactions.md`
- `referance/sqldelight-master/docs/multiplatform_sqlite/grouping_statements.md`
- `referance/sqldelight-master/docs/js_sqlite/multiplatform.md`
- `referance/sqldelight-master/docs/js_sqlite/coroutines.md`
- `referance/sqldelight-master/docs/js_sqlite/androidx_paging.md`
- `referance/sqldelight-master/docs/android_sqlite/fts5_virtual_tables.md`

Gradle and dependency setup:

- `core/database/build.gradle.kts`
- `build-logic/convention/src/main/kotlin/com/yet/plugins/SqlDelightConventionPlugin.kt`
- `gradle/libs.versions.toml`

Core database runtime:

- `core/database/src/commonMain/kotlin/com/yet/tetris/database/db/DatabaseDriverFactory.kt`
- `core/database/src/commonMain/kotlin/com/yet/tetris/database/db/TetrisDatabase.kt`
- `core/database/src/commonMain/kotlin/com/yet/tetris/database/utils/Adapters.kt`

Platform driver implementations:

- `core/database/src/androidMain/kotlin/com/yet/tetris/database/db/DatabaseDriverFactory.android.kt`
- `core/database/src/desktopMain/kotlin/com/yet/tetris/database/db/DatabaseDriverFactory.desktop.kt`
- `core/database/src/jsMain/kotlin/com/yet/tetris/database/db/DatabaseDriverFactory.js.kt`
- `core/database/src/nativeMain/kotlin/com/yet/tetris/database/db/DatabaseDriverFactory.native.kt`

Schema and migrations:

- `core/database/src/commonMain/sqldelight/com/yet/tetris/database/GameHistory.sq`
- `core/database/src/commonMain/sqldelight/com/yet/tetris/database/CurrentGameState.sq`
- `core/database/src/commonMain/sqldelight/com/yet/tetris/database/BoardCells.sq`
- `core/database/src/commonMain/sqldelight/com/yet/tetris/database/1.sqm`
- `core/database/src/commonMain/sqldelight/com/yet/tetris/database/2.sqm`
- `core/database/src/commonMain/sqldelight/com/yet/tetris/database/3.sqm`

Representative query consumers:

- `core/database/src/commonMain/kotlin/com/yet/tetris/database/dao/GameHistoryDao.kt`
- `core/database/src/commonMain/kotlin/com/yet/tetris/database/dao/GameStateDao.kt`
- `core/database/src/commonMain/kotlin/com/yet/tetris/database/mapper/GameHistoryMapper.kt`
- `core/database/src/commonMain/kotlin/com/yet/tetris/database/mapper/GameStateMapper.kt`
- `core/data/src/commonMain/kotlin/com/yet/tetris/data/repository/GameHistoryRepositoryImpl.kt`
- `core/data/src/commonMain/kotlin/com/yet/tetris/data/repository/GameStateRepositoryImpl.kt`

DI and app graph integration:

- `core/database/src/commonMain/kotlin/com/yet/tetris/database/di/DatabaseBindings.kt`
- `core/database/src/androidMain/kotlin/com/yet/tetris/database/di/AndroidDatabaseBindings.kt`
- `shared/src/androidMain/kotlin/com/yet/tetris/di/AndroidAppGraph.kt`

Tests:

- `core/database/src/commonTest/README.md`
- `core/database/src/commonTest/kotlin/com/yet/tetris/database/db/DatabaseManagerTest.kt`
- `core/database/src/commonTest/kotlin/com/yet/tetris/database/dao/GameHistoryDaoTest.kt`
- `core/database/src/commonTest/kotlin/com/yet/tetris/database/dao/GameStateDaoTest.kt`

## Repo Reality

Official SQLDelight supports many features across multiple drivers and extensions.

This repo currently uses a narrower subset:

- SQLite via SQLDelight in a KMP module
- a single generated database class: `TetrisLiteDatabase`
- async schema generation
- `SqlSchema<QueryResult.AsyncValue<Unit>>`
- `awaitAsList`, `awaitAsOne`, `awaitAsOneOrNull`
- `asFlow().mapToList(...)` and `asFlow().mapToOneOrNull(...)`
- platform-specific drivers:
  - `AndroidSqliteDriver`
  - `JdbcSqliteDriver`
  - `NativeSqliteDriver`
  - `WebWorkerDriver`
- Metro DI around the database layer

This repo currently does not use in production code:

- AndroidX Paging integration
- FTS5 virtual tables
- foreign key enforcement callbacks
- hand-written access to generated query listeners
- direct SQLDelight calls from feature or UI modules

Do not introduce optional SQLDelight features by default just because upstream supports them.

## Non-Negotiable Rules

1. Keep schema files under `core/database/src/commonMain/sqldelight/com/yet/tetris/database/`.
2. Never edit generated files under `core/database/build/generated/sqldelight/`.
3. Preserve `generateAsync = true` unless the task intentionally refactors the whole driver setup.
4. Keep platform driver creation behind `DatabaseDriverFactory`.
5. Run database I/O on `dispatchers.io`.
6. Wrap multi-statement writes in `transaction {}`.
7. Add or update numbered `.sqm` migrations when schema changes must preserve existing installs.
8. Keep direct SQLDelight access in `core/database`; repositories in `core/data` adapt that to domain interfaces.
9. Put entity-domain mapping in mapper files, not in Compose, MVIKotlin stores, or Decompose components.
10. Add custom Kotlin column types via `AS Type` plus a `ColumnAdapter`, usually in `utils/Adapters.kt`.
11. Use SQL for ordering, filtering, and simple projections before reaching for post-query mapping lambdas.
12. Keep DI bindings in the module's `di/` package and register new containers in the app graph.

## Current Architecture Pattern

### 1) Gradle and generation

The database module applies the repo's local SQLDelight convention plugin and declares a single async database:

```kotlin
plugins {
    alias(libs.plugins.local.sqlDelight)
}

sqldelight {
    databases {
        create("TetrisLiteDatabase") {
            packageName.set("com.yet.tetris.database")
            generateAsync.set(true)
        }
    }
}
```

The local convention plugin centralizes driver dependencies:

- `coroutines-extensions` in `commonMain`
- Android, desktop, native, and JS drivers in platform source sets
- JS worker npm dependencies in `jsMain` and `jsTest`

When changing shared SQLDelight dependency wiring, update the convention plugin instead of patching individual modules ad hoc.

### 2) Schema lives in `.sq`, generated APIs live in build output

This repo keeps all schema and labeled queries under:

```text
core/database/src/commonMain/sqldelight/com/yet/tetris/database/
  GameHistory.sq
  CurrentGameState.sq
  BoardCells.sq
  1.sqm
  2.sqm
  3.sqm
```

From those files SQLDelight generates:

- `TetrisLiteDatabase`
- one `*Queries` type per `.sq` file
- row data classes matching table or projection shapes

Inspect generated code when you need to confirm signatures, but do not edit it.

### 3) Driver factory pattern

Because this repo uses async schema generation, the common driver contract is:

```kotlin
expect class DatabaseDriverFactory {
    suspend fun provideDbDriver(schema: SqlSchema<QueryResult.AsyncValue<Unit>>): SqlDriver
}
```

Platform actuals follow two patterns:

- Android, desktop, and native adapt the async schema with `schema.synchronous()`
- JS uses `WebWorkerDriver` and then explicitly runs `schema.create(it).await()`

Follow the existing pattern:

```kotlin
actual suspend fun provideDbDriver(schema: SqlSchema<QueryResult.AsyncValue<Unit>>): SqlDriver =
    AndroidSqliteDriver(schema.synchronous(), context, DB_FILE_NAME)
```

```kotlin
actual suspend fun provideDbDriver(schema: SqlSchema<QueryResult.AsyncValue<Unit>>): SqlDriver =
    WebWorkerDriver(Worker(scriptURL = resolveWorkerScriptUrl()))
        .also { schema.create(it).await() }
```

Do not quietly switch the schema type back to synchronous APIs. The JS worker path depends on the async setup.

### 4) Database bootstrap pattern

The repo does not construct `TetrisLiteDatabase` directly all over the codebase. It centralizes that in `DatabaseManager`.

That class is responsible for:

- singleton-like lazy initialization
- concurrency-safe first access via `Mutex`
- holding the actual `SqlDriver`
- closing the driver on shutdown
- attaching required adapters when creating `TetrisLiteDatabase`

Follow that pattern whenever new tables or adapters are added.

## Query and DAO Pattern

### Labeled SQL first

Put SQL in `.sq` files and label statements so SQLDelight generates typesafe APIs:

```sql
getGameById:
SELECT * FROM GameHistory
WHERE id = ?;
```

For recurring behavior:

- use named args when the same parameter appears multiple times
- use `IN ?` for variable argument sets
- use `VALUES ?` only when inserting a generated row type is actually the cleanest call site

Prefer SQL-level projections before custom Kotlin-side mappers. Upstream SQLDelight supports projection mapper lambdas, but this repo usually benefits more from explicit SQL.

### Thin DAO wrappers

DAOs wrap the generated `*Queries` APIs and expose coroutine-friendly methods:

```kotlin
suspend fun getAllGames(): List<GameHistory> =
    withContext(dispatchers.io) {
        databaseManager
            .getDb()
            .gameHistoryQueries
            .getAllGames()
            .awaitAsList()
    }
```

For reactive queries, follow the existing pattern:

```kotlin
fun observeAllGames(): Flow<List<GameHistory>> =
    flow {
        val db = databaseManager.getDb()
        emitAll(
            db.gameHistoryQueries
                .getAllGames()
                .asFlow()
                .mapToList(dispatchers.io),
        )
    }
```

Rules:

- fetch the database from `DatabaseManager`
- do one-shot query execution with `awaitAs*`
- do reactive observation with `asFlow().mapTo*`
- keep DAOs small and concrete
- do not move domain reconstruction logic into the DAO if it belongs in a mapper

### Transactions

Upstream SQLDelight supports both Kotlin-side transactions and grouped SQL statements.

This repo currently prefers Kotlin-side transactions:

```kotlin
db.transaction {
    db.currentGameStateQueries.insertOrReplaceGameState(...)
    db.boardCellsQueries.clearAllCells()
    boardCells.forEach { cell ->
        db.boardCellsQueries.insertCell(...)
    }
}
```

Use grouped statement blocks in `.sq` only when they materially simplify the schema layer. Do not introduce them by habit.

## Types and Adapters

Upstream SQLDelight maps SQLite primitives to Kotlin primitives and supports custom types with `AS`.

The repo already uses typed columns such as:

- `TEXT AS Difficulty`
- `TEXT AS TetrominoType`
- `INTEGER AS Boolean`

Current adapter strategy:

- keep reusable adapters in `core/database/src/commonMain/kotlin/com/yet/tetris/database/utils/Adapters.kt`
- pass required adapters when constructing `TetrisLiteDatabase`
- prefer simple, queryable encodings like `TEXT` or `INTEGER`

For new custom types:

1. define the column in `.sq` with `AS Type`
2. add a `ColumnAdapter` if SQLDelight does not already supply what you need
3. wire that adapter into the generated table adapter when creating `TetrisLiteDatabase`
4. update tests that cover round-trips and nullability

This repo currently uses a local `enumAdapter()` helper instead of the upstream `EnumColumnAdapter` convenience type. Preserve local consistency unless there is a reason to standardize broadly.

## Migrations

The repo already ships numbered `.sqm` migrations with additive `ALTER TABLE` statements and defaults for older installs.

Follow that pattern:

- add a new migration file with the next integer name
- preserve existing rows with safe defaults where possible
- keep migration SQL explicit and minimal
- update any affected DAOs, mappers, and tests together

When a change only affects a fresh schema but not upgrade safety, still ask whether an existing install path needs a migration. Do not assume a new `CREATE TABLE` definition is enough.

## JS, Android, and Optional Features

### JS worker setup

The JS driver is not a generic sample. It resolves `sqlite.worker.js` relative to the loaded app bundle and uses `WebWorkerDriver`.

Before changing JS database behavior, inspect:

- `core/database/src/jsMain/kotlin/com/yet/tetris/database/db/DatabaseDriverFactory.js.kt`
- `build-logic/convention/src/main/kotlin/com/yet/plugins/SqlDelightConventionPlugin.kt`

Do not replace this with a different worker import style unless you also verify the web packaging flow.

### Android foreign keys

Upstream SQLDelight enables Android foreign key enforcement through an `AndroidSqliteDriver.Callback`.

This repo does not currently do that. If a task introduces actual foreign key constraints that must be enforced on Android, add the callback deliberately and verify the migration behavior.

### FTS5

SQLDelight supports FTS5 virtual tables, but this repo does not currently use them.

If you add FTS5:

- keep it limited to an explicit search use case
- remember hidden columns need explicit aliases in projections
- verify target-driver support before assuming parity everywhere

### Paging

SQLDelight offers Paging 3 extensions, including multiplatform paging support.

This repo does not currently use paging extensions. Do not add them unless the user explicitly needs paged database reads.

## Boundaries With Data and DI

Database entities should not leak upward as the app's public model layer.

The current layering is:

1. SQLDelight schema and generated queries in `core/database`
2. DAOs in `core/database`
3. entity-domain mappers in `core/database`
4. repository implementations in `core/data`
5. domain repositories consumed by use cases, stores, and components

Keep that boundary intact. If a feature needs new persisted data:

1. extend schema and migrations
2. update or add DAOs
3. add mapper logic
4. expose it through a repository implementation
5. wire it through Metro bindings

Do not inject DAOs directly into UI, feature stores, or Decompose components unless the architecture is intentionally being changed.

## Testing and Validation

When changing SQLDelight code, extend the database module tests.

Current test strategy:

- `commonTest` holds shared DAO, mapper, and manager tests
- platform `TestUtils` provide `createTestDatabaseDriverFactory()`
- Flow assertions use Turbine
- tests run against platform-appropriate in-memory or test-friendly drivers

Useful commands:

- `./gradlew :core:database:test`
- `./gradlew :core:database:desktopTest`
- `./gradlew :core:database:testDebugUnitTest`

When schema or migration behavior changes, validate more than just compilation:

- insert and read the new fields
- verify nullability/default handling
- verify reactive flows still emit correctly
- verify transaction boundaries when multiple tables are involved

## Checklist

For most SQLDelight tasks in this repo, follow this order:

1. Update `.sq` files and add `.sqm` migrations if needed.
2. Let SQLDelight regenerate code through the build.
3. Update `DatabaseManager` adapters if types changed.
4. Update DAOs to use the new generated queries.
5. Update mappers and repository implementations.
6. Update Metro bindings if new database services are introduced.
7. Update tests in `core/database`.

If the task touches web or driver initialization, also verify the platform factory files and the SQLDelight convention plugin.

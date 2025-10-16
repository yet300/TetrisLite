# Database Module Tests

This directory contains comprehensive tests for the TetrisLite database module.

## Test Coverage

### DAO Tests

#### GameHistoryDaoTest
Tests for game history persistence operations:
- `insertGame_shouldSaveGameToDatabase` - Verifies game records are saved correctly
- `getAllGames_shouldReturnAllGamesOrderedByTimestamp` - Tests retrieval and ordering
- `observeAllGames_shouldEmitUpdates` - Tests reactive Flow updates
- `getGameById_shouldReturnNullForNonExistentGame` - Tests null handling
- `deleteGame_shouldRemoveGameFromDatabase` - Tests deletion
- `clearAllGames_shouldRemoveAllGames` - Tests bulk deletion
- `getGamesCount_shouldReturnCorrectCount` - Tests count queries
- `deleteOldestGames_shouldRemoveOldestGamesByTimestamp` - Tests selective deletion

#### GameStateDaoTest
Tests for game state persistence operations:
- `saveGameState_shouldSaveCompleteGameState` - Tests saving full game state
- `saveGameState_shouldReplaceExistingState` - Tests state replacement
- `getGameState_shouldReturnNullWhenNoStateSaved` - Tests null handling
- `getBoardCells_shouldReturnEmptyListWhenNoStateSaved` - Tests empty state
- `clearGameState_shouldRemoveStateAndCells` - Tests state clearing
- `hasSavedState_shouldReturnTrueWhenStateExists` - Tests state existence check
- `hasSavedState_shouldReturnFalseWhenNoStateExists` - Tests negative case
- `observeGameState_shouldEmitUpdates` - Tests reactive Flow updates
- `saveGameState_withNullCurrentPiece_shouldSaveCorrectly` - Tests null piece handling

### Mapper Tests

#### GameHistoryMapperTest
Tests for game history entity-domain mapping:
- `toDomain_shouldMapAllFieldsCorrectly` - Tests entity to domain conversion
- `toEntity_shouldMapAllFieldsCorrectly` - Tests domain to entity conversion
- `roundTrip_shouldPreserveData` - Tests bidirectional mapping integrity

#### GameStateMapperTest
Tests for game state entity-domain mapping:
- `toDomain_shouldMapGameStateWithCurrentPiece` - Tests full state mapping
- `toDomain_shouldMapGameStateWithoutCurrentPiece` - Tests null piece handling
- `toEntities_shouldMapGameStateToEntities` - Tests domain to entities conversion
- `toEntities_shouldMapGameStateWithoutCurrentPiece` - Tests null piece in conversion
- `roundTrip_shouldPreserveGameState` - Tests bidirectional mapping integrity

### Database Manager Tests

#### DatabaseManagerTest
Tests for database initialization and management:
- `getDb_shouldReturnDatabaseInstance` - Tests database initialization
- `getDb_shouldReturnSameInstanceOnMultipleCalls` - Tests singleton pattern
- `getDb_shouldHandleConcurrentInitialization` - Tests thread-safety
- `getDb_shouldInitializeDatabaseSchema` - Tests schema creation

## Running Tests

### All Platforms
```bash
./gradlew :core:database:test
```

### Desktop Only
```bash
./gradlew :core:database:desktopTest
```

### Android Only
```bash
./gradlew :core:database:testDebugUnitTest
```

## Test Architecture

The tests use an `expect`/`actual` pattern for platform-specific test setup:

- **commonTest**: Contains shared test logic
- **desktopTest**: Desktop-specific test setup using JDBC SQLite driver
- **androidUnitTest**: Android-specific test setup using Robolectric
- **nativeTest**: Native platform test setup
- **jsTest**: JavaScript platform test setup

### Test Database Setup

Each platform provides its own implementation of `createTestDatabaseManager()` which returns a `DatabaseManager` configured with an in-memory database suitable for testing.

## Dependencies

- `kotlin-test` - Kotlin testing framework
- `kotlinx-coroutines-test` - Coroutine testing utilities
- `turbine` - Flow testing library
- `sqldelight-driver` - SQLite JDBC driver for desktop tests

## Notes

- All tests use in-memory databases to ensure isolation
- Tests are designed to be fast and deterministic
- Each test cleans up after itself to prevent state leakage
- Flow-based tests use Turbine for easier async testing

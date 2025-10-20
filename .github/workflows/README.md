# GitHub Actions CI/CD Workflows

This directory contains GitHub Actions workflows for continuous integration and deployment of the Tetris Lite Kotlin Multiplatform project.

## Workflows

### 1. Pull Request Check (`pr-check.yml`)

**Triggers:** Pull requests and pushes to `main` and `develop` branches

**Jobs:**
- **Code Quality**: Runs Ktlint and Detekt for code style and static analysis
- **Test**: Executes all unit tests across all modules
- **Build Android**: Builds debug APK
- **Build Desktop**: Builds desktop distributions for Linux, macOS, and Windows
- **Build Web**: Builds the web application

**Artifacts:**
- Test results and reports
- Android debug APK
- Desktop distributions
- Web build

### 2. Release Build (`release.yml`)

**Triggers:** 

- Push to tags matching `v*` (e.g., `v1.0.0`) - Creates full release
- Push to `main` branch - Deploys web app to GitHub Pages only
- Manual workflow dispatch

**Jobs:**

- **Create Release**: Creates a GitHub release (tags only)
- **Build Android Release**: Builds signed APK and AAB (tags only)
- **Build Desktop Release**: Builds platform-specific installers (tags only)
- **Build Web and Deploy to Pages**: Builds JS app and deploys to GitHub Pages (always)
- **Upload Source Code**: Packages and uploads source code archives (tags only)

**Artifacts (on release tags):**
- Signed Android APK and AAB
- Desktop installers for all platforms (DEB, DMG, MSI)
- Web application archive (tar.gz)
- Source code (tar.gz and zip)

**Continuous Deployment:**

- Every push to `main` automatically deploys the web app to GitHub Pages
- Full release artifacts only created when pushing tags

**Required Secrets:**
- `KEYSTORE_BASE64`: Base64-encoded Android keystore
- `KEYSTORE_PASSWORD`: Keystore password
- `KEY_ALIAS`: Key alias
- `KEY_PASSWORD`: Key password

### 3. Test Suite (`test.yml`)

**Triggers:**
- Daily schedule (2 AM UTC)
- Manual workflow dispatch

**Jobs:**
- **Unit Tests**: Runs all unit tests with coverage reporting
- **Android Instrumented Tests**: Runs on Android emulators (API 28 and 33)
- **Desktop Tests**: Runs desktop-specific tests on all platforms
- **Test Report**: Aggregates and publishes test results

**Features:**
- Code coverage reporting to Codecov
- Android emulator caching for faster runs
- Comprehensive test result artifacts

### 4. Code Quality (`code-quality.yml`)

**Triggers:**
- Push to `main` and `develop`
- Pull requests
- Weekly schedule (Monday 9 AM UTC)

**Jobs:**
- **Detekt Analysis**: Static code analysis with SARIF upload
- **Ktlint Check**: Code style verification
- **Dependency Check**: Checks for dependency updates
- **Build Validation**: Validates Gradle wrapper and build configuration

## Setup Instructions

### 1. Enable GitHub Actions

GitHub Actions are enabled by default for public repositories. For private repositories, go to:
- Repository Settings → Actions → General → Enable Actions

### 2. Enable GitHub Pages

To enable GitHub Pages deployment:

1. Go to **Settings → Pages**
2. Under **Source**, select **GitHub Actions**
3. Your app will be available at: `https://YOUR_USERNAME.github.io/TetrisLite/`

**Note:** The first deployment may take a few minutes. Subsequent deployments are faster.

### 3. Configure Secrets

For release builds, add the following secrets in:
**Settings → Secrets and variables → Actions**

#### Required for Android Release:
```
KEYSTORE_BASE64       # Base64-encoded keystore file
KEYSTORE_PASSWORD     # Keystore password
KEY_ALIAS             # Key alias
KEY_PASSWORD          # Key password
```

#### Optional:
```
CUSTOM_DOMAIN         # Custom domain for GitHub Pages deployment
CODECOV_TOKEN         # Token for Codecov integration
```

### 3. Generate Keystore Base64

To encode your keystore file:

```bash
# On macOS/Linux
base64 -i your-keystore.jks | pbcopy

# On Windows (PowerShell)
[Convert]::ToBase64String([IO.File]::ReadAllBytes("your-keystore.jks")) | Set-Clipboard
```

### 4. Branch Protection Rules

Recommended branch protection for `main`:

1. Go to **Settings → Branches → Add rule**
2. Branch name pattern: `main`
3. Enable:
   - ✅ Require a pull request before merging
   - ✅ Require status checks to pass before merging
   - ✅ Require branches to be up to date before merging
   - Select required checks:
     - Code Quality
     - Run Tests
     - Build Android
     - Build Desktop
     - Build Web

## Usage

### Running Workflows Manually

1. Go to **Actions** tab
2. Select the workflow
3. Click **Run workflow**
4. Fill in required inputs (if any)

### Creating a Release

#### Option 1: Git Tag
```bash
git tag v1.0.0
git push origin v1.0.0
```

#### Option 2: Manual Dispatch
1. Go to **Actions → Release Build**
2. Click **Run workflow**
3. Enter version number (e.g., `1.0.0`)

### Viewing Test Results

Test results are automatically published as:
- **Check runs** on pull requests
- **Artifacts** downloadable from workflow runs
- **Comments** on pull requests (if configured)

## Workflow Status Badges

Add these badges to your README.md:

```markdown
![PR Check](https://github.com/YOUR_USERNAME/TetrisLite/workflows/Pull%20Request%20Check/badge.svg)
![Tests](https://github.com/YOUR_USERNAME/TetrisLite/workflows/Test%20Suite/badge.svg)
![Code Quality](https://github.com/YOUR_USERNAME/TetrisLite/workflows/Code%20Quality/badge.svg)
![Release](https://github.com/YOUR_USERNAME/TetrisLite/workflows/Release%20Build/badge.svg)
```

## Caching Strategy

Workflows use caching to speed up builds:

- **Gradle**: Build cache and dependencies
- **Node.js**: npm packages for web builds
- **Android AVD**: Emulator images for instrumented tests

Cache is automatically managed by GitHub Actions.

## Troubleshooting

### Build Failures

1. **Check logs**: Click on failed job to view detailed logs
2. **Run locally**: Reproduce the issue with `./gradlew <task>`
3. **Clear cache**: Re-run workflow with cache cleared

### Keystore Issues

If Android release build fails:
- Verify keystore is correctly base64-encoded
- Check all secrets are set correctly
- Ensure keystore passwords match

### Test Failures

- Check test reports in artifacts
- Run tests locally: `./gradlew test`
- For instrumented tests, check emulator logs

### Timeout Issues

If builds timeout:
- Increase `timeout-minutes` in workflow
- Optimize build by enabling parallel execution
- Use `--no-daemon` flag for CI builds

## Performance Optimization

Current optimizations:
- ✅ Gradle build cache
- ✅ Dependency caching
- ✅ Parallel job execution
- ✅ Conditional job execution
- ✅ AVD snapshot caching

## Security

- Secrets are encrypted and never exposed in logs
- Keystore is cleaned up after use
- SARIF reports are uploaded for security scanning
- Dependency vulnerability checks run weekly

## Maintenance

### Regular Tasks

- **Weekly**: Review dependency updates
- **Monthly**: Update workflow actions to latest versions
- **Quarterly**: Review and optimize build times

### Updating Actions

Check for updates:
```bash
# List all actions used
grep -r "uses:" .github/workflows/
```

Update to latest versions in workflow files.

## Resources

- [GitHub Actions Documentation](https://docs.github.com/en/actions)
- [Gradle Build Action](https://github.com/gradle/gradle-build-action)
- [Android Emulator Runner](https://github.com/ReactiveCircus/android-emulator-runner)
- [Compose Multiplatform](https://www.jetbrains.com/lp/compose-multiplatform/)

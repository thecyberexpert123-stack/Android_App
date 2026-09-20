# Security

## Principles
- Security-first per guideline 15
- Least privilege, defense in depth, fail securely

## Network
- `network_security_config.xml`: Cleartext disabled, only HTTPS allowed (except localhost for debug if needed)
- OkHttp: 10s connect, 30s read/write timeouts, retry on failure false for non-idempotent (explicit)
- Logging interceptor: Only in DEBUG, NONE in release
- Certificate pinning: Placeholder documented, enable via `CertificatePinner` when backend certs known
- Auth: `AuthInterceptor` adds Bearer token from DataStore, refreshes via Authenticator (future)

## Storage
- Room: Local cache, not sensitive
- DataStore Preferences: Non-sensitive settings
- EncryptedSharedPreferences / DataStore with `androidx.security.crypto` for tokens (guidance, not plain)
- No sensitive data in logs, no PII in Crashlytics

## Secrets
- `local.properties`: `api.base.url`, `api.key` if needed, never committed (in .gitignore)
- `BuildConfig`: Generated field `API_BASE_URL` from local.properties via build.gradle.kts
- CI: Use GitHub Secrets for signing, API URLs
- No hardcoded keys, no API keys in code

## Code
- Input validation: All remote DTOs validated before mapping, use `require` for domain invariants
- ProGuard/R8: Full mode, obfuscation, optimization, keep rules for models (serialization)
- Permissions: Minimal, only INTERNET, no READ_EXTERNAL_STORAGE unless needed
- Exported components: Only MainActivity exported, others not

## Dependencies
- Version catalog pinned, Dependabot recommended
- Check for vulnerabilities via `gradle dependencyCheck` (future) and GitHub Dependabot alerts

## Authentication (Future)
- OAuth2 + PKCE, token refresh, biometric for sensitive actions
- Logout clears encrypted storage

## Reporting
- Security issues: Do not open public issue, contact maintainer directly
- Responsible disclosure

## References
- developer.android.com/training/articles/security-tips
- developer.android.com/privacy-and-security/security-config
- OWASP Mobile Top 10

# Phase 1 Acceptance Checklist

- [ ] PostgreSQL starts from Docker Compose
- [ ] Flyway creates schema and seeds genres
- [ ] Bootstrap admin can sign in
- [ ] Unauthenticated `/admin/**` redirects to login
- [ ] Movie can be created with EN / FR / AR translations
- [ ] Empty translation slug is generated server-side
- [ ] Duplicate `(locale, slug)` is rejected
- [ ] Movie can be edited without replacing its public UUID
- [ ] Movie can be published and archived
- [ ] Movie can be permanently deleted
- [ ] Genre can be created in EN / FR / AR
- [ ] Genre assigned to movies cannot be deleted
- [ ] Dashboard counts come from database
- [ ] Admin works on mobile layout
- [ ] CSRF remains enabled for browser admin operations

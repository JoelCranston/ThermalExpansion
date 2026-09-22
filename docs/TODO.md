# TODO — ThermalExpansion

Current, outstanding work only. See [progress-log.md](progress-log.md) for what's
already done and why.

## Next up

1. **Blocked on CoFHCore's 1.20.6 hop.** `gradle.properties`/`build.gradle` here are
   already bumped to `mc_version=1.20.6`/`neo_version=20.6.141`/`java_version=21`
   locally (uncommitted, on top of the `1.20.4` branch) — don't commit or start
   migrating this repo's own code until `../CoFHCore` compiles clean on 1.20.6 (check
   its `docs/TODO.md`), since this repo depends on it (and on ThermalCore) directly.
2. **Switch to the local `1.20.6` branch before committing that bump** — it already
   exists (currently identical to the old `1.20.4` HEAD) but isn't checked out; the
   uncommitted bump is sitting on `1.20.4` right now and shouldn't be committed there.
3. Once unblocked: run `./gradlew compileJava`, triage by root-cause category (see
   CoFHCore's `docs/progress-log.md` "working method" section), verifying each API
   shape via `javap` against the real mapped jar. Check
   `../CoFHCore/docs/api-notes-1.20.6.md` first — several categories will likely recur
   here.

## Inbox

_(nothing yet — add anything noticed mid-session here rather than letting it get lost)_

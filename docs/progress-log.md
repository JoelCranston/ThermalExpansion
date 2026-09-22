# Progress log — ThermalExpansion

Append-only. Later entries correct earlier ones rather than editing them away. See
[TODO.md](TODO.md) for what's outstanding right now.

## Phase 0 — pre-existing state

This repo already had a working NeoForge **1.20.4** port before the current porting
effort started (`53511a0` "1.20.4 Initial Port Work" and follow-ups through `8ca074d`
"1.20.4 Fixes and Refactors" and `cb2396e` "Capability work.") — ThermalExpansion's own
1.20.4 hop wasn't part of this effort; it was the starting point, same as the other
three repos.

## Phase 1 — modern tooling, verified running

Brought the existing 1.20.4 codebase onto modern tooling, matching CoFHCore's Phase 1
(`../CoFHCore/docs/progress-log.md`):
- `03affe6` Wire composite build to local CoFHCore/ThermalCore for NeoForge port.
- `415aa76` Add dev run configs (client/server/data) and Gradle heap settings.
- `4473539` Fix broken `javafml` loaderVersion requirement in `mods.toml` (same bug hit
  in all four repos).
- `cadeae2` Upgrade to Gradle 9.2.1 + NeoGradle userdev 7.1.38.

## Phase 2 — the primer climb

Full primer chain, decision to target 26.1.2, and CoFHCore-first dependency ordering:
see `../CoFHCore/docs/progress-log.md`'s Phase 2 entry — this repo follows the same
plan, behind CoFHCore and its other two dependencies.

### 1.20.6 hop

Not yet started. `gradle.properties`/`build.gradle` are bumped locally but uncommitted,
still sitting on the `1.20.4` branch (a local `1.20.6` branch exists but hasn't been
checked out yet) — waiting on CoFHCore. See [TODO.md](TODO.md).

## Session tooling (2026-09-21)

Split off the unrelated **Pyronetics** side project (a from-scratch mod, no dependency
on this codebase) into its own repo/session so this session stays dedicated to the
Thermal Series port — see `../Pyronetics/CLAUDE.md`.

Added local-only session-context export tooling (`c95bef0`): `scripts/export_transcript.py`
renders a Claude Code session `.jsonl` to readable Markdown; output goes under
`docs/context/`, which is gitignored — this whole 4-repo effort runs in one shared
session here, so exports for all four repos live in this one place rather than being
duplicated per repo.

## Session tooling (2026-09-22)

Set up this file, `docs/TODO.md`, and `CLAUDE.md` (this "Where things are" structure)
across all four repos, mirroring the pattern already established in Pyronetics — moving
accumulated context out of chat history and into the repos themselves. CoFHCore's
`docs/api-notes-1.20.6.md` and `docs/progress-log.md` were built from its own
`1.20.6:`-prefixed commit messages (already API-note-shaped) plus a fresh
`./gradlew compileJava` run to capture the true current error breakdown; the other
three repos' docs are lighter, since their own 1.20.6 migrations haven't started yet.

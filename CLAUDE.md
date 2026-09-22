# ThermalExpansion — NeoForge port

## Where things are

| File | Read it when |
|---|---|
| **This file** | Always. Context, decisions, and current state. |
| [docs/TODO.md](docs/TODO.md) | Picking up work. **Anything noticed mid-session goes in its Inbox.** |
| [docs/progress-log.md](docs/progress-log.md) | The story behind a decision, or what's already been done and why. Append-only. |
| `../CoFHCore/docs/api-notes-1.20.6.md` | Writing any code against a 1.20.5/1.20.6 API. CoFHCore is the foundation library and hits every hop's API breakage first — check there before re-deriving something this repo will very likely also hit. |
| `docs/context/` (**local only, gitignored**) | The progress log doesn't have the detail you need. Full session transcript exports, made with `scripts/export_transcript.py` from `~/.claude/projects/` — this is the session's project directory, so exports for **all four repos'** work land here rather than being duplicated. `grep` them, don't read them whole. |

## Context

ThermalExpansion depends on **CoFHCore** and **ThermalCore** (`../CoFHCore`,
`../ThermalCore`). **ThermalDynamics** is a sibling at the same level. All four repos
are worked in **one shared Claude Code session** — this repo's project directory — in
dependency order each hop: CoFHCore first, then the others.

This is a **primer-climbing port**: starting from this repo's existing NeoForge 1.20.4
codebase and climbing NeoForge's official primers one version at a time toward the
target, **26.1.2** — see `../CoFHCore/CLAUDE.md` for why (no pre-existing NeoForge port
of CoFHCore existed to build the chain on top of instead).

This is unrelated to **Pyronetics** (`../Pyronetics`), a separate from-scratch mod in
its own repo/session, inspired by this mod's classic (1.7.10) designs but with zero
code dependency on this codebase.

## Decisions already made

Same as CoFHCore's (`../CoFHCore/CLAUDE.md`) — license (CoFH "Don't Be a Jerk"), target
26.1.2 via the full primer chain, branch-per-target-version, verify every API shape
against the real mapped jar via `javap` before writing code against it.

## Current state

Still checked out on branch **`1.20.4`** (unlike the other three repos, already on
`1.20.6`) — a local `1.20.6` branch exists but hasn't been switched to yet.
`gradle.properties`/`build.gradle` are bumped to 1.20.6 locally but **uncommitted**,
sitting on top of the `1.20.4` branch — waiting on CoFHCore to compile clean on 1.20.6
first. See [docs/TODO.md](docs/TODO.md).

**Next step**: once `../CoFHCore` compiles clean on 1.20.6, switch this repo to its
local `1.20.6` branch, commit the version bump there, and start this repo's own 1.20.6
migration, checking `../CoFHCore/docs/api-notes-1.20.6.md` first for API shapes already
confirmed.

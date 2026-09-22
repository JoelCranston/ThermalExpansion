# ThermalExpansion — NeoForge port

## Where things are

| File | Read it when |
|---|---|
| **This file** | Always. Context, decisions, and current state. |
| `../CoFHCore/docs/port-plan.md` | **Starting any porting work.** The approved plan (2026-09-21) for all four repos: 1.21.1 then 26.1.2 directly, with every version, coordinate, replacement API and reference stated. This repo's part: §5 A.2/A.3 and §6 B.10. |
| [docs/TODO.md](docs/TODO.md) | Picking up work. **Anything noticed mid-session goes in its Inbox.** |
| `../ThermalExpansionForNeoForge/` | Porting this repo to 1.21.1. SPLIGAN's minimal-diff 1.21.1 port of this exact codebase — `diff -ru src ../ThermalExpansionForNeoForge/src` is the Phase A worklist (review each hunk, apply the API changes, skip their build/IDE noise). |
| `../Pyronetics/` + its `docs/api-notes-26.1.2.md` | Any 26.1.2 shape. Joel's from-scratch mod, built and running on NeoForge 26.1.2.109 — read it before deriving a shape from the primer. |
| `../CoFHCore/docs/reference/` (local only) | Confirming an API shape: vendored primers, NeoForge release notes, 26.1/1.21.1 docs, source file lists. `../CoFHCore/scripts/fetch_reference.sh` recreates it. |
| [docs/progress-log.md](docs/progress-log.md) | The story behind a decision, or what's already been done and why. Append-only. |
| `../CoFHCore/docs/api-notes-1.20.6.md` (then `-1.21.1.md`, `-26.1.2.md`) | Writing any code against a 1.20.5+ API. CoFHCore is the foundation library and hits every hop's API breakage first — check there before re-deriving something this repo will very likely also hit. |
| `docs/context/` (**local only, gitignored**) | The progress log doesn't have the detail you need. Full session transcript exports, made with `scripts/export_transcript.py` from `~/.claude/projects/` — this is the session's project directory, so exports for **all four repos'** work land here rather than being duplicated. `grep` them, don't read them whole. |

## Context

ThermalExpansion depends on **CoFHCore** and **ThermalCore** (`../CoFHCore`,
`../ThermalCore`). **ThermalDynamics** is a sibling at the same level. All four repos
are worked in **one shared Claude Code session** — this repo's project directory — in
dependency order each hop: CoFHCore first, then the others.

This is a port of the existing NeoForge 1.20.4 codebase to **26.1.2** in exactly two hops —
**1.21.1**, then **26.1.2** directly (Joel, 2026-09-21; no other intermediates). Not a
one-version-at-a-time primer climb any more — see `../CoFHCore/docs/port-plan.md` §1/§3
and `../CoFHCore/docs/progress-log.md`'s revised Phase 2 entry.

This is unrelated to **Pyronetics** (`../Pyronetics`), a separate from-scratch mod in
its own repo/session, inspired by this mod's classic (1.7.10) designs but with zero
code dependency on this codebase.

## Decisions already made

Same as CoFHCore's (`../CoFHCore/CLAUDE.md`) — license (CoFH "Don't Be a Jerk"), target
26.1.2.109 via 1.21.1 (21.1.251) only, ModDevGradle 2.0.147 as the build plugin from Phase 0,
branch-per-target-version, verify every API shape against the real (patched-sources) jar
before writing code against it.

**Code style matches upstream CoFH** (Joel, 2026-09-22). The port is meant to go upstream as
pull requests, so every added line follows `../CoFHCore/docs/code-style.md`: comments are rare
and one line (no `// 1.21:` tags, porting narration or doc pointers), every method body opens
with a blank line, and annotation arguments take a space (`@Inject (…)`). API findings go in
the api-notes docs, not in code comments.

## Current state

**Phase A done (2026-09-22): builds clean and boots headless on NeoForge 21.1.251.**
Branch **`1.21.1`**, ModDevGradle 2.0.147, `META-INF/neoforge.mods.toml`, JEI 19.57.0.446.

This repo was ported **source-level, in parallel with ThermalCore**, against the 21.1.251
sources jar and CoFHCore's already-ported code — it could not compile at the time, since its
build `includeBuild`s ThermalCore. It compiled clean on the first attempt once ThermalCore
landed. A `runServer` here loads CoFHCore + ThermalCore + ThermalExpansion together and reaches
`Done (…)`.

Where this repo deliberately diverges from `../ThermalExpansionForNeoForge`:

- **No `FriendlyByteBuf` → `RegistryFriendlyByteBuf` casts.** CoFH's config/GUI packet buffers
  are plain scratch buffers with no registry context; the fork's cast is a runtime
  `ClassCastException`. Stacks go through `saveOptional`/`parseOptional` instead.
- **`Tags.Items.STONE/SAND/GLASS` became `STONES`/`SANDS`/`GLASS_BLOCKS`**, not the fork's
  downgrade to concrete `Blocks.*`, which would have silently de-tagged six recipes.
- **Resources are ours, not theirs.** Their `data/thermal/recipes` stayed plural, so ~470 of
  their machine recipes never load.

CoFH's own machine recipes are parsed by `RecipeJsonUtils`, not by a codec, so they keep the
`{"item": …}` result shape while vanilla-type recipes moved to `{"id": …}`.

**Next step**: nothing blocking. `runData` has not been run here; the client pass (machine GUIs,
JEI recipe pages) is Joel's — see `../CoFHCore/docs/TODO.md`. API shapes:
`../CoFHCore/docs/api-notes-1.21.1.md`. One pre-existing content gap is filed in this repo's
Inbox (`insolator_rubberwood_sapling` references items ThermalCore never registered).

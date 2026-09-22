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

## Phase 2, revised (2026-09-21)

The route is now 1.21.1 → 26.1.2 with no other intermediates, ModDevGradle from Phase 0, and
the 1.20.6 hop abandoned before this repo ever started it — see `../CoFHCore/docs/port-plan.md`
and the matching entry in `../CoFHCore/docs/progress-log.md` for what re-verification changed.
Branch `1.21.1` created today. `../ThermalExpansionForNeoForge` (SPLIGAN's 1.21.1 port of this repo) is the Phase A
worklist; `../Pyronetics` is the 26.1.2 reference. The uncommitted 1.20.6 build bump is left
uncommitted on purpose.

## Phase A.3 source sweeps (2026-09-22)

Eight commits, one per root cause, all source-level: ThermalCore does not compile yet, so
`./gradlew compileJava` still stops in `:ThermalCore:compileJava` and this repo has never
produced an error count of its own. Every API shape below was confirmed against
`neoforge-21.1.251-sources.jar` / the resolved JEI 19.57.0.446 jars, not from the fork.

- `@Mod.EventBusSubscriber` → top-level `@EventBusSubscriber`; `ConfigManager#register` now
  takes the `ModContainer` the mod constructor is handed.
- The potion JEI plugin's raw-NBT round trip → `DataComponents.POTION_CONTENTS` via CoFHCore's
  `PotionFluid.getItemFromPotionFluid`/`getPotionFluidFromItem`; potions are `Holder<Potion>`
  and `Potions.EMPTY` is gone.
- `new FluidStack(FluidStack, int)` → `copyWithAmount` (12 sites; the `(Fluid, int)` ctor stays).
- `FriendlyByteBuf#writeItem/readItem` → `saveOptional`/`parseOptional` with
  `ProxyUtils.registryAccess()`. **Not** `ItemStack.STREAM_CODEC` with a
  `(RegistryFriendlyByteBuf)` cast, which is what SPLIGAN's fork does: CoFHCore's
  `TileConfigPacket#sendToServer` builds the buffer from `Unpooled.buffer()`, so that cast
  throws. CoFHCore hit the same wall in `FluidFilterMenu#getGuiPacket`.
- Crafting recipe lookups take a `RecipeInput`: `CraftingContainer#asCraftInput()` (vanilla
  already provides it - no hand-rolled `CraftingInput.of(3, 3, …)` helper needed).
- `ItemStack#hurt(int, RandomSource, ServerPlayer)` → `hurtAndBreak(int, ServerLevel,
  ServerPlayer, Consumer<Item>)`, guarded on `level instanceof ServerLevel` rather than cast.
- Datagen providers take the registries (loot, block loot, recipes); `Tags.Items.STONE/SAND/GLASS`
  → `STONES/SANDS/GLASS_BLOCKS`, matching the `c:` names the resources sweep already emitted.
  The fork instead downgraded these to concrete `Blocks.STONE/SAND/GLASS`, losing the tag.
- `accesstransformer.cfg` replaced with CoFHCore's byte-identical 1.21.1 copy (ours was still
  SRG-named and listed deleted members, which `validateAccessTransformers` rejects).

Nothing else was needed. All 56 `net.minecraft` and 20 `net.neoforged` imports in this repo
resolve against 21.1.251; the 27 JEI files' entire API surface (`IRecipeCategory`,
`IRecipeLayoutBuilder`, `IRecipeSlotBuilder`, `IModPlugin`, the registration interfaces,
`NeoForgeTypes.FLUID_STACK`, `IRecipeManagerPlugin`) exists unchanged in 19.57.0.446 - the
only JEI file that needed work was the potion plugin, for a vanilla reason. There are no
mixins, no Curios integration, no vertex-API use and no `BlockEntity` save/load overrides in
this repo, so those categories are no-ops here.

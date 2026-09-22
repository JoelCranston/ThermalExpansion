# TODO — ThermalExpansion

Current, outstanding work only. See [progress-log.md](progress-log.md) for what's
already done and why.

## Next up

The plan for all four repos is `../CoFHCore/docs/port-plan.md`; this repo's steps are §4 (Phase 0,
per repo), §5 A.2/A.3 (1.21.1) and §6 B.10 (26.1.2).

Done: Phase 0.3 (ModDevGradle 2.0.147), 0.4 (`neoforge.mods.toml`), A.0 (1.21.1
`gradle.properties` values) and the A.1.15 resources sweep. Gradle configures; nothing
compiles yet.

1. **Blocked on CoFHCore's Phase A** (`../CoFHCore/docs/TODO.md`), then ThermalCore's.
   Nothing here compiles independently of them.
2. When unblocked: `diff -ru src ../ThermalExpansionForNeoForge/src` and apply the 1.21.1
   hunks; compile for a baseline count; fix what remains by category, reusing CoFHCore's
   confirmed shapes (`../CoFHCore/docs/api-notes-1.21.1.md`) rather than re-deriving them.
   Note SPLIGAN's fork is **not** a guide for resources — it left `data/thermal/recipes`
   plural and every `forge:` tag in place, both of which are wrong on 1.21.1.
3. Re-check `RecipeJsonUtils.parseIngredient` against 1.21.1: `Ingredient.fromJson` is gone
   since 1.20.5 (port-plan.md §5 A.3).
4. `../Pyronetics/scripts/verify_runserver.sh` passes; Joel's `runClient` check; commit.

## Inbox

- `data/thermal/recipe/machines/insolator/insolator_rubberwood_sapling.json` references
  `thermal:rubberwood_sapling` / `thermal:rubberwood_log`, which **ThermalCore does not
  register** - the only trace of rubberwood there is `FLAG_RESOURCE_RUBBERWOOD` and a
  guidebook page. Pre-existing content gap, not a port regression: on 1.20.4 the old parser
  produced an empty recipe silently, while 1.21's codec path logs a parse error every boot.
  Either add the rubberwood tree to ThermalCore or delete the recipe - a content decision.


- **`c:` tags referenced here that NeoForge 21.1.251 does not define.** Every distinct `c:`
  tag produced by the sweep was checked against `net/neoforged/neoforge/common/Tags.java` in
  `neoforge-21.1.251-sources.jar`. The six whose *name* changed (`c:glass` → `c:glass_blocks`
  and the five pluralisations) are already fixed. What is left, and why each is fine or not:
  - **Deliberately undefined, guarded.** `c:tools/{iron,gold,copper,diamond}` and
    `c:armor/{iron,gold,copper,diamond}` — the smelter/pulverizer recycling recipes. NeoForge
    defines `c:tools` and `c:armors` but subdivides them by *function* (`tools/bow`,
    `tools/shield`, …), never by material, so there is no convention name to move these to.
    Each recipe is wrapped in a `cofh_core:tag_exists` condition and simply does not load
    unless another mod supplies the tag. Correct as-is.
  - **Thermal's own / other mods' materials.** `{dusts,ingots,gears,plates,nuggets}/…` for
    signalum, lumium, enderium, invar, constantan, bronze, electrum, silver, tin, lead;
    `gems/{apatite,cinnabar,niter,sulfur}`; `ores/{apatite,cinnabar,niter,sulfur,ruby,sapphire}`;
    `c:coal_coke`, `c:crude_oil`. ThermalCore already generates `data/c/tags/{item,fluid}/…`
    for the Thermal-native ones; the rest are cross-mod recipes that no-op without the other
    mod. Nothing to invent here — if a gap shows up at runtime it belongs in ThermalCore's
    tag datagen, not in this repo.
  - **`c:storage_blocks/quartz`** — one reference (`machine_press`), and neither NeoForge nor
    ThermalCore defines it; vanilla's quartz block is not a convention "storage block".
    Probably wants `minecraft:quartz_block` directly. Needs a decision.
- **Who owns ThermalCore's shared `CONFIG_MANAGER` container?** `ConfigManager#register` takes
  a `ModContainer` now, and all three Thermal repos call it on the same static instance, so the
  field is simply overwritten by whichever mod constructor runs last - and that container is the
  one `setupServer/setupClient/setupCommon` register the specs against, which decides the config
  file's name. The 1.20.4 code had the same last-writer-wins shape via `ModLoadingContext.get()`,
  so this is not a regression, but it should be settled deliberately in ThermalCore.
- **`ThermalMachineConfig` pushes the Crystallizer section under `"Brewer"`** (a copy-paste bug
  in the 1.20.4 source, not a port issue). SPLIGAN's fork fixes it to `"Crystallizer"`. Left
  alone here because it is out of category and changes the config file's shape.
- **`MachineCrafterMenu#slotChangedCraftingGrid` calls
  `craftResult.setRecipeUsed(craftResult.getRecipeUsed())`**, which never records the recipe that
  was just found - `calcCraftingGrid` a few lines below does it correctly. Also an upstream bug,
  also fixed in SPLIGAN's fork. Out of category, left alone.
- **`pack.mcmeta` still declares `pack_format: 15`** (1.20.1) in all four repos; 1.21.1 wants 34
  for assets / 48 for data. CoFHCore's is the same, so this is a family-wide decision, not a
  ThermalExpansion one.
- **`.DS_Store` is untracked/ignorable noise across all four repos** — port-plan.md §4.2 wants
  a `.gitignore` entry. Not done here.

# Unbound / ESS jar naming convention

All shipping mod jars use **loader + Minecraft version** in the filename so multiple MC lines can coexist in one `mods` folder.

## Pattern

| Loader | Filename |
|--------|----------|
| **Forge** | `{mod_id}-{mod_version}+{minecraft_version}-forge.jar` |
| **NeoForge** | `{mod_id}-{mod_version}+{minecraft_version}-neoforge.jar` |

### Examples

- `extraspecialcore-1.3.0+1.20.1-forge.jar`
- `extraspecialcore-1.3.0+1.21.1-neoforge.jar`
- `calmtheleaks-1.2.1+1.20.1-forge.jar`
- `radiotowers-1.0.8+1.20.1-forge.jar`

## Gradle

Forge / NeoForge `build.gradle`:

```gradle
tasks.named('jar', Jar).configure {
    archiveFileName = "${mod_id}-${version}+${minecraft_version}-forge.jar"
    // NeoForge: ...-neoforge.jar
}
```

Requires `minecraft_version` in `gradle.properties` (e.g. `1.20.1`, `1.21.1`).

## Mod metadata vs jar filename

The **`+minecraft_version`** suffix is **filename only**. In `mods.toml` / `neoforge.mods.toml`:

- Your mod’s `version=` field is **semver only** (e.g. `1.2.2`, not `1.2.2+1.21.1`).
- ExtraSpecialCore dependency `versionRange=` is **semver only** (e.g. `[1.0.0,2.0)`), never `1.21.1-1.0.0` or jar-style strings.

See [ESC_DEPENDENCIES.md](ESC_DEPENDENCIES.md) for the full consumer checklist.

## Deploy

Copy **reobfuscated** jars from `build/libs/` only — not `*-sources.jar` or `*-javadoc.jar`.

When updating a test instance, **remove** older jars for the same mod (different naming scheme or version) to avoid duplicate mod IDs.

## Test instances

- **Forge 1.20.1:** `C:\Users\Ksivi\curseforge\minecraft\Instances\C.Ideas\mods`
- **NeoForge 1.21.1:** `C:\Users\Ksivi\curseforge\minecraft\Instances\NEOFORGE 1.21.1 TESTS\mods`

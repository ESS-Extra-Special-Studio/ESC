# ExtraSpecialCore — dependency metadata for consumer mods

FML compares **mod metadata versions**, not jar filenames. Mixing Minecraft version into semver fields is a common startup failure.

## Three different “version” strings

| Where | Example | MC suffix? |
|-------|---------|------------|
| **Jar filename** | `extraspecialcore-1.3.0+1.21.1-neoforge.jar` | Yes — see [JAR_NAMING.md](JAR_NAMING.md) |
| **`mods.toml` / `neoforge.mods.toml` `version=`** (your mod and ESC) | `1.3.0` | **No** — semver only |
| **ESC dependency `versionRange=`** | `[1.0.0,2.0)` | **No** — semver range only |

Jar names may include `+1.21.1` so multiple MC lines coexist in one `mods` folder. **Loader dependency checks never read the filename.**

## ESC dependency block (required pattern)

In every mod that depends on ExtraSpecialCore:

### `gradle.properties`

```properties
esc_version=1.3.0
esc_version_range=[1.0.0,2.0)
```

- `esc_version` — Gradle/Maven coordinate for dev builds and composite substitution.
- `esc_version_range` — **single source of truth** for the FML dependency range.

### Forge — `META-INF/mods.toml`

```toml
[[dependencies.${mod_id}]]
modId = "extraspecialcore"
mandatory = true
versionRange = "${esc_version_range}"
ordering = "AFTER"
side = "BOTH"
```

### NeoForge — `src/main/templates/META-INF/neoforge.mods.toml`

```toml
[[dependencies.${mod_id}]]
modId="extraspecialcore"
type="required"
versionRange="${esc_version_range}"
ordering="AFTER"
side="BOTH"
```

Wire `esc_version_range` into `processResources` / `generateModMetadata` `replaceProperties` (see Not Another Guide Book or Calm The Leaks CTL NeoForge).

## Never do this

```toml
# WRONG — MC prefix in semver range (NeoForge ESC reports 1.2.x, not 1.21.1-1.x)
versionRange="[1.21.1-1.0.0,2.0)"

# WRONG — jar-style version in metadata
version="1.2.2+1.21.1"

# WRONG — hardcoded range duplicated in mods.toml instead of ${esc_version_range}
versionRange="[1.2.0,2.0)"
```

The first form caused [CTL] Calm The Leaks to abort on NeoForge 1.21.1 when ESC declared `version="1.2.1"`.

## Recommended range

Use **`[1.0.0,2.0)`** for the FML dependency so any 1.x ESC loads. Feature requirements (anchor UI, wrapped text, etc.) are enforced by **`esc_version`** in Gradle and release notes — bump `esc_version` when you rely on new ESC APIs.

## Checklist before publishing a consumer mod

1. `esc_version_range` in `gradle.properties` — semver only, no MC prefix.
2. `mods.toml` / `neoforge.mods.toml` uses `${esc_version_range}`, not a literal.
3. `build.gradle` expands `esc_version_range` in resource processing.
4. Built jar: open `META-INF/mods.toml` or `neoforge.mods.toml` and confirm the expanded range is e.g. `[1.0.0,2.0)`, not `[1.21.1-…`.
5. Your mod’s own `version="${mod_version}"` is semver only (no `+1.21.1`).

## Canonical consumer projects

Reference implementations:

- **Forge:** `Not Another Guide Book` — `${esc_version_range}` in `mods.toml`
- **NeoForge:** `Calm The Leaks CTL - 1.21.1 NeoForge` — template + `generateModMetadata`

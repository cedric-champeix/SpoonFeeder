---
name: spoonfeeder-brand-design
description: Use when designing, implementing, or reviewing any UI in the Spoonfeeder (WeeklyRecipes) app — colors, typography, components, mascot, copy, spacing. Translates the Spoonfeeder brand guidelines into Compose Multiplatform terms. Authoritative source for visual and tonal consistency.
metadata:
  version: "2.0.0"
  source: Spoonfeeder_DesignSystem.pdf v1.0
---

# Spoonfeeder — Brand Design

Use this skill whenever you:

- add or modify a composable that renders user-visible UI
- pick a color, font, radius, spacing value, or icon
- write user-facing copy (labels, empty states, errors, onboarding)
- introduce or restyle a screen, card, chip, button, divider, or input
- add assets involving the mascot "Spoony" or the logo

This skill is the **single source of truth for Spoonfeeder brand decisions in code**. It overrides generic Material 3 defaults wherever there is a conflict. The design system PDF (`Spoonfeeder_DesignSystem.pdf`, v1.0) is translated here into Compose Multiplatform (`Color`, `dp`, `FontFamily`, `Shape`) so contributors apply it correctly in Kotlin.

---

## 1. Brand identity

- **Name:** Spoonfeeder
- **Tagline:** Meal planning, made easy for everyone.
- **Feeling:** Warm, friendly, effortless. We spoon-feed the answer so users don't have to decide.
- **Tone of voice pillars:** Friendly · Simple · Inclusive · Playful

Every visual and copy decision should reinforce *warm + effortless*. If a change makes the UI feel clinical, pressured, or corporate, revise it.

---

## 2. Color tokens (Compose)

Define brand colors in one place (`ui/theme/SpoonfeederColors.kt`) and reference them from composables. **Never hardcode hex values inside composables.**

### Light palette (canonical)

```kotlin
object SpoonfeederColors {
    // Meal & brand accents — identical across light and dark
    val Butter    = Color(0xFFF7E4A0) // Breakfast chip, butter accents
    val Peach     = Color(0xFFF5C8A8) // Dinner chip, mascot, recipe detail accent
    val Blush     = Color(0xFFF2A99A) // Snack chip, tone-of-voice accent
    val Mint      = Color(0xFFB8DDD5) // Lunch chip, Recipes list accent, mascot card bg
    val Sage      = Color(0xFFA8C5A0) // Fresh accents, tags
    val Clay      = Color(0xFFD4856A) // Spoon in mascot, dark-theme primary
    val Cocoa     = Color(0xFF6B3F2A) // Light-theme primary, logo text

    // Light surfaces & text
    val Cream     = Color(0xFFFDF6EE) // App background, card background
    val SoftGray  = Color(0xFFE8E0D8) // Dividers, borders
    val Text      = Color(0xFF3A2518) // Body text
    val TextLight = Color(0xFF8A6A58) // Labels, secondary text, hints

    // Dark surfaces & text (design system page 4)
    val Void       = Color(0xFF120A04) // Scrim, deepest surface
    val DarkBg     = Color(0xFF1C1008) // Dark screen background
    val Surface    = Color(0xFF2A1A0E) // Dark card / sheet background
    val Raised     = Color(0xFF3A2518) // Dark elevated surface
    val Fg2        = Color(0xFFC4A090) // Dark secondary text
    val DarkBorder = Color(0x17FDF6EE) // ~9% Cream — dark outline/divider
}
```

### Mapping to Material 3 `ColorScheme`

Wire tokens into both `lightColorScheme(...)` and `darkColorScheme(...)` in `SpoonfeederTheme`. Use `isSystemInDarkTheme()` by default, but allow callers to force a scheme via a `darkTheme: Boolean` parameter.

| M3 role              | Light token | Dark token        |
|----------------------|-------------|-------------------|
| `primary`            | `Cocoa`     | `Clay`            |
| `onPrimary`          | `Cream`     | `Cream`           |
| `primaryContainer`   | `Butter`    | `Raised`          |
| `onPrimaryContainer` | `Cocoa`     | `Cream`           |
| `secondary`          | `Mint`      | `Mint`            |
| `onSecondary`        | `Cocoa`     | `Cocoa`           |
| `tertiary`           | `Peach`     | `Peach`           |
| `onTertiary`         | `Cocoa`     | `Cocoa`           |
| `background`         | `Cream`     | `DarkBg`          |
| `onBackground`       | `Text`      | `Cream`           |
| `surface`            | `Cream`     | `Surface`         |
| `onSurface`          | `Text`      | `Cream`           |
| `surfaceVariant`     | `SoftGray`  | `Raised`          |
| `onSurfaceVariant`   | `TextLight` | `Fg2`             |
| `surfaceTint`        | `Cocoa`     | `Clay`            |
| `outline`            | `SoftGray`  | `DarkBorder`      |
| `scrim`              | (default)   | `Void`            |

### Meal type → color mapping (canonical, unchanged on dark)

Meal colors are **identical on light and dark**. Meal-tinted surfaces keep pastel backgrounds with `Cocoa` text/icon in both themes — do not swap them for dark variants.

| `MealType` | Color token |
|------------|-------------|
| `BREAKFAST`| `Butter`    |
| `LUNCH`    | `Mint`      |
| `DINNER`   | `Peach`     |
| `SNACK`    | `Blush`     |

Implement once as a helper:

```kotlin
fun MealType.brandColor(): Color = when (this) {
    MealType.BREAKFAST -> SpoonfeederColors.Butter
    MealType.LUNCH     -> SpoonfeederColors.Mint
    MealType.DINNER    -> SpoonfeederColors.Peach
    MealType.SNACK     -> SpoonfeederColors.Blush
}
```

Use this helper in chips, meal cards, and any meal-tagged UI. Do **not** rotate or randomize meal colors per index.

### Color rules

- Never introduce a new color outside this palette without explicit approval.
- For generic list/ingredient chips, use `colorScheme.surfaceVariant` + `onSurface` — not a rainbow palette.
- Never use `colorScheme.error` red tones in empty states or tips — error red is only for validation failures.
- Default surface for a new screen: `colorScheme.background` + `colorScheme.onBackground`, never raw `Cream`/`Text`, so the dark theme follows automatically.
- Highlighted/active states: the relevant meal color, or `Mint` as a neutral positive accent.

---

## 3. Typography

Two fonts, no substitutes.

| Font        | Role                    | File in `composeResources/font/`                                                |
|-------------|-------------------------|---------------------------------------------------------------------------------|
| **Fredoka** | Display / headings / titles / meal names | `Fredoka-Regular.ttf`                                      |
| **Nunito**  | Body / labels / buttons | `Nunito-Regular.ttf`, `Nunito-SemiBold.ttf`, `Nunito-Bold.ttf`, `Nunito-ExtraBold.ttf` |

Load via Compose Multiplatform resources (`Res.font.Fredoka_Regular`, etc.). Never fall back to a system font at runtime.

### Canonical type scale (design system page 2)

Wire these into `MaterialTheme.typography` so callers use semantic roles (`headlineLarge`, `bodyMedium`, `labelSmall`, …) and land on-brand by default.

| Material role     | Font    | Size   | Weight     | Extra          |
|-------------------|---------|--------|------------|----------------|
| `displayLarge`    | Fredoka | 72.sp  | Normal     | — (splash only)|
| `headlineLarge`   | Fredoka | 36.sp  | Normal     | screen titles  |
| `headlineMedium`  | Fredoka | 32.sp  | Normal     | screen titles  |
| `headlineSmall`   | Fredoka | 26.sp  | Normal     | recipe names (detail) |
| `titleLarge`      | Fredoka | 24.sp  | Normal     | form section titles |
| `titleMedium`     | Fredoka | 18.sp  | Normal     | card titles, day headers |
| `titleSmall`      | Fredoka | 14.sp  | Normal     | sub-labels     |
| `bodyLarge`       | Nunito  | 16.sp  | Normal (400) | primary body |
| `bodyMedium`      | Nunito  | 14.sp  | Normal (400) | secondary body |
| `bodySmall`       | Nunito  | 12.sp  | Normal (400) | hints, captions |
| `labelLarge`      | Nunito  | 14.sp  | ExtraBold (800) | button labels, meal-chip recipe names |
| `labelMedium`     | Nunito  | 12.sp  | Bold (700) | meal-type chip labels |
| `labelSmall`      | Nunito  | 11.sp  | ExtraBold (800) | section labels — `letterSpacing = 3.sp` |

Reserve direct `TextStyle` construction for the rare case where the scale above doesn't fit. In composables, **prefer `style = MaterialTheme.typography.X` with no `fontWeight`/`fontFamily` overrides** — if a weight is wrong, fix the typography definition, not the call site.

### Typography rules

- Fredoka only for: displays, headlines, titles, meal names, logo wordmark.
- Nunito only for: body text, labels, button text, hints, placeholders.
- Section labels (`labelSmall` usage) are uppercase with `letterSpacing = 3.sp`.
- Never use a Nunito weight below `Normal` (400). The only loaded weights are Regular, SemiBold, Bold, ExtraBold — don't reference `FontWeight.Medium`, it won't load.
- Never substitute the branded fonts with a system font, even temporarily.

---

## 4. Shapes, radii, spacing, shadows

Rounded everywhere. No sharp corners anywhere in the UI.

### Border radii (exposed via `SpoonfeederRadii` and `MaterialTheme.shapes`)

| Surface            | Token                    | Value            | `Shapes` role  |
|--------------------|--------------------------|------------------|----------------|
| Inputs             | `SpoonfeederRadii.Input` | 12.dp            | `extraSmall`   |
| Chips              | `SpoonfeederRadii.Chip`  | 14.dp            | `small`        |
| Buttons            | `SpoonfeederRadii.Button`| 20.dp            | `medium`       |
| Cards / sheets     | `SpoonfeederRadii.Card`  | 24.dp            | `large`, `extraLarge` |
| Pill (search, chip tags) | `SpoonfeederRadii.Pill` | `RoundedCornerShape(50)` | — |

Dividers: `HorizontalDivider(thickness = 1.dp, color = colorScheme.outline)` — radius N/A.

### Spacing scale (base 8.dp)

The brand defines six spacing steps. Prefer these over arbitrary values.

| Token        | Value   | Typical use                      |
|--------------|---------|----------------------------------|
| `--space-1`  | 8.dp    | Tight inner padding, icon↔label  |
| `--space-2`  | 16.dp   | Default screen horizontal padding, card inner padding |
| `--space-3`  | 24.dp   | Gap between cards, generous card padding |
| `--space-4`  | 32.dp   | Section inner padding             |
| `--space-5`  | 40.dp   | Section vertical margin           |
| `--space-6`  | 48.dp   | Hero spacing, top-of-screen breathing room |

Any padding or gap should be a multiple of `8.dp`. If you need a value that isn't, justify it in a code comment.

### Shadow system (three tiers)

| Tier     | Usage                      | Compose |
|----------|----------------------------|---------|
| **Card** | Default cards              | `Modifier.shadow(elevation = 4.dp, shape = SpoonfeederRadii.Card, ambientColor = Color(0x123A2518), spotColor = Color(0x123A2518))` — equivalent to `0 4px 24px rgba(58,37,24, 0.07)` |
| **Elevated** | Modals, dialogs, FABs, elevated sheets | Use `0 8px 32px rgba(58,37,24, 0.12)` — `Modifier.shadow(elevation = 8.dp, shape = SpoonfeederRadii.Card, ambientColor = Color(0x1F3A2518), spotColor = Color(0x1F3A2518))` |
| **Flat** | Lists of items that shouldn't appear to float | No shadow; 1.dp `colorScheme.outline` border instead |

Do not use default M3 elevation tint — it adds a cool-toned overlay that fights the warm palette.

---

## 5. Components — how to implement

### Primary button

```kotlin
Button(
    onClick = ...,
    shape = SpoonfeederRadii.Button,
    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 10.dp),
) {
    Text("Save", style = MaterialTheme.typography.labelLarge) // Nunito ExtraBold 14
}
```

`Button` picks up `primary` / `onPrimary` from the scheme automatically — Cocoa/Cream on light, Clay/Cream on dark. Don't override unless the design calls for it.

### Secondary / outlined button

`OutlinedButton` with default content/border colors from the scheme, `shape = SpoonfeederRadii.Button`.

### Meal chip (weekly plan)

- Shape: `SpoonfeederRadii.Chip` (14.dp)
- Background: `mealType.brandColor()` — same on both themes
- Meal-type mini-label: `labelMedium` (Nunito Bold 12), `Cocoa.copy(alpha = 0.7f)`
- Recipe name: `labelLarge` (Nunito ExtraBold 14), `Cocoa`
- Time label: `labelSmall` (Nunito ExtraBold 11, 3sp letter-spacing), `Cocoa.copy(alpha = 0.7f)`
- Padding: `start = 12.dp, top = 8.dp, bottom = 8.dp, end = 4.dp`

### Filter chip / meal-type selector

Use Material 3 `FilterChip`. Selected: `Cocoa` container + `Cream` text (light) or `Clay` container (dark). Unselected: outline only. Shape from `SpoonfeederRadii.Chip`.

### Ingredient chip (removable)

Single brand-consistent pill — never rainbow-coded per index.

```kotlin
InputChip(
    selected = false,
    onClick = { onRemove(index) },
    shape = SpoonfeederRadii.Pill,
    colors = InputChipDefaults.inputChipColors(
        containerColor = MaterialTheme.colorScheme.surfaceVariant,
        labelColor = MaterialTheme.colorScheme.onSurface,
        trailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
        selectedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
    ),
    label = { Text(item, style = MaterialTheme.typography.bodyMedium) },
    trailingIcon = { Icon(Icons.Default.Close, contentDescription = "Remove $item") },
)
```

### Section label

```kotlin
Text(
    text = "INGREDIENTS",
    style = MaterialTheme.typography.labelSmall, // Nunito ExtraBold 11, 3sp ls
    color = MaterialTheme.colorScheme.onSurfaceVariant,
)
```

### Divider

```kotlin
HorizontalDivider(
    thickness = 1.dp,
    color = MaterialTheme.colorScheme.outline,
)
```

### Search field (pill)

```kotlin
OutlinedTextField(
    value = query,
    onValueChange = { query = it },
    placeholder = { Text("Search recipes…") },
    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
    singleLine = true,
    shape = SpoonfeederRadii.Pill,
    modifier = Modifier.fillMaxWidth(),
)
```

### Standard text input

Default `OutlinedTextField` with `shape = SpoonfeederRadii.Input` (12.dp). Error state: let Material's `isError` drive the red outline — don't hand-roll error coloring. Error supporting text uses `bodySmall`.

---

## 6. Mascot — Spoony

Spoony is an abstract bowl + spoon character. Warm, round, cheerful.

### Colors (must match)

- Bowl: `Butter` or `Peach`
- Spoon: `Clay`
- Cheeks: `Blush`
- Eyes / line work: `Cocoa`
- Background card behind Spoony: `Mint`

### Rules

- Use Spoony in: onboarding, empty states, celebratory moments ("week fully planned").
- **Never** use Spoony in: error states, warnings, validation failures, destructive confirmations.
- Never recolor, rotate, stretch, or distort Spoony beyond the approved palette.
- Minimum render size: `32.dp` square.

If an empty state is mildly negative ("No recipes match your search"), Spoony is optional — prefer a neutral icon. Spoony appears when the moment is *encouraging*, not *apologetic*.

---

## 7. Logo usage

Icon (Spoony) + wordmark ("Spoonfeeder" in Fredoka) travel together. Both `logo_light` and `logo_dark` drawable variants exist in `composeResources/drawable/`.

| Variant | Background | Icon                 | Wordmark |
|---------|------------|----------------------|----------|
| Light   | `Cream`    | Standard palette     | `Cocoa`  |
| Dark    | `DarkBg`   | Standard palette     | `Cream`  |
| Peach   | `Peach`    | Muted brown tones    | `Cocoa`  |

In composables that render the logo, pick the variant reactively:

```kotlin
val logo = if (isSystemInDarkTheme()) Res.drawable.logo_dark else Res.drawable.logo_light
```

The same rule applies to `empty_state_no_recipes_*`, `empty_state_no_meals_*`, `onboarding_*`, `splash_*`, and `icon_*` drawables.

Clear space around the logo: at least the height of the capital "S" in the wordmark. Icon minimum size: `32.dp`. Never present the wordmark alone in primary contexts (splash, app header, onboarding) — pair with the icon.

---

## 8. Screen-level accent colors

Each main screen has a recommended accent:

| Screen            | Accent                                         |
|-------------------|------------------------------------------------|
| Weekly plan       | Mix of all meal colors per row (`mealType.brandColor()`) |
| Recipes list      | `Mint`                                         |
| Add / edit recipe | `Butter`                                       |
| Recipe detail     | `Peach`                                        |
| Settings          | `SoftGray` (light) / `Raised` (dark)           |
| Onboarding        | `Mint` + Spoony                                |
| Pick recipe       | Meal color of the target slot                  |

The accent typically appears on: the hero element, an active tab underline, section dividers' decoration, or a top-of-screen band. It should **not** flood an entire screen — the theme's `background` stays dominant.

---

## 9. Tone of voice

Four principles apply to every user-facing string — labels, placeholders, empty states, onboarding, errors, notifications, confirmations.

| Principle    | What it means                                              | Example                                     |
|--------------|------------------------------------------------------------|---------------------------------------------|
| **Friendly** | Helpful friend, not a nutrition coach                      | "Here's what you could make tonight 👋"     |
| **Simple**   | No jargon, no guilt. Short sentences.                      | "No recipes yet. Add one to get started."   |
| **Inclusive**| Any diet, skill level, household size                      | "Whatever you eat, we've got you."          |
| **Playful**  | Light humour, warm energy, sparing emoji                   | "Looks like your week is empty — let's fix that 🍽️" |

### Copy rules

- No guilt, urgency, or pressure. Never "You haven't planned this week yet!".
- No nutritional or technical jargon in default flows.
- Empty states are warm and encouraging, never cold.
- Error messages are short, human, and propose a next step. Example: "That didn't save — check your connection and try again."
- Exclamation marks: only in celebratory moments. Not on errors, not on confirmations.
- Emoji: at most one per message. Skip them entirely in error/destructive copy.
- Prefer contractions ("we've", "let's", "it's") — they read warmer.

### Placeholder and label examples

Good: "Recipe name", "Search recipes…", "Add an ingredient"
Avoid: "Enter recipe name *", "SEARCH", "INGREDIENT INPUT"

---

## 10. Do / Don't — quick reference

### ✅ Do
- Reference `SpoonfeederColors.*` tokens; never raw hex inside composables.
- Prefer `MaterialTheme.colorScheme.*` over raw tokens when the value should switch with theme (surfaces, text, outlines).
- Use Fredoka for titles/meal names, Nunito for everything else, via `MaterialTheme.typography.*` roles.
- Apply meal-type color coding via `mealType.brandColor()` — same on light and dark.
- Round every corner — pick from `SpoonfeederRadii` (`Input`/`Chip`/`Button`/`Card`/`Pill`).
- Keep spacing on the 8-dp scale.
- Swap `_light` / `_dark` drawable variants using `isSystemInDarkTheme()`.
- Keep Spoony positive.
- Write copy that reads like a friend talking.

### ❌ Don't
- Introduce colors outside the palette.
- Substitute system fonts for Fredoka or Nunito.
- Reference `FontWeight.Medium` for Nunito — only Regular/SemiBold/Bold/ExtraBold are loaded.
- Override `fontWeight`/`fontFamily` on a theme typography role — fix the theme, not the call site.
- Show Spoony in error, warning, or destructive confirmation surfaces.
- Use sharp corners (0.dp radius) anywhere.
- Rotate/randomize meal colors — the mapping is canonical.
- Use a rainbow palette for ingredient/generic chips — they share one neutral style.
- Write clinical, urgent, or judgmental copy.
- Stack exclamation marks or emoji.
- Hardcode `Cream`/`Cocoa` for surface/text on a card that should follow the theme — use `colorScheme.surface`/`onSurface`.

---

## 11. Review checklist

When reviewing a PR or a new screen, confirm:

- [ ] No raw hex values in composables — all colors come from `SpoonfeederColors` or `MaterialTheme.colorScheme`.
- [ ] Surface/text colors use `colorScheme.*` (not raw `Cream`/`Text`) so dark theme follows for free.
- [ ] All text uses either Fredoka (titles, meal names) or Nunito (everything else), via `MaterialTheme.typography.*`.
- [ ] No stray `fontWeight` / `fontFamily` overrides on theme typography roles.
- [ ] All corners are rounded; radii come from `SpoonfeederRadii`.
- [ ] Spacing values are multiples of `8.dp` unless explicitly justified.
- [ ] Meal-type colors follow the canonical mapping via `brandColor()` and stay pastel on dark.
- [ ] Ingredient/generic chips use the single brand style, not a multicolor palette.
- [ ] `_light` / `_dark` drawable variants swap on theme.
- [ ] Copy passes the tone test: friendly, simple, inclusive, playful.
- [ ] Spoony (if present) is in an encouraging context and uses approved colors.
- [ ] Cards use `SpoonfeederRadii.Card` + warm shadow (`Card` tier), not default M3 elevation tint.
- [ ] Dark-mode rendering checked on every surface the PR touches (not just light).

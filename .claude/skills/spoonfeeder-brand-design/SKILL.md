---
name: spoonfeeder-brand-design
description: Use when designing, implementing, or reviewing any UI in the Spoonfeeder (WeeklyRecipes) app — colors, typography, components, mascot, copy, spacing. Translates the Spoonfeeder brand guidelines into Compose Multiplatform terms. Authoritative source for visual and tonal consistency.
metadata:
  version: "1.0.0"
---

# Spoonfeeder — Brand Design

Use this skill whenever you:

- add or modify a composable that renders user-visible UI
- pick a color, font, radius, spacing value, or icon
- write user-facing copy (labels, empty states, errors, onboarding)
- introduce or restyle a screen, card, chip, button, divider, or input
- add assets involving the mascot "Spoony" or the logo

This skill is the **single source of truth for Spoonfeeder brand decisions in code**. It overrides generic Material 3 defaults wherever there is a conflict. The web-oriented brand guidelines (CSS variables) are translated here into Compose Multiplatform (`Color`, `dp`, `FontFamily`, `Shape`) so contributors apply them correctly in Kotlin.

---

## 1. Brand identity

- **Name:** Spoonfeeder
- **Tagline:** Meal planning, made easy for everyone.
- **Feeling:** Warm, friendly, effortless. We spoon-feed the answer so users don't have to decide.

Every visual and copy decision should reinforce *warm + effortless*. If a change makes the UI feel clinical, pressured, or corporate, revise it.

---

## 2. Color tokens (Compose)

Define brand colors in one place (e.g. `ui/theme/SpoonfeederColors.kt`) and reference them from composables. **Never hardcode hex values inside composables.**

```kotlin
object SpoonfeederColors {
    val Butter    = Color(0xFFF7E4A0) // Breakfast chip, butter accents
    val Peach     = Color(0xFFF5C8A8) // Dinner chip, mascot, recipe detail accent
    val Blush     = Color(0xFFF2A99A) // Snack chip, tone-of-voice accent
    val Sage      = Color(0xFFA8C5A0) // Fresh accents, tags
    val Mint      = Color(0xFFB8DDD5) // Lunch chip, Recipes list accent, mascot card bg
    val Cocoa     = Color(0xFF6B3F2A) // Primary button, dark surface, logo text
    val Clay      = Color(0xFFD4856A) // Spoon in mascot, accent on icons
    val Cream     = Color(0xFFFDF6EE) // App background, card background
    val SoftGray  = Color(0xFFE8E0D8) // Dividers, borders
    val Text      = Color(0xFF3A2518) // Body text
    val TextLight = Color(0xFF8A6A58) // Labels, secondary text, hints
}
```

### Mapping to Material 3 ColorScheme

When building the app theme, wire tokens into `lightColorScheme(...)` like this:

| M3 role           | Spoonfeeder token | Notes |
|-------------------|-------------------|-------|
| `background`      | `Cream`           | Screen background |
| `surface`         | `Cream`           | Card, sheet background |
| `onBackground`    | `Text`            | Body text on background |
| `onSurface`       | `Text`            | Body text on cards |
| `onSurfaceVariant`| `TextLight`       | Labels, hints |
| `primary`         | `Cocoa`           | Primary buttons, active states |
| `onPrimary`       | `Cream`           | Text on primary buttons |
| `secondary`       | `Mint`            | Neutral positive accents |
| `outline`         | `SoftGray`        | Dividers, outlined text field borders |

A dark scheme is **not** part of the brand yet — do not introduce one speculatively.

### Meal type → color mapping (canonical)

This mapping is strict. Every meal-type-colored UI surface must respect it.

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

Use this helper in chips, meal cards, and any meal-tagged UI. Do **not** rotate or randomize meal colors per index (the existing `chipPalette` in `EditableChipList.kt` is not brand-conformant for meal chips — it's fine for generic list items like ingredients, but never use it for `MealType` display).

### Color rules

- Never introduce a new color outside this palette without explicit approval.
- Never use `MaterialTheme.colorScheme.error` red tones in empty states or tips — error red is only for validation failures.
- Default surface for a new screen: `Cream` background, `Text` foreground.
- Highlighted/active states: the relevant meal color, or `Mint` as a neutral positive accent.

---

## 3. Typography

Two fonts, no substitutes.

| Font          | Role                     | Compose `FontFamily` |
|---------------|--------------------------|----------------------|
| **Fredoka One** | Display / headings     | `FontFamily(Font(Res.font.fredoka_one))` |
| **Nunito**      | Body / UI              | `FontFamily(Font(Res.font.nunito_regular, FontWeight.Normal), Font(Res.font.nunito_semibold, FontWeight.SemiBold), Font(Res.font.nunito_bold, FontWeight.Bold), Font(Res.font.nunito_extrabold, FontWeight.ExtraBold))` |

Place fonts under `composeApp/src/commonMain/composeResources/font/`. Load via Compose Multiplatform resources. Never fall back to a system font at runtime.

### Type scale (sp, not px — brand px values map 1:1 to sp for mobile)

| Level              | Font         | Size  | Weight       | Color      |
|--------------------|--------------|-------|--------------|------------|
| App title          | Fredoka One  | 72.sp | —            | `Cocoa`    |
| Screen heading     | Fredoka One  | 32–52.sp | —         | `Cocoa`    |
| Card title         | Fredoka One  | 22–28.sp | —         | `Cocoa`    |
| Meal name (chip)   | Fredoka One  | 14.sp | —            | `Cocoa`    |
| Body text          | Nunito       | 16.sp | `Normal`     | `Text`     |
| UI label           | Nunito       | 13–14.sp | `Bold`    | `TextLight`|
| Section label      | Nunito       | 10–11.sp | `ExtraBold`, uppercase, `letterSpacing = 3.sp` | `TextLight`|
| Timestamp / hint   | Nunito       | 11.sp | `Bold`       | `TextLight`|

Wire these into `MaterialTheme.typography` so callers can use `titleLarge`, `bodyLarge`, `labelSmall`, etc., and they land on-brand by default. Reserve direct `TextStyle` construction for the rare case where the scale above doesn't fit.

### Typography rules

- Fredoka One only. Always. For: app name, screen titles, card titles, meal names.
- Nunito only for everything else.
- Section labels are always uppercase (`text.uppercase()` or the `Text` parameter), with letter spacing.
- Never use Nunito weight below `Normal` (400).
- Never substitute the branded fonts with a system font, even temporarily.

---

## 4. Shapes, radii, spacing

Rounded everywhere. No sharp corners anywhere in the UI.

| Surface            | `Shape`                              |
|--------------------|--------------------------------------|
| Cards              | `RoundedCornerShape(24.dp)`          |
| Chips (generic)    | `RoundedCornerShape(14.dp)`          |
| Meal chips         | `RoundedCornerShape(14.dp)`          |
| Buttons            | `RoundedCornerShape(20.dp)`          |
| Inputs (`OutlinedTextField`) | `RoundedCornerShape(12.dp)` by default; use `RoundedCornerShape(50)` (pill) for search fields |
| Dividers           | n/a (height = 1.dp, color = `SoftGray`) |

### Spacing tokens

| Token                    | Value  |
|--------------------------|--------|
| Base unit                | 8.dp   |
| Card padding             | 32.dp  |
| Screen horizontal padding| 16–24.dp |
| Gap between cards        | 24.dp  |
| Section margin (vertical)| 40.dp  |

Prefer multiples of the base unit (`8.dp`) for any new padding or spacing. If you need a value that isn't a multiple of 8, justify it in a code comment.

### Card elevation / shadow

Cards use a soft warm shadow, not default M3 elevation tint:

```kotlin
Modifier.shadow(
    elevation = 4.dp,
    shape = RoundedCornerShape(24.dp),
    ambientColor = Color(0x123A2518),
    spotColor = Color(0x123A2518),
)
```

Equivalent to the brand's `0 4px 24px rgba(58, 37, 24, 0.07)`.

---

## 5. Components — how to implement

### Primary button

```kotlin
Button(
    onClick = ...,
    colors = ButtonDefaults.buttonColors(
        containerColor = SpoonfeederColors.Cocoa,
        contentColor = SpoonfeederColors.Cream,
    ),
    shape = RoundedCornerShape(20.dp),
    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 10.dp),
) {
    Text("Save", fontWeight = FontWeight.ExtraBold) // Nunito ExtraBold
}
```

### Secondary / outlined button

`OutlinedButton` with `Cocoa` content and `SoftGray` border, same shape.

### Meal chip

- Shape: `RoundedCornerShape(14.dp)`
- Background: `mealType.brandColor()`
- Meal name: Fredoka One 14.sp, `Cocoa`
- Time label: Nunito 11.sp Bold, `TextLight`
- Padding: `horizontal = 14.dp, vertical = 12.dp`
- Horizontal gap between icon/avatar and label: `10.dp`

### Section label

```kotlin
Text(
    text = "INGREDIENTS",
    style = TextStyle(
        fontFamily = NunitoFamily,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 11.sp,
        letterSpacing = 3.sp,
        color = SpoonfeederColors.TextLight,
    ),
)
```

### Divider

```kotlin
HorizontalDivider(
    thickness = 1.dp,
    color = SpoonfeederColors.SoftGray,
    modifier = Modifier.padding(vertical = 40.dp),
)
```

### Search field (pill)

Keep the current pill style consistent across screens (`AllRecipesScreen`, `PickRecipeScreen`):

```kotlin
OutlinedTextField(
    value = query,
    onValueChange = { query = it },
    placeholder = { Text("Search recipes…") },
    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
    singleLine = true,
    shape = RoundedCornerShape(50),
    modifier = Modifier.fillMaxWidth(),
)
```

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

- Use Spoony in: onboarding, empty states, celebratory moments (e.g. "week fully planned").
- **Never** use Spoony in: error states, warnings, validation failures, destructive confirmations.
- Never recolor, rotate, stretch, or distort Spoony beyond the approved palette.
- Minimum render size: `32.dp` square.

If an empty state is mildly negative (e.g. "No recipes match your search"), Spoony is optional — prefer a neutral icon. Spoony appears when the moment is *encouraging*, not *apologetic*.

---

## 7. Logo usage

Icon (Spoony) + wordmark ("Spoonfeeder" in Fredoka One) travel together.

| Variant | Background | Icon                 | Wordmark |
|---------|------------|----------------------|----------|
| Light   | `Cream`    | Standard palette     | `Cocoa`  |
| Dark    | `Cocoa`    | Muted with opacity   | `Cream`  |
| Peach   | `Peach`    | Muted brown tones    | `Cocoa`  |

Clear space around the logo: at least the height of the capital "S" in the wordmark. Icon minimum size: `32.dp`. Never present the wordmark alone in primary contexts (splash, app header, onboarding) — pair with the icon.

---

## 8. Screen-level accent colors

Each main screen has a recommended accent:

| Screen          | Accent                                         |
|-----------------|------------------------------------------------|
| Weekly plan     | Mix of all meal colors per row (use `mealType.brandColor()`) |
| Recipes list    | `Mint`                                         |
| Add / edit recipe | `Butter`                                    |
| Recipe detail   | `Peach`                                        |
| Settings        | `SoftGray`                                     |
| Onboarding      | `Mint` + Spoony                                |
| Pick recipe     | Meal color of the target slot (`mealType.brandColor()`) |

The accent typically appears on: the hero element, an active tab underline, section dividers' decoration, or a top-of-screen band. It should **not** flood an entire screen — `Cream` stays dominant.

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
- No nutritional or technical jargon in default flows. If a user opts into a pro setting, jargon there is fine.
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
- Use Fredoka One for all titles and meal names.
- Use Nunito for everything else.
- Apply meal-type color coding via `mealType.brandColor()`.
- Round every corner (min `12.dp`, typically `14–24.dp`).
- Keep Spoony positive.
- Write copy that reads like a friend talking.

### ❌ Don't
- Introduce colors outside the palette.
- Substitute system fonts for Fredoka One or Nunito.
- Show Spoony in error, warning, or destructive confirmation surfaces.
- Use sharp corners (0.dp radius) anywhere.
- Rotate/randomize meal colors — the mapping is canonical.
- Write clinical, urgent, or judgmental copy.
- Stack exclamation marks or emoji.

---

## 11. Review checklist

When reviewing a PR or a new screen, confirm:

- [ ] No raw hex values in composables — all colors come from `SpoonfeederColors`.
- [ ] All text uses either Fredoka One (titles, meal names) or Nunito (everything else), via theme typography.
- [ ] All corners are rounded; radii come from the shapes table.
- [ ] Spacing values are multiples of `8.dp` unless explicitly justified.
- [ ] Meal-type colors follow the canonical mapping via a shared helper.
- [ ] Copy passes the tone test: friendly, simple, inclusive, playful.
- [ ] Spoony (if present) is in an encouraging context and uses approved colors.
- [ ] Cards use the 24.dp radius + soft warm shadow, not default M3 elevation.
- [ ] No new dark-mode assumptions — brand only defines the light scheme today.

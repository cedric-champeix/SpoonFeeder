# Weekly Recipes — Contribution Guidelines

These guidelines define *how* we write code in this project. They complement [CLAUDE.md](.claude/CLAUDE.md), which documents *what* the project is and *how it is organized*.

When these rules conflict with code you encounter, the code is wrong — not the rules. Fix the code or challenge the rule, but do not silently drift.

---

## 1. Philosophy

- **Compose-first.** We are a Compose Multiplatform app. Always reach for idiomatic Compose + Kotlin patterns before introducing abstractions or third-party libraries.
- **Common code by default.** Anything that can live in `commonMain` must live in `commonMain`. `androidMain` and `iosMain` only contain what truly differs.
- **Prepare for persistence.** Even today (in-memory storage), the code is structured so that swapping `InMemoryRecipeRepository` for a `SqlDelightRecipeRepository` is a one-line change in DI wiring.
- **Immutability by default.** Domain models are `data class`es with `val` fields. Lists are read-only `List<T>`.

---

## 2. State Management

### Rules
- **A `ViewModel` exposes `StateFlow<T>` only.** Never expose `mutableStateOf` from a ViewModel.
- **Local UI state** (`TextField` value, expansion state, current step index, checkbox state) lives in the composable via `remember { mutableStateOf(...) }`.
- **Complex form state** goes into a dedicated `@Stable` holder class plus a `remember{State}()` factory. See [RecipeFormState.kt](composeApp/src/commonMain/kotlin/com/champeic/weeklyrecipes/ui/state/RecipeFormState.kt) as the reference.
- Consume `StateFlow` from Compose via `viewModel.someFlow.collectAsStateWithLifecycle()`.

### Why
`StateFlow` plays well with coroutines (database reads, network), survives recomposition naturally, and is the pattern that every modern Compose sample uses. `mutableStateOf` in a ViewModel works today but traps you when you add suspending operations.

### Good
```kotlin
class RecipeViewModel(repository: RecipeRepository) : ViewModel() {
    val recipes: StateFlow<List<Recipe>> = repository.recipes
    fun add(recipe: Recipe) = viewModelScope.launch { repository.addRecipe(recipe) }
}
```

### Bad
```kotlin
class RecipeViewModel : ViewModel() {
    val recipes = mutableStateOf<List<Recipe>>(emptyList()) // avoid
    fun add(recipe: Recipe) { recipes.value = recipes.value + recipe }
}
```

---

## 3. Data Layer

### Rules
- Every data access goes through a `Repository` interface in `data/repository/`.
- The ViewModel knows only the `Repository` interface — never a concrete implementation, SQL, or HTTP client.
- Domain models (`data class Recipe`) have no references to UI, ViewModel, or Repository types.
- ID generation is explicit via a factory (see `RecipeIdFactory.newId()`). **Do not** use `Uuid.random()` as a default argument on a `data class` — the default is evaluated at call sites in surprising ways.

### Good
```kotlin
Recipe(id = existingRecipe?.id ?: RecipeIdFactory.newId(), ...)
```

### Bad
```kotlin
data class Recipe(val id: String = Uuid.random().toString(), ...) // implicit, leaks into recomposition
```

---

## 4. Navigation

### Rules
- Destinations live in a `sealed interface ModalScreen` in `navigation/`.
- Use the `AppNavigator` helper to push/pop modal screens.
- **Screens do not know their callers.** They expose `onBack: () -> Unit`, `onSave: (...) -> Unit`, etc. Wiring happens in [App.kt](composeApp/src/commonMain/kotlin/com/champeic/weeklyrecipes/App.kt).
- **Every modal screen has a `TopAppBar` with a back button.** Users should never have to guess how to leave a screen.
- Bottom-tab switches clear the modal stack (see `navigator.clear()` in `App.kt`).

---

## 5. Composables

### Rules
- `@Composable` functions are `PascalCase` and have no side effects outside Compose scopes.
- `Modifier` is always the last parameter, defaulting to `Modifier`:
  ```kotlin
  fun MyComponent(foo: String, modifier: Modifier = Modifier)
  ```
- Prefer **stateless** composables; hoist state to the caller.
- Extract a shared composable as soon as you copy-paste a block a second time. The cost of an extra file is lower than the cost of a drift between copies.
- Composables do not own business rules. They display state and emit events.

### Good
```kotlin
@Composable
fun RecipeCard(recipe: Recipe, onClick: () -> Unit, modifier: Modifier = Modifier)
```

### Bad
```kotlin
@Composable
fun RecipeCard(recipe: Recipe) { /* reads view model internally */ }
```

---

## 6. Forms & Validation

### Rules
- Validation logic lives in the form state holder, not in the composable body.
- A "Save" button is disabled while `formState.isValid == false`.
- Inline errors go on the offending `OutlinedTextField` via `isError = true` and `supportingText = { ... }` — not in a toast or a snackbar.
- Use `filter { it.isDigit() }` on numeric inputs to reject bad characters as the user types.

### Reference
See `RecipeFormState` in [RecipeFormState.kt](composeApp/src/commonMain/kotlin/com/champeic/weeklyrecipes/ui/state/RecipeFormState.kt) for `isValid`, `minInvalid`, `rangeInvalid`, and the `toRecipe(existingId)` conversion.

---

## 7. Material 3

### Rules
- Typography always via `MaterialTheme.typography.*`. No hardcoded font sizes.
- Colors always via `MaterialTheme.colorScheme.*`. No hex literals.
- Spacing is a multiple of `4.dp`: `4, 8, 12, 16, 24, 32`.
- Prefer Material 3 components: `FilterChip`, `TopAppBar`, `AlertDialog`, `OutlinedTextField`, `FloatingActionButton`.
- `FlowRow` for selectors that might wrap on small screens, not `Row` with `weight(1f)` + truncated text.

---

## 8. Accessibility

### Rules
- Every `Icon` / `IconButton` that is **interactive** has a `contentDescription`.
- Decorative icons inside a `TextField`'s `leadingIcon` may pass `contentDescription = null` — the field itself is already labelled.
- Use `Icons.Default.*` (or `Icons.AutoMirrored.Filled.*` for directional icons like `ArrowBack`). **Do not** use emoji for interactive elements — screen readers skip them.
- Buttons must have meaningful text labels — avoid icon-only buttons without an accompanying description.

---

## 9. Multiplatform

### Rules
- Write common code in `commonMain`. Platform-specific code in `androidMain` / `iosMain` uses `expect` / `actual`.
- No Android framework imports (`android.*`, `androidx.activity.*`) in `commonMain`.
- Anything from `androidx.compose.*`, `androidx.lifecycle.*`, `kotlinx.coroutines.*` is safe in `commonMain`.

---

## 10. Kotlin Conventions

### Rules
- `val` by default. Only use `var` when state actually mutates.
- `List<T>` in signatures, never `MutableList<T>` — `MutableStateFlow` is the right place to mutate.
- **No `!!`.** Use `?.let { }`, `requireNotNull(x) { "message" }`, or make the type non-nullable.
- `@OptIn` at the call site that needs it, not `@file:OptIn`.
- `data class` for value types; `sealed interface` for closed hierarchies; `object` for stateless helpers.

---

## 11. Things We Do Not Do

| Anti-pattern | Why it is wrong | Instead |
|---|---|---|
| `mutableStateOf` inside a `ViewModel` | Breaks coroutine integration, hides lifecycle | `MutableStateFlow` + `StateFlow` |
| `onClick = { }` | Non-functional UI that looks real | Wire a real callback, or remove the button |
| `if (x || true)` / dead conditions | Bug waiting to be shipped | Remove the branch, decide what you actually want |
| Emoji icons in the nav bar | Not readable by assistive tech | `Icons.Default.*` |
| Duplicated forms (`AddX`, `QuickAddX`) | Two places to keep in sync | One `XFormScreen` + a state holder |
| `Uuid.random()` in a `data class` default | Generates unpredictable IDs during recomposition | `RecipeIdFactory.newId()` at the creation site |
| Business logic in composables | Hard to test, couples UI to domain | Move to ViewModel or state holder |
| Mutation of a `List` via `list.value = list.value + item` in a VM | Non-atomic, breaks with concurrent updates | `MutableStateFlow.update { it + item }` |

---

## 12. Pull Request Checklist

Before opening a PR:

- [ ] `./gradlew composeApp:assembleDebug` succeeds.
- [ ] iOS build succeeds (`./gradlew iosSimulatorArm64Test` or via Xcode).
- [ ] Manual smoke test of the affected flow on both platforms — describe what you tested in the PR body.
- [ ] No `mutableStateOf` has been introduced inside a `ViewModel`.
- [ ] No new composable duplicates an existing one (ingredient list? step list? day selector? use the shared component).
- [ ] Every new `Icon` / `IconButton` has a `contentDescription`.
- [ ] If you added a new `ModalScreen` variant, `App.kt` handles it.
- [ ] No `// TODO` without an accompanying GitHub issue number.

---

## 13. Tests (Planned)

The codebase doesn't have tests yet. When we add them:

- **ViewModel tests** (Kotlin test + `kotlinx-coroutines-test`) verify that CRUD operations update the `StateFlow` as expected, using a fake `RecipeRepository`.
- **State holder tests** for `RecipeFormState` verify validation (`isValid`, `rangeInvalid`).
- **Compose UI tests** (`createComposeRule()`) verify critical flows: add a recipe, edit, delete, cook.

The current architecture — Repository interface, StateFlow ViewModel, stateless composables, state holders — was chosen specifically to make these tests writable without mocking the world.

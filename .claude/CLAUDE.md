# Weekly Recipes App - Project Documentation

## Project Overview

**Weekly Recipes** is a Kotlin Compose Multiplatform mobile/desktop application for planning and managing weekly meals. Users can create recipes with ingredients and cooking steps, organize them by day of the week and meal type (breakfast, lunch, dinner, snack), view cooking instructions, and track prep times.

**Current Status:** MVP with core features implemented
**Platforms:** Android, iOS (built with Compose Multiplatform)
**State Management:** In-memory (ready for database integration)

---

## Architecture Overview

### Technology Stack
- **Language:** Kotlin 2.3.20
- **UI Framework:** Jetpack Compose Multiplatform 1.10.3
- **Architecture Pattern:** MVVM with reactive state management
- **State Management:** Mutable state (ready for upgrade to StateFlow + Repository pattern)
- **Navigation:** Manual screen-based navigation via App.kt

### Project Structure

```
composeApp/src/
├── commonMain/kotlin/com/champeic/weeklyrecipes/
│   ├── App.kt                           # Main app entry, navigation logic
│   ├── Greeting.kt                      # Placeholder component
│   ├── Platform.kt                      # Platform detection interface
│   │
│   ├── data/
│   │   └── models/
│   │       ├── Recipe.kt                # Recipe data class
│   │       ├── DayOfWeek.kt             # Enum: Monday-Sunday
│   │       └── MealType.kt              # Enum: Breakfast, Lunch, Dinner, Snack
│   │
│   ├── viewmodel/
│   │   └── RecipeViewModel.kt           # State management for recipes
│   │
│   └── ui/
│       ├── screens/
│       │   ├── WeeklyPlanScreen.kt      # Main weekly view
│       │   ├── AllRecipesScreen.kt      # Browse all recipes
│       │   ├── RecipeDetailScreen.kt    # Recipe details with edit/delete
│       │   ├── AddEditRecipeScreen.kt   # Add/edit recipes (from Weekly tab)
│       │   ├── QuickAddRecipeScreen.kt  # Dedicated add recipe form (from Recipes tab)
│       │   ├── CookingScreen.kt         # Step-by-step cooking instructions
│       │   └── SettingsScreen.kt        # App info and statistics
│       └── components/
│           └── RecipeCard.kt            # Reusable recipe display card
│
├── androidMain/kotlin/com/champeic/weeklyrecipes/
│   ├── MainActivity.kt                  # Android entry point
│   └── Platform.android.kt              # Android-specific implementations
│
└── iosMain/kotlin/com/champeic/weeklyrecipes/
    ├── MainViewController.kt            # iOS entry point
    └── Platform.ios.kt                  # iOS-specific implementations
```

---

## Core Data Models

### Recipe
```kotlin
@OptIn(ExperimentalUuidApi::class)
data class Recipe(
    val id: String = Uuid.random().toString(),
    val name: String,
    val ingredients: List<String> = emptyList(),
    val steps: List<String> = emptyList(),
    val minPrepTimeMinutes: Int = 0,
    val maxPrepTimeMinutes: Int = 0,
    val day: DayOfWeek,
    val mealType: MealType,
)
```

**Key Fields:**
- `id`: Unique UUID identifier
- `steps`: List of individual cooking steps (each step is a separate string)
- `minPrepTimeMinutes` / `maxPrepTimeMinutes`: Prep time range
- `day`: Which day of the week (Monday-Sunday)
- `mealType`: Type of meal (Breakfast, Lunch, Dinner, Snack)

### Supporting Enums
- **DayOfWeek:** MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY
- **MealType:** BREAKFAST, LUNCH, DINNER, SNACK

---

## State Management

### RecipeViewModel
**Location:** `viewmodel/RecipeViewModel.kt`

**Responsibilities:**
- Maintains list of all recipes in `recipes: MutableState<List<Recipe>>`
- Provides CRUD operations: `addRecipe()`, `updateRecipe()`, `deleteRecipe()`
- Filters recipes by day and meal type

**Key Methods:**
```kotlin
fun addRecipe(recipe: Recipe)                          // Add new recipe
fun updateRecipe(recipe: Recipe)                       // Update existing recipe
fun deleteRecipe(recipeId: String)                     // Delete recipe
fun getRecipesForDay(day: DayOfWeek): List<Recipe>    // Filter by day
fun getRecipesForDayAndMeal(day, mealType): List      // Filter by day + meal type
fun getRecipeById(id: String): Recipe?                // Get single recipe
```

**Current Implementation:** In-memory with mutableStateOf
**Future Enhancement:** Should upgrade to StateFlow + Repository pattern for database integration

---

## Screen Architecture

### Navigation Structure

```
App (Bottom Navigation with 3 Tabs)
│
├── 📅 WEEKLY TAB
│   ├── WeeklyPlanScreen (main)
│   │   ├── onClick recipe → RecipeDetailScreen
│   │   ├── "+ Add Recipe" → AddEditRecipeScreen
│   │   └── "All Recipes" link → RECIPES TAB
│   ├── AddEditRecipeScreen (modal)
│   │   └── Save → back to WeeklyPlanScreen
│   ├── RecipeDetailScreen (modal)
│   │   ├── Edit → AddEditRecipeScreen
│   │   ├── Delete → back to WeeklyPlanScreen
│   │   └── "Cook" → CookingScreen
│   └── CookingScreen (modal)
│       └── Step navigation → back to RecipeDetailScreen
│
├── 🍽️ RECIPES TAB
│   ├── AllRecipesScreen (main)
│   │   ├── onClick recipe → RecipeDetailScreen
│   │   └── ➕ FAB → QuickAddRecipeScreen
│   ├── QuickAddRecipeScreen (modal)
│   │   └── Save → back to AllRecipesScreen
│   ├── RecipeDetailScreen (modal)
│   │   └── (same as Weekly tab)
│   └── CookingScreen (modal)
│       └── (same as Weekly tab)
│
└── ⚙️ SETTINGS TAB
    └── SettingsScreen (main)
        └── Shows app info and recipe count
```

### Screen Details

#### WeeklyPlanScreen
**Purpose:** Display recipes organized by day and meal type
**Key Components:**
- Title with "Weekly Meal Plan" heading
- DayColumn for each day (Monday-Sunday)
- MealTypeSection grouping within each day
- "+ Add Recipe" button at bottom

#### AllRecipesScreen
**Purpose:** Browse all created recipes in a single scrollable list
**Key Features:**
- List of all recipes
- ➕ Floating Action Button (bottom right) to add new recipe
- Click any recipe to view details

#### RecipeDetailScreen
**Purpose:** View full recipe details
**Displays:**
- Recipe name
- Day and meal type
- Prep time (min-max range)
- Ingredients list (bullet points)
- Instructions (if available)
- Buttons: Edit, Delete, Cook (if instructions present)

#### AddEditRecipeScreen (Weekly tab add form)
**Purpose:** Add or edit recipes with full details
**Fields:**
- Recipe name
- Day selector (7 buttons for days)
- Meal type selector (4 buttons)
- Min & Max prep time
- Ingredients (add/remove)
- Instructions (text field)
- Save/Cancel buttons

#### QuickAddRecipeScreen (Recipes tab add form)
**Purpose:** Dedicated recipe creation form with enhanced UX
**Fields:** Same as AddEditRecipeScreen PLUS:
- Individual cooking steps (add/remove each step)
- Back button (top left)
- Back button positioned at top of form

#### CookingScreen
**Purpose:** Step-by-step cooking guide
**Features:**
- Recipe name and prep time range
- Ingredients checklist
- Current step display with step counter
- Previous/Next/Restart buttons for navigation
- Steps sourced from either `recipe.steps` list or split from `instructions` string

#### SettingsScreen
**Purpose:** App information and statistics
**Displays:**
- Total number of recipes created
- App description
- Feature list
- Version number

---

## Feature Breakdown

### 1. Weekly Planning
- Organize recipes by day of week (Monday-Sunday)
- Sub-organize by meal type (Breakfast, Lunch, Dinner, Snack)
- Visual card layout for each day
- Quick add button for each meal type slot

### 2. Recipe Management
- **Create:** Add recipes with title, ingredients, cooking steps, prep time range
- **Read:** View recipe details including ingredients and instructions
- **Update:** Edit existing recipes (from detail screen)
- **Delete:** Remove recipes permanently
- **Browse:** View all recipes in a single list or by weekly plan

### 3. Cooking Mode
- Step-by-step instructions for cooking
- Navigate between steps with Previous/Next buttons
- Restart button to go back to step 1
- Shows ingredient checklist
- Displays prep time range
- Supports both old `instructions` string format and new `steps` list format

### 4. Meal Type Organization
- 4 built-in meal types: Breakfast, Lunch, Dinner, Snack
- Easy-to-add new meal types by extending MealType enum

### 5. Prep Time Tracking
- Min and max prep time for each recipe
- Displayed as "min-max minutes" format
- Useful for planning: "15-20 minutes"

---

## Best Practices

### Code Organization
1. **Separation of Concerns**
   - Models in `data/` package
   - UI logic in `ui/screens/` package
   - Reusable components in `ui/components/`
   - State in `viewmodel/` package

2. **Naming Conventions**
   - Screen files: `[Name]Screen.kt`
   - Component files: `[Name].kt`
   - Model files: `[Name].kt`
   - Functions are camelCase, classes are PascalCase

3. **Composables**
   - Always add `@Composable` annotation
   - Use `Modifier` as last parameter
   - Extract common patterns to reusable composables
   - Keep composables focused and single-purpose

### State Management
1. **Use ViewModel for shared state**
   - Don't pass state through many composable parameters
   - ViewModel should handle CRUD operations
   - Use `by remember { viewModel() }` to get instance

2. **Local state vs. ViewModel state**
   - Local state: UI-only state (expanded/collapsed, text input, current step index)
   - ViewModel state: Shared state that should persist across navigation

3. **Immutability**
   - Prefer immutable data classes (all our models are immutable)
   - Use `copy()` method to update data instead of mutation

### UI/UX
1. **Navigation**
   - Use bottom navigation for main tabs
   - Modal screens for details/editing
   - Clear back navigation via tab switching (no explicit back buttons needed)

2. **Form Design**
   - Use OutlinedTextField for inputs
   - Validate before save (e.g., recipe name required)
   - Provide feedback (disabled buttons when invalid)
   - Clear labeling for all inputs

3. **Lists and Scrolling**
   - Use `verticalScroll(rememberScrollState())` for scrollable content
   - Apply padding from Scaffold to prevent content overlap
   - Use `fillMaxWidth()` for full-width content

### Material Design 3
- Use `MaterialTheme.typography` for consistent text styling
- Use `MaterialTheme.colorScheme` for consistent colors
- Prefer elevated/outlined components for visual hierarchy
- Button layout: OutlinedButton for secondary, Button for primary

---

## How to Add New Features

### Adding a New Meal Type
1. Add variant to `MealType` enum in `data/models/MealType.kt`
2. Update `MealType.values()` calls will automatically include it
3. Existing UI will automatically display it in selectors

### Adding Recipe Fields
1. Add field to `Recipe` data class in `data/models/Recipe.kt`
2. Update UI screens to display/input the new field:
   - `RecipeDetailScreen` to display it
   - `AddEditRecipeScreen` and/or `QuickAddRecipeScreen` to input it
3. Update `ViewModel.addRecipe()` and `ViewModel.updateRecipe()` if needed

### Creating a New Screen
1. Create new file in `ui/screens/[Name]Screen.kt`
2. Define `@Composable fun [Name]Screen(...)`
3. Add callbacks for navigation (e.g., `onBack: () -> Unit`)
4. Add modal screen enum case if it's a modal (e.g., `ModalScreen.MY_SCREEN`)
5. Add navigation logic in `App.kt` `when` statement
6. Update `ModalScreen` enum to include new screen

### Adding a Reusable Component
1. Create file in `ui/components/[Name].kt`
2. Keep it generic and configurable via parameters
3. Example: `RecipeCard` is reused in multiple screens

---

## Database Integration (Future)

**Current State:** In-memory storage only

**Plan for Migration:**
1. **Add SQLDelight dependency** to `build.gradle.kts`
   - Multiplatform SQLite wrapper
   - Type-safe queries
   - Works on Android, iOS, Desktop

2. **Create database schema**
   - `.sq` files in `commonMain/sqldelight/`
   - Define Recipe, Ingredient, Step tables

3. **Build Repository layer**
   - `RecipeRepository` interface
   - Platform-specific implementations for Android/iOS if needed

4. **Update ViewModel**
   - Inject `RecipeRepository`
   - Replace mutableStateOf with StateFlow
   - Update operations to call repository instead of internal list

5. **Add persistence logic**
   - Save on every CRUD operation
   - Load all recipes on app start

---

## Known Limitations & TODOs

### Current Limitations
1. **No persistence:** Data is lost on app close (in-memory only)
2. **No sync:** No cloud backup or multi-device sync
3. **No search:** Cannot search for recipes (could be added easily)
4. **Limited validation:** Minimal form validation
5. **No unit tests:** Currently manual testing only

### Potential Enhancements
1. Add database persistence (SQLDelight)
2. Add recipe search/filter functionality
3. Add ability to mark recipes as favorites
4. Add recipe ratings/notes
5. Add ingredient quantity/units
6. Add shopping list generation from weekly plan
7. Add recipe categories/tags
8. Add user authentication for cloud sync
9. Add unit tests and integration tests
10. Add dark mode support

---

## Important Files Reference

| File | Purpose |
|------|---------|
| `App.kt` | Main entry point, navigation hub |
| `viewmodel/RecipeViewModel.kt` | All state and business logic |
| `data/models/Recipe.kt` | Core data structure |
| `ui/screens/WeeklyPlanScreen.kt` | Main user interface |
| `ui/screens/CookingScreen.kt` | Step-by-step cooking guide |
| `ui/components/RecipeCard.kt` | Reusable recipe display |

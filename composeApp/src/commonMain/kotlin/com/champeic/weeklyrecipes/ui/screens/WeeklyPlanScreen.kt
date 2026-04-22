package com.champeic.weeklyrecipes.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import com.champeic.weeklyrecipes.data.models.*
import com.champeic.weeklyrecipes.ui.theme.SpoonfeederColors
import com.champeic.weeklyrecipes.ui.theme.SpoonfeederRadii
import com.champeic.weeklyrecipes.ui.theme.brandColor
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import weeklyrecipes.composeapp.generated.resources.Res
import weeklyrecipes.composeapp.generated.resources.empty_state_no_recipes_dark
import weeklyrecipes.composeapp.generated.resources.empty_state_no_recipes_light
import weeklyrecipes.composeapp.generated.resources.logo_dark
import weeklyrecipes.composeapp.generated.resources.logo_light
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.number
import kotlinx.datetime.plus

private data class MealTypeTheme(
    val containerColor: Color,
    val contentColor: Color,
    val icon: ImageVector,
)

private val mealTypeThemes = mapOf(
    MealType.BREAKFAST to MealTypeTheme(
        containerColor = MealType.BREAKFAST.brandColor(),
        contentColor = SpoonfeederColors.Cocoa,
        icon = Icons.Default.FreeBreakfast,
    ),
    MealType.LUNCH to MealTypeTheme(
        containerColor = MealType.LUNCH.brandColor(),
        contentColor = SpoonfeederColors.Cocoa,
        icon = Icons.Default.LunchDining,
    ),
    MealType.DINNER to MealTypeTheme(
        containerColor = MealType.DINNER.brandColor(),
        contentColor = SpoonfeederColors.Cocoa,
        icon = Icons.Default.DinnerDining,
    ),
    MealType.SNACK to MealTypeTheme(
        containerColor = MealType.SNACK.brandColor(),
        contentColor = SpoonfeederColors.Cocoa,
        icon = Icons.Default.Cookie,
    ),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeeklyPlanScreen(
    recipes: List<Recipe>,
    plannedMeals: List<PlannedMeal>,
    onPlanMeal: (LocalDate, MealType) -> Unit,
    onUnplanMeal: (String) -> Unit,
    onRecipeClick: (Recipe) -> Unit,
    modifier: Modifier = Modifier,
) {
    val today = remember { today() }
    val weeks = remember(today) {
        val thisMonday = today.mondayOfWeek()
        listOf(
            thisMonday.weekDates(),
            thisMonday.plus(7, DateTimeUnit.DAY).weekDates(),
        )
    }
    val recipesById = remember(recipes) { recipes.associateBy { it.id } }
    val pagerState = rememberPagerState(pageCount = { 2 })
    val scope = rememberCoroutineScope()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    val logo = if (isSystemInDarkTheme()) Res.drawable.logo_dark else Res.drawable.logo_light
                    Image(
                        painter = painterResource(logo),
                        contentDescription = "Spoonfeeder",
                        modifier = Modifier.height(32.dp),
                    )
                },
            )
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            WeekSelector(
                selectedIndex = pagerState.currentPage,
                onSelect = { index -> scope.launch { pagerState.animateScrollToPage(index) } },
                thisWeekRange = weeks[0].first() to weeks[0].last(),
                nextWeekRange = weeks[1].first() to weeks[1].last(),
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            )

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize(),
            ) { page ->
                val currentWeek = weeks[page]
                val listState = rememberLazyListState()

                LaunchedEffect(Unit) {
                    val todayIndex = currentWeek.indexOfFirst { it == today }
                    if (todayIndex > 0) {
                        listState.animateScrollToItem(todayIndex)
                    }
                }

                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                ) {
                    if (recipes.isEmpty()) {
                        item {
                            EmptyRecipesHint(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
                        }
                    }
                    items(currentWeek) { date ->
                        val plannedForDate = plannedMeals.filter { it.date == date }
                        DaySection(
                            date = date,
                            isToday = date == today,
                            plannedMeals = plannedForDate,
                            recipesById = recipesById,
                            onPlanMeal = onPlanMeal,
                            onUnplanMeal = onUnplanMeal,
                            onRecipeClick = onRecipeClick,
                        )
                        HorizontalDivider()
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WeekSelector(
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    thisWeekRange: Pair<LocalDate, LocalDate>,
    nextWeekRange: Pair<LocalDate, LocalDate>,
    modifier: Modifier = Modifier,
) {
    SingleChoiceSegmentedButtonRow(modifier = modifier.fillMaxWidth()) {
        SegmentedButton(
            selected = selectedIndex == 0,
            onClick = { onSelect(0) },
            shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2),
        ) {
            Text("This week (${rangeLabel(thisWeekRange)})")
        }
        SegmentedButton(
            selected = selectedIndex == 1,
            onClick = { onSelect(1) },
            shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2),
        ) {
            Text("Next week (${rangeLabel(nextWeekRange)})")
        }
    }
}

private fun rangeLabel(range: Pair<LocalDate, LocalDate>): String {
    val (start, end) = range
    return "${start.month.number}/${start.day}–${end.month.number}/${end.day}"
}

@Composable
private fun EmptyRecipesHint(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        val emptyArt = if (isSystemInDarkTheme()) Res.drawable.empty_state_no_recipes_dark
                       else Res.drawable.empty_state_no_recipes_light
        Image(
            painter = painterResource(emptyArt),
            contentDescription = null,
            modifier = Modifier.size(200.dp),
        )
        Text(text = "Your week is empty", style = MaterialTheme.typography.titleMedium)
        Text(
            text = "Add a recipe from the Recipes tab, then come back to plan it.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun DaySection(
    date: LocalDate,
    isToday: Boolean,
    plannedMeals: List<PlannedMeal>,
    recipesById: Map<String, Recipe>,
    onPlanMeal: (LocalDate, MealType) -> Unit,
    onUnplanMeal: (String) -> Unit,
    onRecipeClick: (Recipe) -> Unit,
) {
    var addExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        // Day header row
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Day name + date
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = date.dayOfWeek.displayName(),
                        style = MaterialTheme.typography.titleMedium,
                    )
                    if (isToday) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primary,
                        ) {
                            Text(
                                text = "Today",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                            )
                        }
                    }
                }
                Text(
                    text = "${date.month.number}/${date.day}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            // Expanded: 4 meal type buttons + close
            AnimatedVisibility(visible = addExpanded) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    MealType.entries.forEach { mealType ->
                        val theme = mealTypeThemes.getValue(mealType)
                        FilledIconButton(
                            onClick = {
                                onPlanMeal(date, mealType)
                                addExpanded = false
                            },
                            modifier = Modifier.size(36.dp),
                            colors = IconButtonDefaults.filledIconButtonColors(
                                containerColor = theme.containerColor,
                                contentColor = theme.contentColor,
                            ),
                        ) {
                            Icon(
                                imageVector = theme.icon,
                                contentDescription = "Add ${mealType.displayName}",
                                modifier = Modifier.size(18.dp),
                            )
                        }
                    }
                    IconButton(
                        onClick = { addExpanded = false },
                        modifier = Modifier.size(36.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Cancel",
                            modifier = Modifier.size(18.dp),
                        )
                    }
                }
            }

            // Collapsed: single + button
            AnimatedVisibility(visible = !addExpanded) {
                FilledTonalIconButton(
                    onClick = { addExpanded = true },
                    modifier = Modifier.size(36.dp),
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add meal",
                        modifier = Modifier.size(20.dp),
                    )
                }
            }
        }

        // Meal event cards grouped by type order
        MealType.entries.forEach { mealType ->
            plannedMeals
                .filter { it.mealType == mealType }
                .forEach { planned ->
                    val recipe = recipesById[planned.recipeId]
                    MealEventCard(
                        mealType = mealType,
                        recipeName = recipe?.name ?: "(deleted recipe)",
                        prepTimeRange = recipe?.let { it.minPrepTimeMinutes to it.maxPrepTimeMinutes }
                            ?.takeIf { (min, max) -> min > 0 || max > 0 },
                        onClick = { recipe?.let(onRecipeClick) },
                        onRemove = { onUnplanMeal(planned.id) },
                    )
                }
        }
    }
}

@Composable
private fun MealEventCard(
    mealType: MealType,
    recipeName: String,
    prepTimeRange: Pair<Int, Int>?,
    onClick: () -> Unit,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val theme = mealTypeThemes.getValue(mealType)
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = SpoonfeederRadii.Chip,
        color = theme.containerColor,
    ) {
        Row(
            modifier = Modifier.padding(start = 12.dp, top = 8.dp, bottom = 8.dp, end = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = theme.icon,
                contentDescription = null,
                tint = theme.contentColor,
                modifier = Modifier.size(16.dp),
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = mealType.displayName,
                    style = MaterialTheme.typography.labelSmall,
                    color = theme.contentColor.copy(alpha = 0.7f),
                )
                Text(
                    text = recipeName,
                    style = MaterialTheme.typography.labelLarge,
                    color = theme.contentColor,
                )
                if (prepTimeRange != null) {
                    Text(
                        text = "${prepTimeRange.first}–${prepTimeRange.second} min",
                        style = MaterialTheme.typography.labelSmall,
                        color = theme.contentColor.copy(alpha = 0.7f),
                    )
                }
            }
            IconButton(
                onClick = onRemove,
                modifier = Modifier.size(32.dp),
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Remove from plan",
                    tint = theme.contentColor.copy(alpha = 0.7f),
                    modifier = Modifier.size(14.dp),
                )
            }
        }
    }
}

package com.champeic.weeklyrecipes.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.champeic.weeklyrecipes.data.models.MealType
import com.champeic.weeklyrecipes.data.models.Recipe
import com.champeic.weeklyrecipes.ui.components.EditableChipList
import com.champeic.weeklyrecipes.ui.components.EditableStringList
import com.champeic.weeklyrecipes.ui.state.RecipeFormState
import com.champeic.weeklyrecipes.ui.state.rememberRecipeFormState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeFormScreen(
    existingRecipe: Recipe?,
    onSave: (Recipe) -> Unit,
    onBack: () -> Unit,
    prefilledMeal: MealType? = null,
    modifier: Modifier = Modifier,
) {
    val formState = rememberRecipeFormState(existingRecipe, prefilledMeal)
    val isEdit = existingRecipe != null

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(if (isEdit) "Edit recipe" else "Add recipe") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                        )
                    }
                },
            )
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            OutlinedTextField(
                value = formState.name,
                onValueChange = { formState.name = it },
                label = { Text("Recipe name") },
                singleLine = true,
                isError = formState.name.isBlank(),
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences,
                ),
            )

            MealTypeSelectorChips(
                selected = formState.suitableMealTypes,
                onToggle = formState::toggleMealType,
            )

            PrepTimeFields(formState)

            EditableChipList(
                items = formState.ingredients,
                onAdd = formState::addIngredient,
                onRemove = formState::removeIngredientAt,
                title = "Ingredients",
                placeholder = "Add ingredient",
            )

            EditableStringList(
                items = formState.steps,
                onAdd = formState::addStep,
                onRemove = formState::removeStepAt,
                title = "Cooking steps",
                placeholder = "Add step",
                numbered = true,
            )

            Button(
                onClick = { onSave(formState.toRecipe(existingRecipe?.id)) },
                enabled = formState.isValid,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(if (isEdit) "Save changes" else "Add recipe")
            }
        }
    }
}

@Composable
private fun PrepTimeFields(formState: RecipeFormState) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        OutlinedTextField(
            value = formState.minPrepTime,
            onValueChange = { new -> formState.minPrepTime = new.filter { it.isDigit() } },
            label = { Text("Min prep (min)") },
            singleLine = true,
            isError = formState.minInvalid || formState.rangeInvalid,
            supportingText = when {
                formState.minInvalid -> { { Text("Positive number") } }
                formState.rangeInvalid -> { { Text("Min must be ≤ Max") } }
                else -> null
            },
            modifier = Modifier.weight(1f),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
            )
        )
        OutlinedTextField(
            value = formState.maxPrepTime,
            onValueChange = { new -> formState.maxPrepTime = new.filter { it.isDigit() } },
            label = { Text("Max prep (min)") },
            singleLine = true,
            isError = formState.maxInvalid,
            supportingText = if (formState.maxInvalid) {
                { Text("Positive number") }
            } else null,
            modifier = Modifier.weight(1f),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
            )
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun MealTypeSelectorChips(
    selected: Set<MealType>,
    onToggle: (MealType) -> Unit,
) {
    Column {
        Text("Suitable for", style = MaterialTheme.typography.titleSmall)
        Text(
            text = "Which meals this recipe is appropriate for. Leave empty for any.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        FlowRow(
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            MealType.entries.forEach { mealType ->
                FilterChip(
                    selected = mealType in selected,
                    onClick = { onToggle(mealType) },
                    label = { Text(mealType.displayName) },
                )
            }
        }
    }
}

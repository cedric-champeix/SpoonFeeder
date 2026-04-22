package com.champeic.weeklyrecipes.data.models

enum class DietType(val displayName: String) {
    NONE("No specific diet"),
    VEGETARIAN("Vegetarian"),
    VEGAN("Vegan"),
    PESCATARIAN("Pescatarian"),
    MEDITERRANEAN("Mediterranean"),
    KETO("Keto"),
    PALEO("Paleo"),
    GLUTEN_FREE("Gluten-free"),
    DAIRY_FREE("Dairy-free"),
    HALAL("Halal"),
    KOSHER("Kosher"),
}

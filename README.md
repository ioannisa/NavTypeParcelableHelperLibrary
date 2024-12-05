# NavType Parcelable Helper Library for Navigation Component
## Simplify passing parcelables and complex strings to the New Navigation library
*by Ioannis Anifantakis*


[**YouTube Video Explaing the Library**]("https://www.youtube.com/watch?v=MXRn_2l8sd4&list=PLp_FpnyDwvuDLq_I-C3tJqDiHOFuIzJgk&t=2150")

The `NavHelper` library provides utilities to enhance Jetpack Compose navigation with type safety for Parcelable objects and the serialization of strings with special characters using URL encoding and decoding. This library requires Navigation Component version 2.8.0-alpha08 or later.

While the Navigation Component version 2.8.0-alpha08 and later supports type safety out of the box for primitive types, passing Parcelable objects and handling strings with special characters require additional code. The NavHelper library provides this extra code, saving you from writing it manually for each project. It includes:
Features

* Type-safe navigation for Parcelable objects.
* URL encoding and decoding for strings with special characters.
* Simplifies code required for passing Parcelables and serialized strings between screens.

## Installation

### Install by adding directly the module to your project
The directory `secure-persist` contains the module of this library, so you can
1. copy it to your root folder of the project (same as you see it in this repo folders)
2. at the bottom of your `settings.gradle` you tell android to treat the folder you copied as a module
```kotlin
rootProject.name = "My App Name"
include(":app")
include(":NavHelper") // <-- add this line so android knows this folder is a module
```
3. At your App-Module's `build.gradle` file dependencies, add the module that your project contains
```kotlin
implementation(project(":NavHelper"))
```

### Install as a Jitpack library to your dependencies

[![](https://jitpack.io/v/ioannisa/NavTypeParcelableHelperLibrary.svg)](https://jitpack.io/#ioannisa/NavTypeParcelableHelperLibrary)


1. Add this to your dependencies
```kotlin
implementation("com.github.ioannisa:NavTypeParcelableHelperLibrary:1.0.1")
```

2. Add Jitpack as a dependencies repository in your `settings.gradle` (or at Project's `build.gradle` for older Android projects) in order for this library to be able to download
```kotlin
repositories {
    google()
    mavenCentral()
    maven(url = "https://jitpack.io") // <-- add this line
}
```

## Usage

### StringSanitizer

`StringSanitizer` is a custom serializer for handling strings with special characters, including but not limited to URLs. This ensures that such strings are safely encoded and decoded, preventing issues with special characters in navigation parameters. This encoding can be applied to any string containing special characters, not just URLs, to ensure they do not break during the serialization phase.

#### Example
```kotlin
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import eu.anifantakis.navhelper.serialization.StringSanitizer

@Serializable
@Parcelize
data class Person(
    val id: Int,
    val section: Int,
    val name: String,
    @Serializable(with = StringSanitizer::class)
    val imageUrl: String,
    @Serializable(with = StringSanitizer::class)
    val landingPage: String
) : Parcelable
```

### NavType.Companion.mapper

NavType.Companion.mapper provides a NavType for Parcelable objects, enabling type-safe navigation in Jetpack Compose. For the Parcelable class you want to pass, you just need to declare it using the `typeMap` named variable.

#### Example

```kotlin
import android.os.Parcelable
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import eu.anifantakis.navhelper.navtype.mapper
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import kotlin.reflect.typeOf

@Serializable
@Parcelize
data class Person(
    val id: Int,
    val section: Int,
    val name: String,
    @Serializable(with = StringSanitizer::class)
    val imageUrl: String,
    @Serializable(with = StringSanitizer::class)
    val landingPage: String
) : Parcelable

sealed class NavRoute {
    @Serializable
    object MainScreen

    @Serializable
    data class DetailScreen(
        val person: Person
    )
}

@Composable
fun NavigationRootNew() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = NavRoute.MainScreen) {
        composable<NavRoute.MainScreen> {
            val viewModel = viewModel<MainScreenViewModel>()
            MainScreen(viewModel, onItemClick = { person ->
                navController.navigate(NavRoute.DetailScreen(person))
            })
        }

        composable<NavRoute.DetailScreen>(
            typeMap = mapOf(typeOf<Person>() to NavType.mapper<Person>())
        ) {
            val person = it.toRoute<NavRoute.DetailScreen>().person

            DetailScreen(person, onBackPress = {
                navController.popBackStack()
            })
        }
    }
}
```

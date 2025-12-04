package com.example.cvstakehome.ui.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.cvstakehome.data.api.RetrofitInstance
import com.example.cvstakehome.data.model.Character
import com.example.cvstakehome.data.repository.CharacterRepository
import com.example.cvstakehome.ui.detail.DetailScreen
import com.example.cvstakehome.ui.search.SearchScreen
import com.example.cvstakehome.ui.search.SearchViewModel
import com.example.cvstakehome.ui.search.SearchViewModelFactory

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun RickAndMortyNavigation(
    navController: NavHostController = rememberNavController()
) {
    val repository = remember { CharacterRepository(RetrofitInstance.api) }
    var selectedCharacter: Character? = null

    SharedTransitionLayout {
        NavHost(
            navController = navController,
            startDestination = Screen.Search.route
        ) {
            composable(Screen.Search.route) { backStackEntry ->
                val searchViewModel: SearchViewModel = viewModel(
                    viewModelStoreOwner = backStackEntry,
                    factory = SearchViewModelFactory(repository)
                )

                SearchScreen(
                    viewModel = searchViewModel,
                    onCharacterClick = { character ->
                        selectedCharacter = character
                        navController.navigate(Screen.Detail.route)
                    },
                    sharedTransitionScope = this@SharedTransitionLayout,
                    animatedContentScope = this@composable
                )
            }

            composable(Screen.Detail.route) {
                selectedCharacter?.let { character ->
                    DetailScreen(
                        character = character,
                        onBackClick = { navController.navigateUp() },
                        sharedTransitionScope = this@SharedTransitionLayout,
                        animatedContentScope = this@composable
                    )
                }
            }
        }
    }
}

sealed class Screen(val route: String) {
    data object Search : Screen("search")
    data object Detail : Screen("detail")
}

package app.mv

import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.NavHost
import androidx.navigation.navArgument
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.lifecycle.viewmodel.compose.viewModel

import app.mv.viewmodel.AppSettingsViewModel


@Composable
fun NavGraph() {
	val navController = rememberNavController()
	val appSettingsViewModel: AppSettingsViewModel = viewModel()
	NavHost(
		navController = navController, 
		startDestination = "folder_list",
		enterTransition = { EnterTransition.None },
		exitTransition = { ExitTransition.None },
	) {
		composable("folder_list") {
			FolderList(
			    toSettings = { navController.navigate("settings_screen") },
			    toFolderContent = { folderId, folderName ->
			        navController.navigate("folder_content/$folderId/$folderName")
			    }
			)
		}
		composable("settings_screen") {
			SettingsScreen()
		}
		composable(
		    route = "folder_content/{folderId}/{folderName}",
		    arguments = listOf( 
			    navArgument("folderId") { type = NavType.LongType },
			    navArgument("folderName") { type = NavType.StringType }
		    )
		) { backStackEntry ->
		    val folderId = backStackEntry.arguments?.getLong("folderId") ?: 0L
		    val folderName = backStackEntry.arguments?.getString("folderName") ?: ""
            FolderContent(
                folderId = folderId,
                folderName = folderName,
                toMediaView = { mediaId ->
                    navController.navigate("media_view/$mediaId/$folderId")
                }
            )
		}
		composable(
		    route = "media_view/{mediaId}/{folderId}",
		    arguments = listOf(
		        navArgument("mediaId") { type = NavType.LongType },
		        navArgument("folderId") { type = NavType.LongType }
		    )
		) { backStackEntry ->
			val mediaId = backStackEntry.arguments?.getLong("mediaId") ?: 0L
			val folderId = backStackEntry.arguments?.getLong("folderId") ?: 0L
			MediaView(
			    mediaId = mediaId,
			    folderId = folderId,
			    appSettingsViewModel = appSettingsViewModel
			)
		}
	}
}
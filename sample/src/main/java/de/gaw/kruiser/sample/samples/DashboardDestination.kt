package de.gaw.kruiser.sample.samples

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import de.gaw.kruiser.destination.Destination
import de.gaw.kruiser.screen.Screen
import de.gaw.kruiser.screen.ScreenModel
import de.gaw.kruiser.service.ScopedServiceProvider.ServiceFactory
import de.gaw.kruiser.service.service
import de.gaw.kruiser.state.NavigationState
import de.gaw.kruiser.state.push

object DashboardDestination : Destination {
    override fun build() = object : Screen {
        override val destination: Destination get() = this@DashboardDestination

        @Composable
        override fun Content() {
            val model = service(DashboardScreenModelFactory)
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(model.menuItems) { (title, onClick) ->
                    ListItem(
                        modifier = Modifier.clickable(onClick = onClick),
                        headlineContent = {
                            Text(
                                text = title,
                            )
                        },
                    )
                }
            }
        }
    }
}

private class DashboardScreenModel(
    private val navigationState: () -> NavigationState,
) : ScreenModel {

    val menuItems = listOf(
        "Pushing and Popping" to ::openPushAndPopSample
    )

    private fun openPushAndPopSample() {
        navigationState().push(PushAndPopDestination(index = 0))
    }
}

private object DashboardScreenModelFactory : ServiceFactory<DashboardScreenModel> {
    override fun create(state: () -> NavigationState): DashboardScreenModel =
        DashboardScreenModel(state)
}
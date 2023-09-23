package de.gaw.kruiser.sample.samples

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ListItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import de.gaw.kruiser.destination.Destination
import de.gaw.kruiser.sample.samples.push.PushAndPopDestinationDefault
import de.gaw.kruiser.sample.theme.KruiserPreviewTheme
import de.gaw.kruiser.screen.Screen
import de.gaw.kruiser.screen.ScreenModel
import de.gaw.kruiser.service.ScopedServiceProvider
import de.gaw.kruiser.service.ScopedServiceProvider.ServiceFactory
import de.gaw.kruiser.service.scopedService
import de.gaw.kruiser.state.InMemoryNavigationState
import de.gaw.kruiser.state.NavigationState
import de.gaw.kruiser.state.push

object DashboardDestination : Destination {
    override fun build(): Screen = DashboardScreen()
}

private class DashboardScreen : Screen {
    override val destination: Destination = DashboardDestination

    @Composable
    override fun Content() {
        val model = scopedService(DashboardScreenModelFactory)
        Dashboard(
            model = model,
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@Composable
private fun Dashboard(
    model: DashboardModel,
    modifier: Modifier = Modifier,
) {
    Dashboard(
        modifier = modifier,
        items = model.menuItems,
    )
}

@Composable
private fun Dashboard(
    items: List<Pair<String, () -> Unit>>,
    modifier: Modifier = Modifier,
) {
    Surface {
        LazyColumn(
            modifier = modifier,
        ) {
            items(items) { (title, onClick) ->
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

@Preview
@Composable
private fun DashboardPreview() = KruiserPreviewTheme {
    Dashboard(
        model = DashboardModel(navigationState = InMemoryNavigationState()),
        modifier = Modifier.fillMaxSize(),
    )
}

private class DashboardModel(
    private val navigationState: NavigationState,
) : ScreenModel {

    val menuItems = listOf(
        "Pushing and Popping" to ::openPushAndPopSample
    )

    private fun openPushAndPopSample() {
        navigationState.push(PushAndPopDestinationDefault(index = 0))
    }
}

private object DashboardScreenModelFactory : ServiceFactory<DashboardModel> {
    override fun ScopedServiceProvider.ServiceContext.create(): DashboardModel =
        DashboardModel(navigationState)
}
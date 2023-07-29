package de.gaw.kruiser.sample.samples

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import de.gaw.kruiser.destination.Destination
import de.gaw.kruiser.sample.transition.VerticalCardStackTransition
import de.gaw.kruiser.screen.Screen
import de.gaw.kruiser.screen.ScreenModel
import de.gaw.kruiser.screen.ScreenTransition
import de.gaw.kruiser.service.ScopedServiceProvider.ServiceContext
import de.gaw.kruiser.service.ScopedServiceProvider.ServiceFactory
import de.gaw.kruiser.service.service
import de.gaw.kruiser.state.NavigationState
import de.gaw.kruiser.state.push
import kotlin.random.Random

data class PushAndPopDestination(
    val index: Int,
) : Destination,
    ScreenTransition by VerticalCardStackTransition() {

    override fun build() = object : Screen {
        override val destination: Destination get() = this@PushAndPopDestination

        @Composable
        override fun Content() {
            val model = service(PushAndPopScreenModelFactory(index))
            val backgroundColor = remember {
                Color(
                    Random.nextFloat() * .5f + .5f,
                    Random.nextFloat() * .5f + .5f,
                    Random.nextFloat() * .5f + .5f,
                    1f,
                )
            }

            Surface(
                modifier = Modifier.fillMaxSize(),
                color = backgroundColor,
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Text("Screen $index")
                    Spacer(modifier = Modifier.size(24.dp))
                    ElevatedButton(onClick = model::onNext) {
                        Text("Next")
                    }
                }
            }
        }
    }
}

private class PushAndPopScreenModel(
    private val index: Int,
    private val navigationState: NavigationState,
) : ScreenModel {
    fun onNext() {
        navigationState.push(PushAndPopDestination(index + 1))
    }
}

private data class PushAndPopScreenModelFactory(
    val index: Int,
) : ServiceFactory<PushAndPopScreenModel> {
    override fun ServiceContext.create(): PushAndPopScreenModel =
        PushAndPopScreenModel(index, navigationState)
}
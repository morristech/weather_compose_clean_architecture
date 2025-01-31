package com.minhdtm.example.weapose.presentation.ui.sevendaysweather

import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshState
import com.google.android.gms.maps.model.LatLng
import com.minhdtm.example.weapose.R
import com.minhdtm.example.weapose.presentation.component.WeatherScaffold
import com.minhdtm.example.weapose.presentation.model.DayWeatherViewData
import com.minhdtm.example.weapose.presentation.ui.Screen
import com.minhdtm.example.weapose.presentation.ui.WeatherAppState
import com.minhdtm.example.weapose.presentation.ui.home.CurrentWeatherAppBar
import com.minhdtm.example.weapose.presentation.utils.Constants
import com.minhdtm.example.weapose.presentation.utils.toUVIndexAttention
import kotlinx.coroutines.flow.collectLatest

@Composable
fun SevenDaysWeather(
    appState: WeatherAppState,
    viewModel: SevenDaysWeatherViewModel = viewModel(),
) {
    val state by viewModel.state.collectAsState()

    // Get data from back
    LaunchedEffect(true) {
        appState.getDataFromNextScreen(Constants.Key.ADDRESS_NAME, "")?.collect {
            if (it.isNotBlank()) {
                viewModel.getWeatherByAddressName(addressName = it)
                appState.removeDataFromNextScreen<LatLng>(Constants.Key.ADDRESS_NAME)
            }
        }
    }

    LaunchedEffect(true) {
        appState.getDataFromNextScreen(Constants.Key.LAT_LNG, Constants.Default.LAT_LNG_DEFAULT)?.collect {
            if (it != LatLng(0.0, 0.0)) {
                viewModel.getWeatherByLocation(it)
                appState.removeDataFromNextScreen<LatLng>(Constants.Key.LAT_LNG)
            }
        }
    }

    // Locale change
    LaunchedEffect(true) {
        appState.localChange.collectLatest {
            viewModel.onRefresh(false)
        }
    }

    // Get event
    LaunchedEffect(true) {
        viewModel.event.collectLatest { event ->
            when (event) {
                is SevenDaysEvent.NavigateToSearchByText -> {
                    appState.navigateToSearchByText(Screen.SevenDaysWeather, event.latLng)
                }
            }
        }
    }

    SevenDaysWeatherScreen(
        state = state,
        snackbarHostState = appState.snackbarHost,
        onRefresh = {
            viewModel.onRefresh()
        },
        onDrawer = {
            appState.openDrawer()
        },
        onShowSnackbar = {
            appState.showSnackbar(it)
        },
        onDismissDialog = {
            viewModel.hideError()
        },
        onNavigateSearch = {
            viewModel.onNavigateToSearch()
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SevenDaysWeatherScreen(
    state: SevenDaysViewState,
    snackbarHostState: SnackbarHostState,
    onRefresh: () -> Unit = {},
    onShowSnackbar: (message: String) -> Unit = {},
    onDrawer: () -> Unit = {},
    onNavigateSearch: () -> Unit = {},
    onDismissDialog: () -> Unit = {},
) {
    WeatherScaffold(
        modifier = Modifier.fillMaxSize(),
        state = state,
        snackbarHostState = snackbarHostState,
        topBar = {
            CurrentWeatherAppBar(
                modifier = Modifier.statusBarsPadding(),
                title = stringResource(id = R.string.seven_days_in),
                city = state.address.ifBlank { stringResource(id = R.string.unknown_address) },
                onDrawer = onDrawer,
                onNavigateSearch = onNavigateSearch,
            )
        },
        onShowSnackbar = onShowSnackbar,
        onDismissErrorDialog = onDismissDialog,
    ) { _, viewState ->
        SwipeRefresh(
            state = SwipeRefreshState(viewState.isRefresh),
            onRefresh = onRefresh,
        ) {
            ListWeatherDay(
                modifier = Modifier.fillMaxSize(),
                list = viewState.listSevenDays,
            )
        }
    }
}

@Composable
fun ListWeatherDay(
    modifier: Modifier = Modifier,
    list: List<DayWeatherViewData> = emptyList(),
) {
    val paddingBottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(bottom = paddingBottom + 10.dp),
    ) {
        itemsIndexed(items = list) { _, item ->
            WeatherDayItem(
                modifier = Modifier.fillMaxWidth(),
                item = item,
            )
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun WeatherDayItem(
    modifier: Modifier = Modifier,
    item: DayWeatherViewData,
) {
    var isExpanded by rememberSaveable {
        mutableStateOf(false)
    }

    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .height(70.dp)
                .fillMaxWidth()
                .clickable {
                    isExpanded = !isExpanded
                }
                .padding(horizontal = 10.dp, vertical = 5.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column {
                Text(
                    text = item.dateTime,
                    modifier = Modifier.padding(bottom = 5.dp),
                    style = MaterialTheme.typography.titleSmall.copy(color = MaterialTheme.colorScheme.secondary),
                )

                Text(
                    text = item.weatherDetail,
                    style = MaterialTheme.typography.titleSmall.copy(color = MaterialTheme.colorScheme.inversePrimary),
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    modifier = Modifier
                        .size(50.dp)
                        .padding(end = 10.dp),
                    painter = painterResource(id = item.icon),
                    contentDescription = null,
                )

                Column {
                    Text(
                        text = stringResource(id = R.string.degrees_c, item.maxTemp.toString()),
                        modifier = Modifier.padding(bottom = 5.dp),
                        style = MaterialTheme.typography.titleSmall.copy(color = MaterialTheme.colorScheme.secondary),
                    )


                    Text(
                        text = stringResource(id = R.string.degrees_c, item.minTemp.toString()),
                        style = MaterialTheme.typography.titleSmall.copy(color = MaterialTheme.colorScheme.inversePrimary),
                    )
                }

                AnimatedContent(targetState = isExpanded, transitionSpec = {
                    if (!targetState) {
                        slideInVertically { height -> height } + fadeIn() with slideOutVertically { height -> -height } + fadeOut()
                    } else {
                        slideInVertically { height -> -height } + fadeIn() with slideOutVertically { height -> height } + fadeOut()
                    }.using(
                        SizeTransform(clip = false)
                    )
                }) { state ->
                    Icon(
                        imageVector = if (!state) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowUp,
                        contentDescription = null,
                        modifier = Modifier.padding(start = 10.dp),
                    )
                }
            }
        }

        AnimatedVisibility(visible = isExpanded) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 5.dp)
            ) {
                WeatherInformation(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 5.dp),
                    title = stringResource(id = R.string.wind),
                    description = stringResource(id = R.string.kilometer_per_hour, item.windSpeed),
                )

                WeatherInformation(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 5.dp),
                    title = stringResource(id = R.string.humidity),
                    description = stringResource(id = R.string.home_text_humidity, item.humidity),
                )

                WeatherInformation(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 5.dp),
                    title = stringResource(id = R.string.uv_index),
                    description = stringResource(id = item.uvIndex.toUVIndexAttention(), item.uvIndex.toString()),
                )

                WeatherInformation(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 5.dp),
                    title = stringResource(id = R.string.sunrise_sunset),
                    description = stringResource(id = R.string.sunrise_sunset, item.sunrise, item.sunset),
                )
            }
        }

        Divider(modifier = Modifier.fillMaxWidth(), color = MaterialTheme.colorScheme.inversePrimary)
    }
}

@Composable
fun WeatherInformation(
    modifier: Modifier = Modifier,
    title: String = "",
    description: String = "",
) {
    Row(modifier = modifier) {
        Text(
            text = title,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.titleSmall.copy(color = MaterialTheme.colorScheme.inversePrimary),
        )

        Text(
            text = description,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.titleSmall.copy(color = MaterialTheme.colorScheme.secondary),
        )
    }
}

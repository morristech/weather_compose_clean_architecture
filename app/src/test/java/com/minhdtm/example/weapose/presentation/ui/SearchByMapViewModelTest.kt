package com.minhdtm.example.weapose.presentation.ui

import androidx.lifecycle.SavedStateHandle
import com.minhdtm.example.weapose.base.BaseTest
import com.minhdtm.example.weapose.domain.usecase.GetAddressFromLocationUseCase
import com.minhdtm.example.weapose.domain.usecase.GetCurrentLocationUseCase
import com.minhdtm.example.weapose.domain.usecase.GetDarkModeGoogleMapUseCase
import com.minhdtm.example.weapose.domain.usecase.SetDarkModeGoogleMapUseCase
import com.minhdtm.example.weapose.presentation.ui.search.map.SearchByMapViewModel
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import io.mockk.spyk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class SearchByMapViewModelTest : BaseTest() {
    @MockK
    lateinit var savedStateHandle: SavedStateHandle

    @MockK
    lateinit var setDarkModeGoogleMapUseCase: SetDarkModeGoogleMapUseCase

    @MockK
    lateinit var getDarkModeGoogleMapUseCase: GetDarkModeGoogleMapUseCase

    @MockK
    lateinit var getAddressFromLocationUseCase: GetAddressFromLocationUseCase

    @MockK
    lateinit var getCurrentLocationUseCase: GetCurrentLocationUseCase

    lateinit var viewModel: SearchByMapViewModel

    @Before
    fun setUp() {
        viewModel = spyk(
            SearchByMapViewModel(
                savedStateHandle,
                setDarkModeGoogleMapUseCase,
                getDarkModeGoogleMapUseCase,
                getAddressFromLocationUseCase,
                getCurrentLocationUseCase,
            ),
            recordPrivateCalls = true,
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `test`() = runTest(mainDispatcherRule.testDispatcher) {

    }
}

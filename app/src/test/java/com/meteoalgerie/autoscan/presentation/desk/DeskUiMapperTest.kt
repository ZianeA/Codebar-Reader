package com.meteoalgerie.autoscan.presentation.desk

import com.meteoalgerie.autoscan.util.createDesk
import com.meteoalgerie.autoscan.util.createDeskUi
import io.mockk.clearAllMocks
import io.mockk.junit5.MockKExtension
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockKExtension::class)
internal class DeskUiMapperTest {
    /*private val mapper = DeskUiMapper()

    @BeforeEach
    internal fun setUp() {
        clearAllMocks()
    }

    @Test
    fun `map DeskUi to Desk`() {
        //Act
        val mappedDesk = mapper.map(createDeskUi())

        //Assert
        assertThat(mappedDesk).isEqualTo(createDesk())
    }

    @Test
    fun `map Desk to DeskUi`() {
        //Act
        val mappedDeskUi = mapper.mapReverse(createDesk())

        //Assert
        assertThat(mappedDeskUi).isEqualTo(createDeskUi())
    }*/
}
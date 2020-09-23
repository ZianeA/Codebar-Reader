package com.meteoalgerie.autoscan.data.desk

import com.meteoalgerie.autoscan.data.mapper.Mapper
import com.meteoalgerie.autoscan.data.equipment.EquipmentEntity
import com.meteoalgerie.autoscan.presentation.equipment.Equipment
import com.meteoalgerie.autoscan.util.createDesk
import com.meteoalgerie.autoscan.util.createDeskWithEquipmentsEntity
import com.meteoalgerie.autoscan.util.createEquipment
import com.meteoalgerie.autoscan.util.createEquipmentEntity
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockKExtension::class)
internal class DeskWithEquipmentsEntityMapperTest {
    private val equipmentMapper: Mapper<EquipmentEntity, Equipment> = mockk()
    private val mapper = DeskWithEquipmentsEntityMapper(equipmentMapper)

    @BeforeEach
    internal fun setUp() {
        clearAllMocks()
    }

    @Test
    fun `map DeskWithEquipmentsEntity to Desk`() {
        //Arrange
        every { equipmentMapper.map(createEquipmentEntity()) } returns createEquipment()

        //Act
        val mappedDesk = mapper.map(createDeskWithEquipmentsEntity())

        //Assert
        assertThat(mappedDesk).isEqualTo(createDesk())
    }

    @Test
    fun `map Desk to DeskWithEquipmentsEntity`() {
        //Arrange
        every { equipmentMapper.mapReverse(createEquipment()) } returns createEquipmentEntity()

        //Act
        val mappedDeskWithEquipmentsEntity = mapper.mapReverse(createDesk())

        //Assert
        assertThat(mappedDeskWithEquipmentsEntity).isEqualTo(createDeskWithEquipmentsEntity())
    }
}
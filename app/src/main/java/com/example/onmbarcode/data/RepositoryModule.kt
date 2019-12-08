package com.example.onmbarcode.data

import com.example.onmbarcode.data.desk.DeskEntity
import com.example.onmbarcode.data.desk.DeskResponseMapper
import com.example.onmbarcode.data.desk.DeskWithEquipmentsEntity
import com.example.onmbarcode.data.desk.DeskWithEquipmentsEntityMapper
import com.example.onmbarcode.data.equipment.EquipmentEntity
import com.example.onmbarcode.data.equipment.EquipmentEntityMapper
import com.example.onmbarcode.data.equipment.EquipmentResponseMapper
import com.example.onmbarcode.data.mapper.Mapper
import com.example.onmbarcode.presentation.desk.Desk
import com.example.onmbarcode.presentation.equipment.Equipment
import dagger.Binds
import dagger.Module

@Module
interface RepositoryModule {
    @Binds
    fun provideDeskWithEquipmentsEntityMapper(mapper: DeskWithEquipmentsEntityMapper): Mapper<DeskWithEquipmentsEntity, Desk>

    @Binds
    fun provideEquipmentEntityMapper(mapper: EquipmentEntityMapper): Mapper<EquipmentEntity, Equipment>

    @Binds
    fun provideEquipmentResponseMapper(mapper: EquipmentResponseMapper): Mapper<HashMap<*, *>, Equipment>

    @Binds
    fun provideDeskResponseMapper(mapper: DeskResponseMapper): Mapper<HashMap<*, *>, DeskEntity>
}
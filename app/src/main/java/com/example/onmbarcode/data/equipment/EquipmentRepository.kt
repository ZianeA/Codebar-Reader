package com.example.onmbarcode.data.equipment

import com.example.onmbarcode.data.mapper.Mapper
import com.example.onmbarcode.presentation.equipment.Equipment
import com.example.onmbarcode.presentation.equipment.Equipment.*
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EquipmentRepository @Inject constructor(
    private val equipmentDao: EquipmentDao,
    private val equipmentService: EquipmentService,
    private val equipmentEntityMapper: Mapper<EquipmentEntity, Equipment>,
    private val equipmentResponseMapper: Mapper<HashMap<*, *>, Equipment>
) {
    fun getEquipments(deskId: Int): Single<List<Equipment>> {
        return equipmentDao.getByDesk(deskId)
            .map { e -> e.map(equipmentEntityMapper::map) }
    }

    fun findEquipment(barcode: String): Maybe<Equipment> {
        return equipmentDao.getByBarcode(barcode)
            .map(equipmentEntityMapper::map)
    }

    // Update network before database to handle scan state
    // Update the database regardless of the network state.
    fun updateEquipment(equipment: Equipment): Completable {
        val scannedAndSyncedEquipment = equipment.copy(scanState = ScanState.ScannedAndSynced)
        return equipmentService.update(
            equipment.id,
            equipmentResponseMapper.mapReverse(scannedAndSyncedEquipment)
        )
            .onErrorResumeNext {
                equipmentDao.update(
                    equipmentEntityMapper.mapReverse(
                        equipment.copy(scanState = ScanState.ScannedButNotSynced)
                    )
                ).andThen(Completable.defer { Completable.error(it) })
            }
            .andThen(Completable.defer {
                //I'm getting unexpected behavior, I had to use defer.
                equipmentDao.update(equipmentEntityMapper.mapReverse(scannedAndSyncedEquipment))
            })
    }
}

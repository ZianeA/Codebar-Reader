package com.meteoalgerie.autoscan.service

import android.content.Context
import androidx.work.RxWorker
import androidx.work.WorkerParameters
import com.meteoalgerie.autoscan.data.equipment.EquipmentDao
import com.meteoalgerie.autoscan.data.equipment.EquipmentEntity
import com.meteoalgerie.autoscan.data.equipment.EquipmentEntityMapper
import com.meteoalgerie.autoscan.data.equipment.EquipmentService
import com.meteoalgerie.autoscan.data.mapper.Mapper
import com.meteoalgerie.autoscan.data.user.UserRepository
import com.meteoalgerie.autoscan.presentation.equipment.Equipment.*
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single

class SyncWorker(
    private val equipmentDao: EquipmentDao,
    private val equipmentService: EquipmentService,
    private val userRepository: UserRepository,
    private val equipmentEntityMapper: EquipmentEntityMapper,
    private val equipmentResponseMapper: Mapper<HashMap<*, *>, EquipmentEntity>,
    context: Context,
    workerParams: WorkerParameters
) :
    RxWorker(context, workerParams) {
    override fun createWork(): Single<Result> {
        return userRepository.getUser()
            .flatMap { user ->
                equipmentDao.getByScanState(ScanState.ScannedButNotSynced)
                    .flatMapObservable { Observable.fromIterable(it) }
                    .map {
                        object {
                            val id = it.id
                            val equipmentResponse =
                                equipmentResponseMapper.mapReverse(it.copy(scanState = ScanState.ScannedAndSynced))
                            val equipmentEntity = it.copy(scanState = ScanState.ScannedAndSynced)
                        }
                    }
                    .flatMapCompletable {
                        equipmentService.update(user, it.id, it.equipmentResponse)
                            .andThen(Completable.defer { equipmentDao.update(it.equipmentEntity) })
                    }.toSingle { }
            }
            .flatMap { Single.just(Result.success()) }
            .onErrorReturnItem(Result.retry())
    }
}
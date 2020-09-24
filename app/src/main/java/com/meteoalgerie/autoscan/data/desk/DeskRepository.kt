package com.meteoalgerie.autoscan.data.desk

import android.util.Log
import com.meteoalgerie.autoscan.data.KeyValueStore
import com.meteoalgerie.autoscan.data.PreferencesIntStore.Companion.EQUIPMENT_COUNT_KEY
import com.meteoalgerie.autoscan.data.PreferencesIntStore.Companion.EQUIPMENT_DOWNLOADED_COUNT_KEY
import com.meteoalgerie.autoscan.data.mapper.Mapper
import com.meteoalgerie.autoscan.data.equipment.EquipmentDao
import com.meteoalgerie.autoscan.data.equipment.Equipment
import com.meteoalgerie.autoscan.data.equipment.EquipmentService
import com.meteoalgerie.autoscan.data.user.UserRepository
import com.meteoalgerie.autoscan.presentation.desk.Desk
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.ceil

@Singleton
class DeskRepository @Inject constructor(
    private val deskDao: DeskDao,
    private val equipmentDao: EquipmentDao,
    private val userRepository: UserRepository, //Should probably use userDao instead
    private val store: KeyValueStore<Int>,
    private val deskService: DeskService,
    private val equipmentService: EquipmentService,
    private val deskEntityMapper: Mapper<DeskWithStatsEntity, Desk>,
    private val deskResponseMapper: Mapper<HashMap<*, *>, DeskEntity>,
    private val equipmentResponseMapper: Mapper<HashMap<*, *>, Equipment>
) {
    fun getScannedDesks(): Observable<List<Desk>> {
        return deskDao.getScanned()
            .map { deskEntities -> deskEntities.map { deskEntityMapper.map(it) } }
    }

    fun isDatabaseEmpty(): Single<Boolean> = deskDao.isEmpty()

    fun isDownloadComplete(): Boolean {
        val downloadedEquipment = store.get(EQUIPMENT_DOWNLOADED_COUNT_KEY, 0)
        val allEquipment = store.get(EQUIPMENT_COUNT_KEY, 0)

        return downloadedEquipment != 0 && downloadedEquipment >= allEquipment
    }

    fun downloadDatabase(): Observable<Int> {
        return userRepository.getUser()
            .flatMapObservable { user ->
                val downloadedEquipment = store.get(EQUIPMENT_DOWNLOADED_COUNT_KEY, 0)
                val allEquipment = store.get(EQUIPMENT_COUNT_KEY, 0)

                if (downloadedEquipment == 0 || downloadedEquipment < allEquipment) {
                    Single.just(downloadedEquipment == 0)
                        .flatMapCompletable {downloadDesks ->
                            if (downloadDesks) {
                                deskService.getAll(user)
                                    .map { list -> list.map { deskResponseMapper.map(it as HashMap<*, *>) } }
                                    .flatMapCompletable { deskDao.addAll(it) }
                            } else Completable.complete()
                        }
                        .andThen(equipmentService.getEquipmentCount(user))
                        .flatMapObservable { count ->
                            store.put(EQUIPMENT_COUNT_KEY, count)

                            val rangeStart =
                                ceil(downloadedEquipment.toDouble() / PAGE_SIZE).toInt()
                            val rangeCount =
                                ceil(count.toDouble() / PAGE_SIZE).toInt() - rangeStart

                            Observable.range(rangeStart, rangeCount)
                                .map {
                                    object {
                                        val offset = it * PAGE_SIZE
                                        val total = count
                                    }
                                }
                        }
                        .doAfterNext {
                            Log.d(
                                "No Tag",
                                "Equipment: ${it.offset} - ${it.offset + PAGE_SIZE}"
                            )
                        }
                        .concatMap { holder ->
                            equipmentService.get(user, holder.offset, PAGE_SIZE)
                                .map { list -> list.map { equipmentResponseMapper.map(it as HashMap<*, *>) } }
                                .flatMapObservable {
                                    equipmentDao.addAll(it)
                                        .andThen(Completable.defer {
                                            Completable.fromAction {
                                                store.add(
                                                    EQUIPMENT_DOWNLOADED_COUNT_KEY,
                                                    it.size,
                                                    0
                                                )
                                            }
                                        })
                                        .andThen(
                                            Observable.just(
                                                ceil(holder.offset * 100f / holder.total).toInt()
                                            )
                                        )
                                }
                        }
                } else Observable.empty<Int>()
            }
    }

    fun findDesk(barcode: String): Maybe<Desk> {
        return deskDao.getByBarcode(barcode)
            .map(deskEntityMapper::map)
    }

    fun getDeskById(id: Int): Single<Desk> {
        return deskDao.getById(id)
            .map(deskEntityMapper::map)
    }

    fun updateDesk(desk: Desk): Completable {
        val deskEntity = deskEntityMapper.mapReverse(desk).deskEntity
        return deskDao.update(deskEntity)
    }

    fun deleteAllDesks(): Completable = deskDao.deleteAll()

    companion object {
        private const val PAGE_SIZE = 500
    }
}

package com.meteoalgerie.autoscan.desk

import androidx.room.*
import com.meteoalgerie.autoscan.equipment.Equipment
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single

@Dao
interface DeskDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addAll(desk: List<DeskEntity>): Completable

    @Insert
    fun addAll(desk: List<DeskEntity>, equipments: List<Equipment>)

    @Query(
        """
        SELECT 
            COUNT(e.id) AS equipmentCount,
            SUM(CASE WHEN e.scanState = 'ScannedAndSynced' THEN 1 ELSE 0 END) AS syncedEquipmentCount,
            SUM(CASE WHEN e.scanState = 'ScannedButNotSynced' THEN 1 ELSE 0 END) AS notSyncedEquipmentCount,
            d.id,
            d.barcode,
            d.isScanned,
            d.scanDate,
            d.isHidden
        FROM DeskEntity d
        LEFT JOIN EquipmentEntity e
            ON d.id = e.deskId
        WHERE d.isScanned = 1 AND d.isHidden = 0
        GROUP BY d.id, d.barcode, d.isScanned, d.scanDate
        ORDER BY d.scanDate Desc
        """
    )
    fun getScanned(): Observable<List<DeskWithStatsEntity>>

    @Query(
        """
        SELECT 
            COUNT(e.id) AS equipmentCount,
            SUM(CASE WHEN e.scanState = 'ScannedAndSynced' THEN 1 ELSE 0 END) AS syncedEquipmentCount,
            SUM(CASE WHEN e.scanState = 'ScannedButNotSynced' THEN 1 ELSE 0 END) AS notSyncedEquipmentCount,
            d.id,
            d.barcode,
            d.isScanned,
            d.scanDate,
            d.isHidden
        FROM DeskEntity d
        LEFT JOIN EquipmentEntity e
            ON d.id = e.deskId
        WHERE d.barcode=:barcode
        GROUP BY d.id, d.barcode, d.isScanned, d.scanDate
        """
    )
    fun getByBarcode(barcode: String): Single<DeskWithStatsEntity>

    @Query(
        """
        SELECT 
            COUNT(e.id) AS equipmentCount,
            SUM(CASE WHEN e.scanState = 'ScannedAndSynced' THEN 1 ELSE 0 END) AS syncedEquipmentCount,
            SUM(CASE WHEN e.scanState = 'ScannedButNotSynced' THEN 1 ELSE 0 END) AS notSyncedEquipmentCount,
            d.id,
            d.barcode,
            d.isScanned,
            d.scanDate,
            d.isHidden
        FROM DeskEntity d
        LEFT JOIN EquipmentEntity e
            ON d.id = e.deskId
        WHERE d.id=:id
        GROUP BY d.id, d.barcode, d.isScanned, d.scanDate
        """
    )
    fun getById(id: Int): Observable<DeskWithStatsEntity>

    @Update
    fun update(desk: DeskEntity): Completable

    @Update
    fun updateAll(desks: List<DeskEntity>): Completable

    @Query("SELECT CASE WHEN EXISTS(SELECT 1 from DeskEntity) THEN 0 ELSE 1 END")
    fun isEmpty(): Single<Boolean>

    @Query("DELETE FROM DeskEntity")
    fun deleteAll(): Completable
}
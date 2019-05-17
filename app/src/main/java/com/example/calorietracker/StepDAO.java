package com.example.calorietracker;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.Date;
import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface StepDAO {
    @Query("SELECT * FROM step")
    List<Step> getAll();

    @Query("SELECT * FROM step WHERE date = :date LIMIT 1")
    Step findByDate(String date);

    @Insert
    void insertAll(Step... steps);

    @Insert
    long insert(Step step);

    @Delete
    void delete(Step step);

    @Update(onConflict = REPLACE)
    public void updateStep(Step... steps);

    @Query("DELETE FROM step")
    void deleteAll();
}

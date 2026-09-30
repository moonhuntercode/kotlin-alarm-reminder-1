package com.victorcode.alarmreminder2.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

// 📦 Versión 0.0.3
// 📄 Archivo: data/MedicineReminderEntityTest.kt

class MedicineReminderEntityTest {

    @Test
    fun `test entity creation and default values`() {
        val entity = MedicineReminderEntity(
            medicineName = "Aspirina",
            intervalMinutes = 30,
            targetDoses = 3,
            durationDays = 1,
            endDateMillis = System.currentTimeMillis() + 86400000L
        )

        assertEquals("Aspirina", entity.medicineName)
        assertEquals(30, entity.intervalMinutes)
        assertEquals(3, entity.targetDoses)
        assertEquals(1, entity.durationDays)
        assertEquals(0, entity.completedDoses)
        assertFalse(entity.isGoalAchieved)
        assertFalse(entity.isRunning)
        assertEquals(0L, entity.targetAlarmTimeMillis)
    }

    @Test
    fun `test goal achievement logic simulation`() {
        var entity = MedicineReminderEntity(
            medicineName = "Vitamina C",
            intervalMinutes = 60,
            targetDoses = 2,
            durationDays = 3,
            endDateMillis = System.currentTimeMillis() + (86400000L * 3)
        )

        // Simula la primera toma
        entity = entity.copy(completedDoses = entity.completedDoses + 1)
        assertFalse("Goal shouldn't be achieved yet", entity.isGoalAchieved)

        // Simula la segunda toma (Meta cumplida)
        val newCompleted = entity.completedDoses + 1
        entity = entity.copy(
            completedDoses = newCompleted,
            isGoalAchieved = newCompleted >= entity.targetDoses
        )
        
        assertTrue("Goal should be achieved", entity.isGoalAchieved)
    }
}

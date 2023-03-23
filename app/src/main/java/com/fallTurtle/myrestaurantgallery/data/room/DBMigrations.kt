package com.fallTurtle.myrestaurantgallery.data.room

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**첫번째 DB 마이그레이션.
 * (변경사항 1. 이미지 로딩 형태 변경을 위한 imagePath 컬럼 추가).
 * (변경사항 2. 혼동 방지를 위해 기존의 image 컬럼을 imageName 컬럼으로 이름 변경).
 **/
val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE Info ADD COLUMN imagePath TEXT")
        database.execSQL("AlTER TABLE Info RENAME COLUMN image TO imageName")
    }
}

/**두번째 DB 마이그레이션.
 * (변경사항. Info 이름의 모호성 해결을 위해 RestaurantInfo 테이블로 이름 변경).
 **/
val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE Info RENAME TO RestaurantInfo")
    }
}


/**세번째 DB 마이그레이션.
 * (변경사항. 사용자의 입장에서 주소는 불필요하다고 결론. 무의미한 데이터인 주소 컬럼은 삭제).
 **/
val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(database: SupportSQLiteDatabase) {
        with(database){
           execSQL("CREATE TEMPORARY TABLE backup(" +
                    "'dbID' TEXT NOT NULL, 'imageName' TEXT, 'imagePath' TEXT," +
                    "'name' TEXT NOT NULL, 'categoryNum' INTEGER NOT NULL, 'category' TEXT NOT NULL," +
                    "'rate' INTEGER NOT NULL, 'memo' TEXT NOT NULL, 'date' TEXT NOT NULL," +
                    "'latitude' REAL NOT NULL, 'longitude' REAL NOT NULL, PRIMARY KEY('dbID'))")
            execSQL("INSERT INTO backup SELECT dbID, imageName, imagePath, name, categoryNum," +
                    "category, rate, memo, date, latitude, longitude FROM RestaurantInfo")
            execSQL("DROP TABLE RestaurantInfo")
            execSQL("CREATE TABLE RestaurantInfo(" +
                    "'dbID' TEXT NOT NULL, 'imageName' TEXT, 'imagePath' TEXT," +
                    "'name' TEXT NOT NULL, 'categoryNum' INTEGER NOT NULL, 'category' TEXT NOT NULL," +
                    "'rate' INTEGER NOT NULL, 'memo' TEXT NOT NULL, 'date' TEXT NOT NULL," +
                    "'latitude' REAL NOT NULL, 'longitude' REAL NOT NULL, PRIMARY KEY('dbID'))")
            execSQL("INSERT INTO RestaurantInfo SELECT * FROM backup")
            execSQL("DROP TABLE backup")
        }

    }
}
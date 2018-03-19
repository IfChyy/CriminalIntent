package com.example.user.criminalintent.database;

/**
 * Created by Ivo Georgiev(IfChyy)
 * Used for creating the table name and table colums for a safer java reference
 */

public class CrimeSQLliteTable {

    public static final class CrimeTable {
        public static final String NAME = "crimes";


        public static final class Columns {
            public static final String UUID = "uuid";
            public static final String TITLE = "title";
            public static final String DATE = "date";
            public static final String TIME = "time";
            public static final String SOLVED = "solved";
        }
    }

}

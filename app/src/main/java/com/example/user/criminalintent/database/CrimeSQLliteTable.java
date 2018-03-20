package com.example.user.criminalintent.database;

/**
 * Created by Ivo Georgiev(IfChyy)
 * CrimeSQLiteTabel class is used for initial creation of the DB
 * its name, and collum names
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
            public static final String SUSPECT = "suspect";
            public static final String SUSPECT_NUMBER = "suspect_number";
        }
    }

}

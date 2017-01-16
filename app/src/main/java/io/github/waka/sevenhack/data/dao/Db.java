package io.github.waka.sevenhack.data.dao;

import android.database.Cursor;

import org.threeten.bp.LocalDateTime;
import org.threeten.bp.ZoneOffset;
import org.threeten.bp.ZonedDateTime;
import org.threeten.bp.format.DateTimeFormatter;

import java.util.Date;

final class Db {
    private static final int BOOLEAN_TRUE = 1;

    static String getString(Cursor cursor, String columnName) {
        return cursor.getString(cursor.getColumnIndexOrThrow(columnName));
    }

    static boolean getBoolean(Cursor cursor, String columnName) {
        return getInt(cursor, columnName) == BOOLEAN_TRUE;
    }

    static long getLong(Cursor cursor, String columnName) {
        return cursor.getLong(cursor.getColumnIndexOrThrow(columnName));
    }

    static int getInt(Cursor cursor, String columnName) {
        return cursor.getInt(cursor.getColumnIndexOrThrow(columnName));
    }

    static Date getDate(Cursor cursor, String columnName) {
        String value = getString(cursor, columnName);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        LocalDateTime dateTime = LocalDateTime.parse(value, formatter);
        ZonedDateTime zonedDateTime = dateTime.atZone(ZoneOffset.systemDefault());
        long time = zonedDateTime.toInstant().toEpochMilli();
        return new Date(time);
    }

    private Db() {
        throw new AssertionError("No instances.");
    }
}

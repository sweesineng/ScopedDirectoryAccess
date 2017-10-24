package com.homenas.netdrive;

import android.os.Environment;
import android.provider.DocumentsContract;

/**
 * Created by engss on 23/10/2017.
 */

public class Constant {

    public static final String DEFAULT_DIRECTORY = "DOWNLOADS";
    public static final int SPAN_COUNT = 2;

    public static final String KEY_LAYOUT_MANAGER = "layoutManager";

    public enum LayoutManagerType {GRID_LAYOUT_MANAGER, LINEAR_LAYOUT_MANAGER}

    public static final int OPEN_DIRECTORY_REQUEST_CODE = 1;

    public static final String SELECTED_DIRECTORY_KEY = "selected_directory";

    public static final String DIRECTORY_ENTRIES_KEY = "directory_entries";

    public static String getDirectoryName(String name) {
        switch (name) {
            case "ALARMS":
                return Environment.DIRECTORY_ALARMS;
            case "DCIM":
                return Environment.DIRECTORY_DCIM;
            case "DOCUMENTS":
                return Environment.DIRECTORY_DOCUMENTS;
            case "DOWNLOADS":
                return Environment.DIRECTORY_DOWNLOADS;
            case "MOVIES":
                return Environment.DIRECTORY_MOVIES;
            case "MUSIC":
                return Environment.DIRECTORY_MUSIC;
            case "NOTIFICATIONS":
                return Environment.DIRECTORY_NOTIFICATIONS;
            case "PICTURES":
                return Environment.DIRECTORY_PICTURES;
            case "PODCASTS":
                return Environment.DIRECTORY_PODCASTS;
            case "RINGTONES":
                return Environment.DIRECTORY_RINGTONES;
            default:
                throw new IllegalArgumentException("Invalid directory representation: " + name);
        }
    }

    public static final String[] DIRECTORY_SELECTION = new String[]{
            DocumentsContract.Document.COLUMN_DISPLAY_NAME,
            DocumentsContract.Document.COLUMN_MIME_TYPE,
            DocumentsContract.Document.COLUMN_DOCUMENT_ID,
    };

}

package com.homenas.netdrive;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.provider.DocumentsContract;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import static com.homenas.netdrive.Constant.DEFAULT_DIRECTORY;
import static com.homenas.netdrive.Constant.DIRECTORY_ENTRIES_KEY;
import static com.homenas.netdrive.Constant.DIRECTORY_SELECTION;
import static com.homenas.netdrive.Constant.KEY_LAYOUT_MANAGER;
import static com.homenas.netdrive.Constant.LayoutManagerType;
import static com.homenas.netdrive.Constant.OPEN_DIRECTORY_REQUEST_CODE;
import static com.homenas.netdrive.Constant.SPAN_COUNT;

import static com.homenas.netdrive.R.id.recyclerView;

public class RecyclerViewFragment extends Fragment {
    private Activity mActivity;
    private StorageManager mStorageManager;
    private DirectoryEntryAdapter mAdapter;
    protected RecyclerView mRecyclerView;
    protected RecyclerView.LayoutManager mLayoutManager;
    protected LayoutManagerType mCurrentLayoutManagerType;
    private ArrayList<DirectoryEntry> mDirectoryEntries;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = getActivity();
        mStorageManager = mActivity.getSystemService(StorageManager.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.container_fragment, container, false);
    }

    @Override
    public void onViewCreated(final View rootView, Bundle savedInstanceState) {
        super.onViewCreated(rootView, savedInstanceState);
        setAccessIntent(DEFAULT_DIRECTORY);
        mRecyclerView = rootView.findViewById(recyclerView);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mCurrentLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER;
        if (savedInstanceState != null) {
            mCurrentLayoutManagerType = (LayoutManagerType) savedInstanceState.getSerializable(KEY_LAYOUT_MANAGER);
            mDirectoryEntries = savedInstanceState.getParcelableArrayList(DIRECTORY_ENTRIES_KEY);
            mAdapter = new DirectoryEntryAdapter(mDirectoryEntries);
            Log.i("madapter", String.valueOf(mAdapter.getItemCount()));
        }else{
            mDirectoryEntries = new ArrayList<>();
            mAdapter = new DirectoryEntryAdapter(mDirectoryEntries);
        }
        setRecyclerViewLayoutManager(mCurrentLayoutManagerType);
        mRecyclerView.setAdapter(mAdapter);
    }

    public void setRecyclerViewLayoutManager(LayoutManagerType layoutManagerType) {
        int scrollPosition = 0;
        if (mRecyclerView.getLayoutManager() != null) {
            scrollPosition = ((LinearLayoutManager) mRecyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition();
        }
        switch (layoutManagerType) {
            case GRID_LAYOUT_MANAGER:
                mLayoutManager = new GridLayoutManager(getActivity(), SPAN_COUNT);
                mCurrentLayoutManagerType = LayoutManagerType.GRID_LAYOUT_MANAGER;
                break;
            case LINEAR_LAYOUT_MANAGER:
                mLayoutManager = new LinearLayoutManager(getActivity());
                mCurrentLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER;
                break;
            default:
                mLayoutManager = new LinearLayoutManager(getActivity());
                mCurrentLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER;
        }
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.scrollToPosition(scrollPosition);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == OPEN_DIRECTORY_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            getActivity().getContentResolver().takePersistableUriPermission(data.getData(), Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            updateDirectoryEntries(data.getData());
        }
    }

    public void updateDirectoryEntries(Uri uri) {
        Log.i("Update", uri.toString());
        mDirectoryEntries.clear();
        ContentResolver contentResolver = getActivity().getContentResolver();
        Uri docUri = DocumentsContract.buildDocumentUriUsingTree(uri, DocumentsContract.getTreeDocumentId(uri));
        Uri childrenUri = DocumentsContract.buildChildDocumentsUriUsingTree(uri, DocumentsContract.getTreeDocumentId(uri));

        try (Cursor docCursor = contentResolver.query(docUri, DIRECTORY_SELECTION, null, null, null)) {
            while (docCursor != null && docCursor.moveToNext()) {
//                mCurrentDirectoryTextView.setText(docCursor.getString(docCursor.getColumnIndex(DocumentsContract.Document.COLUMN_DISPLAY_NAME)));
                Log.i("mCurrentDirectoryTV",docCursor.getString(docCursor.getColumnIndex(DocumentsContract.Document.COLUMN_DISPLAY_NAME)));
            }
        }

        try (Cursor childCursor = contentResolver.query(childrenUri, DIRECTORY_SELECTION, null, null, null)) {
            while (childCursor != null && childCursor.moveToNext()) {
                DirectoryEntry entry = new DirectoryEntry();
                entry.fileName = childCursor.getString(childCursor.getColumnIndex(DocumentsContract.Document.COLUMN_DISPLAY_NAME));
                entry.mimeType = childCursor.getString(childCursor.getColumnIndex(DocumentsContract.Document.COLUMN_MIME_TYPE));
                mDirectoryEntries.add(entry);
            }
            mAdapter.setDirectoryEntries(mDirectoryEntries);
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
//        outState.putString(SELECTED_DIRECTORY_KEY, mCurrentDirectoryTextView.getText().toString());
        outState.putParcelableArrayList(DIRECTORY_ENTRIES_KEY, mDirectoryEntries);
    }

    public void setAccessIntent(String selection){
        String directoryName = Constant.getDirectoryName(selection);
        StorageVolume storageVolume = mStorageManager.getPrimaryStorageVolume();
        Intent intent = storageVolume.createAccessIntent(directoryName);
        startActivityForResult(intent, OPEN_DIRECTORY_REQUEST_CODE);
    }
}

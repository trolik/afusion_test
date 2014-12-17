package com.trolik77.afusion.afusiontest;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.trolik77.afusion.afusiontest.dto.ImageData;
import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class RecyclerViewFragment extends Fragment {

    public static final String DATA = "DATA";

    protected List<ImageData> images = new ArrayList<>();

    protected RecyclerView mRecyclerView;
    protected CustomAdapter mAdapter;
    private TextView emptyView;
    protected RecyclerView.LayoutManager mLayoutManager;

    private Handler handler;

    public RecyclerViewFragment() {}

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new CustomAdapter(getActivity(), images);
        mRecyclerView.setAdapter(mAdapter);

        emptyView = (TextView) rootView.findViewById(R.id.empty_view);

        getLoaderManager().initLoader(1, new Bundle(), imageLoader);

        handler = new Handler() {
            @Override public void handleMessage(Message msg) {
                final Bundle bundle = msg.getData();
                if (bundle != null) {
                    mRecyclerView.post(new Runnable() {
                        @Override public void run() {
                            // UI thread
                            ImageData imageData = (ImageData) bundle.getSerializable(DATA);
                            mAdapter.addItems(imageData);

                            if (emptyView.getVisibility() != View.GONE) {
                                emptyView.setVisibility(View.GONE);
                            }

                            getActivity().invalidateOptionsMenu();

                            Log.d(RecyclerViewFragment.class.getSimpleName(), "file: " + imageData.path + ", w = " + imageData.width + ", h = " + imageData.height);
                        }
                    });
                }
            }
        };

        return rootView;
    }

    LoaderManager.LoaderCallbacks imageLoader = new LoaderManager.LoaderCallbacks<List<String>>() {
        @Override public Loader<List<String>> onCreateLoader(int id, Bundle args) {
            return new AsyncTaskLoader<List<String>>(getActivity()) {

                private boolean canceled = false;

                @Override public List<String> loadInBackground() {
                    return getFileList(Environment.getExternalStorageDirectory().getPath(), 0);
                }

                @Override protected void onStartLoading() {
                    super.onStartLoading();
                    forceLoad();
                }

                @Override protected void onStopLoading() {
                    super.onStopLoading();

                    canceled = true;
                }

                private List<String> getFileList(String path, int recursionLevel) {
                    List<String> files = new ArrayList<>();

                    ImageFileFilter imageFileFilter = new ImageFileFilter();

                    File file = new File(path);
                    if (file.exists()) {
                        if (file.isDirectory() && file.listFiles() != null) {
                            for (File f : file.listFiles(imageFileFilter)) {
                                if (canceled) {
                                    return files;
                                }

                                if (f.isDirectory()) {
                                    files.addAll(getFileList(f.getPath(), recursionLevel++));
                                } else {
                                    Drawable drawable = Drawable.createFromPath(f.getPath());
                                    if (drawable != null) {
                                        notifyFileFound(f, drawable);
                                    }
                                }
                            }
                        }
                    }

                    return files;
                }
            };
        }

        @Override public void onLoadFinished(Loader<List<String>> loader, List<String> data) {
            Log.d(RecyclerViewFragment.class.getSimpleName(), "onLoadFinished");

            emptyView.setText("No media found on " + Environment.getExternalStorageDirectory().getPath());

            Toast.makeText(getActivity(), "Done", Toast.LENGTH_SHORT).show();
        }



        @Override public void onLoaderReset(Loader<List<String>> loader) {}
    };

    @Override public void onStop() {
        super.onStop();

        if (getLoaderManager().hasRunningLoaders()) {
            getLoaderManager().getLoader(1).stopLoading();
        }
    }

    private void notifyFileFound(File f, Drawable drawable) {
        ImageData imageData = new ImageData();
        imageData.name = f.getName();
        imageData.path = f.getPath();
        imageData.height = drawable.getIntrinsicHeight();
        imageData.width = drawable.getIntrinsicWidth();

        Bundle bundle = new Bundle();
        bundle.putSerializable(DATA, imageData);

        Message message = new Message();
        message.setData(bundle);
        handler.dispatchMessage(message);
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);

        MenuItem item = menu.findItem(R.id.action_value);
        if (mAdapter.getItemCount() > 0) {
            item.setTitle(Integer.toString(mAdapter.getItemCount()) + " images found");
        }
    }
}

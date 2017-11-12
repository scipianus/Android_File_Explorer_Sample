package com.scipianus.finder;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.scipianus.finder.model.Directory;
import com.scipianus.finder.model.FileSystem;
import com.scipianus.finder.recyclerview.DirectoryAdapter;
import com.scipianus.finder.utils.NetworkUtils;

import java.net.URL;

public class MainActivity extends AppCompatActivity {

    public static final String[] inputFiles = {"input1.json", "input2.json", "input3.json"};

    private ProgressBar mProgressBar;
    private TextView mErrorMessage;
    private LinearLayout mResultsView;
    private RecyclerView mLeftView;
    private RecyclerView mRightView;

    private DirectoryAdapter mLeftAdapter;
    private DirectoryAdapter mRightAdapter;

    private DevAcademyAsyncTask mDevAcademyAsyncTask;
    private FileSystem mRoot;
    private Directory mCurrentDirectory;
    private Gson mGson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mProgressBar = findViewById(R.id.loading_bar);
        mErrorMessage = findViewById(R.id.error_message);
        mResultsView = findViewById(R.id.results_view);
        mLeftView = findViewById(R.id.left_recycler_view);
        mRightView = findViewById(R.id.right_recycler_view);

        mLeftAdapter = new DirectoryAdapter(new LeftClickListener());
        mRightAdapter = new DirectoryAdapter(new RightClickListener());

        mLeftView.setLayoutManager(new LinearLayoutManager(this));
        mLeftView.setHasFixedSize(false);
        mLeftView.setAdapter(mLeftAdapter);

        mRightView.setLayoutManager(new LinearLayoutManager(this));
        mRightView.setHasFixedSize(false);
        mRightView.setAdapter(mRightAdapter);

        mGson = new Gson();

        loadData(inputFiles[0]);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.input_options, menu);

        boolean shouldShowBackButton = (mCurrentDirectory != null);
        MenuItem backButton = menu.findItem(R.id.back_button);
        if (backButton != null) {
            backButton.setVisible(shouldShowBackButton);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.back_button:
                onBackPressed();
                return true;
            case R.id.inputOption1:
                loadData(inputFiles[0]);
                return true;
            case R.id.inputOption2:
                loadData(inputFiles[1]);
                return true;
            case R.id.inputOption3:
                loadData(inputFiles[2]);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        if (mCurrentDirectory.getParent() != null) {
            if (mCurrentDirectory.getParent().getParent() == null) {
                mLeftAdapter.setFileSystem(mRoot);
            } else {
                mLeftAdapter.setFileSystem(new FileSystem(mCurrentDirectory.getParent().getParent()));
            }
            mRightAdapter.setFileSystem(new FileSystem(mCurrentDirectory.getParent()));
            mCurrentDirectory = mCurrentDirectory.getParent();
        } else {
            mLeftAdapter.setFileSystem(mRoot);
            mRightAdapter.setFileSystem(null);
            mCurrentDirectory = null;
        }

        invalidateOptionsMenu();
    }

    private void loadData(String inputFile) {
        if (mDevAcademyAsyncTask != null) {
            mDevAcademyAsyncTask.cancel(true);
        }
        mDevAcademyAsyncTask = new DevAcademyAsyncTask();
        mDevAcademyAsyncTask.execute(inputFile);
    }

    private void bindData(FileSystem fileSystem) {
        mRoot = fileSystem;
        mLeftAdapter.setFileSystem(mRoot);
        mRightAdapter.setFileSystem(null);
    }

    class DevAcademyAsyncTask extends AsyncTask<String, Void, FileSystem> {

        @Override
        protected void onPreExecute() {
            mProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected FileSystem doInBackground(String... strings) {
            if (strings == null || strings.length == 0)
                return null;
            String inputFile = strings[0];
            URL queryURL = NetworkUtils.buildUrl(inputFile);
            String jsonResponse = null;
            try {
                jsonResponse = NetworkUtils.getResponseFromHttpUrl(queryURL);
                FileSystem root = mGson.fromJson(jsonResponse, FileSystem.class);
                root.assignParents();
                return root;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(FileSystem fileSystem) {
            mProgressBar.setVisibility(View.INVISIBLE);

            if (fileSystem == null) {
                mErrorMessage.setVisibility(View.VISIBLE);
                mResultsView.setVisibility(View.INVISIBLE);
            } else {
                bindData(fileSystem);
                mErrorMessage.setVisibility(View.INVISIBLE);
                mResultsView.setVisibility(View.VISIBLE);
            }
        }
    }

    class LeftClickListener implements DirectoryAdapter.DirectoryClickListener {

        @Override
        public void onDirectoryClicked(Directory directory) {
            mCurrentDirectory = directory;
            mRightAdapter.setFileSystem(new FileSystem(directory));
            invalidateOptionsMenu();
        }
    }

    class RightClickListener implements DirectoryAdapter.DirectoryClickListener {

        @Override
        public void onDirectoryClicked(Directory directory) {
            mCurrentDirectory = directory;
            mLeftAdapter.setFileSystem(new FileSystem(directory.getParent()));
            mRightAdapter.setFileSystem(new FileSystem(directory));
            invalidateOptionsMenu();
        }
    }
}

package com.scipianus.finder.recyclerview;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.scipianus.finder.R;
import com.scipianus.finder.model.Directory;
import com.scipianus.finder.model.FileSystem;

public class DirectoryAdapter extends RecyclerView.Adapter<DirectoryAdapter.DirectoryViewHolder> {

    private FileSystem mFileSystem;
    private DirectoryClickListener mClickListener;

    public DirectoryAdapter(DirectoryClickListener clickListener) {
        this.mClickListener = clickListener;
    }

    @Override
    public DirectoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new DirectoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DirectoryViewHolder holder, int position) {
        holder.bind(mFileSystem.getFileSystem().get(position));
    }

    @Override
    public int getItemCount() {
        if (mFileSystem == null || mFileSystem.getFileSystem() == null)
            return 0;
        return mFileSystem.getFileSystem().size();
    }

    public void setFileSystem(FileSystem fileSystem) {
        mFileSystem = fileSystem;
        notifyDataSetChanged();
    }

    public interface DirectoryClickListener {
        void onDirectoryClicked(Directory directory);
    }

    class DirectoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private Directory directory;
        private TextView mItemName;
        private ImageView mFolderImage;
        private ImageView mFileImage;
        private boolean isClickable;

        public DirectoryViewHolder(View itemView) {
            super(itemView);

            mItemName = itemView.findViewById(R.id.item_name);
            mFolderImage = itemView.findViewById(R.id.item_image_folder);
            mFileImage = itemView.findViewById(R.id.item_image_file);

            itemView.setOnClickListener(this);
        }

        public void bind(Directory directory) {
            this.directory = directory;
            isClickable = directory.getType().equals("directory");
            mFolderImage.setVisibility(isClickable ? View.VISIBLE : View.INVISIBLE);
            mFileImage.setVisibility(isClickable ? View.INVISIBLE : View.VISIBLE);
            if (directory == null) {
                mItemName.setText(R.string.null_directory);
            } else {
                mItemName.setText(directory.getName());
            }
        }

        @Override
        public void onClick(View v) {
            if (!isClickable)
                return;
            mClickListener.onDirectoryClicked(directory);
        }
    }
}

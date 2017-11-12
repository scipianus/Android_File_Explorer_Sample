package com.scipianus.finder.model;


import com.google.gson.annotations.SerializedName;

import java.util.List;

public class FileSystem {
    @SerializedName("file_system")
    private List<Directory> fileSystem;

    public FileSystem(List<Directory> fileSystem) {
        this.fileSystem = fileSystem;

        assignParents();
    }

    public FileSystem(Directory directory) {
        this.fileSystem = directory.getContents();

        assignParents();
    }

    public void assignParents() {
        for (Directory directory : fileSystem) {
            directory.assignParents();
        }
    }

    public List<Directory> getFileSystem() {
        return fileSystem;
    }

    public void setFileSystem(List<Directory> fileSystem) {
        this.fileSystem = fileSystem;
    }
}

package com.scipianus.finder.model;

import java.util.List;

public class Directory {
    private Integer id;
    private String type;
    private String name;
    private List<Directory> contents;
    private Directory parent;

    public Directory(Integer id, String type, String name, List<Directory> contents) {
        this.id = id;
        this.type = type;
        this.name = name;
        this.contents = contents;
    }

    public void assignParents() {
        for (Directory directory : contents) {
            directory.setParent(this);
            directory.assignParents();
        }
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Directory> getContents() {
        return contents;
    }

    public void setContents(List<Directory> contents) {
        this.contents = contents;
    }

    public Directory getParent() {
        return parent;
    }

    public void setParent(Directory parent) {
        this.parent = parent;
    }
}

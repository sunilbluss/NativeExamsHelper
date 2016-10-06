package com.grudus.nativeexamshelper.helpers;

import java.util.List;

public class ListWrapper<T> {
    private List<T> list;

    public ListWrapper(List<T> list) {
        this.list = list;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }
}

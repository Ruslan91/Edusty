package ru.edusty.android.Classes;

/**
 * Created by Руслан on 25.07.2014.
 */
public class Response<T> {
    T Item;
    String Status;

    public T getItem() {
        return Item;
    }

    public void setItem(T item) {
        Item = item;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }
}

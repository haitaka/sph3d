package me.haitaka.sph3d.utils;

import java.lang.ref.WeakReference;

public class Ref<T> {
    private final WeakReference<T> weakRef;

    public Ref(T data) {
        assert data != null;
        weakRef = new WeakReference<>(data);
    }

    private static int i = 0;

    public T get() {
        i++;
        if (i % 1000000 == 0) {
            System.gc();
        }
        assert weakRef.get() != null;
        return weakRef.get();
    }
}

package com.alibaba.tac.sdk.tangram4tac.utils;

/**
 * Created by longerian on 2017/10/31.
 */
public class Pair<F, S> {
    public final F first;
    public final S second;

    public Pair(F first, S second) {
        this.first = first;
        this.second = second;
    }

    public boolean equals(Object o) {
        if(!(o instanceof Pair)) {
            return false;
        } else {
            Pair p = (Pair)o;
            return objectsEqual(p.first, this.first) && objectsEqual(p.second, this.second);
        }
    }

    private static boolean objectsEqual(Object a, Object b) {
        return a == b || a != null && a.equals(b);
    }

    public int hashCode() {
        return (this.first == null?0:this.first.hashCode()) ^ (this.second == null?0:this.second.hashCode());
    }

    public static <A, B> Pair<A, B> create(A a, B b) {
        return new Pair(a, b);
    }
}

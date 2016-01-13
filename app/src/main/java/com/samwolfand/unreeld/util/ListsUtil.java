package com.samwolfand.unreeld.util;

import java.util.Collection;


public final class ListsUtil {

    public static <E> boolean isEmpty(Collection<E> list) {
        return (list == null || list.size() == 0);
    }

}

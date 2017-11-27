package org.java.cache;

import java.io.Serializable;

abstract class AbstractCache implements Cache {

    // Удаление значения из кэша
    abstract Serializable remove(String key);
}

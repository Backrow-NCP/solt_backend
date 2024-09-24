package org.backrow.solt.fetcher;

import java.util.List;

public interface Fetcher {
    List<T> fetch();
}

package com.github.alcereo;

import java.time.Instant;
import java.util.UUID;

public interface HistoryItem extends Comparable<HistoryItem>{

    UUID getCursor();

    String getAtmId();

    Instant getTimestamp();

    @Override
    default int compareTo(HistoryItem o) {
        int result = o.getTimestamp().compareTo(getTimestamp());
        return result == 0 ? o.getCursor().compareTo(getCursor()) : result;
    }

}

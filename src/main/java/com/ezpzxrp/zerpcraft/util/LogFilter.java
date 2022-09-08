package com.ezpzxrp.zerpcraft.util;

import com.ezpzxrp.zerpcraft.ZerpCraft;

import java.util.logging.Filter;
import java.util.logging.LogRecord;

public class LogFilter implements Filter {

    private final boolean debug;

    public LogFilter(ZerpCraft plugin) {

        debug = plugin.getConfig().getBoolean("General.Debug");
    }

    @Override
    public boolean isLoggable(LogRecord record) { return !(record.getMessage().contains("[Debug]") && ! debug); }
}

module com.gba.eugene.kernel {
    requires org.slf4j;
    requires java.sql;
    requires org.postgresql.jdbc;
    requires com.zaxxer.hikari;

    exports com.gba.eugene.kernel.util;
    exports com.gba.eugene.kernel.db;
    exports com.gba.eugene.kernel.exceptions;
    exports com.gba.eugene.kernel.model;
}
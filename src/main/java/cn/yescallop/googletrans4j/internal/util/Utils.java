package cn.yescallop.googletrans4j.internal.util;

import java.io.IOException;
import java.net.ConnectException;
import java.net.http.HttpConnectTimeoutException;
import java.net.http.HttpTimeoutException;
import java.util.concurrent.ExecutionException;

/**
 * A class for miscellaneous utilities.
 *
 * @author Scallop Ye
 */
public final class Utils {

    private Utils() {
        //no instance
    }

    public static IOException toIOException(ExecutionException e) {
        Throwable cause = e.getCause();
        String msg = cause.getMessage();

        if (cause instanceof IllegalArgumentException) {
            throw new IllegalArgumentException(msg, cause);
        } else if (cause instanceof SecurityException) {
            throw new SecurityException(msg, cause);
        } else if (cause instanceof HttpConnectTimeoutException) {
            HttpConnectTimeoutException hcte = new HttpConnectTimeoutException(msg);
            hcte.initCause(cause);
            return hcte;
        } else if (cause instanceof HttpTimeoutException) {
            return new HttpTimeoutException(msg);
        } else if (cause instanceof ConnectException) {
            ConnectException ce = new ConnectException(msg);
            ce.initCause(cause);
            return ce;
        } else if (cause instanceof IOException) {
            return new IOException(msg, cause);
        } else {
            return new IOException(msg, cause);
        }
    }
}

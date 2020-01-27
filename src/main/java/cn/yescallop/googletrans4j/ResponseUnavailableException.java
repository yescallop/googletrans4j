package cn.yescallop.googletrans4j;

/**
 * Thrown if the response is unavailable, that is,
 * the server responded with status code other than 200 OK.
 *
 * @author Scallop Ye
 */
public class ResponseUnavailableException extends RuntimeException {

    public ResponseUnavailableException(String message) {
        super(message);
    }
}

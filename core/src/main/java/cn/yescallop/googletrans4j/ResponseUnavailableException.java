package cn.yescallop.googletrans4j;

/**
 * Thrown if the response is unavailable, that is,
 * the server responded with status code other than 200 OK.
 *
 * @author Scallop Ye
 */
public class ResponseUnavailableException extends RuntimeException {

    private final int code;
    private final String response;

    /**
     * Constructs a {@code ResponseUnavailableException}.
     *
     * @param statusCode the HTTP status code.
     * @param response the response {@code String}.
     */
    public ResponseUnavailableException(int statusCode, String response) {
        super();
        code = statusCode;
        this.response = response;
    }

    @Override
    public String getMessage() {
        switch (code) {
            case 302:
                return "302 Moved: unusual traffic detected by Google";
            case 403:
                return "403 Forbidden: check your token or report an issue";
        }
        return "Server responded with status code " + code;
    }

    /**
     * Returns the status code of the HTTP response.
     *
     * @return the status code.
     */
    public int statusCode() {
        return code;
    }

    /**
     * Returns the response {@code String}.
     *
     * @return the response.
     */
    public String response() {
        return response;
    }
}

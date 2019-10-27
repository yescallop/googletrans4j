/**
 * Defines the module of the project <i>googletrans4j</i>.
 *
 * @author Scallop Ye
 */
module cn.yescallop.googletrans4j {
    requires java.net.http;
    requires gson;

    exports cn.yescallop.googletrans4j;
    exports cn.yescallop.googletrans4j.util;
}
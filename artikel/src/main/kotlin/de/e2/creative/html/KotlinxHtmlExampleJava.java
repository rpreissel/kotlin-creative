package de.e2.creative.html;

import kotlinx.html.DIV;
import kotlinx.html.TagConsumer;
import kotlinx.html.consumers.DelayedConsumer;
import kotlinx.html.stream.HTMLStreamBuilder;

import java.io.PrintStream;
import java.util.Collections;

public class KotlinxHtmlExampleJava {
    static class HTMLUtil {
        public static <T extends Appendable> TagConsumer<T> append(T out) {
            return new DelayedConsumer<T>(new HTMLStreamBuilder<T>(out, true));
        }
    }

    public static void main(String[] args) {
        TagConsumer<PrintStream> tagConsumer = HTMLUtil.append(System.out);
        tagConsumer.onTagStart(new DIV(Collections.emptyMap(),tagConsumer));
    }
}

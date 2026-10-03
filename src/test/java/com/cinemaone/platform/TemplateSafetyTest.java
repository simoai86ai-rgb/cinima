package com.cinemaone.platform;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertFalse;

class TemplateSafetyTest {
    private static final Pattern TAG = Pattern.compile("<[^!][^>]*>", Pattern.DOTALL);

    @Test
    void noElementCombinesIterationWithFragmentReplacement() throws IOException {
        Path root = Path.of("src/main/resources/templates");
        try (var paths = Files.walk(root)) {
            for (Path path : paths.filter(p -> p.toString().endsWith(".html")).toList()) {
                String html = Files.readString(path);
                Matcher matcher = TAG.matcher(html);
                while (matcher.find()) {
                    String tag = matcher.group();
                    assertFalse(tag.contains("th:each=") && (tag.contains("th:replace=") || tag.contains("th:insert=") || tag.contains("th:include=")),
                        () -> "Unsafe Thymeleaf processor precedence in " + path + ": " + tag);
                }
            }
        }
    }

    @Test
    void templatesDoNotUseUnsupportedPadLeftExpression() throws IOException {
        Path root = Path.of("src/main/resources/templates");
        try (var paths = Files.walk(root)) {
            for (Path path : paths.filter(p -> p.toString().endsWith(".html")).toList()) {
                assertFalse(Files.readString(path).contains("#strings.padLeft"), () -> "Unsupported #strings.padLeft found in " + path);
            }
        }
    }
}

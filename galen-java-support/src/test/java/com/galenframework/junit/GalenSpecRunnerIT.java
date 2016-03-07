package com.galenframework.junit;

import org.hamcrest.Matcher;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.RunWith;
import org.junit.runner.notification.Failure;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.Files.delete;
import static java.nio.file.Files.write;
import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.*;
import static org.junit.Assume.assumeTrue;

public class GalenSpecRunnerIT {
    private static final String HTML_FILE = "/tmp/GalsenSpecRunnerIT.html";

    @BeforeClass
    public static void createHtmlFile() throws IOException {
        if (existsTmpFolder()) {
            write(Paths.get(HTML_FILE),
                    asList("<!DOCTYPE html>",
                            "<html>",
                            "<head>",
                            "</head>",
                            "<body>",
                            "<p id=\"p1\" style=\"width:400px;float:left;\">First paragraph.</p>",
                            "<p id=\"p2\">Second paragraph.</p>",
                            "</body>",
                            "</html>"),
                    UTF_8);
        }
    }

    @AfterClass
    public static void deleteHtmlFile() throws IOException {
        if (existsTmpFolder()) {
            delete(Paths.get(HTML_FILE));
        }
    }

    @Rule
    public final ErrorCollector collector = new ErrorCollector();

    @RunWith(GalenSpecRunner.class)
    @Size(width = 640, height = 480)
    @Spec("/com/galenframework/junit/homepage_small.gspec")
    @Url("file://" + HTML_FILE)
    public static class ValidSpec {
    }

    @Test
    public void shouldBeSuccessfulForValidSpec() {
        assumeTrue(existsTmpFolder());
        Result result = runTest(ValidSpec.class);
        //We use an error collector because running a test for each assertion takes too much time.
        collector.checkThat("is successful", result.wasSuccessful(), is(true));
        collector.checkThat("has no failures", result.getFailures(), is(empty()));
        collector.checkThat("has a test for each spec", result.getRunCount(), is(4));
    }

    @RunWith(GalenSpecRunner.class)
    @Size(width = 640, height = 480)
    @Spec("/com/galenframework/junit/inapplicable.gspec")
    @Url("file://" + HTML_FILE)
    public static class InapplicableSpec {
    }

    @Test
    public void shouldFailForInapplicableSpec() {
        assumeTrue(existsTmpFolder());
        Result result = runTest(InapplicableSpec.class);
        //We use an error collector because running a test for each assertion takes too much time.
        collector.checkThat("is not successful", result.wasSuccessful(), is(false));
        collector.checkThat("has failures", result.getFailures(), hasSize(2));
        collector.checkThat("has only assertion errors", result.getFailures(),
                not(hasFailureWithException(not(instanceOf(AssertionError.class)))));
        collector.checkThat("describes failure", result.getFailures(),
                hasFailureWithException(hasProperty("message", equalTo(
                        "[\"first_paragraph\" width is 400px but it should be less than 10px]"))));
        collector.checkThat("has a test for each spec", result.getRunCount(), is(3));
    }

    @RunWith(GalenSpecRunner.class)
    @Include("variantA")
    @Size(width = 640, height = 480)
    @Spec("/com/galenframework/junit/tag.gspec")
    @Url("file://" + HTML_FILE)
    public static class ExcludeTag {
    }

    @Test
    public void shouldNotRunTestsForSectionsThatAreExcluded() {
        assumeTrue(existsTmpFolder());
        Result result = runTest(ExcludeTag.class);
        collector.checkThat("has only tests for not excluded sections", result.getRunCount(), is(3));
    }

    @RunWith(GalenSpecRunner.class)
    @Include("variantA")
    @Exclude("variantB")
    @Size(width = 640, height = 480)
    @Spec("/com/galenframework/junit/tag.gspec")
    @Url("file://" + HTML_FILE)
    public static class IncludeTag {
    }

    @Test
    public void shouldOnlyRunTestsForSectionsThatAreIncluded() {
        assumeTrue(existsTmpFolder());
        Result result = runTest(IncludeTag.class);
        collector.checkThat("has only tests for included sections", result.getRunCount(), is(2));
    }

    @RunWith(GalenSpecRunner.class)
    @Spec("/com/galenframework/junit/homepage_small.gspec")
    @Url("file://" + HTML_FILE)
    public static class NoSizeAnnotation {
    }

    @Test
    public void shouldProvideHelpfulMessageIfSizeAnnotationsAreMissing() {
        Result result = runTest(com.galenframework.junit.GalenSpecRunnerIT.NoSizeAnnotation.class);
        //We use an error collector because running a test for each assertion takes too much time.
        collector.checkThat("is successful", result.wasSuccessful(), is(false));
        collector.checkThat("has failure", result.getFailures(), hasSize(1));
        collector.checkThat("describes failure", result.getFailures(),
                hasFailureWithException(hasProperty("message",
                        equalTo("The annotation @Size is missing."))));
    }

    @RunWith(GalenSpecRunner.class)
    @Size(width = 640, height = 480)
    @Url("file://" + HTML_FILE)
    public static class NoSpecAnnotation {
    }

    @Test
    public void shouldProvideHelpfulMessageIfSpecAnnotationIsMissing() {
        Result result = runTest(NoSpecAnnotation.class);
        //We use an error collector because running a test for each assertion takes too much time.
        collector.checkThat("is successful", result.wasSuccessful(), is(false));
        collector.checkThat("has failure", result.getFailures(), hasSize(1));
        collector.checkThat("describes failure", result.getFailures(),
                hasFailureWithException(hasProperty("message",
                        equalTo("The annotation @Spec is missing."))));
    }

    @RunWith(GalenSpecRunner.class)
    @Size(width = 640, height = 480)
    @Spec("/com/galenframework/junit/homepage_small.gspec")
    public static class NoUrlAnnotation {
    }

    @Test
    public void shouldProvideHelpfulMessageIfUrlAnnotationIsMissing() {
        Result result = runTest(NoUrlAnnotation.class);
        //We use an error collector because running a test for each assertion takes too much time.
        collector.checkThat("is successful", result.wasSuccessful(), is(false));
        collector.checkThat("has failure", result.getFailures(), hasSize(1));
        collector.checkThat("describes failure", result.getFailures(),
                hasFailureWithException(hasProperty("message",
                        equalTo("The annotation @Url is missing."))));
    }

    private Matcher<Iterable<? super Failure>> hasFailureWithException(Matcher<?> matcher) {
        return hasItem(hasProperty("exception", matcher));
    }

    private Result runTest(Class<?> test) {
        return JUnitCore.runClasses(test);
    }

    private static boolean existsTmpFolder() {
        File tmpFolder = new File("/tmp");
        return tmpFolder.exists();
    }
}
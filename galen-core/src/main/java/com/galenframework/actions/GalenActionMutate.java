package com.galenframework.actions;

import com.galenframework.browser.SeleniumBrowserFactory;
import com.galenframework.runner.CombinedListener;
import com.galenframework.runner.EventHandler;
import com.galenframework.suite.GalenPageAction;
import com.galenframework.suite.GalenPageTest;
import com.galenframework.suite.actions.GalenPageActionMutate;
import com.galenframework.tests.GalenBasicTest;
import com.galenframework.tests.GalenTest;

import java.io.IOException;
import java.io.PrintStream;
import java.util.LinkedList;
import java.util.List;

import static java.util.Arrays.asList;

public class GalenActionMutate extends GalenAction {

    private final CombinedListener listener;
    private final GalenActionMutateArguments mutateArguments;

    public GalenActionMutate(String[] arguments, PrintStream outStream, PrintStream errStream, CombinedListener listener) {
        super(arguments, outStream, errStream);
        this.mutateArguments = GalenActionMutateArguments.parse(arguments);
        this.listener = createListeners(listener);
    }

    @Override
    public void execute() throws IOException {
        verifyArguments();

        loadConfigIfNeeded(mutateArguments.getConfig());

        List<GalenTest> galenTests = new LinkedList<>();

        for (String pageSpecPath : mutateArguments.getPaths()) {
            GalenBasicTest test = new GalenBasicTest();
            test.setName(pageSpecPath);
            test.setPageTests(asList(new GalenPageTest()
                .withTitle("Mutation test")
                .withUrl(mutateArguments.getUrl())
                .withSize(mutateArguments.getScreenSize())
                .withBrowserFactory(new SeleniumBrowserFactory())
                .withActions(
                    asList((GalenPageAction) new GalenPageActionMutate().withSpec(pageSpecPath).withIncludedTags(mutateArguments.getIncludedTags())
                        .withExcludedTags(mutateArguments.getExcludedTags()).withOriginalCommand(originalCommand(arguments))))));
            galenTests.add(test);
        }

        GalenActionTestArguments testArguments = new GalenActionTestArguments();
        GalenActionTest.runTests(new EventHandler(), galenTests, testArguments, listener);
    }

    private void verifyArguments() {
        if (mutateArguments.getUrl() == null) {
            throw new IllegalArgumentException("Url is not specified");
        }

        if (mutateArguments.getScreenSize() == null) {
            throw new IllegalArgumentException("Screen size is not specified");
        }

        if (mutateArguments.getPaths().size() < 1) {
            throw new IllegalArgumentException("There are no specs specified");
        }

    }
}

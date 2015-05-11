package net.mindengine.galen.junit;

import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Intercepts JUnit events and reports them to Galen.
 */
public class JUnitListener extends RunListener {

    private static final Logger LOG = LoggerFactory.getLogger(JUnitListener.class);

    /**
     * @see org.junit.runner.notification.RunListener#testRunStarted(org.junit.runner.Description)
     */
    @Override
    public void testRunStarted(Description description) throws Exception {
        LOG.debug("Starting test run: " + description);
        super.testRunStarted(description);
    }

    /**
     * @see org.junit.runner.notification.RunListener#testRunFinished(org.junit.runner.Result)
     */
    @Override
    public void testRunFinished(Result result) throws Exception {
        LOG.debug("Finished test run: " + result);
        super.testRunFinished(result);
    }

    /**
     * @see org.junit.runner.notification.RunListener#testStarted(org.junit.runner.Description)
     */
    @Override
    public void testStarted(Description description) throws Exception {
        LOG.debug("Starting test: " + description);
        super.testStarted(description);
    }

    /**
     * 
     * @see org.junit.runner.notification.RunListener#testFinished(org.junit.runner.Description)
     */
    @Override
    public void testFinished(Description description) throws Exception {
        LOG.debug("Finished test: " + description);
        super.testFinished(description);
    }

    /**
     * 
     * @see org.junit.runner.notification.RunListener#testFailure(org.junit.runner.notification.Failure)
     */
    @Override
    public void testFailure(Failure failure) throws Exception {
        LOG.debug("Test lead to errors: " + failure);
        super.testFailure(failure);
    }

    /**
     * 
     * @see org.junit.runner.notification.RunListener#testAssumptionFailure(org.junit.runner.notification.Failure)
     */
    @Override
    public void testAssumptionFailure(Failure failure) {
        LOG.debug("Test with assumption: " + failure);
        super.testAssumptionFailure(failure);
    }

    /**
     * 
     * @see org.junit.runner.notification.RunListener#testIgnored(org.junit.runner.Description)
     */
    @Override
    public void testIgnored(Description description) throws Exception {
        LOG.debug("Test was ignored: " + description);
        super.testIgnored(description);
    }

}

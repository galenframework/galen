package net.mindengine.galen.tests.validation;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import net.mindengine.galen.specs.Range;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class RangeTest {

	
	@Test(dataProvider="provideRangeChecks")
	public void shouldCheckRange(Range range, Double offset, boolean expectedResult) {
		assertThat(range.holds(offset), is(expectedResult));
	}
	
	
	@DataProvider
	public Object[][] provideRangeChecks() {
		return new Object[][]{
				{Range.exact(10.0), 10.0, true},
				{Range.exact(10.0), 10.1, false},
				{Range.exact(10.0), 9.0, false},
				{Range.exact(-10.0), -10.0, true},
				{Range.exact(-10.0), -10.1, false},
				{Range.exact(-10.0), -9.0, false},
				
				{Range.between(10.0, 20.0), 10.0, true},
				{Range.between(10.0, 20.0), 11.0, true},
				{Range.between(10.0, 20.0), 20.0, true},
				{Range.between(10.0, 20.0), 10.1, true},
				{Range.between(10.0, 20.0), 9.9, false},
				{Range.between(10.0, 20.0), 20.1, false},
				{Range.between(-10.0, -20.0), -11.0, true},
				{Range.between(-10.0, -20.0), -9.0, false},
				
				{Range.greaterThan(10.0), 10.0, false},
				{Range.greaterThan(10.0), 10.1, true},
				{Range.greaterThan(10.0), 15.0, true},
				{Range.greaterThan(10.0), 5.0, false},
				
				{Range.lessThan(10.0), 9.0, true},
				{Range.lessThan(10.0), 10.0, false},
				{Range.lessThan(10.0), 15.0, false}
		};
	}
}

/*******************************************************************************
* Copyright 2014 Ivan Shubin http://mindengine.net
* 
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
* 
*   http://www.apache.org/licenses/LICENSE-2.0
* 
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
******************************************************************************/
package net.mindengine.galen.tests.parser;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import net.mindengine.galen.parser.MathParser;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class MathParserTest {

	
	@Test(dataProvider = "provideGoodSamples")
	public void shouldParseSuccessfully(String template, String initialValue, String expectedResult) {
		
		MathParser mathParser = new MathParser();
		String realResult = mathParser.parse(template, initialValue);
		assertThat(realResult, is(expectedResult));
	}
	
	
	@DataProvider
	public Object[][] provideGoodSamples() {
		return new Object[][]{
				{"inside: box-@ 20px left", "10", "inside: box-10 20px left"},
				{"width: 10px", "10", "width: 10px"},
				{"inside: box-@{+1} 20px left", "10", "inside: box-11 20px left"},
				{"inside: box-@{*2} 20px left", "10", "inside: box-20 20px left"},
				{"inside: box-@{/2} 20px left", "10", "inside: box-5 20px left"},
				{"inside: box-@{-1} 20px left", "10", "inside: box-9 20px left"},
				{"inside: box-@{%3} 20px left", "10", "inside: box-1 20px left"},
				{"centered horizontally inside: box-@", "10", "centered horizontally inside: box-10"},
				{"text is: box-@@{*2} 20px left", "10", "text is: box-@{*2} 20px left"},
		};
	}
	
	//TODO Negative tests for MathParser
}

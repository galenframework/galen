/*******************************************************************************
* Copyright 2015 Ivan Shubin http://mindengine.net
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
package net.mindengine.galen.api;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import net.mindengine.galen.runner.GalenJavaExecutor;

/**
 * This annotation lets directly inject the Galen Java executor. If you want to
 * add an own implementation, extend {@link GalenJavaExecutor} and set the
 * implementation property to this class. <br>
 * <br>
 * 
 * <pre>
 * <code>@Inject(implementation = MyGalenJavaExecutor.class)
 * private GalenExecutor runner;</code>
 * </pre>
 */
@Documented
@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = { ElementType.FIELD })
public @interface Inject {

	Class<? extends GalenExecutor> implementation() default GalenJavaExecutor.class;
}

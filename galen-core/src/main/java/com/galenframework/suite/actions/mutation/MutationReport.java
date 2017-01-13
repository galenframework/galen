/*******************************************************************************
* Copyright 2017 Ivan Shubin http://galenframework.com
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
package com.galenframework.suite.actions.mutation;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class MutationReport {

    private Map<String, MutationStatistic> objectMutationStatistics = new HashMap<>();
    private int totalPassed = 0;
    private int totalFailed = 0;

    public void reportSuccessMutation(PageMutation pageMutation) {
        MutationStatistic mutationStatistic = obtainObjectMutationStatistic(pageMutation);
        mutationStatistic.passed++;
        totalPassed++;
    }

    public void reportFailedMutation(PageMutation pageMutation) {
        MutationStatistic mutationStatistic = obtainObjectMutationStatistic(pageMutation);
        mutationStatistic.failed++;
        mutationStatistic.failedMutations.add(convertElementMutationsToString(pageMutation.getPageElementMutations()));
        totalFailed++;
    }

    private String convertElementMutationsToString(List<PageElementMutation> pageElementMutations) {
        StringBuilder s = new StringBuilder();
        boolean isFirst = true;
        for (PageElementMutation pageElementMutation : pageElementMutations) {
            if (!isFirst) {
                s.append(" and ");
            }
            s.append(pageElementMutation.getElementName());
            s.append(": ");
            s.append(pageElementMutation.getAreaMutation().getMutationName());
            isFirst = false;
        }
        return s.toString();
    }

    private MutationStatistic obtainObjectMutationStatistic(PageMutation pageMutation) {
        String objectName = pageMutation.getPageElementMutations().get(0).getElementName();

        MutationStatistic mutationStatistic = objectMutationStatistics.get(objectName);
        if (mutationStatistic == null) {
            mutationStatistic = new MutationStatistic();
            objectMutationStatistics.put(objectName, mutationStatistic);
        }
        return mutationStatistic;
    }

    public int getTotalPassed() {
        return totalPassed;
    }

    public int getTotalFailed() {
        return totalFailed;
    }

    public class MutationStatistic {
        private int passed = 0;
        private int failed = 0;
        private List<String> failedMutations = new LinkedList<>();

        public int getPassed() {
            return passed;
        }

        public int getFailed() {
            return failed;
        }

        public List<String> getFailedMutations() {
            return failedMutations;
        }
    }

    public Map<String, MutationStatistic> getObjectMutationStatistics() {
        return objectMutationStatistics;
    }
}

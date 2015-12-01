package com.galenframework.support;

import com.galenframework.reports.model.LayoutReport;
import com.galenframework.speclang2.pagespec.SectionFilter;
import com.galenframework.validation.ValidationResult;

import java.util.List;

public class LayoutValidationException extends RuntimeException {

    private static final String ERROR_INDENTATION = "  ";
    private final LayoutReport layoutReport;

    public LayoutValidationException(String specPath, LayoutReport layoutReport, SectionFilter sectionFilter) {
        super(createMessage(specPath, layoutReport, sectionFilter));
        this.layoutReport = layoutReport;
    }

    public LayoutReport getLayoutReport() {
        return layoutReport;
    }

    public static String createMessage(String specPath, LayoutReport layoutReport, SectionFilter sectionFilter) {
        try {
            StringBuilder messageBuilder = new StringBuilder()
                    .append(specPath);

            if (sectionFilter != null) {
                if (sectionFilter.getIncludedTags() != null && !sectionFilter.getIncludedTags().isEmpty()) {
                    messageBuilder.append(", tags: ").append(sectionFilter.getIncludedTags()).append("\n");
                }
                if (sectionFilter.getExcludedTags() != null && !sectionFilter.getExcludedTags().isEmpty()) {
                    messageBuilder.append(", excludedTags: ").append(sectionFilter.getExcludedTags()).append("\n");
                }
            }



            messageBuilder.append(collectAllErrors(layoutReport.getValidationErrorResults()));
            return messageBuilder.toString();

        } catch (Exception ex) {
            return specPath;
        }

    }

    private static StringBuilder collectAllErrors(List<ValidationResult> validationErrorResults) {
        return collectAllErrors(validationErrorResults, ERROR_INDENTATION);
    }

    private static StringBuilder collectAllErrors(List<ValidationResult> validationErrorResults, String indentation) {
        StringBuilder builder = new StringBuilder();
        if (validationErrorResults != null) {
            String childIndentation = indentation + ERROR_INDENTATION;

            for (ValidationResult validationResult : validationErrorResults) {
                for (String errorMessage : validationResult.getError().getMessages()) {
                    builder.append(indentation)
                            .append("- ")
                            .append(errorMessage);

                    if (validationResult.getSpec() != null && validationResult.getSpec().getPlace() != null) {
                        builder.append(" (").append(validationResult.getSpec().getPlace().toPrettyString()).append(")");
                    }
                    builder.append("\n");
                }

                builder.append(collectAllErrors(validationResult.getChildValidationResults(), childIndentation));
            }
        }
        return builder;
    }
}

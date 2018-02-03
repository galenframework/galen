<#list pageSpec.sections as section>
<#if section.hasContent>
= ${section.name} =
    <#if section.hasRules>
    <#list section.rules as rule>
    ${rule.statement}
    </#list>

    </#if>
    <#list section.objectSpecs as objectSpec>
    ${objectSpec.objectName}:
        <#list objectSpec.specs as spec>
        ${spec.statement}
        </#list>

    </#list>
</#if>
</#list>


mvn release:prepare -DskipTests=true -Darguments='-DskipTests=true'

mvn release:perform -DskipTests=true -Darguments='-DskipTests=true'

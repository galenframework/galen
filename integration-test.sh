docker run -d -p 4444:4444 -e SE_OPTS="-debug true" --name selenium-hub selenium/hub:3.2.0-actinium
docker run -d --link selenium-hub:hub --name node-chrome selenium/node-chrome:3.2.0-actinium
docker run -d --link selenium-hub:hub --name node-firefox selenium/node-firefox:3.2.0-actinium
docker run -d --link selenium-hub:hub --name node-phantomjs selenium/node-phantomjs:3.2.0-actinium
mvn verify
docker stop selenium-hub
docker stop node-chrome
docker stop node-firefox
docker stop node-phantomjs
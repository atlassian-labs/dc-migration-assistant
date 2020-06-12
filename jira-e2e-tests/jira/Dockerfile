FROM atlassian/jira-software

RUN apt-get update && apt-get upgrade -y \
    && apt-get install -y --no-install-recommends netcat-openbsd \
    && apt-get clean

COPY waitport /usr/local/bin/waitport

# jira-plugin-1.0.0.jar is expected to have been placed in this directory first.
COPY jira-plugin-1.0.0.jar /var/atlassian/application-data/jira/plugins/installed-plugins/jira-plugin-1.0.0.jar

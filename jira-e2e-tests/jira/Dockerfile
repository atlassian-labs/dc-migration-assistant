ARG JIRA_VERSION
FROM atlassian/jira-software:${JIRA_VERSION}

RUN apt-get update && apt-get install gnupg2 -y -q
RUN apt-key adv --keyserver keyserver.ubuntu.com --recv-keys B97B0AFCAA1A47F044F244A07FCC7D46ACCC4CF8

# We need to use PostgreSQL v11 as v12 is not supported for Jira
RUN apt-get update -y && apt-get upgrade \
    && apt-get install -y --no-install-recommends netcat-openbsd wget \
    && wget --quiet -O - https://www.postgresql.org/media/keys/ACCC4CF8.asc | apt-key add - \
    && echo "deb http://apt.postgresql.org/pub/repos/apt/ focal"-pgdg main | tee /etc/apt/sources.list.d/pgdg.list \
    && apt-get update -y \
    && apt-get install -y -q software-properties-common \
    && apt-get install -y -q postgresql-11 \
    && apt-get clean

COPY waitport /usr/local/bin/waitport

COPY jira-home/ /var/atlassian/application-data/jira/

# jira-plugin-1.0.0.jar is expected to have been placed in this directory first.
COPY jira-plugin-*.jar /var/atlassian/application-data/jira/plugins/installed-plugins/

FROM atlassian/jira-software

RUN apt-get update && apt-get install gnupg2 -y -q

RUN apt-key adv --keyserver keyserver.ubuntu.com --recv-keys B97B0AFCAA1A47F044F244A07FCC7D46ACCC4CF8
RUN echo "deb http://apt.postgresql.org/pub/repos/apt/ precise-pgdg main" > /etc/apt/sources.list.d/pgdg.list

RUN apt-get update && apt-get upgrade -y \
    && apt-get install -y --no-install-recommends netcat-openbsd \
    && apt-get install -y -q software-properties-common \
    && apt-get install -y -q  postgresql-client-9.6 postgresql-contrib-9.6 \
    && apt-get clean

COPY waitport /usr/local/bin/waitport

# jira-plugin-1.0.0.jar is expected to have been placed in this directory first.
COPY jira-plugin-1.0.0.jar /var/atlassian/application-data/jira/plugins/installed-plugins/jira-plugin-1.0.0.jar

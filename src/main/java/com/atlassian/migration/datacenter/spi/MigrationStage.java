/*
 * Copyright 2020 Atlassian
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.atlassian.migration.datacenter.spi;

import java.util.Optional;


/**
 * Represents all possible states of an on-premise to cloud migration.
 */
public enum MigrationStage {
    NOT_STARTED(),
    ERROR(),

    FINISHED(), // TODO: Valid transition
    CUTOVER(), // TODO: Valid transition
    VALIDATE(), // TODO: Valid transition

    DB_MIGRATION_EXPORT_WAIT(NOT_STARTED),
    DB_MIGRATION_EXPORT(DB_MIGRATION_EXPORT_WAIT),
    DB_MIGRATION_UPLOAD(DB_MIGRATION_EXPORT),
    DB_MIGRATION_UPLOAD_COMPLETE(DB_MIGRATION_UPLOAD),
    DB_MIGRATION_IMPORT(), // TODO: Valid transition

    OFFLINE_WARNING(), // TODO: Valid transition

    FS_MIGRATION_COPY_WAIT(), // TODO: Valid transition
    FS_MIGRATION_COPY(FS_MIGRATION_COPY_WAIT),

    AUTHENTICATION(), // TODO: Valid transition
    PROVISION_MIGRATION_STACK_WAIT(AUTHENTICATION),
    PROVISION_MIGRATION_STACK(PROVISION_MIGRATION_STACK_WAIT),
    PROVISION_APPLICATION_WAIT(PROVISION_MIGRATION_STACK),
    PROVISION_APPLICATION(PROVISION_APPLICATION_WAIT);

    private Optional<MigrationStage> validFrom;
    private Optional<Throwable> exception;

    MigrationStage() {
        this.exception = Optional.empty();
        this.validFrom = Optional.empty();
    }

    MigrationStage(MigrationStage validFrom) {
        this.exception = Optional.empty();
        this.validFrom = Optional.of(validFrom);
    }

    //TODO: Refactor away from static to an instance method
    public static boolean isValidTransition(MigrationStage from, MigrationStage to) {
        return !to.validFrom.isPresent() || to.validFrom.get().equals(from);
    }

//    TODO: Review develop endpoint is see if migration stage will be (de)serialized
//    If not, then create a JSON creator method to convert from string to enum type.
//    @JsonProperty
//    private String key;

    @Override
    public String toString() {
        return super.toString().toLowerCase();
    }

    public Optional<Throwable> getException() {
        return exception;
    }

    public void setException(Optional<Throwable> exception) {
        this.exception = exception;
    }
}

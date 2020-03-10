package com.atlassian.migration.datacenter.spi.fs.reporting;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Tracks the progress of the file system migration
 */
@JsonSerialize(as = FileSystemMigrationProgress.class)
public interface FileSystemMigrationProgress {

    /**
     * Gets the number of files which have been found to migrate.
     */
    @JsonProperty("filesFound")
    Long getNumberOfFilesFound();

    void reportFileFound();

    /**
     * Gets the number of files which have had their upload commenced
     */
    @JsonProperty("filesInFlight")
    Long getNumberOfCommencedFileUploads();

    void reportFileUploadCommenced();

    /**
     * Gets the number of files which have been successfully migrated
     */
    @JsonProperty("migratedFiles")
    Long getCountOfMigratedFiles();

    /**
     * Reports that a file was migrated successfully. Implementers should be careful that the underlying
     * collection is thread safe as this may be called from multiple file upload threads.
     */
    void reportFileMigrated();
}

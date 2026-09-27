package com.velevedi.karabi;

import java.util.Comparator;
import java.util.Objects;

public record Artifact(
        String groupId,
        String artifactId,
        String version
) {

    /// Reload only if a new version is bigger than the existing one
    public static Comparator<Artifact> versionComparator  = Comparator.comparing(Artifact::version);

    public Artifact(String groupId, String artifactId, String version) {
        this.groupId = Objects.requireNonNull(groupId, "groupId is null");
        this.artifactId = Objects.requireNonNull(artifactId, "artifactId is null");
        this.version = Objects.requireNonNull(version, "version is null");
    }

    public String id()  {
        return groupId + ":" + artifactId;
    }

}

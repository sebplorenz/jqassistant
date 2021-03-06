package com.buschmais.jqassistant.plugin.m2repo.impl.scanner;

import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.maven.repository.internal.MavenRepositorySystemUtils;
import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.connector.basic.BasicRepositoryConnectorFactory;
import org.eclipse.aether.impl.DefaultServiceLocator;
import org.eclipse.aether.repository.Authentication;
import org.eclipse.aether.repository.LocalRepository;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.resolution.ArtifactRequest;
import org.eclipse.aether.resolution.ArtifactResolutionException;
import org.eclipse.aether.resolution.ArtifactResult;
import org.eclipse.aether.spi.connector.RepositoryConnectorFactory;
import org.eclipse.aether.spi.connector.transport.TransporterFactory;
import org.eclipse.aether.transport.file.FileTransporterFactory;
import org.eclipse.aether.transport.http.HttpTransporterFactory;
import org.eclipse.aether.util.repository.AuthenticationBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Transfers artifacts from a remote repository to a local repository.
 * 
 * @author pherklotz
 */
public class ArtifactResolver {

    private static final Logger LOGGER = LoggerFactory.getLogger(ArtifactResolver.class);

    private final RemoteRepository repository;
    private final RepositorySystem repositorySystem;

    private final DefaultRepositorySystemSession session;

    /**
     * Creates a new object.
     * 
     * @param repositoryUrl
     *            the repository url
     * @param localDirectory
     *            the directory for resolved artifacts
     */
    public ArtifactResolver(URL repositoryUrl, File localDirectory) {
        this(repositoryUrl, localDirectory, null, null);
    }

    /**
     * Creates a new object.
     * 
     * @param repositoryUrl
     *            the repository url
     * @param localDirectory
     *            the directory for resolved artifacts
     * @param username
     *            an username for authentication
     * @param password
     *            a password for authentication
     */
    public ArtifactResolver(URL repositoryUrl, File localDirectory, String username, String password) {
        String url = StringUtils.replace(repositoryUrl.toString(), repositoryUrl.getUserInfo() + "@", StringUtils.EMPTY);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Create new " + this.getClass().getSimpleName() + " for URL " + url);
        }
        AuthenticationBuilder authBuilder = new AuthenticationBuilder();
        if (username != null) {
            authBuilder.addUsername(username);
        }
        if (password != null) {
            authBuilder.addPassword(password);
        }
        Authentication auth = authBuilder.build();
        repository = new RemoteRepository.Builder("jqa", "default", url).setAuthentication(auth).build();
        repositorySystem = newRepositorySystem();
        session = newRepositorySystemSession(repositorySystem, localDirectory);
    }

    /**
     * Resolves an artifact with the given properties and transfers it in a
     * local repository.
     * 
     * @param artifact
     *            the artifact to resolve
     * @return the local file handle
     * @throws ArtifactResolutionException
     *             in case of a unresolvable artifacts
     */
    public List<ArtifactResult> downloadArtifact(Artifact artifact) throws ArtifactResolutionException {
        Set<ArtifactRequest> artifactRequests = newArtifactRequests(artifact);
        return repositorySystem.resolveArtifacts(session, artifactRequests);
    }

    /**
     * Creates a new {@link ArtifactRequest} Object with the artifact GAV and
     * the repository.
     * 
     * @param artifactGav
     * @return
     */
    private Set<ArtifactRequest> newArtifactRequests(Artifact artifact) {
        ArtifactRequest artifactRequest = new ArtifactRequest();
        artifactRequest.setArtifact(artifact);
        final List<RemoteRepository> repositories = Arrays.asList(repository);
        artifactRequest.setRepositories(repositories);

        Set<ArtifactRequest> requests = new HashSet<>();
        requests.add(artifactRequest);

        if ((artifact.getExtension() == null || "jar".equals(artifact.getExtension().toLowerCase())) && StringUtils.isBlank(artifact.getClassifier())) {
            ArtifactRequest pomRequest = new ArtifactRequest();
            Artifact pomArtifact = new DefaultArtifact(artifact.getGroupId(), artifact.getArtifactId(), null, "pom", artifact.getVersion());
            pomRequest.setArtifact(pomArtifact);
            pomRequest.setRepositories(repositories);

            requests.add(pomRequest);
        }

        return requests;
    }

    /**
     * Creates a new {@link RepositorySystem} object.
     * 
     * @return the new object
     */
    private RepositorySystem newRepositorySystem() {
        DefaultServiceLocator locator = MavenRepositorySystemUtils.newServiceLocator();
        locator.addService(RepositoryConnectorFactory.class, BasicRepositoryConnectorFactory.class);
        locator.addService(TransporterFactory.class, FileTransporterFactory.class);
        locator.addService(TransporterFactory.class, HttpTransporterFactory.class);

        return locator.getService(RepositorySystem.class);
    }

    /**
     * Creates a new {@link RepositorySystemSession}.
     * 
     * @param system
     *            the {@link RepositorySystem}
     * @return a new {@link RepositorySystemSession}.
     */
    private DefaultRepositorySystemSession newRepositorySystemSession(RepositorySystem system, File localDirectory) {
        DefaultRepositorySystemSession session = MavenRepositorySystemUtils.newSession();

        LocalRepository localRepo = new LocalRepository(localDirectory + "/repository");
        session.setLocalRepositoryManager(system.newLocalRepositoryManager(session, localRepo));

        return session;
    }
}

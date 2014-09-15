package com.buschmais.jqassistant.plugin.tycho.impl.scanner;

import static com.buschmais.jqassistant.plugin.java.api.scanner.JavaScope.CLASSPATH;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.components.io.resources.PlexusIoFileResourceCollection;
import org.codehaus.plexus.components.io.resources.PlexusIoResource;
import org.codehaus.plexus.util.AbstractScanner;
import org.eclipse.tycho.core.TychoConstants;
import org.eclipse.tycho.core.facade.BuildProperties;
import org.eclipse.tycho.core.osgitools.project.EclipsePluginProject;

import com.buschmais.jqassistant.core.scanner.api.Scanner;
import com.buschmais.jqassistant.core.scanner.api.ScannerPlugin;
import com.buschmais.jqassistant.core.scanner.api.Scope;
import com.buschmais.jqassistant.core.store.api.Store;
import com.buschmais.jqassistant.core.store.api.type.FileDescriptor;
import com.buschmais.jqassistant.plugin.common.api.type.ArtifactDirectoryDescriptor;
import com.buschmais.jqassistant.plugin.maven3.api.scanner.AbstractMavenProjectScannerPlugin;

/**
 * Implementation of a {@link ScannerPlugin} for tycho projects
 */
public class TychoProjectScannerPlugin extends AbstractMavenProjectScannerPlugin {

    @Override
    public boolean accepts(MavenProject item, String path, Scope scope) throws IOException {
        return true;
    }

    @Override
    public FileDescriptor scan(MavenProject project, String path, Scope scope, Scanner scanner) throws IOException {
        Store store = getStore();
        store.beginTransaction();
        try {
            final ArtifactDirectoryDescriptor artifact = resolveArtifact(project.getArtifact(), false, ArtifactDirectoryDescriptor.class);
            for (File file : getPdeFiles(project)) {
                String filePath = getDirectoryPath(project.getBasedir(), file);
                FileDescriptor fileDescriptor = scanner.scan(file, filePath, CLASSPATH);
                if (fileDescriptor != null) {
                    artifact.addContains(fileDescriptor);
                }
            }
            return artifact;
        } finally {
            store.commitTransaction();
        }
    }

    private List<File> getPdeFiles(MavenProject project) throws IOException {
        Object value = project.getContextValue(TychoConstants.CTX_ECLIPSE_PLUGIN_PROJECT);
        final List<File> pdeFiles = new ArrayList<>();
        if (value instanceof EclipsePluginProject) {
            EclipsePluginProject pdeProject = (EclipsePluginProject) value;
            Iterator<PlexusIoResource> iterator = getPDEBinaries(project, pdeProject);
            if (iterator.hasNext()) {
                do {
                    PlexusIoResource resource = iterator.next();
                    File file = new File(resource.getURL().getFile());
                    if (file.exists() && !file.isDirectory()) {
                        pdeFiles.add(file);
                    }
                } while (iterator.hasNext());
            }
        }
        return pdeFiles;
    }

    private Iterator<PlexusIoResource> getPDEBinaries(MavenProject project, EclipsePluginProject pdeProject) throws IOException {
        BuildProperties buildProperties = pdeProject.getBuildProperties();
        return getResourceFileCollection(project.getBasedir(), buildProperties.getBinIncludes(), buildProperties.getBinExcludes()).getResources();
    }

    /**
     * @return a {@link PlexusIoFileResourceCollection} with the given includes
     *         and excludes and the configured default excludes. An empty list
     *         of includes leads to an empty collection.
     */
    protected PlexusIoFileResourceCollection getResourceFileCollection(File basedir, List<String> includes, List<String> excludes) {
        PlexusIoFileResourceCollection collection = new PlexusIoFileResourceCollection();
        collection.setBaseDir(basedir);
        if (includes.isEmpty()) {
            collection.setIncludes(new String[] { "" });
        } else {
            collection.setIncludes(includes.toArray(new String[includes.size()]));
        }
        Set<String> allExcludes = new LinkedHashSet<String>();
        if (excludes != null) {
            allExcludes.addAll(excludes);
        }
        allExcludes.addAll(Arrays.asList(AbstractScanner.DEFAULTEXCLUDES));
        collection.setExcludes(allExcludes.toArray(new String[allExcludes.size()]));
        return collection;
    }

}

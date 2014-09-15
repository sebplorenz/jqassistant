package com.buschmais.jqassistant.plugin.tycho.impl.scanner;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.project.MavenProject;
import org.eclipse.tycho.core.TychoConstants;
import org.eclipse.tycho.core.facade.BuildProperties;
import org.eclipse.tycho.core.osgitools.project.EclipsePluginProject;
import org.hamcrest.Matcher;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.mockito.Mockito;

import com.buschmais.jqassistant.core.scanner.api.Scanner;
import com.buschmais.jqassistant.core.store.api.Store;
import com.buschmais.jqassistant.plugin.common.api.type.ArtifactDirectoryDescriptor;

@RunWith(Parameterized.class)
public class TychoProjectScannerPluginTest {

    private final static Class<?> clazz = TychoProjectScannerPluginTest.class;

    private final Scanner scanner;
    private final MavenProject project;
    private final Store store;
    private final Matcher<? super Collection<? extends File>> matcher;

    @Parameters
    public static List<Object[]> data() {
        Object[] nothingSelected = new Object[] { new ArrayList<String>(), new ArrayList<String>(), is(empty()) };
        Object[] oneFileSelected = new Object[] { Collections.singletonList("log"), new ArrayList<String>(),
                hasItems(new File(clazz.getResource("log").getFile())) };
        Object[] oneFileAndFolderSelected = new Object[] { Arrays.asList(new String[] { "log", "cache" }), new ArrayList<String>(),
                hasItems(new File(clazz.getResource("log").getFile())) };
        Object[] oneFileExcluded = new Object[] { Collections.singletonList("log"), Collections.singletonList("log"), is(empty()) };

        return Arrays.asList(new Object[][] { nothingSelected, oneFileSelected, oneFileAndFolderSelected, oneFileExcluded });
    }

    public TychoProjectScannerPluginTest(List<String> includes, List<String> excludes, Matcher<? super Collection<? extends File>> matcher) throws IOException {
        this.scanner = mock(Scanner.class);
        this.store = mock(Store.class);
        this.project = mock(MavenProject.class);
        this.matcher = matcher;

        EclipsePluginProject pdeProject = mock(EclipsePluginProject.class);
        BuildProperties properties = mock(BuildProperties.class);
        when(properties.getBinExcludes()).thenReturn(excludes);
        when(properties.getBinIncludes()).thenReturn(includes);

        when(project.getContextValue(TychoConstants.CTX_ECLIPSE_PLUGIN_PROJECT)).thenReturn(pdeProject);
        when(pdeProject.getBuildProperties()).thenReturn(properties);
        when(project.getBasedir()).thenReturn(new File(getClass().getResource(".").getFile()));
        Artifact artifact = mock(Artifact.class);
        when(artifact.getType()).thenReturn("jar");
        when(artifact.getGroupId()).thenReturn("group");
        when(artifact.getArtifactId()).thenReturn("artifact");
        when(project.getArtifact()).thenReturn(artifact);

        ArtifactDirectoryDescriptor artifactDescriptor = mock(ArtifactDirectoryDescriptor.class);
        when(store.create(Mockito.any(Class.class), Mockito.anyString())).thenReturn(artifactDescriptor);
    }

    @Test
    public void testGetAdditionalFiles() throws Exception {
        TychoProjectScannerPlugin plugin = new TychoProjectScannerPlugin();
        plugin.initialize(store, Collections.<String, Object> emptyMap());
        plugin.scan(project, null, null, scanner);
        // FIXME: add assertions
    }
}

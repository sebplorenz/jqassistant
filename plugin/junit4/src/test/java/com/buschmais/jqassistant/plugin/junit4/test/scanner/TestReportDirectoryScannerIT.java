package com.buschmais.jqassistant.plugin.junit4.test.scanner;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.util.List;

import org.junit.Test;

import com.buschmais.jqassistant.plugin.java.test.AbstractJavaPluginIT;
import com.buschmais.jqassistant.plugin.junit4.api.model.TestSuiteDescriptor;
import com.buschmais.jqassistant.plugin.junit4.api.scanner.JunitScope;
import com.buschmais.jqassistant.plugin.junit4.impl.scanner.TestReportDirectory;

public class TestReportDirectoryScannerIT extends AbstractJavaPluginIT {

    /**
     * Verifies that test reports files are scanned.
     * 
     * @throws java.io.IOException
     *             If the test fails.
     */
    @Test
    public void reportFile() throws IOException {
        store.beginTransaction();
        getScanner().scan(new TestReportDirectory(getClassesDirectory(TestReportDirectoryScannerIT.class)), JunitScope.TESTREPORTS);
        List<TestSuiteDescriptor> testSuiteDescriptors = query("MATCH (suite:TestSuite:File) RETURN suite").getColumn("suite");
        assertThat(testSuiteDescriptors.size(), equalTo(1));
        store.commitTransaction();
    }

}

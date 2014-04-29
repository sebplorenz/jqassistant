package com.buschmais.jqassistant.scm.maven.shell;

import org.neo4j.helpers.Service;
import org.neo4j.shell.App;
import org.neo4j.shell.AppCommandParser;
import org.neo4j.shell.Continuation;
import org.neo4j.shell.Output;
import org.neo4j.shell.Session;

import com.buschmais.jqassistant.core.analysis.api.PluginReaderException;
import com.buschmais.jqassistant.core.report.api.ReportHelper;

@Service.Implementation(App.class)
public class EffectiveRulesApp extends AbstractJQAssistantApp {

    public EffectiveRulesApp() throws PluginReaderException {
    }

    @Override
    public String getCommand() {
        return "effective-rules";
    }

    @Override
    public Continuation execute(AppCommandParser parser, Session session, Output out) throws Exception {
        new ReportHelper(new ShellConsole(out)).printRuleSet(getEffectiveRuleSet(parser));
        return Continuation.INPUT_COMPLETE;
    }
}

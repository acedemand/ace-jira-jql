package com.acedemand.plugins.jira.jql;

import com.acedemand.plugins.service.JqlService;
import com.atlassian.crowd.embedded.api.User;
import com.atlassian.jira.JiraDataType;
import com.atlassian.jira.JiraDataTypes;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.IssueManager;
import com.atlassian.jira.issue.search.SearchException;
import com.atlassian.jira.jql.operand.QueryLiteral;
import com.atlassian.jira.jql.query.QueryCreationContext;
import com.atlassian.jira.plugin.jql.function.AbstractJqlFunction;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.util.MessageSet;
import com.atlassian.plugin.spring.scanner.annotation.component.Scanned;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.query.clause.TerminalClause;
import com.atlassian.query.operand.FunctionOperand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Echoes the the string passed in as an argument.
 */
@Scanned
@Named
public class SubtaskCountJqlFunction extends AbstractJqlFunction {
    private static final Logger LOG = LoggerFactory.getLogger(SubtaskCountJqlFunction.class);

    @ComponentImport
    private IssueManager issueManager;


    private JqlService jqlService;

    @Inject
    public SubtaskCountJqlFunction(JqlService jqlService, IssueManager issueManager){
        this.issueManager = issueManager;
        this.jqlService = jqlService;
    }


    @Nonnull
    @Override
    public MessageSet validate(ApplicationUser applicationUser, @Nonnull FunctionOperand functionOperand, @Nonnull TerminalClause terminalClause) {
        return validateNumberOfArgs(functionOperand, 1);
    }

    public List<QueryLiteral> getValues(QueryCreationContext queryCreationContext, FunctionOperand operand, TerminalClause terminalClause) {

        try {
            Collection<Issue> issueCollection = this.jqlService.findIssues(operand.getArgs().get(0), queryCreationContext.getApplicationUser().getName());
            List<QueryLiteral>  queryLiterals = new ArrayList<>(issueCollection.size());
            for(Issue issue : issueCollection){
                if(issue.getSubTaskObjects().size() == 0) {
                    queryLiterals.add(new QueryLiteral(operand, issue.getId()));
                }
            }
            return queryLiterals;
        }catch (SearchException ae){
            LOG.error("Error on searching ",ae);
        }
        return Collections.EMPTY_LIST;
    }

    public int getMinimumNumberOfExpectedArguments() {
        return 1;
    }

    public JiraDataType getDataType() {
        return JiraDataTypes.ISSUE;
    }
}
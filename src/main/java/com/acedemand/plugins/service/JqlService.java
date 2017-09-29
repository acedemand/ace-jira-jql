package com.acedemand.plugins.service;

import com.atlassian.crowd.embedded.api.User;
import com.atlassian.jira.bc.issue.search.SearchService;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.search.SearchException;
import com.atlassian.jira.issue.search.SearchResults;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.web.bean.PagerFilter;
import com.atlassian.plugin.spring.scanner.annotation.component.Scanned;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Named;
import java.util.Collection;

/**
 * Created by Pamir on 11/19/2015.
 */
@Scanned
@Named
public class JqlService {

    private static final Logger LOG = LoggerFactory.getLogger(JqlService.class);

    public Collection<Issue> findIssues(String jql, String user) throws SearchException {
        LOG.debug("User : {} JQL : {}", user, jql);

        ApplicationUser cUser = ComponentAccessor.getUserManager().getUserByName(user);

        SearchService searchService = ComponentAccessor.getComponent(SearchService.class);
        SearchService.ParseResult parseResult = searchService.parseQuery(cUser, jql);
        if (!parseResult.isValid()) {
            LOG.debug("ParseRequest is not valid for {}", jql);
            throw new IllegalArgumentException(String.format("Jql is not valid %s", jql));
        }
        SearchResults results = searchService.search(cUser, parseResult.getQuery(), PagerFilter.newPageAlignedFilter(0, 1000));
        return results.getIssues();

    }

}


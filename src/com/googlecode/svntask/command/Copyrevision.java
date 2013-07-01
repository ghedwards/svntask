package com.googlecode.svntask.command;

import java.io.File;
import java.util.Iterator;
import java.util.Collection;
import java.util.Set;

import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
//import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
//import org.tmatesoft.svn.core.internal.io.fs.FSRepositoryFactory;
//import org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

//import org.tmatesoft.svn.core.ISVNLogEntryHandler;
//import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
//import org.tmatesoft.svn.core.SVNLogEntryPath;
import org.tmatesoft.svn.core.SVNURL;
//import org.tmatesoft.svn.core.wc.SVNLogClient;
//import org.tmatesoft.svn.core.wc.SVNRevision;
//import org.tmatesoft.svn.core.wc.SVNWCClient;
//import org.tmatesoft.svn.core.wc.SVNInfo;

import com.googlecode.svntask.Command;

/**
 * Used for executing svn log. Output is similar to command line client except
 * that it doesn't (yet) include the number of lines that have changed in the
 * revision and the date format is different.
 *
 * Includes these optional params:
 * url: use a svn url rather than a path to a working copy
 * startRevision: revision number or "HEAD" or "BASE" (defaults to "HEAD")
 * endRevision: revision number or "HEAD" or "BASE" (defaults to "BASE")
 * limit: max number of log entries to get, 0 for all (defaults to 0)
 * stopOnCopy: do we stop on copy? (defaults to false)
 * discoverChangedPaths: do we report of all changed paths for every revision being processed? (defaults to false)
 *
 * @author rayvanderborght
 */
public class Copyrevision extends Command
{
    /** */
    public static final String SVN_LOG = "svn.log";

    private static final String LINE_SEPARATOR = System.getProperty("line.separator", "\n");

    private String url;
    private String logProperty;

    /** */
    @Override
    public void execute() throws Exception
    {
        final StringBuilder logBuffer = new StringBuilder(1024);
        
        SVNRepository repository = SVNRepositoryFactory.create(SVNURL.parseURIEncoded(this.url));

        ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager(this.getTask().getUsername(), this.getTask().getPassword());
        repository.setAuthenticationManager(authManager);

        Collection logEntries = null;
    
        logEntries = repository.log(new String[] {""}, null,
                0, repository.getLatestRevision(), true, true);

        for (Iterator entries = logEntries.iterator(); entries.hasNext();) {

            /*
             * gets a next SVNLogEntry
             */
            SVNLogEntry logEntry = (SVNLogEntry) entries.next();

            /*
             * gets the revision number
             */
            this.getTask().log("revision "+ logEntry.getRevision());

            logBuffer.append(logEntry.getRevision());

            break;

        }

        this.getProject().setProperty(this.logProperty, logBuffer.toString());

        repository.closeSession();

    }

    /** */
    @Override
    protected void validateAttributes() throws Exception
    {
        if (this.url == null )
            throw new NullPointerException("must specify a url");

        if (this.logProperty == null)
            this.logProperty = SVN_LOG;

    }

    /**
    * the svn url to use (as opposed to the path of a working copy)
    */
    public void setUrl(String url)
    {
        this.url = url;
    }

    /**
    * the log output goes here
    */
    public void setLogProperty(String logProperty)
    {
        this.logProperty = logProperty;
    }
}

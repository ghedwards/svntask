package com.googlecode.svntask;

import java.util.ArrayList;
import java.util.List;

import com.googlecode.svntask.command.Add;
import com.googlecode.svntask.command.Checkout;
import com.googlecode.svntask.command.Cleanup;
import com.googlecode.svntask.command.Commit;
import com.googlecode.svntask.command.Copy;
import com.googlecode.svntask.command.Delete;
import com.googlecode.svntask.command.Info;
import com.googlecode.svntask.command.Log;
import com.googlecode.svntask.command.Ls;
import com.googlecode.svntask.command.MkDir;
import com.googlecode.svntask.command.Status;
import com.googlecode.svntask.command.Switch;
import com.googlecode.svntask.command.Unlock;
import com.googlecode.svntask.command.Update;
import com.googlecode.svntask.command.Copyrevision;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.fs.FSRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

/**
 *
 * @author jonstevens
 * @author christoph polcin (christoph-polcin.com)
 */
public class SvnTask extends Task
{
	/** */
	private final List<Command> commands = new ArrayList<Command>();

	/** */
	private boolean failonerror = true;

	/** */
	private SVNClientManager manager = null;

	private String password;
	private String user;

	/** */
	public void setPassword(String pass){
		this.password = pass;
	}

	/** */
	public void setUsername(String user){
		this.user = user;
	}


	/** */
	public String getPassword(){
		return this.password;
	}

	/** */
	public String getUsername(){
		return this.user;
	}

	/** */
	public void addCleanup(Cleanup cleanup){
		this.addCommand(cleanup);
	}

	/** */
	public void addUnlock(Unlock unlock){
		this.addCommand(unlock);
	}

	/** */
	public void addCopy(Copy cp){
		this.addCommand(cp);
	}

	/** */
	public void addMkDir(MkDir mkdir){
		this.addCommand(mkdir);
	}

	/** */
	public void addAdd(Add add)
	{
		this.addCommand(add);
	}

	/** */
	public void addDelete(Delete del){
		this.addCommand(del);
	}

	/** */
	public void addCommit(Commit commit)
	{
		this.addCommand(commit);
	}

	/** */
	public void addInfo(Info info)
	{
		this.addCommand(info);
	}

	/** */
	public void addLog(Log log)
	{
		this.addCommand(log);
	}

	/** */
	public void addLs(Ls ls)
	{
		this.addCommand(ls);
	}

	/** */
	public void addStatus(Status status)
	{
		this.addCommand(status);
	}

	/** */
	public void addSwitch(Switch switchTask)
	{
		this.addCommand(switchTask);
	}

	/** */
	public void addUpdate(Update update)
	{
		this.addCommand(update);
	}

	/** */
	public void addCheckout(Checkout checkout)
	{
		this.addCommand(checkout);
	}
	
	/** */
	public void addCopyrevision(Copyrevision copyrevision)
	{
		this.addCommand(copyrevision);
	}

	/** */
	private void addCommand(Command command)
	{
		command.setTask(this);
		this.commands.add(command);
	}


	/** */
	public boolean isFailonerror()
	{
		return this.failonerror;
	}

	/** */
	public void setFailonerror(boolean failonerror)
	{
		this.failonerror = failonerror;
	}

	/** */
	@Override
	public void init() throws BuildException
	{
		super.init();

		try
		{
			this.setupSvnKit();
		}
		catch (Exception e)
		{
			if (this.isFailonerror())
			{
				throw new BuildException(e);
			}
			else
			{
				this.log(e.getMessage());
			}
		}
	}

	/** */
	@Override
	public void execute() throws BuildException
	{
		if (this.user != null && this.password != null){
			ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager(this.user, this.password);
			this.manager.setAuthenticationManager(authManager);
		}

		try
		{
			for (Command command : this.commands)
			{
				command.executeCommand();
			}

			this.manager.dispose();
		}
		catch (Exception e)
		{
			throw new BuildException(e);
		}
	}

	/** */
	public SVNClientManager getSvnClient()
	{
		return this.manager;
	}

	/**
	 * Initializes the library to work with a repository via different
	 * protocols.
	 */
	private void setupSvnKit()
	{
		/*
		 * For using over http:// and https://
		 */
		DAVRepositoryFactory.setup();
		/*
		 * For using over svn:// and svn+xxx://
		 */
		SVNRepositoryFactoryImpl.setup();

		/*
		 * For using over file:///
		 */
		FSRepositoryFactory.setup();

		/*
		 * Create the client manager with defaults
		 */
		this.manager = SVNClientManager.newInstance();
	}
}

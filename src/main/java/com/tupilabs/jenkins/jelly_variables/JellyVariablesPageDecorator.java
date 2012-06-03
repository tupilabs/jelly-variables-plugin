package com.tupilabs.jenkins.jelly_variables;
import hudson.Extension;
import hudson.model.PageDecorator;
import hudson.model.Hudson;
import hudson.model.Job;

import java.io.IOException;
import java.io.StringWriter;
import java.util.StringTokenizer;

import net.sf.json.JSON;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

/**
 * A Page Decorator that displays the jelly variables available.
 * 
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 0.1
 */
@Extension
public class JellyVariablesPageDecorator extends PageDecorator {

    // Fields in config.jelly must match the parameter names in the "DataBoundConstructor"
    @DataBoundConstructor
    public JellyVariablesPageDecorator() {
    	super(JellyVariablesPageDecorator.class);
    	load();
    }
    
    /* (non-Javadoc)
     * @see hudson.model.Descriptor#configure(org.kohsuke.stapler.StaplerRequest, net.sf.json.JSONObject)
     */
    @Override
    public boolean configure(StaplerRequest request, JSONObject json)
    		throws hudson.model.Descriptor.FormException {
    	request.bindJSON(this, json);
    	save();
    	return Boolean.TRUE;
    }

    /* (non-Javadoc)
     * @see hudson.model.PageDecorator#getDisplayName()
     */
    @Override
    public String getDisplayName() {
    	return "Display available jelly variables";
    }
    
    public String handle(final String url) {
    	final StringBuilder sb = new StringBuilder();
    	final String jenkinsURL = Hudson.getInstance().getRootUrl();
    	int index = url.indexOf(jenkinsURL);
    	if(index >= 0) {
    		index += jenkinsURL.length();
    		String urlToken = url.substring(index, url.length());
    		if(urlToken.startsWith("job/")) {
    			this.handleJob(urlToken, sb);
    		}
    	}
    	return sb.toString();
    }

	private void handleJob(String urlToken, StringBuilder sb) {
		StringTokenizer st = new StringTokenizer(urlToken, "/");
		String[] tokens = new String[st.countTokens()];
		int i = 0;
		while(st.hasMoreTokens()) {
			tokens[i] = st.nextToken();
			++i;
		}
		String jobName = tokens[1];
		// TODO: handle job
		
		if(tokens.length >= 3) {
			int buildNumber = Integer.parseInt(tokens[2]);
			Job<?, ?> job = (Job<?, ?>) Hudson.getInstance().getItem(jobName);
			Object o = job.getBuildByNumber(buildNumber);
			JSON obj = JSONSerializer.toJSON(o);
			StringWriter sw = new StringWriter();
			try {
				obj.write(sw);
			} catch (IOException e) {
				e.printStackTrace();
			}
			sb.append(sw.toString());
		}
	}

}

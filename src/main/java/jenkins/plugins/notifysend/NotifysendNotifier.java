package jenkins.plugins.notifysend;

import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.model.Result;
import hudson.model.BallColor;
import hudson.model.Hudson;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Publisher;
import hudson.tasks.Notifier;

import hudson.Proc;

import org.kohsuke.stapler.DataBoundConstructor;

import java.io.IOException;
import java.util.Map;

import net.sf.json.JSONObject;
import org.kohsuke.stapler.StaplerRequest;

import org.kohsuke.stapler.QueryParameter;
import hudson.util.FormValidation;
import javax.servlet.ServletException;


public class NotifysendNotifier extends Notifier {

	private final String name;
	private final String message;

	public String getName() {
		return name;
	}
	public String getMessage() {
		return message;
	}

    @DataBoundConstructor
    public NotifysendNotifier(String name, String message) {
	this.name = name;
	this.message = message;
    }

    @Override
    public boolean perform(AbstractBuild build, Launcher launcher, BuildListener listener) 
    {
      BallColor color = build.getResult().color;
      String chemin   = Hudson.getInstance().getRootPath().toString() + "/war/images/48x48/";
      String image    = chemin + color.getImage();

      try{
        String result  = build.getResult().toString();
//        String shortname = "Jenkins local";
//        String description = "Job ${JOB_NAME} terminé (branche : ${BRANCH}, build #${BUILD_NUMBER}, etat : ${BUILD_STATUS})";
	
	String shortname = getDescriptor().name();
	String description = getDescriptor().message();


        String command = "notify-send --icon=" + image + " \"" + shortname + "\" \"" + description + "\"";
        Map<String, String> vars = build.getEnvVars();
        vars.put("BUILD_STATUS", result);
        Proc proc = launcher.launch(command, vars, listener.getLogger(), build.getProject().getWorkspace());
         int exitCode = proc.join();
      }catch (IOException e) {
        return false;
      }catch (InterruptedException e) {
        return false;
      }

      return true;
    }

    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.NONE;
    }

    @Override
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl) super.getDescriptor();
    }

    /**
     * Descriptor for {@link NotifysendNotifier}. Used as a singleton.
     */
    @Extension // this marker indicates Hudson that this is an implementation of an extension point.
    public static final class DescriptorImpl extends BuildStepDescriptor<Publisher> {


	public DescriptorImpl()
	{
		load();
	}

	private String name;
	private String message;

	public String name() {
		return name;
	}
	public String message() {
		return message;
	}


        @Override
        public boolean isApplicable(Class<? extends AbstractProject> jobType) {
            return true;
        }

	@Override
	public boolean configure(StaplerRequest req, JSONObject formData) throws FormException {
		name = formData.getString("name");
		message = formData.getString("message");
		save();
		return super.configure(req, formData);
	}

        @Override
        public String getDisplayName() {
            return "Send notification via notify-send";
        }

	@Override
	public String getHelpFile() {
		return "/plugin/notifysend/help.html";
	}
    }

}

